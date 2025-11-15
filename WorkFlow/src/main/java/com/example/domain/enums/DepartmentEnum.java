package com.example.domain.enums;

public enum DepartmentEnum {
	HR(1, "人事部"), IT(2, "IT部"), SALES(3, "営業部");

	private final int id;
	private final String label;

	DepartmentEnum(int id, String label) {
		this.id = id;
		this.label = label;
	}

	public int getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public static DepartmentEnum fromId(int id) {
		for (DepartmentEnum d : values()) {
			if (d.id == id)
				return d;
		}
		return null;
	}
}