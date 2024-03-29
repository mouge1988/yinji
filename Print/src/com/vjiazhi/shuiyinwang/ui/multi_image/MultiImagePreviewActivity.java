package com.vjiazhi.shuiyinwang.ui.multi_image;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.vjiazhi.yinji.R;
import com.vjiazhi.shuiyinwang.ui.multi_image.adapter.MyAdapter;
import com.vjiazhi.shuiyinwang.utils.L;

public class MultiImagePreviewActivity extends Activity {

	private ArrayList<View> listViews = null;
	private ViewPager pager;
	private MyPageAdapter adapter;
	private int count;
	public List<String> bmpStr = new ArrayList<String>();

	RelativeLayout photo_relativeLayout;

	Button btnConfirm;
	TextView tvImageNum;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				int size = MyAdapter.mSelectedImage.size();
				if (size == 0) {
					btnConfirm.setEnabled(false);
					tvImageNum.setText("");
				} else {
					tvImageNum.setText((count + 1) + "/" + size);
				}
			}

		};
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preview);

		photo_relativeLayout = (RelativeLayout) findViewById(R.id.photo_relativeLayout);

		tvImageNum = (TextView) findViewById(R.id.tv_num_of_images);

		btnConfirm = (Button) findViewById(R.id.btn_to_confirm);
		btnConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		for (int i = 0; i < MyAdapter.mSelectedImage.size(); i++) {
			bmpStr.add(MyAdapter.mSelectedImage.get(i));
		}

		Button photo_bt_del = (Button) findViewById(R.id.photo_bt_del);
		photo_bt_del.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				bmpStr.remove(count);
				MyAdapter.mSelectedImage.remove(count);
				pager.removeAllViews();
				listViews.remove(count);
				myAdapterHasChanged(0);
				adapter.setListViews(listViews);
				adapter.notifyDataSetChanged();
				mHandler.sendEmptyMessage(1);
			}
		});

		pager = (ViewPager) findViewById(R.id.viewpager);
		pager.setOnPageChangeListener(pageChangeListener);
		for (int i = 0; i < bmpStr.size(); i++) {
			initListViews(bmpStr.get(i));//
		}

		adapter = new MyPageAdapter(listViews);// 构造adapter
		pager.setAdapter(adapter);// 设置适配器
		Intent intent = getIntent();
		int id = intent.getIntExtra("ID", 0);
		pager.setCurrentItem(id);
		count = id;
		mHandler.sendEmptyMessage(1);
	}

	public static MyAdapterHasChangedInterface mAdapterHasChangedInterface = null;

	public interface MyAdapterHasChangedInterface {
		public void hasChaged(int code);
	}

	public static void setAdpaterInterface(
			MyAdapterHasChangedInterface mInterface) {
		mAdapterHasChangedInterface = mInterface;
	}

	public static void myAdapterHasChanged(int code) {
		if (mAdapterHasChangedInterface != null) {
			mAdapterHasChangedInterface.hasChaged(code);
		}
	}

	private void initListViews(String bmString) {
		Bitmap bm = ImageLoader.decodeSampledBitmapFromResource(bmString, 720,
				1280);// 缩放图片显示
		initListViews(bm);

	}

	private void initListViews(Bitmap bm) {
		if (listViews == null)
			listViews = new ArrayList<View>();
		ImageView img = new ImageView(this);// 构造textView对象
		img.setBackgroundColor(0xff000000);
		img.setImageBitmap(bm);
		img.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		listViews.add(img);// 添加view
	}

	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

		public void onPageSelected(int arg0) {// 页面选择响应函数
			count = arg0;
			mHandler.sendEmptyMessage(1);
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {// 滑动中。。。

		}

		public void onPageScrollStateChanged(int arg0) {// 滑动状态改变
		}
	};

	class MyPageAdapter extends PagerAdapter {

		private ArrayList<View> listViews;// content

		private int size;// 页数

		public MyPageAdapter(ArrayList<View> listViews) {// 构造函数
															// 初始化viewpager的时候给的一个页面
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		public void setListViews(ArrayList<View> listViews) {// 自己写的一个方法用来添加数据
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		public int getCount() {// 返回数量
			return size;
		}

		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		public void destroyItem(View arg0, int arg1, Object arg2) {// 销毁view对象
			L.l("=======size===:"+arg1);
			if (size <= 0) {
				finish();
			} else {
				((ViewPager) arg0).removeView(listViews.get(arg1 % size));
			}
		}

		public void finishUpdate(View arg0) {
		}

		public Object instantiateItem(View arg0, int arg1) {// 返回view对象
			try {
				((ViewPager) arg0).addView(listViews.get(arg1 % size), 0);

			} catch (Exception e) {
			}
			return listViews.get(arg1 % size);
		}

		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}
}
