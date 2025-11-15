package com.example.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.domain.entity.UserEntity;
import com.example.domain.enums.DepartmentEnum;
import com.example.form.UserDetailForm;
import com.example.service.UserService;

/**
 * ユーザーディテールコントローラークラス ユーザー詳細編集画面を表示する
 */
@Controller
@RequestMapping("/user")
public class UserDetailController {
	@Autowired
	private UserService userService;

	@Autowired
	private ModelMapper modelMapper;

	/**
	 * ユーザー詳細画面表示
	 * 
	 * @param form
	 * @param model
	 * @param email
	 * @return
	 */
	@GetMapping("/detail/{email:.+}")
	public String getUser(UserDetailForm form, Model model, @PathVariable String email) {

		// ユーザー一見取得
		UserEntity userEntity = userService.getUserOne(email);
		userEntity.setPassword(null);

		// formに変換
		form = modelMapper.map(userEntity, UserDetailForm.class);

		// Modelに登録
		model.addAttribute("userDetailForm", form);
		model.addAttribute("departments", DepartmentEnum.values()); 

		return "user/detail";
	}

	/**
	 * ユーザー情報更新
	 * 
	 * @param form
	 * @param model
	 * @return
	 */
	@PostMapping(value = "/detail", params = "update")
	public String updateUser(UserDetailForm form, Model model) {
		userService.updateUserOne(form.getEmail(), form.getPassword(), form.getName(), form.getDepartmentId(),
				form.getJoiningDate());

		// Enum一覧をModelに渡す
		model.addAttribute("departments", DepartmentEnum.values());

		return "redirect:/dashboard";
	}

	@PostMapping(value = "/detail", params = "delete")
	public String deleteUser(UserDetailForm form, Model model) {
		userService.deleteByEmail(form.getEmail());

		return "redirect:/dashboard";
	}
}