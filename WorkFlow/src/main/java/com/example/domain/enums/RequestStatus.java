package com.example.domain.enums;

/**
 * RequestStatusクラス
 * 申請状況のエンティティ
 */
public enum RequestStatus {
	PENDING("未承認"), 
	APPROVED("承認"), 
	REJECTED("未承認"),
	REMAND("差し戻し");
	
	private final String statusLabel;
	
	private RequestStatus(String label) {
		this.statusLabel = label;
	}
	
	public String getLabel() {
		return statusLabel;
	}
}