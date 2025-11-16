package com.example.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.domain.entity.RequestEntity;
import com.example.domain.entity.UserEntity;
import com.example.domain.enums.RequestKind;
import com.example.domain.enums.RequestStatus;
import com.example.form.RequestForm;
import com.example.repository.RequestRepository;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

	@InjectMocks
	private RequestServiceImpl requestServiceImpl;

	@Mock
	private RequestRepository requestRepository;

	private UserEntity user;
	private RequestForm form;
	private List<RequestEntity> dummyList;
	private LocalDate today;

	@BeforeEach
	void setup() {
		user = new UserEntity();
		user.setId(1);
		user.setName("テストユーザー");

		form = new RequestForm();
		form.setTargetDate(LocalDate.of(2025, 10, 1));
		form.setComment("体調不良");

		RequestEntity request = new RequestEntity();
		request.setUserEntity(user);
		request.setKind(RequestKind.PAID_LEAVE);
		request.setStatus(RequestStatus.PENDING);
		request.setTargetDate(form.getTargetDate());
		request.setComment(form.getComment());
		request.setSubmittedAt(LocalDateTime.now());

		dummyList = List.of(request);
		today = LocalDate.now();
	}

	@Test
	@DisplayName("ユーザーの全申請取得：ユーザー指定で全申請を取得できること")
	void testGetUserRequests() {
		when(requestRepository.findByUserEntity(user)).thenReturn(dummyList);
		List<RequestEntity> result = requestServiceImpl.getUserRequests(user);
		assertEquals(dummyList, result);
		verify(requestRepository).findByUserEntity(user);
	}

	@Test
	@DisplayName("申請ステータス指定：ステータスで申請を絞り込めること")
	void testGetRequestsByStatus() {
		when(requestRepository.findByStatus(RequestStatus.PENDING)).thenReturn(dummyList);
		List<RequestEntity> result = requestServiceImpl.getRequestsByStatus(RequestStatus.PENDING);
		assertEquals(dummyList, result);
		verify(requestRepository).findByStatus(RequestStatus.PENDING);
	}

	@Test
	@DisplayName("有給申請保存：kind=PAID_LEAVE, status=PENDING で保存されること")
	void testSavePaidLeaveRequest() {
		requestServiceImpl.savePaidLeaveRequest(user, form);
		verify(requestRepository).save(org.mockito.ArgumentMatchers
				.argThat(req -> req.getKind() == RequestKind.PAID_LEAVE && req.getStatus() == RequestStatus.PENDING
						&& req.getUserEntity().equals(user) && req.getTargetDate().equals(form.getTargetDate())
						&& req.getComment().equals(form.getComment())));
	}

	@Test
	@DisplayName("有給申請一覧取得：kind=PAID_LEAVE の申請が返されること")
	void testGetUserPaidLeaveRequests() {
		when(requestRepository.findByUserEntityAndKind(user, RequestKind.PAID_LEAVE)).thenReturn(dummyList);
		List<RequestEntity> result = requestServiceImpl.getUserPaidLeaveRequests(user);
		assertEquals(dummyList, result);
		verify(requestRepository).findByUserEntityAndKind(user, RequestKind.PAID_LEAVE);
	}

	@Test
	@DisplayName("勤務修正申請保存：kind=CORRECTION, status=PENDING で保存されること")
	void testSaveCorrectionRequest() {
		requestServiceImpl.saveCorrectionRequest(user, form);
		verify(requestRepository).save(org.mockito.ArgumentMatchers
				.argThat(req -> req.getKind() == RequestKind.CORRECTION && req.getStatus() == RequestStatus.PENDING
						&& req.getUserEntity().equals(user) && req.getTargetDate().equals(form.getTargetDate())
						&& req.getComment().equals(form.getComment())));
	}

	@Test
	@DisplayName("勤務修正申請一覧取得：kind=CORRECTION の申請が返されること")
	void testGetUserCorrectionRequests() {
		when(requestRepository.findByUserEntityAndKind(user, RequestKind.CORRECTION)).thenReturn(dummyList);
		List<RequestEntity> result = requestServiceImpl.getUserCorrectionRequests(user);
		assertEquals(dummyList, result);
		verify(requestRepository).findByUserEntityAndKind(user, RequestKind.CORRECTION);
	}

	@Test
	@DisplayName("全ユーザー申請の取得（提出日降順）")
	void testFindAllWithUserOrderBySubmittedAtDesc() {
		when(requestRepository.findAllWithUserOrderBySubmittedAtDesc()).thenReturn(dummyList);
		List<RequestEntity> result = requestServiceImpl.findAllWithUserOrderBySubmittedAtDesc();
		assertEquals(dummyList, result);
		verify(requestRepository).findAllWithUserOrderBySubmittedAtDesc();
	}

	@Test
	@DisplayName("承認済み有給申請のカレンダー取得：期間・承認済み・有給条件で絞られること")
	void testGetApprovedList() {
		LocalDate start = today.withDayOfMonth(1);
		LocalDate end = today.withDayOfMonth(today.lengthOfMonth());

		when(requestRepository.findByUserEntityAndStatusAndKindAndTargetDateBetween(user, RequestStatus.APPROVED,
				RequestKind.PAID_LEAVE, start, end)).thenReturn(dummyList);

		List<RequestEntity> result = requestServiceImpl.getApprovedList(user, start, end);
		assertEquals(dummyList, result);
		verify(requestRepository).findByUserEntityAndStatusAndKindAndTargetDateBetween(user, RequestStatus.APPROVED,
				RequestKind.PAID_LEAVE, start, end);
	}
}