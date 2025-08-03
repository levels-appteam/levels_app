package com.example.form;

import java.time.LocalDate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

/**
 * サインアップフォームクラス ユーザー登録画面の入力内容をマッピングするためのクラス
 */
@Data
public class SignupForm {
	/**
	 * mailアドレス(ユーザーid) 文字列が空文字じゃないかつメールアドレス形式かチェックする
	 */
	@NotBlank(groups = ValidGroup1.class)
	@Email(groups = ValidGroup2.class)
	private String email;

	/**
	 * パスワード 文字列が空文字じゃないかつ４文字以上100文字以下かチェックする
	 */
	@NotBlank(groups = ValidGroup1.class)
	@Length(min = 4, max = 100, groups = ValidGroup2.class)
	@Pattern(regexp = "^[a-zA-Z0-9]+$", groups = ValidGroup2.class)
	private String password;

	/**
	 * ユーザー名 文字列が空文字じゃないかチェックする
	 */
	@NotBlank(groups = ValidGroup1.class)
	private String name;

	/**
	 * 部署名
	 */
	private Integer departmentId;

	// 入社日
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate joiningDate;
}