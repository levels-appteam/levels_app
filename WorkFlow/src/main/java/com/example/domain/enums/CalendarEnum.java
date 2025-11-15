package com.example.domain.enums;

/**
 * カレンダーのステータス情報をもつクラス
 */
public enum CalendarEnum {
	HOLIDAY("休日"),
	PUBLIC_HOLIDAY("祝日"),
	WORKDAY("出勤日");
	
	private final String calendarLabel;
	
	/**
	 * ステータス情報を日本語に変換
	 * @param label
	 */
	private CalendarEnum(String label) {
		this.calendarLabel = label;
	}
	
	public String getLabel() {
		return calendarLabel;
	}
}