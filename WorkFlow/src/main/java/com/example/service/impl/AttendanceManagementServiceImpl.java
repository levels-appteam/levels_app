package com.example.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.domain.entity.UserEntity;
import com.example.repository.UserRepository;
import com.example.service.AttendanceManagementService;

@Service
public class AttendanceManagementServiceImpl implements AttendanceManagementService {

	@Autowired
	private UserRepository userRepository;

	/**
	 * ユーザー情報を取得
	 * 
	 * @param userEntity
	 * @return
	 */
	@Override
	public UserEntity getUserByEmail(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("ユーザーが見つかりません: " + email));
	}

}