package com.vjiazhi.shuiyinwang.ui;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.vjiazhi.shuiyinwang.R;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

public class AboutAppActivity extends Activity {
	Button mBackButton;
	WebView qingang;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		//
		// this.setTheme(R.style.MyAppTheme);
		// ActionBar actionBar = getActionBar();
		// actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		// ViewGroup v = (ViewGroup) LayoutInflater.from(this).inflate(
		// R.layout.main_action_title, null);
		// actionBar.setCustomView(v);

		setContentView(R.layout.activity_aboutapp_main);
		qingang = (WebView) findViewById(R.id.qingang_info);
		mBackButton = (Button) findViewById(R.id.button_back);
		mBackButton.setVisibility(View.VISIBLE);
		mBackButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		readHtmlFormAssets();

		// titleAbout.setText(R.string.menu_introduce);

	}

	private void readHtmlFormAssets() {
		final String mimeType = "text/html";
		final String encoding = "UTF-8";
		WebSettings webSettings = qingang.getSettings();

		//xinghe:下面两句话会使得字体很小，无法阅读，还不能自动适配
		//webSettings.setLoadWithOverviewMode(true); 
		//webSettings.setUseWideViewPort(true);
		
		//xinghe 20150227 提供用户缩放操作
		webSettings.setSupportZoom(true);
		webSettings.setBuiltInZoomControls(true);
			
		String content = getFromAssets("aboutapp.html");
		
		//xinghe：这个属性会导致滚动时出现刷新闪烁问题
		//qingang.setBackgroundColor(Color.TRANSPARENT); // WebView 背景透明效果
		
		//qingang.loadUrl("file:///android_asset/qingang_intro.htm");会出现乱码问题
		qingang.loadDataWithBaseURL("", content, mimeType, encoding, "");

	}

	public String getFromAssets(String fileName) {
		String Result = "";
		try {
			InputStreamReader inputReader = new InputStreamReader(
					getResources().getAssets().open(fileName));
			BufferedReader bufReader = new BufferedReader(inputReader);
			String line = "";

			while ((line = bufReader.readLine()) != null)
				Result += line;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Result;
	}

}
