package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.domain.entity.PaidleavesEntity;
import com.example.domain.entity.UserEntity;
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

		// ユーザー一覧取得
		List<UserEntity> paidLeaveList = paidleavesService.getUsersList();

		// modelに登録
		model.addAttribute("paidLeaves", paidLeaveList);

		return "paidleaves/list";
	}

	/**
	 * 特定ユーザーの有給履歴
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@GetMapping("/paidleaves/user/{id}")
	public String showUserPaidLeaveDetail(@PathVariable Integer id, Model model) {

		// idでユーザーを取得
		UserEntity user = paidleavesService.findUserById(id);
		// 有給を取得
		List<PaidleavesEntity> leaves = paidleavesService.getPaidleavesByUser(user);

		// モデルに登録
		model.addAttribute("user", user);
		model.addAttribute("paidleaves", leaves);

		return "paidleaves/paidleavesdetail";
	}
}