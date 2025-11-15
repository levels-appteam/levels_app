package com.example.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.domain.entity.UserEntity;
import com.example.repository.UserRepository;
import com.example.service.UserService;

/**
 * 
 */
@Service
@Primary
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	PasswordEncoder encoder;

	/**
	 * ユーザー１件取得 userId参照にして
	 */
	@Override
	public UserEntity getUserById(Integer id) {
		return userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("ユーザーが見つかりません"));
	}

	/**
	 * ユーザー取得
	 */
	@Override
	public List<UserEntity> getUserEntities(UserEntity userEntity) {
		return userRepository.findAll();
	}

	/** ユーザー1件取得 */
	@Override
	public UserEntity getUserOne(String email) {
		Optional<UserEntity> option = userRepository.findByEmail(email);
		UserEntity userEntity = option.orElse(null);
		return userEntity;
	}

	/**
	 * ユーザー更新
	 */
	@Transactional
	@Override
	public void updateUserOne(String email, String password, String name, Integer departmentId, LocalDate joiningDate) {
		// ユーザーの取得
		UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("ユーザーが見つかりません"));

		// 値を更新（必要ならパスワードを暗号化）
		user.setPassword(encoder.encode(password));
		user.setName(name);
		user.setDepartmentId(departmentId);
		user.setJoiningDate(joiningDate);

		// 保存（saveはUPDATEもINSERTも行う）
		userRepository.save(user);
	}

	/**
	 * ユーザー削除
	 */
	@Transactional
	@Override
	public void deleteByEmail(String email) {
		userRepository.deleteByEmail(email);
	}

	/**
	 * ログインユーザー取得
	 */
	@Override
	public UserEntity getLoginUser(String email) {
		Optional<UserEntity> optional = userRepository.findByEmail(email);
		UserEntity userEntity = optional.orElse(null);
		return userEntity;
	}

	/**
	 * パスワード更新用
	 */
	@Override
	public void updatePassword(String email, String encodedPassword) {
		UserEntity u = userRepository.findByEmail(email).orElse(null);
		if (u == null) {
			throw new IllegalArgumentException("ユーザーが見つかりません");
		}
		u.setPassword(encodedPassword);
		userRepository.save(u);
	}

}