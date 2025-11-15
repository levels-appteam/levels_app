package com.example.domain.enums;

/**
 * 管理者用承認権限
 */
public enum ApprovalsDecision {
	PENDING("未承認"), 
	APPROVED("承認済み"), 
	REJECTED("却下"), 
	REMAND("差し戻し");
	
	private final String decisionLabel;
	
	private ApprovalsDecision (String label) {
		this.decisionLabel = label;
	}
	
	public String getLabel() {
		return decisionLabel;
	}
}