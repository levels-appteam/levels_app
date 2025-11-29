package com.example.service;

import java.time.LocalDate;
import java.util.List;

import com.example.domain.dto.AttendanceSummaryDto;
import com.example.domain.entity.AttendanceSummaryEntity;
import com.example.domain.entity.UserEntity;
import com.example.domain.enums.AttendanceType;

/**
 * 勤務時間のロジックのサービスインターフェースクラス
 */
public interface AttendanceSummaryService {
	/**
	 * 打刻の情報
	 * @param userEntity
	 * @param type
	 */
	void updateSummary(UserEntity userEntity, AttendanceType type);
	
	/**
	 * 打刻時間の取得
	 * @param workStart
	 * @param workEnd
	 * @return
	 */
	AttendanceSummaryDto getWorkTimeDto(String workStart, String workEnd);
	
	/**
	 * ユーザーに紐づく勤怠サマリーデータを取得する
	 * @param userEntity
	 * @return 勤怠サマリのリスト
	 */
	List<AttendanceSummaryEntity> getSummaryByUser(UserEntity userEntity, LocalDate startMonth, LocalDate endMonth);
	
	/**
	 * 勤務サマリデータを取得（DTO）
	 * @param userEntity
	 * @return
	 */
	List<AttendanceSummaryDto> getSummaryDtoByUser(UserEntity userEntity, LocalDate startMonth, LocalDate endMonth);
	
	/**
	 * 勤怠情報更新用
	 * @param user
	 * @param date
	 * @param workStart
	 * @param workEnd
	 */
	void updateWorkTime(UserEntity user, LocalDate date, String workStart, String workEnd);
}