package com.example.service;

import java.time.LocalDate;
import java.util.List;

import com.example.domain.entity.PaidleavesEntity;
import com.example.domain.entity.UserEntity;

public interface PaidleavesService {

	/**
	 * ユーザー一覧を取得
	 * 
	 * @return
	 */
	public List<UserEntity> getUsersList();

	/**
	 * 勤務年数計算
	 * @param joiningDate
	 * @return
	 */
	public long calculateMonthsWorked(LocalDate joiningDate);

	/**
	 * 有給付与
	 * @param months
	 * @return
	 */
	public float calculateGrantDays(long months);

	/**
	 * 勤務年数
	 * @param user
	 */
	public void grantPaidLeaveIfEligible(UserEntity user);
	
	/**
	 * 有給付与一覧取得
	 * @return
	 */
	public List<PaidleavesEntity> getAllPaidleaves(UserEntity userEntity);

	/**
	 * ユーザーごとの有給情報
	 * @param user
	 * @return
	 */
	public List<PaidleavesEntity> getPaidleavesByUser(UserEntity user);

	/**
	 * 
	 * @param id
	 * @return
	 */
	public UserEntity findUserById(Integer id);
	
	
	/**
	 * 有給申請情報
	 * @param userEntity
	 * @param daysToDeduct
	 */
	void deductPaidLeaveDays(UserEntity userEntity, float daysToDeduct);
	
	/**
	 * 有給残日数
	 * @param user
	 * @return
	 */
	float getRemainingDays(UserEntity user);
	
	/**
	 * 全ユーザー有給取得
	 * @return
	 */
	List<PaidleavesEntity> getAllPaidleaves();
	
}