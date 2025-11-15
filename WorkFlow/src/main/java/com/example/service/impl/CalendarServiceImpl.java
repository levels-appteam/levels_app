package com.example.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.domain.entity.CalendarEntity;
import com.example.form.CalendarForm;
import com.example.repository.CalendarRepository;
import com.example.service.CalendarService;

import jakarta.transaction.Transactional;

@Service
public class CalendarServiceImpl implements CalendarService {

	@Autowired
	private CalendarRepository calendarRepository;

	@Override
	public List<CalendarEntity> getCalendar(LocalDate start, LocalDate end) {
		return calendarRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqual(end, start);
	}

	/**
	 *フォーム登録
	 */
	@Override
	@Transactional
	public void createRange(CalendarForm form) {
		LocalDate start = form.getStartDate();
		LocalDate end = form.getEndDate();
		
		//バリデーション
		if (start == null || end == null) {
			throw new IllegalArgumentException("開始日・終了日は必須です。");
		}
		if (end.isBefore(start)) {
			throw new IllegalArgumentException("終了日は開始日以降を指定してください。");
		}
		
		CalendarEntity calendarEntity = new CalendarEntity();
		calendarEntity.setStartDate(start);
		calendarEntity.setEndDate(end);
		calendarEntity.setEventTitle(form.getEventTitle());
		calendarEntity.setEventType(form.getEventType());
		calendarRepository.save(calendarEntity);
	}
}