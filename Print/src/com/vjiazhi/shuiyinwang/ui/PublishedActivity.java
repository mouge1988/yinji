package com.vjiazhi.shuiyinwang.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.vjiazhi.shuiyinwang.R;
import com.vjiazhi.shuiyinwang.ui.adapter.GridAdapter;
import com.vjiazhi.shuiyinwang.ui.multi.MultiImgsProcessActivity;
import com.vjiazhi.shuiyinwang.ui.multi_image.MultiImageActivity;
import com.vjiazhi.shuiyinwang.ui.multi_image.MultiImagePreviewActivity;
import com.vjiazhi.shuiyinwang.ui.multi_image.adapter.MyAdapter;

public class PublishedActivity extends Activity {
	private GridView noScrollgridview;
	private GridAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_published_main);
		initViews();
	}

	public void initViews() {
		Button btnProcess = (Button) findViewById(R.id.btn_save_share);
		btnProcess.setText("加水印");
		btnProcess.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (MyAdapter.mSelectedImage.size() == 0) {
					Toast.makeText(getApplicationContext(),
							"Please choose pictures to process!",
							Toast.LENGTH_SHORT).show();
					return;
				}
				Intent intent = new Intent(getApplicationContext(),
						MultiImgsProcessActivity.class);
				startActivity(intent);
				finish();
			}
		});

		noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
		noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new GridAdapter(this, MyAdapter.mSelectedImage);
		adapter.update();
		noScrollgridview.setAdapter(adapter);
		noScrollgridview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (arg2 == MyAdapter.mSelectedImage.size()) {
					Intent intent = new Intent(PublishedActivity.this,
							MultiImageActivity.class);
					startActivity(intent);
				} else {
					Intent intent = new Intent(PublishedActivity.this,
							MultiImagePreviewActivity.class);
					intent.putExtra("ID", arg2);
					startActivity(intent);
				}
			}
		});
	}
}
