package com.example.logic;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.domain.entity.RequestEntity;
import com.example.domain.entity.UserEntity;
import com.example.domain.enums.RequestKind;
import com.example.domain.enums.RequestStatus;
import com.example.repository.RequestRepository;
import com.example.service.PaidleavesService;

import jakarta.transaction.Transactional;

@Component
public class PaidleavesScheduler {

	@Autowired
	private PaidleavesService paidleavesService;

	@Autowired
	private RequestRepository requestRepository;

	/**
	 * 毎日 深夜0時に実行（cron: 秒 分 時 日 月 曜日）
	 */
	@Scheduled(cron = "0 0 0 1 * *")
	public void grantPaidLeaveDaily() {
		System.out.println("★ 有給付与スケジューラー実行開始");

		List<UserEntity> users = paidleavesService.getUsersList();
		for (UserEntity user : users) {
			paidleavesService.grantPaidLeaveIfEligible(user);
		}

		System.out.println("★ 有給付与スケジューラー実行終了");
	}

	@Scheduled(cron = "0 0 1 * * ?") // 毎日1:00実行
	@Transactional
	public void processPaidLeaveConsumption() {
		LocalDate today = LocalDate.now();
		List<RequestEntity> requests = requestRepository.findByKindInAndStatusAndTargetDate(List.of(RequestKind.PAID_LEAVE,
				RequestKind.HALF_DAYOFF), RequestStatus.APPROVED, today);

		for (RequestEntity request : requests) {
			if (!Boolean.TRUE.equals(request.getUsedFlag())) {
				float daysToDeduct = switch (request.getKind()) {
				case PAID_LEAVE -> 1.0f;
				case HALF_DAYOFF -> 0.5f;
				default -> 0.0f;
				};

				if (daysToDeduct > 0.0f) {
					paidleavesService.deductPaidLeaveDays(request.getUserEntity(), daysToDeduct,
							request.getTargetDate());
					request.setUsedFlag(true);
				}
			}
		}
		requestRepository.saveAll(requests);
	}
}