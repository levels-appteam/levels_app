package com.example.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.example.domain.enums.RequestKind;
import com.example.domain.enums.RequestStatus;

import lombok.Data;

/**
 * Requestsテーブルのエンティティクラス
 * 有給申請用
 */
@Data
@Entity
@Table(name = "requests")
public class RequestEntity {
	/**
	 * 主キー(id)
	 * user_idとの紐付け
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private UserEntity userEntity;
	
	/**
	 * 有給申請
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "kind")
	private RequestKind kind;
	
	/**
	 * 申請状況
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private RequestStatus status;
	
	/**
	 * 申請対象日
	 */
	@Column(name = "target_date")
	private LocalDate targetDate;
	
	/**
	 * 申請日
	 */
	@Column(name = "submitted_at")
	private LocalDateTime submittedAt = LocalDateTime.now();
	
	/**
	 * 申請理由
	 */
	@Column(name = "comment", columnDefinition = "TEXT")
	private String comment;
	
	/**
	 * approvalsEntityとのリレーション
	 */
	@OneToMany(mappedBy = "request")
	private List<ApprovalsEntity> approvalsList;
	
}