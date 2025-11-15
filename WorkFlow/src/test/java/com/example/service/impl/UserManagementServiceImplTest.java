package com.example.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.domain.entity.UserEntity;
import com.example.domain.enums.Role;
import com.example.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserManagementServiceImplTest {

	@InjectMocks
	private UserManagementServiceImpl userService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	private UserEntity user;

	@BeforeEach
	void setUp() {
		user = new UserEntity();
		user.setEmail("taro@example.com");
		user.setPassword("rawpass");
	}

	@Test
	@DisplayName("正常：getUsersで全件取得できる")
	void testGetUsers() {
		UserEntity user2 = new UserEntity();
		when(userRepository.findAll()).thenReturn(List.of(user, user2));

		List<UserEntity> result = userService.getUsers();
		assertThat(result).containsExactly(user, user2);
	}

	@Test
	@DisplayName("正常：signupで新規ユーザー登録（パスワードエンコード＆初期ロール）")
	void testSignup_success() {
		when(userRepository.existsByEmail("taro@example.com")).thenReturn(false);
		when(passwordEncoder.encode("rawpass")).thenReturn("encodedpass");
		when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

		userService.signup(user);

		assertThat(user.getRole()).isEqualTo(Role.GENERAL);
		assertThat(user.getPassword()).isEqualTo("encodedpass");
		verify(userRepository).save(user);
	}

	@Test
	@DisplayName("異常：signupで既存ユーザーの場合は例外を投げる")
	void testSignup_alreadyExists() {
		when(userRepository.existsByEmail("taro@example.com")).thenReturn(true);

		assertThatThrownBy(() -> userService.signup(user)).isInstanceOf(DataAccessException.class)
				.hasMessageContaining("ユーザーが既に存在");

		verify(userRepository, never()).save(any());
	}

	@Test
	@DisplayName("正常：部署IDでユーザーを絞り込み")
	void testGetUsersByDepartment() {
		when(userRepository.findByDepartmentId(1)).thenReturn(List.of(user));
		List<UserEntity> result = userService.getUsersByDepartment(1);
		assertThat(result).containsExactly(user);
	}

	@Test
	@DisplayName("正常：部分一致でメール検索")
	void testFindUsersByPartialName() {
		when(userRepository.findByEmailContaining("taro")).thenReturn(List.of(user));
		List<UserEntity> result = userService.findUsersByPartialName("taro");
		assertThat(result).containsExactly(user);
	}

	@Test
	@DisplayName("正常：メール＋部署IDで絞り込み検索")
	void testFindByEmailAndDepartment_match() {
		when(userRepository.findByEmailContainingAndDepartmentId("taro", 1)).thenReturn(List.of(user));
		List<UserEntity> result = userService.findByEmailAndDepartment("taro", 1);
		assertThat(result).containsExactly(user);
	}

}
