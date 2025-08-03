package com.example.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.domain.entity.UserEntity;
import com.example.service.PaidleavesService;

@Component
public class PaidleavesSchedulerServiceImpl {

	@Autowired
	private PaidleavesService paidleavesService;

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
}