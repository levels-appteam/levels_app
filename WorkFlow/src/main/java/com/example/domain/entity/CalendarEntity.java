package com.example.domain.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.example.domain.enums.CalendarEnum;

import lombok.Data;

@Entity
@Table(name = "holidays")
@Data
public class CalendarEntity {

	/**
	 * 主キー
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/**
	 * イベントの開始日
	 */
	@Column(name = "start_date", nullable = false)
	private LocalDate startDate;
	
	/**
	 * イベントの終了日
	 */
	@Column(name = "end_date", nullable = false)
	private LocalDate endDate;
	
	/**
	 * イベントの名称（祝日の名前とか）
	 */
	@Column(name = "event_title", nullable = false)
	private String eventTitle;
	
	/**
	 * 勤務種類ステータス（出勤日、休日、祝日）
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "event_type", nullable = false)
	private CalendarEnum eventType;
	
	
}