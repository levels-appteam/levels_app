package com.example.controller;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.domain.entity.UserEntity;
import com.example.form.GroupOrder;
import com.example.form.SignupForm;
import com.example.service.UserManagementService;

import lombok.extern.slf4j.Slf4j;

/**
 * サインアップコントローラークラス ユーザー初期登録のクラス
 */
@Controller
@RequestMapping("/usermanagement")
@Slf4j
public class UserManagementController {

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private UserManagementService userManagementService;

	/**
	 * ユーザー一覧画面表示
	 * 
	 * @param model
	 * @param userEntity
	 * @return
	 */
	@GetMapping("/userlist")
	public String getUserList(@RequestParam(required = false) String email,
			@RequestParam(required = false) Integer departmentId, Model model) {

		List<UserEntity> getList;

		if ((email != null && !email.isEmpty()) && departmentId != null) {
			//部署id条件と検索
			getList = userManagementService.findByEmailAndDepartment(email, departmentId);
		} else if (email != null && !email.isEmpty()) {
			// 部分一致でメール検索（検索優先）
			getList = userManagementService.findUsersByPartialName(email);
		} else if (departmentId != null) {
			// 部署IDで絞り込み
			getList = userManagementService.getUsersByDepartment(departmentId);
		} else {
			// 全件
			getList = userManagementService.getUsers();
		}

		model.addAttribute("getList", getList);
		model.addAttribute("departmentId", departmentId);
		model.addAttribute("email", email);

		return "user/managementlist";
	}

	/**
	 * ユーザー登録画面表示
	 * 
	 * @param model
	 * @return user/signup
	 */
	@GetMapping("/signup")
	public String getSignup(Model model, @Validated @ModelAttribute SignupForm form) {
		// ユーザー登録画面に遷移
		return "usermanagement/signup";
	}

	/**
	 * ユーザー登録処理
	 * 
	 * @param form
	 * @param bindingResult
	 * @return
	 */
	@PostMapping("/signup")
	public String postSignup(Model model, @ModelAttribute @Validated(GroupOrder.class) SignupForm form,
			BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			return getSignup(model, form);
		}

		log.info(form.toString());

		UserEntity userEntity = modelMapper.map(form, UserEntity.class);

		userManagementService.signup(userEntity);

		// ログイン画面にリダイレクト
		return "redirect:/usermanagement/userlist";
	}
}