package com.qkwl.common.dto.Enum;

public enum ExcelExportStatusEnum {
	EXPORTING(1, "导出中"),		
	FINISHED(2, "已完成"),
	FAILED(3,"导出失败")
	;

	private Integer code;
	private String value;
	
	ExcelExportStatusEnum(int code, String value) {
		this.code = code;
		this.value = value;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public static ExcelExportStatusEnum getEnumByCode(Integer code) {
		for (ExcelExportStatusEnum entrustsource : ExcelExportStatusEnum.values()) {
			if (entrustsource.getCode().equals(code)) {
				return entrustsource;
			}
		}
		return null;
	}
}
