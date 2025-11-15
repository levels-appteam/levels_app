package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.domain.entity.PaidleavesEntity;
import com.example.service.PaidleavesService;

/**
 * 有給管理用コントローラー
 */
@Controller
public class PaidleavesController {

	@Autowired
	private PaidleavesService paidleavesService;

	/**
	 * 社員一覧（ユーザーごとのリンク付き）
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping("/paidleaves/list")
	public String showUserList(Model model) {
		// 全ユーザーの明細をそのまま出す
		List<PaidleavesEntity> all = paidleavesService.getAllPaidleaves();
		model.addAttribute("paidleaves", all);
		return "paidleaves/list";
	}

}