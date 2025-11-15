package com.example.service;

import java.time.LocalDate;
import java.util.List;

import com.example.domain.entity.RequestEntity;
import com.example.domain.entity.UserEntity;
import com.example.domain.enums.RequestStatus;
import com.example.form.RequestForm;

public interface RequestService {

	/**
	 * ログインユーザーの申請したリクエスト取得
	 * 
	 * @param userEntity
	 * @return
	 */
	List<RequestEntity> getUserRequests(UserEntity userEntity);

	/**
	 * 全ユーザーの申請取得
	 * 
	 * @param status
	 * @return
	 */
	List<RequestEntity> getRequestsByStatus(RequestStatus status);

	/**
	 * 有給申請情報取得
	 * 
	 * @param userEntity
	 * @param requestForm
	 */
	void savePaidLeaveRequest(UserEntity userEntity, RequestForm requestForm);

	/**
	 * 有給申請一覧を取得
	 * 
	 * @param userEntity
	 * @return
	 */
	List<RequestEntity> getUserPaidLeaveRequests(UserEntity userEntity);

	/**
	 * 勤務修正一覧取得
	 * 
	 * @param userEntity
	 * @return
	 */
	List<RequestEntity> getUserCorrectionRequests(UserEntity userEntity);

	/**
	 * 勤務修正を取得
	 * 
	 * @param userEntity
	 * @param requestForm
	 */
	void saveCorrectionRequest(UserEntity userEntity, RequestForm requestForm);

	
	/**
	 * カレンダー用有給承認取得
	 * @param userEntity
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<RequestEntity> getApprovedList(UserEntity userEntity, LocalDate startDate, LocalDate endDate);

	/**
	 * 承認画面用リスト表示
	 * @return
	 */
	List<RequestEntity> findAllWithUserOrderBySubmittedAtDesc();

}