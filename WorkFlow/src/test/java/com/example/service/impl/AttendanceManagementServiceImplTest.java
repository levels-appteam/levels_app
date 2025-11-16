package com.example.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.domain.entity.UserEntity;
import com.example.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AttendanceManagementServiceImplTest {

	@InjectMocks
	private AttendanceManagementServiceImpl attendanceManagementService;

	@Mock
	private UserRepository userRepository;

	@Test
	@DisplayName("正常：ユーザーが存在し、UserEntity が返る")
	void testGetUserByEmail_Success() {

		String email = "test@example.com";
		UserEntity user = new UserEntity();
		user.setEmail(email);
		when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

		UserEntity result = attendanceManagementService.getUserByEmail(email);

		assertNotNull(result);
		assertEquals(email, result.getEmail());
		verify(userRepository).findByEmail(email);
	}

	@Test
	@DisplayName("異常：ユーザーが存在せず UsernameNotFoundException")
	void testGetUserByEmail_NotFound() {
		String email = "notfound@example.com";
		when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

		UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
				() -> attendanceManagementService.getUserByEmail(email));

		assertEquals("ユーザーが見つかりません: " + email, exception.getMessage());
		verify(userRepository).findByEmail(email);
	}
}