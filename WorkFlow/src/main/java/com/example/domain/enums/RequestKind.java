package com.example.domain.enums;

/**
 * RequeatKindクラス 申請の種類のエンティティ
 */
public enum RequestKind {
	PAID_LEAVE("有給申請"), 
	CORRECTION("勤務修正"),
	HALF_DAYOFF("半休"), 
	SPECIAL_LEAVE("特別休暇");

	private final String label;

	/**
	 * 日本語に変換
	 * 表示用
	 * @param label
	 */
	RequestKind(String label) {
		this.label = label;
	}

	/**
	 * 日本語の申請種類を取得
	 * @return
	 */
	public String getLabel() {
		return label;
	}
}