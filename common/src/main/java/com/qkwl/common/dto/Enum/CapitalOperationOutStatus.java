package com.qkwl.common.dto.Enum;

import com.qkwl.common.util.I18NUtils;

public class CapitalOperationOutStatus {
	public static final int WaitForOperation = 1;// 等待提现
	public static final int OperationLock = 2;// 锁定，等待处理
	public static final int OperationSuccess = 3;// 提现成功
	public static final int Cancel = 4;// 用户取消
	public static final int OnLineLock = 5;// 等待银行到账

	public static String getEnumString(int value) {
		String name = ""; //$NON-NLS-1$
		if (value == WaitForOperation) {
			name = I18NUtils.getString("CapitalOperationOutStatus.1"); //$NON-NLS-1$
		} else if (value == OperationLock) {
			name = I18NUtils.getString("CapitalOperationOutStatus.2"); //$NON-NLS-1$
		} else if (value == OperationSuccess) {
			name = I18NUtils.getString("CapitalOperationOutStatus.3"); //$NON-NLS-1$
		} else if (value == Cancel) {
			name = I18NUtils.getString("CapitalOperationOutStatus.4"); //$NON-NLS-1$
		} else if (value == OnLineLock) {
			name = I18NUtils.getString("CapitalOperationOutStatus.5"); //$NON-NLS-1$
		}
		return name;
	}
}
