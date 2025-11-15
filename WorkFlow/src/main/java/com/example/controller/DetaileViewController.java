package com.example.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.domain.entity.UserEntity;
import com.example.form.UserDetailForm;
import com.example.service.PaidleavesService;
import com.example.service.UserService;

/**
 * ディテールビューコントローラークラス ユーザー詳細画面（表示のみ）を表示する
 */
@Controller
@RequestMapping("/user")
public class DetaileViewController {

	@Autowired
	private UserService userService;

	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private PaidleavesService paidleavesService;

	/**
	 * ユーザー詳細画面（表示用）のユーザー情報取得
	 * 
	 * @param form
	 * @param model
	 * @param email
	 * @return user/detailview
	 */
	@GetMapping("/detailview/{email:.+}")
	public String viewUser(UserDetailForm form, Model model, @PathVariable String email) {
		UserEntity userEntity = userService.getUserOne(email);

		// formに変換
		form = modelMapper.map(userEntity, UserDetailForm.class);
		// 合計残日数を計算して載せる
		float remainingDays = paidleavesService.getRemainingDays(userEntity);

		// Modelに登録
		model.addAttribute("userDetailForm", form);
		 model.addAttribute("remainingDays", remainingDays);

		return "user/detailview";
	}
}