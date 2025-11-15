package com.example.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.domain.entity.CalendarEntity;
import com.example.domain.entity.RequestEntity;
import com.example.domain.entity.UserEntity;
import com.example.service.CalendarService;
import com.example.service.RequestService;
import com.example.service.UserService;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

	@Autowired
	private CalendarService calendarService;

	@Autowired
	private UserService userService;

	@Autowired
	private RequestService requestService;

	/**
	 * 表示用イベントを取得 ダッシュボードカレンダーに表示
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	@GetMapping("/events")
	public List<Map<String, Object>> getEvents(@RequestParam("start") String start, @RequestParam("end") String end,
			@AuthenticationPrincipal UserDetails userDetails) {

		LocalDate startDate = LocalDate.parse(start, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		LocalDate endDate = LocalDate.parse(end, DateTimeFormatter.ISO_OFFSET_DATE_TIME);

		List<CalendarEntity> events = calendarService.getCalendar(startDate, endDate);

		// 表示用イベントのリスト(JSON変換)
		List<Map<String, Object>> eventList = new ArrayList<>();

		// calendar.js用のリストに変換
		for (CalendarEntity clList : events) {
			Map<String, Object> eventMap = new HashMap<>();
			eventMap.put("title", clList.getEventTitle());
			eventMap.put("start", clList.getStartDate().toString());
			// FullCalendarはendが「翌日」を期待（排他的）なので +1 日
			eventMap.put("end", clList.getEndDate().plusDays(1).toString());
			eventMap.put("allDay", true);
			eventList.add(eventMap);
		}

		// ログインユーザー取得
		String email = userDetails.getUsername();
		UserEntity loginUser = userService.getLoginUser(email);

		// calendar.js用のリストに変換（有給）
		List<RequestEntity> approvedList = requestService.getApprovedList(loginUser, startDate, endDate);

		for (RequestEntity reqListEntity : approvedList) {
			Map<String, Object> reqEventMap = new HashMap<>();
			reqEventMap.put("title", "有給休暇");
			reqEventMap.put("start", reqListEntity.getTargetDate().toString());
			eventList.add(reqEventMap);
		}

		return eventList;
	}
}