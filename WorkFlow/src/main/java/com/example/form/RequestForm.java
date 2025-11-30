package com.example.form;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.example.domain.enums.RequestKind;

import lombok.Data;

/**
 * リクエストフォームクラス
 * 申請フォーム画面のDTO
 */
@Data
public class RequestForm {
	
	/**
	 * 申請対象日
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate targetDate;
	
	/**
	 * 休暇種別
	 */
	private RequestKind requestKind;
	
	/**
	 * 申請理由
	 */
	private String comment;
}