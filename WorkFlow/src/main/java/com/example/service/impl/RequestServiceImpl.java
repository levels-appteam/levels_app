package com.example.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.domain.entity.RequestEntity;
import com.example.domain.entity.UserEntity;
import com.example.domain.enums.RequestKind;
import com.example.domain.enums.RequestStatus;
import com.example.form.RequestForm;
import com.example.repository.RequestRepository;
import com.example.service.RequestService;

/**
 * Requestサービスの実装クラス
 */
@Service
public class RequestServiceImpl implements RequestService {

	@Autowired
	private RequestRepository requestRepository;

	/**
	 * ログインユーザーの申請取得
	 */
	@Override
	public List<RequestEntity> getUserRequests(UserEntity userEntity) {
		return requestRepository.findByUserEntity(userEntity);
	}

	/**
	 * 全ユーザーの申請取得
	 */
	@Override
	public List<RequestEntity> getRequestsByStatus(RequestStatus status) {
		return requestRepository.findByStatus(status);
	}

	/**
	 * 有給申請リスト取得
	 */
	@Override
	public void savePaidLeaveRequest(UserEntity userEntity, RequestForm requestForm) {
		// 有給申請新規作成
		RequestEntity requestEntity = new RequestEntity();

		requestEntity.setUserEntity(userEntity);
		requestEntity.setKind(RequestKind.PAID_LEAVE);
		requestEntity.setStatus(RequestStatus.PENDING);
		requestEntity.setTargetDate(requestForm.getTargetDate());
		requestEntity.setSubmittedAt(LocalDateTime.now());
		requestEntity.setComment(requestForm.getComment());

		requestRepository.save(requestEntity);
	}

	@Override
	public List<RequestEntity> getUserPaidLeaveRequests(UserEntity userEntity) {
		return requestRepository.findByUserEntityAndKind(userEntity, RequestKind.PAID_LEAVE);
	}

	/**
	 * 勤怠修正リスト取得
	 */
	@Override
	public void saveCorrectionRequest(UserEntity userEntity, RequestForm requestForm) {
		RequestEntity requestEntity = new RequestEntity();

		requestEntity.setUserEntity(userEntity);
		requestEntity.setKind(RequestKind.CORRECTION);
		requestEntity.setStatus(RequestStatus.PENDING);
		requestEntity.setTargetDate(requestForm.getTargetDate());
		requestEntity.setSubmittedAt(LocalDateTime.now());
		requestEntity.setComment(requestForm.getComment());

		requestRepository.save(requestEntity);
	}

	@Override
	public List<RequestEntity> getUserCorrectionRequests(UserEntity userEntity) {
		return requestRepository.findByUserEntityAndKind(userEntity, RequestKind.CORRECTION);
	}
	
	/**
	 *承認用のユーザー申請一覧を取得する
	 */
	@Override
	public List<RequestEntity> findAllWithUserOrderBySubmittedAtDesc() {
		return requestRepository.findAllWithUserOrderBySubmittedAtDesc();
	}

	/**
	 * カレンダー画面用 有給取得日情報取得
	 */
	@Override
	public List<RequestEntity> getApprovedList(UserEntity userEntity, LocalDate startDate, LocalDate endDate) {
		return requestRepository.findByUserEntityAndStatusAndKindAndTargetDateBetween(userEntity,
				RequestStatus.APPROVED, RequestKind.PAID_LEAVE, startDate, endDate);
	}
}