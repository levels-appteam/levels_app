package com.example.service;

import java.util.List;

import com.example.domain.entity.AttendanceEntity;
import com.example.domain.entity.UserEntity;
import com.example.domain.enums.AttendanceType;

/**
 * アテンダンスサービスクラス
 * 打刻に関するビジネスロジックを定義するサービスインターフェース
 */
public interface AttendanceService {
	void recordPunch(Integer userId, AttendanceType type);
	
	/**
	 * 勤怠の履歴を取得する
	 * @param userId
	 * @return
	 */
	public List<AttendanceEntity> getAttendanceHistory(UserEntity userEntity);
	
	/**
	 * ログインユーザー取得
	 * @param email
	 */
	public AttendanceType getLoginUserType(Integer userId);
	
	/**
	 * 今日打刻したかチェック
	 * @param userId
	 * @param type
	 * @return
	 */
	boolean hasPunchedToday(Integer userId);
}