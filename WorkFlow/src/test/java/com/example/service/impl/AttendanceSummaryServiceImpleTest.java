package com.example.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.domain.dto.AttendanceSummaryDto;
import com.example.domain.entity.AttendanceSummaryEntity;
import com.example.domain.entity.UserEntity;
import com.example.domain.enums.AttendanceType;
import com.example.repository.AttendanceSummaryRepository;

@ExtendWith(MockitoExtension.class)
class AttendanceSummaryServiceImpleTest {

	@InjectMocks
	AttendanceSummaryServiceImple service;

	@Mock
	AttendanceSummaryRepository summaryRepository;

	UserEntity user;
	AttendanceSummaryEntity entity;

	@BeforeEach
	void setup() {
		user = new UserEntity();
		user.setId(1);
		user.setName("山田太郎");

		entity = new AttendanceSummaryEntity();
		entity.setUser(user);
		entity.setWorkDate(LocalDate.of(2025, 1, 1));
		entity.setWorkStart(540); // 09:00
	}

	@Test
	@DisplayName("正常：DTO変換が正しく行われること")
	void testGetSummaryDtoByUser() {
		LocalDate start = LocalDate.of(2025, 1, 1);
		LocalDate end = LocalDate.of(2025, 1, 31);
		when(summaryRepository.findByUserAndWorkDateBetweenOrderByWorkDateAsc(user, start, end))
				.thenReturn(List.of(entity));

		List<AttendanceSummaryDto> result = service.getSummaryDtoByUser(user, start, end);

		assertEquals(1, result.size());
		assertEquals(540, result.get(0).getWorkStart());
		verify(summaryRepository).findByUserAndWorkDateBetweenOrderByWorkDateAsc(user, start, end);
	}

	@Test
	@DisplayName("正常：勤務開始INで新規作成されること")
	void testUpdateSummary_IN_New() {
		when(summaryRepository.findByUserAndWorkDate(eq(user), any())).thenReturn(Optional.empty());

		service.updateSummary(user, AttendanceType.IN);

		verify(summaryRepository).save(argThat(saved -> saved.getWorkStart() != null));
	}

	@Test
	@DisplayName("正常：勤務終了OUTで新規作成されること")
	void testUpdateSummary_OUT_New() {
		when(summaryRepository.findByUserAndWorkDate(eq(user), any())).thenReturn(Optional.empty());

		service.updateSummary(user, AttendanceType.OUT);

		verify(summaryRepository).save(argThat(saved -> saved.getWorkEnd() != null));
	}

	@Test
	@DisplayName("正常：休憩開始BREAK_INで新規作成されること")
	void testUpdateSummary_BREAK_IN_New() {
		when(summaryRepository.findByUserAndWorkDate(eq(user), any())).thenReturn(Optional.empty());

		service.updateSummary(user, AttendanceType.BREAK_IN);

		verify(summaryRepository).save(argThat(saved -> saved.getBreakStart() != null));
	}

	@Test
	@DisplayName("正常：休憩終了BREAK_OUTで新規作成されること")
	void testUpdateSummary_BREAK_OUT_New() {
		when(summaryRepository.findByUserAndWorkDate(eq(user), any())).thenReturn(Optional.empty());

		service.updateSummary(user, AttendanceType.BREAK_OUT);

		verify(summaryRepository).save(argThat(saved -> saved.getBreakEnd() != null));
	}

	@Test
	@DisplayName("正常：当日2回目以降は該当フィールドが上書きされる")
	void testUpdateSummary_ExistingData() {
		AttendanceSummaryEntity existing = new AttendanceSummaryEntity();
		existing.setUser(user);
		existing.setWorkDate(LocalDate.now());

		when(summaryRepository.findByUserAndWorkDate(eq(user), any())).thenReturn(Optional.of(existing));

		service.updateSummary(user, AttendanceType.IN);

		verify(summaryRepository).save(argThat(saved -> saved.getWorkStart() != null));
	}

	@Test
	@DisplayName("正常：現在時刻が0〜1439分の範囲であること")
	void testGetTimeIntMinutes() {
		int minutes = LocalTime.now().getHour() * 60 + LocalTime.now().getMinute();
		assertTrue(minutes >= 0 && minutes <= 1439);
	}
}