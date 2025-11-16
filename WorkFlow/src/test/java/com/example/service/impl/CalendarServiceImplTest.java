package com.example.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.domain.entity.CalendarEntity;
import com.example.form.CalendarForm;
import com.example.repository.CalendarRepository;

@ExtendWith(MockitoExtension.class)
class CalendarServiceImplTest {

	@InjectMocks
	private CalendarServiceImpl calendarService;

	@Mock
	private CalendarRepository calendarRepository;

	@Test
	@DisplayName("正常：期間が重複するイベントが取得されること")
	void getCalendar_returnsOverlappingEvents() {
		// Arrange
		LocalDate start = LocalDate.of(2025, 10, 1);
		LocalDate end = LocalDate.of(2025, 10, 31);
		List<CalendarEntity> expected = List.of(new CalendarEntity());

		when(calendarRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqual(end, start))
				.thenReturn(expected);

		// Act
		List<CalendarEntity> result = calendarService.getCalendar(start, end);

		// Assert
		assertEquals(expected, result);
		verify(calendarRepository).findByStartDateLessThanEqualAndEndDateGreaterThanEqual(end, start);
	}

	@Test
	@DisplayName("正常：境界値（start==end, end==start）も含まれること")
	void getCalendar_includesBoundaryDates() {
		LocalDate start = LocalDate.of(2025, 10, 1);
		LocalDate end = LocalDate.of(2025, 10, 1);
		List<CalendarEntity> expected = List.of(new CalendarEntity());

		when(calendarRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqual(end, start))
				.thenReturn(expected);

		List<CalendarEntity> result = calendarService.getCalendar(start, end);

		assertEquals(expected, result);
		verify(calendarRepository).findByStartDateLessThanEqualAndEndDateGreaterThanEqual(end, start);
	}

	@Test
	@DisplayName("正常：項目入力後、登録ボタン押下で保存されること")
	void createRange_savesCalendarEvent() {
		CalendarForm form = new CalendarForm();
		form.setStartDate(LocalDate.of(2025, 10, 1));
		form.setEndDate(LocalDate.of(2025, 10, 5));
		form.setEventTitle("会議");

		calendarService.createRange(form);

		verify(calendarRepository).save(argThat(entity -> entity.getStartDate().equals(form.getStartDate())
				&& entity.getEndDate().equals(form.getEndDate()) && form.getEventTitle().equals(entity.getEventTitle())));
	}

	@Test
	@DisplayName("異常：開始日または終了日が未入力の場合、例外がスローされる")
	void createRange_missingDates_throwsException() {
		CalendarForm form = new CalendarForm(); // startDate, endDateはnull

		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
			calendarService.createRange(form);
		});

		assertEquals("開始日・終了日は必須です。", ex.getMessage());
		verify(calendarRepository, never()).save(any());
	}

	@Test
	@DisplayName("異常：終了日が開始日より前の場合、例外がスローされる")
	void createRange_endBeforeStart_throwsException() {
		CalendarForm form = new CalendarForm();
		form.setStartDate(LocalDate.of(2025, 10, 5));
		form.setEndDate(LocalDate.of(2025, 10, 1));

		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
			calendarService.createRange(form);
		});

		assertEquals("終了日は開始日以降を指定してください。", ex.getMessage());
		verify(calendarRepository, never()).save(any());
	}
}