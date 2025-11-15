package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.form.CalendarForm;
import com.example.service.CalendarService;

@Controller
@RequestMapping("/admin/calendar")
public class CalendarManagementController {
	
	@Autowired
	private CalendarService calendarService;

	@GetMapping
	public String getcalendar(Model model) {
		model.addAttribute("calendarForm", new CalendarForm());
		
		return "admin/calendarList";
	}

	/**
	 * イベント送信用フォーム
	 * @param form
	 * @return
	 */
	@PostMapping
	public String submit(@ModelAttribute("calendarForm") CalendarForm form) {
		calendarService.createRange(form);
		return "redirect:/admin/calendar";
	}
}