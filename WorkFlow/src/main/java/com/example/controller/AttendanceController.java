package com.example.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.domain.dto.AttendanceSummaryDto;
import com.example.domain.entity.AttendanceSummaryEntity;
import com.example.domain.entity.UserEntity;
import com.example.domain.enums.AttendanceType;
import com.example.mapper.AttendanceSummaryMapper;
import com.example.service.AttendanceService;
import com.example.service.AttendanceSummaryService;
import com.example.service.UserService;

/**
 * アテンダンスコントローラー
 * 勤怠打刻を管理する
 */
@Controller
@RequestMapping("/attendance")
public class AttendanceController {
	@Autowired
	private AttendanceService attendanceService;
	
	@Autowired
	private AttendanceSummaryService summaryService;
	
	@Autowired
	private UserService userService;
	
	/**
	 * 打刻情報を扱う
	 * 打刻したログインユーザーの取得
	 * @param type
	 * @param userDetails
	 * @return
	 */
	@PostMapping("/punch")
	public String punch(
			@RequestParam("type") String type, 
			@AuthenticationPrincipal UserDetails userDetails
			) {
		String email = userDetails.getUsername();
		UserEntity loginUser = userService.getLoginUser(email);
		
		//String型からENUM型に変換して渡す
		AttendanceType attendanceType = AttendanceType.valueOf(type);
		attendanceService.recordPunch(loginUser.getId(), attendanceType);
		
		return "redirect:/dashboard";
	}
	
	/**
	 * 勤怠情報の取得
	 * @param userDetails
	 * @param year
	 * @param month
	 * @param model
	 * @return
	 */
	@GetMapping("/history/{email:.+}")
	public String getHistory(
			@AuthenticationPrincipal UserDetails userDetails, 
			@RequestParam(required = false) Integer year,
			@RequestParam(required = false) Integer month, 
			Model model) {
		
		//EmailからUserEntityを取得
		String email = userDetails.getUsername();
		UserEntity loginUser = userService.getLoginUser(email);
		
		//年月の取得
		LocalDate nowDate = LocalDate.now();
		int targetYear = (year != null) ? year : nowDate.getYear();
		int targetMonth = (month != null) ? month : nowDate.getMonthValue();
		
		LocalDate startMonth = LocalDate.of(targetYear, targetMonth, 1);
		LocalDate endMonth = startMonth.withDayOfMonth(startMonth.lengthOfMonth());
		
		//勤怠サマリーの取得
		List<AttendanceSummaryEntity> summaryList = summaryService.getSummaryByUser(loginUser, startMonth, endMonth);
		
		//DTOに変換（リスト形式）
		List<AttendanceSummaryDto> dtoList = summaryList.stream()
				.map(AttendanceSummaryMapper::toSummaryDto)
				.toList();
		
		dtoList.forEach(dto -> System.out.println("DTO: " + dto));
		
		//モデルに渡す
		model.addAttribute("history", dtoList);
		model.addAttribute("targetYear", targetYear);
		model.addAttribute("targetMonth", targetMonth);
		
		return "attendance/history";
	}
	
	@GetMapping("/history/csv")
	public void exportCsv(
			@AuthenticationPrincipal UserDetails userDetails, 
			@RequestParam(required = false) Integer year,
			@RequestParam(required = false) Integer month, 
			HttpServletResponse response
	) throws IOException {
		
		//ログインユーザーの取得
		String email = userDetails.getUsername();
		UserEntity user = userService.getLoginUser(email);
		
		//年月の取得
		LocalDate nowDate = LocalDate.now();
		int targetYear = (year != null) ? year : nowDate.getYear();
		int targetMonth = (month != null) ? month : nowDate.getMonthValue();
		
		LocalDate startMonth = LocalDate.of(targetYear, targetMonth, 1);
		LocalDate endMonth = startMonth.withDayOfMonth(startMonth.lengthOfMonth());
		
		//勤怠履歴の取得
		List<AttendanceSummaryDto> summaries = summaryService.getSummaryDtoByUser(user, startMonth, endMonth);
		
		//文字コードの指定
		response.setContentType("text/csv; charset=UTF-8");
		//ファイルの指定
		response.setHeader("Content-Disposition", "attachment; filename=\"attendance_history.csv\"");
		
		PrintWriter writer = response.getWriter();
		writer.println("日付,出勤時間,退勤時間,実働時間,休憩時間");
		
		for (AttendanceSummaryDto dto : summaries) {
			writer.printf("%s,%s,%s,%s,%s%n",
					dto.getWorkDate(),
					dto.getWorkStart(),
					dto.getWorkEnd(),
					dto.getWorkingMinutes(),
					dto.getBreakMinutes()
			);
		}
		writer.flush();
	}
}