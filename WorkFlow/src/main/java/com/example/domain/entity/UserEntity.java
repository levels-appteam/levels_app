package com.example.domain.entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.example.domain.enums.DepartmentEnum;
import com.example.domain.enums.Role;

import lombok.Data;
import lombok.ToString;

/**
 * 社員情報（usersテーブル）を保持するEntity MYSQLのusersテーブルとマッピングされる
 */
@Data
@ToString(exclude = "attendanceList")
@Entity
@Table(name = "users")
public class UserEntity {

	/**
	 * usersテーブルの主キー
	 * 
	 * @GeneratedValueで自動採番
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/**
	 * email（ログインid）のインスタンス
	 */
	private String email;
	/**
	 * passwordのインスタンス
	 */
	private String password;
	/**
	 * ユーザーネームのインスタンス
	 */
	private String name;

	/**
	 * 部署id
	 */
	@Column(name = "department_id")
	private Integer departmentId;

	
	/**
	 * 部署名をもつ
	 * Enum変数で変換
	 * @return
	 */
	@Transient
	public String getDepartmentName() {
		DepartmentEnum dep = DepartmentEnum.fromId(this.departmentId);
		return dep != null ? dep.getLabel() : "";
	}

	/**
	 * 入社日
	 */
	@Column(name = "joining_date")
	private LocalDate joiningDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "role")
	private Role role;

	/**
	 * このユーザーに紐づく打刻一覧（attendanceテーブル） AttendanceEntity 側の `user` フィールドを参照
	 */
	@OneToMany(mappedBy = "userEntity")
	private List<AttendanceEntity> attendanceList;

	/**
	 * ApprovalsEntityとのリレーション 申請者IDの紐付け
	 */
	@OneToMany(mappedBy = "approver")
	private List<ApprovalsEntity> approvalsList;
}