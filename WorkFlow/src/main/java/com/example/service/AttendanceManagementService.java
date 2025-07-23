package com.example.service;

import com.example.domain.entity.UserEntity;

public interface AttendanceManagementService {
	
	/**
	 * ユーザー情報を取得
	 * @param userEntity
	 * @return
	 */
	UserEntity getUserByEmail(String email);
	
}