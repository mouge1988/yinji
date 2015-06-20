package com.vjiazhi.shuiyinwang.ui;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.vjiazhi.shuiyinwang.R;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

public class ColorSetActivity extends Activity implements OnClickListener {
	private ToggleButton myButton_01 = null;
	private ToggleButton myButton_02 = null;
	private ToggleButton myButton_03 = null;
	private ToggleButton myButton_04 = null;
	private ToggleButton myButton_05 = null;
	private ToggleButton myButton_06 = null;
	private ToggleButton myButton_07 = null;
	private ToggleButton myButton_08 = null;
	private ToggleButton myButton_09 = null;
	private ToggleButton myButton_10 = null;
	private ToggleButton myButton_11 = null;
	private ToggleButton myButton_12 = null;
	private ToggleButton myButton_13 = null;
	private ToggleButton myButton_14 = null;
	private ToggleButton myButton_15 = null;
	private ToggleButton myButton_16 = null;
	private ToggleButton myButton_17 = null;
	private ToggleButton myButton_18 = null;
	private ToggleButton myButton_19 = null;
	private ToggleButton myButton_20 = null;
	private ToggleButton myButton_21 = null;
	private ToggleButton myButton_22 = null;
	private ToggleButton myButton_23 = null;
	private ToggleButton myButton_24 = null;
	private ToggleButton myButton_25 = null;
	private ToggleButton myButton_26 = null;
	private ToggleButton myButton_27 = null;
	private ToggleButton myButton_28 = null;
	private ToggleButton myButton_29 = null;
	private ToggleButton myButton_30 = null;
	private ToggleButton myButton_31 = null;
	private ToggleButton myButton_32 = null;
	private ToggleButton myButton_33 = null;
	private ToggleButton myButton_34 = null;
	private ToggleButton myButton_35 = null;
	private ToggleButton myButton_36 = null;

	private Button myCancel = null;
	private Button myConfirm = null;

	int mColor;
	private HashMap<Integer, Integer> mColorMap = new HashMap<Integer, Integer>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setTitle(R.string.title_color);

		setContentView(R.layout.color_selector);
		myButton_01 = (ToggleButton) findViewById(R.id.button_01);
		// myButton_01.setBackgroundColor(Color.RED);
		mColorMap.put(R.id.button_01, 0xFFFFFFCC);
		myButton_01.setOnClickListener(this);

		myButton_02 = (ToggleButton) findViewById(R.id.button_02);
		mColorMap.put(R.id.button_02, 0xFF66FFCC);
		myButton_02.setOnClickListener(this);

		myButton_03 = (ToggleButton) findViewById(R.id.button_03);
		mColorMap.put(R.id.button_03, 0xFFCCFFFF);
		myButton_03.setOnClickListener(this);

		myButton_04 = (ToggleButton) findViewById(R.id.button_04);
		mColorMap.put(R.id.button_04, 0xFFFFCCFF);
		myButton_04.setOnClickListener(this);

		myButton_05 = (ToggleButton) findViewById(R.id.button_05);
		mColorMap.put(R.id.button_05, 0xFF0099FF);
		myButton_05.setOnClickListener(this);

		myButton_06 = (ToggleButton) findViewById(R.id.button_06);
		mColorMap.put(R.id.button_06, 0xFFFF9966);
		myButton_06.setOnClickListener(this);

		myButton_07 = (ToggleButton) findViewById(R.id.button_07);
		mColorMap.put(R.id.button_07, 0xFFFFFF99);
		myButton_07.setOnClickListener(this);

		myButton_08 = (ToggleButton) findViewById(R.id.button_08);
		mColorMap.put(R.id.button_08, 0xFF33FF99);
		myButton_08.setOnClickListener(this);

		myButton_09 = (ToggleButton) findViewById(R.id.button_09);
		mColorMap.put(R.id.button_09, 0xFF99FFFF);
		myButton_09.setOnClickListener(this);

		myButton_10 = (ToggleButton) findViewById(R.id.button_10);
		mColorMap.put(R.id.button_10, 0xFFFF99FF);
		myButton_10.setOnClickListener(this);

		myButton_11 = (ToggleButton) findViewById(R.id.button_11);
		mColorMap.put(R.id.button_11, 0xFF0066FF);
		myButton_11.setOnClickListener(this);

		myButton_12 = (ToggleButton) findViewById(R.id.button_12);
		mColorMap.put(R.id.button_12, 0xFFFF6633);
		myButton_12.setOnClickListener(this);

		myButton_13 = (ToggleButton) findViewById(R.id.button_13);
		mColorMap.put(R.id.button_13, 0xFFFFFF00);
		myButton_13.setOnClickListener(this);

		myButton_14 = (ToggleButton) findViewById(R.id.button_14);
		mColorMap.put(R.id.button_14, 0xFF00FF00);
		myButton_14.setOnClickListener(this);

		myButton_15 = (ToggleButton) findViewById(R.id.button_15);
		mColorMap.put(R.id.button_15, 0xFF00FFFF);
		myButton_15.setOnClickListener(this);

		myButton_16 = (ToggleButton) findViewById(R.id.button_16);
		mColorMap.put(R.id.button_16, 0xFFFF66FF);
		myButton_16.setOnClickListener(this);

		myButton_17 = (ToggleButton) findViewById(R.id.button_17);
		mColorMap.put(R.id.button_17, 0xFF0000FF);
		myButton_17.setOnClickListener(this);

		myButton_18 = (ToggleButton) findViewById(R.id.button_18);
		mColorMap.put(R.id.button_18, 0xFFFF3300);
		myButton_18.setOnClickListener(this);

		myButton_19 = (ToggleButton) findViewById(R.id.button_19);
		mColorMap.put(R.id.button_19, 0xFFCCCC00);
		myButton_19.setOnClickListener(this);

		myButton_20 = (ToggleButton) findViewById(R.id.button_20);
		mColorMap.put(R.id.button_20, 0xFF009900);
		myButton_20.setOnClickListener(this);

		myButton_21 = (ToggleButton) findViewById(R.id.button_21);
		mColorMap.put(R.id.button_21, 0xFF00CCFF);
		myButton_21.setOnClickListener(this);

		myButton_22 = (ToggleButton) findViewById(R.id.button_22);
		mColorMap.put(R.id.button_22, 0xFFCC33FF);
		myButton_22.setOnClickListener(this);

		myButton_23 = (ToggleButton) findViewById(R.id.button_23);
		mColorMap.put(R.id.button_23, 0xFF0000CC);
		myButton_23.setOnClickListener(this);

		myButton_24 = (ToggleButton) findViewById(R.id.button_24);
		mColorMap.put(R.id.button_24, 0xFFCC3300);
		myButton_24.setOnClickListener(this);

		myButton_25 = (ToggleButton) findViewById(R.id.button_25);
		mColorMap.put(R.id.button_25, 0xFF999900);
		myButton_25.setOnClickListener(this);

		myButton_26 = (ToggleButton) findViewById(R.id.button_26);
		mColorMap.put(R.id.button_26, 0xFF006633);
		myButton_26.setOnClickListener(this);

		myButton_27 = (ToggleButton) findViewById(R.id.button_27);
		mColorMap.put(R.id.button_27, 0xFF0099FF);
		myButton_27.setOnClickListener(this);

		myButton_28 = (ToggleButton) findViewById(R.id.button_28);
		mColorMap.put(R.id.button_28, Color.rgb(166, 0, 221));//原有的颜色不对
		myButton_28.setOnClickListener(this);

		myButton_29 = (ToggleButton) findViewById(R.id.button_29);
		mColorMap.put(R.id.button_29, 0xFF000066);
		myButton_29.setOnClickListener(this);

		myButton_30 = (ToggleButton) findViewById(R.id.button_30);
		mColorMap.put(R.id.button_30, 0xFF990000);
		myButton_30.setOnClickListener(this);

		myButton_31 = (ToggleButton) findViewById(R.id.button_31);
		mColorMap.put(R.id.button_31, 0xFFFFFFFF);
		myButton_31.setOnClickListener(this);

		myButton_32 = (ToggleButton) findViewById(R.id.button_32);
		mColorMap.put(R.id.button_32, 0xFFCCCCCC);
		myButton_32.setOnClickListener(this);

		myButton_33 = (ToggleButton) findViewById(R.id.button_33);
		mColorMap.put(R.id.button_33, 0xFF999999);
		myButton_33.setOnClickListener(this);

		myButton_34 = (ToggleButton) findViewById(R.id.button_34);
		mColorMap.put(R.id.button_34, 0xFF666666);
		myButton_34.setOnClickListener(this);

		myButton_35 = (ToggleButton) findViewById(R.id.button_35);
		mColorMap.put(R.id.button_35, 0xFF333333);
		myButton_35.setOnClickListener(this);

		myButton_36 = (ToggleButton) findViewById(R.id.button_36);
		mColorMap.put(R.id.button_36, 0xFF000000);
		myButton_36.setOnClickListener(this);

		myCancel = (Button) findViewById(R.id.cancel);
		myCancel.setOnClickListener(this);
		myConfirm = (Button) findViewById(R.id.confirm);
		myConfirm.setOnClickListener(this);
/*
		Intent intent = getIntent();
		int color = intent.getIntExtra("selectedColor", Color.BLACK);

		mColorMap.values();
		Set set = mColorMap.entrySet();
		Iterator it = set.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			Integer btncolor=(Integer)entry.getValue();
			if ((0xff000000 | color)==btncolor) {
				changeColor(findViewById((Integer) entry.getKey()));
			}

		}
*/
	}

	public void onConfirm() {

		Bundle bundle = new Bundle();
		bundle.putInt("color", mColor);
		Intent intent = new Intent();
		intent.putExtras(bundle);
		setResult(RESULT_OK, intent);
		finish();
	}

	private void changeColor(View v) {
		myButton_01.setChecked(false);
		myButton_02.setChecked(false);
		myButton_03.setChecked(false);
		myButton_04.setChecked(false);
		myButton_05.setChecked(false);
		myButton_06.setChecked(false);
		myButton_07.setChecked(false);
		myButton_08.setChecked(false);
		myButton_09.setChecked(false);
		myButton_10.setChecked(false);
		myButton_11.setChecked(false);
		myButton_12.setChecked(false);
		myButton_13.setChecked(false);
		myButton_14.setChecked(false);
		myButton_15.setChecked(false);
		myButton_16.setChecked(false);
		myButton_17.setChecked(false);
		myButton_18.setChecked(false);
		myButton_19.setChecked(false);
		myButton_20.setChecked(false);
		myButton_21.setChecked(false);
		myButton_22.setChecked(false);
		myButton_23.setChecked(false);
		myButton_24.setChecked(false);
		myButton_25.setChecked(false);
		myButton_26.setChecked(false);
		myButton_27.setChecked(false);
		myButton_28.setChecked(false);
		myButton_29.setChecked(false);
		myButton_30.setChecked(false);
		myButton_31.setChecked(false);
		myButton_32.setChecked(false);
		myButton_33.setChecked(false);
		myButton_34.setChecked(false);
		myButton_35.setChecked(false);
		myButton_36.setChecked(false);
		((ToggleButton) v).setChecked(true);
		mColor = mColorMap.get(v.getId());
	}

	@Override
	public void onClick(View v) {

		// myCancel.setChecked(false);

		switch (v.getId()) {
		case R.id.confirm:
			onConfirm();
			break;
		case R.id.cancel:
			finish();
			break;
		default:
			changeColor(v);
		}

	}

}
