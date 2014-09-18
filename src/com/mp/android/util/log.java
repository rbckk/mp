package com.mp.android.util;

import android.util.Log;

/**
 * 自定义日志输�?
 */
public final class log {

	private static StackTraceElement e;

	// 当前文件�?行号 函数�?
	public static String getFileLineMethod(StackTraceElement e) {

		StringBuffer sb = new StringBuffer("[").append(e.getFileName()).append(" | ").append(e.getLineNumber()).append(" | ").append(e.getMethodName()).append("()").append("]");

		return sb.toString();
	}

	public static void v(String msg) {

		e = new Exception().getStackTrace()[1];

		Log.v(getFileLineMethod(e), msg);
	}

	public static void d(String msg) {

		e = new Exception().getStackTrace()[1];

		Log.d(getFileLineMethod(e), msg);
	}

	public static void i(String msg) {

		e = new Exception().getStackTrace()[1];

		Log.i(getFileLineMethod(e), msg);
	}

	public static void w(String msg) {

		e = new Exception().getStackTrace()[1];

		Log.w(getFileLineMethod(e), msg);
	}

	public static void e(String msg) {

		e = new Exception().getStackTrace()[1];

		Log.e(getFileLineMethod(e), msg);
	}
}
