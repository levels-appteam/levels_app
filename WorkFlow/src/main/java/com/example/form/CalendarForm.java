package com.example.form;

import java.time.LocalDate;

import com.example.domain.enums.CalendarEnum;

import lombok.Data;

/**
 * カレンダー管理のフォームクラス
 */
@Data
public class CalendarForm {

	/**
	 * イベント開始日
	 */
	private LocalDate startDate;
	/**
	 * イベント終了日
	 */
	private LocalDate endDate;

	/**
	 * イベントの名称
	 */
	private String eventTitle;

	/**
	 * イベント種類
	 */
	private CalendarEnum eventType;
}