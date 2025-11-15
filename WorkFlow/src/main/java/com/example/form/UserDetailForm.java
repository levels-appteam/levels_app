package com.example.form;

import java.time.LocalDate;

import com.example.domain.enums.DepartmentEnum;

import lombok.Data;

@Data
public class UserDetailForm {

	/**
	 * ユーザーid
	 */
	private String email;

	/**
	 * パスワード
	 */
	private String password;

	/**
	 * ユーザー名
	 */
	private String name;

	/**
	 * 部署名
	 */
	private Integer departmentId;
	
	
	/**
	 * 入社日
	 */
	private LocalDate joiningDate;

	/**
	 * 部署名（変換）
	 * @return
	 */
	public String getDepartmentName() {
		DepartmentEnum d = DepartmentEnum.fromId(this.departmentId);
		return d != null ? d.getLabel() : "";
	}

}