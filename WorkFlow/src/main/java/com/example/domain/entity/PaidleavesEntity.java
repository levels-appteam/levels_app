package com.example.domain.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

/**
 * 有給管理用のエンティティクラス
 */
@Data
@Entity
@Table(name = "paidleaves")
public class PaidleavesEntity {
	
	//主キー
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	//外部キー、ユーザーid
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private UserEntity userEntity;
	
	//有給付与日
	@Column(name = "grant_date")
	private LocalDate grantDate;
	
	//有給失効日
	@Column(name = "revocation_date")
	private LocalDate revocationDate;
	
	//有給付与日数
	private Float days;
	
	//有給使用日数
	@Column(name = "used_days")
	private Float usedDays;
}