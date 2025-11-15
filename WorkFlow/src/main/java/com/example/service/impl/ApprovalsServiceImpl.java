package com.example.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.domain.entity.ApprovalsEntity;
import com.example.domain.entity.RequestEntity;
import com.example.domain.entity.UserEntity;
import com.example.domain.enums.ApprovalsDecision;
import com.example.domain.enums.RequestKind;
import com.example.domain.enums.RequestStatus;
import com.example.repository.ApprovalsRepository;
import com.example.repository.RequestRepository;
import com.example.service.ApprovalsService;
import com.example.service.PaidleavesService;

import jakarta.transaction.Transactional;

@Service
public class ApprovalsServiceImpl implements ApprovalsService {

	@Autowired
	private ApprovalsRepository approvalsRepository;

	@Autowired
	private RequestRepository requestRepository;
	
	@Autowired
	private PaidleavesService paidleavesService;

	/**
	 * ユーザーの申請状況を取得
	 */
	@Override
	public List<ApprovalsEntity> getApprovalsByRequest(RequestEntity request) {
		return approvalsRepository.findByRequest(request);
	}

	/**
	 * 全ユーザーの申請取得
	 */
	public List<ApprovalsEntity> getAllApprovals() {
		return approvalsRepository.findAll();
	}

	/**
	 * ユーザーに紐づく申請一覧 部署ごと
	 */
	public List<ApprovalsEntity> getApprovalsByApprover(UserEntity userApprover) {
		return approvalsRepository.findByApprover(userApprover);
	}

	@Transactional
	@Override
	public void processApproval(Integer requestId, String decision, UserEntity loginUser) {
		RequestEntity request = requestRepository.findById(requestId)
				.orElseThrow(() -> new IllegalArgumentException("該当申請が見つかりません"));

		// 承認レコード作成
		ApprovalsEntity approval = new ApprovalsEntity();
		approval.setRequest(request);
		approval.setDecision(ApprovalsDecision.valueOf(decision));
		approval.setDecidedAt(LocalDateTime.now());
		approval.setApprover(loginUser);

		approvalsRepository.save(approval);

		// Requestのステータス更新
		switch (decision) {
		case "APPROVED":
			request.setStatus(RequestStatus.APPROVED);
			if (request.getKind() == RequestKind.PAID_LEAVE) {
				paidleavesService.deductPaidLeaveDays(request.getUserEntity(), 1.0f);
			}
			break;
		case "REJECTED":
			request.setStatus(RequestStatus.REJECTED);
			break;
		case "REMAND":
			request.setStatus(RequestStatus.REMAND);
			break;
		default:
			throw new IllegalArgumentException("無効なdecision値: " + decision);
		}
		requestRepository.save(request);

	}
}