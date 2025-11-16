package com.example.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.example.domain.entity.PaidleavesEntity;
import com.example.domain.entity.UserEntity;
import com.example.repository.PaidleavesRepository;
import com.example.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class PaidleavesServiceImplTest {

	@InjectMocks
	private PaidleavesServiceImpl service;

	@Mock
	private UserRepository userRepository;

	@Mock
	private PaidleavesRepository paidleavesRepository;

	private UserEntity user;
	private PaidleavesEntity leave;

	@BeforeEach
	void setup() {
		user = new UserEntity();
		user.setId(1);
		user.setName("テストユーザー");
		user.setJoiningDate(LocalDate.now().minusMonths(18));

		leave = new PaidleavesEntity();
		leave.setUserEntity(user);
		leave.setDays(10f);
		leave.setUsedDays(2f);
		leave.setGrantDate(LocalDate.now().minusMonths(6));
		leave.setRevocationDate(LocalDate.now().plusYears(1));
	}

	@Test
	@DisplayName("全ユーザー取得")
	void testGetUsersList() {
		when(userRepository.findAll()).thenReturn(List.of(user));
		List<UserEntity> result = service.getUsersList();
		assertEquals(1, result.size());
		verify(userRepository).findAll();
	}

	@Test
	@DisplayName("付与一覧取得（昇順）")
	void testGetAllPaidleavesSorted() {
		when(paidleavesRepository.findByUserEntityOrderByGrantDateAsc(user)).thenReturn(List.of(leave));
		List<PaidleavesEntity> result = service.getAllPaidleaves(user);
		assertEquals(leave, result.get(0));
	}

	@Test
	@DisplayName("既存ID取得：正常系")
	void testFindUserById_success() {
		when(userRepository.findById(1)).thenReturn(Optional.of(user));
		UserEntity result = service.findUserById(1);
		assertEquals(user, result);
	}

	@Test
	@DisplayName("未存在ID取得：nullを返す")
	void testFindUserById_notFound() {
		when(userRepository.findById(999)).thenReturn(Optional.empty());
		UserEntity result = service.findUserById(999);
		assertNull(result);
	}

	@Test
	@DisplayName("ユーザーの有給レコード取得")
	void testGetPaidleavesByUser() {
		when(paidleavesRepository.findByUserEntity(user)).thenReturn(List.of(leave));
		List<PaidleavesEntity> result = service.getPaidleavesByUser(user);
		assertEquals(1, result.size());
	}

	@Test
	@DisplayName("勤続月数計算：18ヶ月")
	void testCalculateMonthsWorked() {
		LocalDate joiningDate = LocalDate.now().minusMonths(18);
		long result = service.calculateMonthsWorked(joiningDate);
		assertEquals(18, result);
	}

	@Test
	@DisplayName("合算：残日数計算（null耐性あり）")
	void testGetRemainingDays() {
		PaidleavesEntity p1 = new PaidleavesEntity();
		p1.setDays(10f);
		p1.setUsedDays(null);
		PaidleavesEntity p2 = new PaidleavesEntity();
		p2.setDays(null);
		p2.setUsedDays(0f);
		when(paidleavesRepository.findByUserEntityAndRevocationDateAfter(eq(user), any())).thenReturn(List.of(p1, p2));
		float result = service.getRemainingDays(user);
		assertEquals(10f, result);
	}

	@Test
	@DisplayName("calculateGrantDays：境界値確認")
	void testCalculateGrantDays_boundary() {
		assertEquals(0f, service.calculateGrantDays(5));
		assertEquals(10f, service.calculateGrantDays(6));
		assertEquals(11f, service.calculateGrantDays(18));
		assertEquals(12f, service.calculateGrantDays(30));
		assertEquals(14f, service.calculateGrantDays(42));
		assertEquals(16f, service.calculateGrantDays(54));
		assertEquals(18f, service.calculateGrantDays(66));
		assertEquals(20f, service.calculateGrantDays(78));
	}

}