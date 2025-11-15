package com.example.controller;

import com.example.form.ChangePasswordForm;
import com.example.domain.entity.UserEntity;
import com.example.service.UserService;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

/**
 * パスワード変更画面用コントローラー
 */
@Controller
@RequestMapping("/password")
public class PasswordController {

	@Autowired
	private UserService userService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * 画面表示
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping("/change")
	public String showChangeForm(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		if (Objects.nonNull(userDetails) && !userDetails.getUsername().isEmpty()) {

			UserEntity loginUser = userService.getLoginUser(userDetails.getUsername());
			model.addAttribute("userId", loginUser.getEmail());
		}
		if (!model.containsAttribute("changePasswordForm")) {
			model.addAttribute("changePasswordForm", new ChangePasswordForm());
		}
		return "user/change_password";
	}

	/**
	 * パスワード変更処理
	 * 
	 * @param form
	 * @param bindingResult
	 * @param userDetails
	 * @param ra
	 * @return
	 */
	@PostMapping("/change")
	public String changePassword(@Valid @ModelAttribute("changePasswordForm") ChangePasswordForm form,
			BindingResult bindingResult, RedirectAttributes ra) {

		if (bindingResult.hasErrors()) {
			ra.addFlashAttribute("org.springframework.validation.BindingResult.changePasswordForm", bindingResult);
			ra.addFlashAttribute("changePasswordForm", form);
			return "redirect:/password/change";
		}

		if (!form.getNewPassword().equals(form.getConfirmPassword())) {
			bindingResult.rejectValue("confirmPassword", "Mismatch", "確認用パスワードが一致しません");
		}

		// TODO：現在のパスワード削除、パラメーターにuserIdを追加


		// 更新
		String encoded = passwordEncoder.encode(form.getNewPassword());
		userService.updatePassword(form.getUserId(), encoded);

		ra.addFlashAttribute("message", "パスワードを変更しました。次回ログインから新しいパスワードをご利用ください。");
		return "redirect:/dashboard";
	}
}