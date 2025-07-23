package com.example.controller;

import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.Email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.domain.dto.AttendanceSummaryDto;
import com.example.domain.entity.AttendanceSummaryEntity;
import com.example.domain.entity.UserEntity;
import com.example.mapper.AttendanceSummaryMapper;
import com.example.service.AttendanceManagementService;
import com.example.service.AttendanceSummaryService;
import com.example.service.UserService;

@Controller
@RequestMapping("/attendancemanagement")
public class AttendanceManagementController {

	@Autowired
	private AttendanceManagementService attendanceManagementService;
	
	@Autowired
	private AttendanceSummaryService summaryService;

	@GetMapping("/history/{email:.+}")
	public String getAttendanceList(@AuthenticationPrincipal UserDetails userDetails,
			@PathVariable String email,
			@RequestParam(required = false) Integer year, @RequestParam(required = false) Integer month, Model model) {

		UserEntity selectUser = attendanceManagementService.getUserByEmail(email);

		// 年月の取得
		LocalDate nowDate = LocalDate.now();
		int targetYear = (year != null) ? year : nowDate.getYear();
		int targetMonth = (month != null) ? month : nowDate.getMonthValue();

		LocalDate startMonth = LocalDate.of(targetYear, targetMonth, 1);
		LocalDate endMonth = startMonth.withDayOfMonth(startMonth.lengthOfMonth());

		// 勤怠サマリーの取得
		List<AttendanceSummaryEntity> summaryList = summaryService.getSummaryByUser(selectUser, startMonth, endMonth);

		// DTOに変換（リスト形式）
		List<AttendanceSummaryDto> dtoList = summaryList.stream().map(AttendanceSummaryMapper::toSummaryDto).toList();

		dtoList.forEach(dto -> System.out.println("DTO: " + dto));

		// モデルに渡す
		model.addAttribute("history", dtoList);
		model.addAttribute("targetYear", targetYear);
		model.addAttribute("targetMonth", targetMonth);

		return "attendance/history";
	}
}