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

public class PositionSetActivity extends Activity implements OnClickListener {
	private ToggleButton myButton_01 = null;
	private ToggleButton myButton_02 = null;
	private ToggleButton myButton_03 = null;
	private ToggleButton myButton_04 = null;
	private ToggleButton myButton_05 = null;
	private ToggleButton myButton_06 = null;
	private ToggleButton myButton_07 = null;
	private ToggleButton myButton_08 = null;
	private ToggleButton myButton_09 = null;
	
	private Button myCancel = null;
	private Button myConfirm = null;

	int mPosIndex = 8;//0-8,对应第1到第9个按钮
	private HashMap<Integer, Integer> mColorMap = new HashMap<Integer, Integer>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setTitle(R.string.title_pos);

		setContentView(R.layout.position_selector);
		myButton_01 = (ToggleButton) findViewById(R.id.button_01);
		// myButton_01.setBackgroundColor(Color.RED);
		mColorMap.put(R.id.button_01, 0);
		myButton_01.setOnClickListener(this);

		myButton_02 = (ToggleButton) findViewById(R.id.button_02);
		mColorMap.put(R.id.button_02, 1);
		myButton_02.setOnClickListener(this);

		myButton_03 = (ToggleButton) findViewById(R.id.button_03);
		mColorMap.put(R.id.button_03, 2);
		myButton_03.setOnClickListener(this);

		myButton_04 = (ToggleButton) findViewById(R.id.button_04);
		mColorMap.put(R.id.button_04, 3);
		myButton_04.setOnClickListener(this);

		myButton_05 = (ToggleButton) findViewById(R.id.button_05);
		mColorMap.put(R.id.button_05, 4);
		myButton_05.setOnClickListener(this);

		myButton_06 = (ToggleButton) findViewById(R.id.button_06);
		mColorMap.put(R.id.button_06, 5);
		myButton_06.setOnClickListener(this);

		myButton_07 = (ToggleButton) findViewById(R.id.button_07);
		mColorMap.put(R.id.button_07, 6);
		myButton_07.setOnClickListener(this);

		myButton_08 = (ToggleButton) findViewById(R.id.button_08);
		mColorMap.put(R.id.button_08, 7);
		myButton_08.setOnClickListener(this);

		myButton_09 = (ToggleButton) findViewById(R.id.button_09);
		mColorMap.put(R.id.button_09, 8);
		myButton_09.setOnClickListener(this);


		myCancel = (Button) findViewById(R.id.cancel);
		myCancel.setOnClickListener(this);
		myConfirm = (Button) findViewById(R.id.confirm);
		myConfirm.setOnClickListener(this);

	/*	Intent intent = getIntent();
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

		}*/

	}

	public void onConfirm() {

		Bundle bundle = new Bundle();
		bundle.putInt("position", mPosIndex);
		Intent intent = new Intent();
		intent.putExtras(bundle);
		setResult(RESULT_OK, intent);
		finish();
	}

	private void changePos(View v) {
		myButton_01.setChecked(false);
		myButton_02.setChecked(false);
		myButton_03.setChecked(false);
		myButton_04.setChecked(false);
		myButton_05.setChecked(false);
		myButton_06.setChecked(false);
		myButton_07.setChecked(false);
		myButton_08.setChecked(false);
		myButton_09.setChecked(false);
	
		((ToggleButton) v).setChecked(true);
		mPosIndex = mColorMap.get(v.getId());
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
			changePos(v);
		}

	}

}
