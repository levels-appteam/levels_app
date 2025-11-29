package com.example.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 勤怠履歴表示用のDTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceSummaryDto {
	/**
	 * 勤怠日時
	 * YYYY/MM/DD
	 */
	private String workDate;
	/**
	 * 勤務開始時間
	 * HH:MM形式
	 */
	private String workStart;
	/**
	 * 勤務終了時間
	 * HH:MM形式
	 */
	private String workEnd;
	/**
	 * 実働時間
	 */
	private String workingMinutes;
	/**
	 * 休憩時間
	 */
	private String breakMinutes;
}