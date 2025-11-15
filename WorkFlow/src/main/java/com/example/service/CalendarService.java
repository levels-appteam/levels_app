package com.example.service;

import java.time.LocalDate;
import java.util.List;

import com.example.domain.entity.CalendarEntity;
import com.example.form.CalendarForm;

public interface CalendarService {
	
	/**
	 * カレンダーのイベントリストを取得
	 * @param start
	 * @param end
	 * @return
	 */
	List<CalendarEntity> getCalendar(LocalDate start, LocalDate end);
	
	/**
	 * イベント登録用
	 * @param form
	 */
	void createRange(CalendarForm form);
	
}