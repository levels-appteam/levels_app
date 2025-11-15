package com.example.aspect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.domain.entity.UserEntity;
import com.example.service.UserService;

/**
 * コントロールアドバイスクラス コントローラーの共通化をする
 */
@ControllerAdvice
public class ControllAdvice {
	@Autowired
	private UserService userService;

	@ModelAttribute("user")
	public UserEntity currentUser(@AuthenticationPrincipal UserDetails details) {
		if (details != null) {
			return userService.getUserOne(details.getUsername());
		}
		return null;
	}

	/**
	 * エラーメッセージに応じたエラー画面表示
	 * 
	 * @param ex
	 * @param model
	 * @return
	 */
	@ExceptionHandler(DataAccessException.class)
	public String handleDataAccessException(DataAccessException ex, Model model) {
		model.addAttribute("errorMessage", ex.getMessage());
		return "error/common_error";
	}

	/**
	 * 共通エラー表示
	 * @param ex
	 * @param model
	 * @return
	 */
	@ExceptionHandler(Exception.class)
	public String handleException(Exception ex, Model model) {
		model.addAttribute("errorMessage", "予期せぬエラーが発生しました。");
		return "error/common_error";
	}

}