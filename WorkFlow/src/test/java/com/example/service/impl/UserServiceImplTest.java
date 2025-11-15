package com.example.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.domain.entity.UserEntity;
import com.example.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder encoder;

	@InjectMocks
	private UserServiceImpl userService;

	private UserEntity user;

	@BeforeEach
	void testSetUp() {
		user = new UserEntity();
		user.setId(11);
		user.setName("山田太郎");
		user.setEmail("taro@example.com");
		user.setPassword("encodedpassword");
		user.setDepartmentId(1);
		user.setJoiningDate(LocalDate.of(2023, 1, 1));
	}

	@Test
	@DisplayName("正常：getUserByIdで存在するIDを指定した場合")
	void testGetUserById() {
		when(userRepository.findById(11)).thenReturn(Optional.of(user));

		UserEntity result = userService.getUserById(11);

		assertThat(result).isEqualTo(user);
	}

	@Test
	@DisplayName("正常：登録されていないID指定した場合")
	void testGetUserById_notFound() {
		when(userRepository.findById(12)).thenReturn(Optional.empty());

		try {
			userService.getUserById(12);
			fail("例外が発生しませんでした");
		} catch (UsernameNotFoundException e) {
			assertThat(e.getMessage()).contains("ユーザーが見つかりません");
		}
	}

	@Test
	@DisplayName("正常：getUserEntitiesがfindAllの結果を返す")
	void testGetUserEntities() {
		UserEntity usersecond = new UserEntity();
		usersecond.setName("test");

		when(userRepository.findAll()).thenReturn(Arrays.asList(user, usersecond));

		List<UserEntity> result = userService.getUserEntities(null);

		assertThat(result).containsExactly(user, usersecond);
	}

	@Test
	@DisplayName("正常：getUserOneでメールアドレスに一致するユーザーを取得")
	void testGetUserOne() {
		when(userRepository.findByEmail("taro@example.com")).thenReturn(Optional.of(user));

		UserEntity result = userService.getUserOne("taro@example.com");

		assertThat(result.getEmail()).isEqualTo("taro@example.com");
	}

	@Test
	@DisplayName("正常：updateUserOneで情報を更新し保存する")
	void testUpdateUserOne() {
		// 既存ユーザー（DB上にいる想定）
		when(userRepository.findByEmail("taro@example.com")).thenReturn(Optional.of(user));

		// パスワードエンコードのモック
		when(encoder.encode("newpassword")).thenReturn("encoded-newpassword");

		// save() の動作は特に戻り値不要（void）
		when(userRepository.save(any())).thenReturn(user);

		// テスト対象呼び出し
		userService.updateUserOne("taro@example.com", "newpassword", "田中一郎", 2, LocalDate.of(2024, 4, 1));

		// 更新されたか検証
		assertThat(user.getName()).isEqualTo("田中一郎");
		assertThat(user.getDepartmentId()).isEqualTo(2);
		assertThat(user.getJoiningDate()).isEqualTo(LocalDate.of(2024, 4, 1));
		assertThat(user.getPassword()).isEqualTo("encoded-newpassword");

		// save() が呼ばれたことを確認
		verify(userRepository).save(user);
	}

	@Test
	@DisplayName("正常：deleteByEmailでユーザーを削除")
	void testDeleteByEmail() {
		doNothing().when(userRepository).deleteByEmail("taro@example.com");

		userService.deleteByEmail("taro@example.com");

		verify(userRepository, times(1)).deleteByEmail("taro@example.com");
	}

	@Test
	@DisplayName("正常：getLoginUserでemail一致するユーザーを取得")
	void testGetLoginUser() {
		when(userRepository.findByEmail("taro@example.com")).thenReturn(Optional.of(user));

		UserEntity result = userService.getLoginUser("taro@example.com");

		assertThat(result).isEqualTo(user);
	}

	@Test
	@DisplayName("異常：updatePasswordでユーザーが見つからない")
	void testUpdatePassword_userNotFound() {
		when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

		assertThatThrownBy(() -> userService.updatePassword("unknown@example.com", "newpass"))
				.isInstanceOf(IllegalArgumentException.class).hasMessageContaining("ユーザーが見つかりません");
	}

}
