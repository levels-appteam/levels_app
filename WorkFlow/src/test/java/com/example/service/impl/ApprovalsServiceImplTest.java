package com.example.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.domain.entity.ApprovalsEntity;
import com.example.domain.entity.RequestEntity;
import com.example.domain.entity.UserEntity;
import com.example.domain.enums.RequestKind;
import com.example.domain.enums.RequestStatus;
import com.example.repository.ApprovalsRepository;
import com.example.repository.RequestRepository;
import com.example.service.PaidleavesService;

@ExtendWith(MockitoExtension.class)
class ApprovalsServiceImplTest {

	@InjectMocks
	private ApprovalsServiceImpl approvalsService;

	@Mock
	private ApprovalsRepository approvalsRepository;

	@Mock
	private RequestRepository requestRepository;

	@Mock
	private PaidleavesService paidleavesService;

	private RequestEntity request;
	private UserEntity loginUser;

	@BeforeEach
	void setup() {
		loginUser = new UserEntity();
		request = new RequestEntity();
		request.setId(1);
		request.setUserEntity(loginUser);
		request.setKind(RequestKind.PAID_LEAVE);
	}

	@Test
	@DisplayName("正常：getApprovalsByRequest → approvalsRepositoryの結果が返る")
	void testGetApprovalsByRequest() {
		List<ApprovalsEntity> dummy = List.of(new ApprovalsEntity());
		when(approvalsRepository.findByRequest(request)).thenReturn(dummy);

		List<ApprovalsEntity> result = approvalsService.getApprovalsByRequest(request);

		assertEquals(dummy, result);
		verify(approvalsRepository).findByRequest(request);
	}

	@Test
	@DisplayName("正常：getApprovalsByApprover → approvalsRepositoryの結果が返る")
	void testGetApprovalsByApprover() {
		when(approvalsRepository.findByApprover(loginUser)).thenReturn(List.of(new ApprovalsEntity()));

		List<ApprovalsEntity> result = approvalsService.getApprovalsByApprover(loginUser);

		assertEquals(1, result.size());
		verify(approvalsRepository).findByApprover(loginUser);
	}

	@Test
	@DisplayName("正常：processApproval → APPROVED（有給申請）")
	void testProcessApproval_Approved_PaidLeave() {
		when(requestRepository.findById(1)).thenReturn(Optional.of(request));

		approvalsService.processApproval(1, "APPROVED", loginUser);

		assertEquals(RequestStatus.APPROVED, request.getStatus());
		verify(approvalsRepository).save(any(ApprovalsEntity.class));
		verify(paidleavesService).deductPaidLeaveDays(loginUser, 1.0f);
		verify(requestRepository).save(request);
	}

	@Test
	@DisplayName("正常：processApproval → APPROVED（修正申請）")
	void testProcessApproval_Approved_Correction() {
		request.setKind(RequestKind.CORRECTION);
		when(requestRepository.findById(1)).thenReturn(Optional.of(request));

		approvalsService.processApproval(1, "APPROVED", loginUser);

		assertEquals(RequestStatus.APPROVED, request.getStatus());
		verify(approvalsRepository).save(any(ApprovalsEntity.class));
		verify(paidleavesService, never()).deductPaidLeaveDays(any(), anyFloat());
		verify(requestRepository).save(request);
	}

	@Test
	@DisplayName("正常：processApproval → REJECTED")
	void testProcessApproval_Rejected() {
		when(requestRepository.findById(1)).thenReturn(Optional.of(request));

		approvalsService.processApproval(1, "REJECTED", loginUser);

		assertEquals(RequestStatus.REJECTED, request.getStatus());
		verify(approvalsRepository).save(any(ApprovalsEntity.class));
		verify(requestRepository).save(request);
	}

	@Test
	@DisplayName("正常：processApproval → REMAND")
	void testProcessApproval_Remand() {
		when(requestRepository.findById(1)).thenReturn(Optional.of(request));

		approvalsService.processApproval(1, "REMAND", loginUser);

		assertEquals(RequestStatus.REMAND, request.getStatus());
		verify(approvalsRepository).save(any(ApprovalsEntity.class));
		verify(requestRepository).save(request);
	}

	@Test
	@DisplayName("異常：requestIdが存在しない → IllegalArgumentException")
	void testProcessApproval_RequestNotFound() {
		when(requestRepository.findById(1)).thenReturn(Optional.empty());

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> approvalsService.processApproval(1, "APPROVED", loginUser));

		assertEquals("該当申請が見つかりません", exception.getMessage());
		verify(approvalsRepository, never()).save(any());
		verify(requestRepository, never()).save(any());
	}
}