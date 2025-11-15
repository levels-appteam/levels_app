package com.example.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.domain.entity.UserEntity;
import com.example.domain.enums.AttendanceType;
import com.example.service.AttendanceService;
import com.example.service.UserService;

/**
 * ダッシュボードコントローラークラス
 */
@Controller
public class DashboardController {

	@Autowired
	private UserService userService;
	@Autowired
	private AttendanceService attendanceService;

	/** ダッシュボード画面の表示 */
	@GetMapping("/dashboard")
	public String getDashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {

		// ログインユーザー取得
		String email = userDetails.getUsername();
		UserEntity loginUser = userService.getLoginUser(email);
		AttendanceType type = attendanceService.getLoginUserType(loginUser.getId());

		// 出退勤ボタン非活性処理
		boolean in = (type == null || type == AttendanceType.OUT);
		boolean out = (type == AttendanceType.IN || type == AttendanceType.BREAK_OUT);
		boolean breakIn = (type == AttendanceType.IN);
		boolean breakOut = (type == AttendanceType.BREAK_IN);

		// 今日の日付
		LocalDate today = LocalDate.now();

		// モデルに値を渡す
		model.addAttribute("today", today);
		model.addAttribute("loginUser", loginUser);
		model.addAttribute("type", type);
		model.addAttribute("in", in);
		model.addAttribute("out", out);
		model.addAttribute("breakIn", breakIn);
		model.addAttribute("breakOut", breakOut);
		model.addAttribute("alreadyIn", attendanceService.hasPunchedToday(loginUser.getId()));

		// Thymeleafテンプレートに遷移
		return "dashboard";
	}
}