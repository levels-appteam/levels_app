package com.example.domain.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.domain.entity.RequestEntity;
import com.example.domain.enums.RequestKind;
import com.example.domain.enums.RequestStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 申請情報のDTO 承認画面用
 */
@Data
@AllArgsConstructor
public class RequestDto {

	/**
	 * ユーザーId
	 */
	private Integer id;

	private String userName;

	/**
	 * 申請種類
	 */
	private RequestKind kind;

	/**
	 * 申請状況
	 */
	private RequestStatus status;

	/**
	 * 申請対象日
	 */
	@JsonProperty("targetDate")
	@JsonFormat(pattern = "yyyy年MM月dd日")
	private LocalDate targetDate;

	/**
	 * 申請した日
	 */
	@JsonProperty("submittedAt")
	@JsonFormat(pattern = "yyyy年MM月dd日")
	private LocalDateTime submittedAt;

	/**
	 * 申請理由
	 */
	private String comment;

	/**
	 * 申請種類を日本語表示 list.jsに送れるようにする
	 * 
	 * @return
	 */
	@JsonProperty("kindLabel")
	public String getKindLabel() {
		return kind.getLabel();
	}

	/**
	 * 承認ステータス表示
	 * 
	 * @return
	 */
	@JsonProperty("statusLabel")
	public String getStatusLabel() {
		return status.getLabel();
	}

	/**
	 * エンティティからDTOに変換するメソッド
	 * 
	 * @param entity
	 * @return
	 */
	public static RequestDto fromEntity(RequestEntity entity) {
		String name = entity.getUserEntity() != null ? entity.getUserEntity().getName() : "";
		return new RequestDto(entity.getId(), name, entity.getKind(), entity.getStatus(), entity.getTargetDate(),
				entity.getSubmittedAt(), entity.getComment());
	}
}