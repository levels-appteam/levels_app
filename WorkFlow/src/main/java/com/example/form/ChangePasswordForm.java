
package com.example.form;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ChangePasswordForm {

	/**
	 * 現在のパスワード
	 */
	@NotBlank(message = "ユーザーIDを入力してください")
	private String userId;

	/**
	 * 新しいパスワード
	 */
	@NotBlank(message = "新しいパスワードを入力してください")
	@Length(min = 4, max = 100, groups = ValidGroup2.class)
	@Pattern(regexp = "^[a-zA-Z0-9]+$", groups = ValidGroup2.class)
	private String newPassword;

	/**
	 * 確認用パスワード
	 */
	@NotBlank(message = "確認用パスワードを入力してください")
	private String confirmPassword;
}