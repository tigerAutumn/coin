package com.qkwl.common.dto.Enum;

import com.qkwl.common.util.I18NUtils;

public class VirtualCapitalOperationInStatusEnum {
	public static final int WAIT_0 = 0;
	public static final int WAIT_1 = 1;
	public static final int WAIT_2 = 2;
	public static final int SUCCESS = 3;

	public static String getEnumString(int value) {
		String name = ""; //$NON-NLS-1$
		if (value == WAIT_0) {
			name = I18NUtils.getString("VirtualCapitalOperationInStatusEnum.1"); 
		} else if (value == WAIT_1) {
			name = I18NUtils.getString("VirtualCapitalOperationInStatusEnum.2"); 
		} else if (value == WAIT_2) {
			name = I18NUtils.getString("VirtualCapitalOperationInStatusEnum.3");
		} else if (value == SUCCESS) {
			name = I18NUtils.getString("VirtualCapitalOperationInStatusEnum.4"); 
		}
		return name;
	}
}
