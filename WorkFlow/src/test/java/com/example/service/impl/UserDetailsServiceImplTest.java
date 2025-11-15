package com.example.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.domain.entity.UserEntity;
import com.example.domain.enums.Role;
import com.example.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

	@InjectMocks
	private UserDetailsServiceImpl userDetailsServiceImpl;

	@Mock
	private UserService userService;

	private UserEntity user;

	@BeforeEach
	void setup() {
		user = new UserEntity();
		user.setEmail("taro@example.com");
		user.setPassword("encodedpassword");
		user.setRole(Role.GENERAL);
	}

	@Test
	@DisplayName("正常：userService.getLoginUser(email) が UserEntity を返す ")
	void testLoadUserByUsername_success() {
		when(userService.getLoginUser("taro@example.com")).thenReturn(user);

		UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername("taro@example.com");

		assertEquals("taro@example.com", userDetails.getUsername());
		assertEquals("encodedpassword", userDetails.getPassword());
		assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_GENERAL")));
	}

	@Test
	@DisplayName("異常： ユーザーが存在しない場合 → UsernameNotFoundExceptionが発生する")
	void testLoadUserByUsername_error() {
		when(userService.getLoginUser("unknown@example.com")).thenReturn(null);

		assertThrows(UsernameNotFoundException.class, () -> {
			userDetailsServiceImpl.loadUserByUsername("unknown@example.com");
		});
	}

}
