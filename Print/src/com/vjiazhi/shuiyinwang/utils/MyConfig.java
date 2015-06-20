package com.vjiazhi.shuiyinwang.utils;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

public class MyConfig {
	private static int m_nWaterMarkColor = Color.YELLOW;
	private static int m_nPostionIndex = 4; //0-8，分别代表左上角到右下角（默认9）
	private static String m_strWaterMark = "";
	private static int m_fSizePerScale = 100;//尺寸缩放的百分比
			
	public static void setNewColor(int nColor) {
		m_nWaterMarkColor = nColor;
	}

	public static void setNewPosition(int nIndex) {
		m_nPostionIndex = nIndex;
	}
	
	public static void setNewText(String  strText) {
		m_strWaterMark = strText;
	}
	
	public static int getNewColor() {
		return m_nWaterMarkColor;
	}

	public static int getNewPosition() {
		return m_nPostionIndex;
	}
	
	public static String getNewText() {
		return m_strWaterMark;
	}
	//尺寸，在默认基础上乘以一个缩放系数（0-200）
	public static void setNewSize(int nPercentScale) {
		if(nPercentScale<=0 || nPercentScale >200)
		{
			return;
		}
		m_fSizePerScale = nPercentScale;	
	}
	
	public static int getNewSize() {
		return m_fSizePerScale;
	}
}