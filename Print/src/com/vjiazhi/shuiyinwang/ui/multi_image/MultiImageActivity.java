package com.vjiazhi.shuiyinwang.ui.multi_image;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.vjiazhi.yinji.R;
import com.vjiazhi.shuiyinwang.ui.ImgMainActivity;
import com.vjiazhi.shuiyinwang.ui.multi_image.ListImageDirPopupWindow.OnImageDirSelected;
import com.vjiazhi.shuiyinwang.ui.multi_image.adapter.MyAdapter;
import com.vjiazhi.shuiyinwang.ui.multi_image.adapter.MyAdapter.AdapterItemListener;
import com.vjiazhi.shuiyinwang.ui.multi_image.bean.ImageFloder;

public class MultiImageActivity extends Activity implements OnImageDirSelected,
		AdapterItemListener,
		MultiImagePreviewActivity.MyAdapterHasChangedInterface {
	private ProgressDialog mProgressDialog;

	/**
	 * 存储文件夹中的图片数量
	 */
	private int mPicsSize;
	/**
	 * 图片数量最多的文件夹
	 */
	private File mImgDir;
	/**
	 * 所有的图片
	 */
	private List<String> mImgs = new ArrayList<String>();

	private GridView mGirdView;
	private MyAdapter mAdapter;
	/**
	 * 临时的辅助类，用于防止同一个文件夹的多次扫描
	 */
	private HashSet<String> mDirPaths = new HashSet<String>();

	/**
	 * 扫描拿到所有的图片文件夹
	 */
	private List<ImageFloder> mImageFloders = new ArrayList<ImageFloder>();

	private RelativeLayout mBottomLy;

	private TextView btnChooseDir;
	private RelativeLayout all_pics_layout;
	private Button btnReviewImages;
	// private TextView mImageCount;
	private Button btnComfirm;
	int totalCount = 0;

	private static final int RESULT_CAMERA_IMAGE = 101;
	private String m_strCameraImgPathName;

	private int mScreenHeight;

	private ListImageDirPopupWindow mListImageDirPopupWindow;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// mProgressDialog.dismiss();
			// 为View绑定数据
			// data2View();
			data3View();
			// 初始化展示文件夹的popupWindw
			initListDirPopupWindw();
		}
	};

	/**
	 * 为View绑定数据
	 */
	private void data2View() {
		if (mImgDir == null) {
			Toast.makeText(getApplicationContext(), "抱歉，一张图片没扫描到。",
					Toast.LENGTH_SHORT).show();
			return;
		}
		List<String> tempList = Arrays.asList(mImgDir.list());
		mImgs.add("capture");
		mImgs.addAll(tempList);
		/**
		 * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
		 */
		mAdapter = new MyAdapter(getApplicationContext(), mImgs,
				R.layout.grid_item, mImgDir.getAbsolutePath());
		mAdapter.setAdapterItemListener(this);
		mGirdView.setAdapter(mAdapter);
	};

	private void data3View() {
		for (int i = 0; i < mImageFloders.size(); i++) {
			mImgDir = new File(mImageFloders.get(i).getDir());
			if (mImgDir != null) {

				List<String> tempList = Arrays.asList(mImgDir.list());
				List<String> pList = new ArrayList<String>();
				for (int j = 0; j < tempList.size(); j++) {
					pList.add(mImgDir + "/" + tempList.get(j));
				}
				mImgs.addAll(pList);
				/**
				 * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
				 */

			}
		}
		mAdapter = new MyAdapter(getApplicationContext(), mImgs,
				R.layout.grid_item, "");
		mAdapter.setAdapterItemListener(this);
		mGirdView.setAdapter(mAdapter);
	}

	/**
	 * 初始化展示文件夹的popupWindw
	 */
	private void initListDirPopupWindw() {

		// 把”所有图片”添加进去
		ImageFloder imageFloder = null;
		imageFloder = new ImageFloder();
		imageFloder.setName("所有图片");
		imageFloder.setCount(mImgs.size());
		imageFloder.setFirstImagePath(mImgs.get(0));

		mImageFloders.add(0, imageFloder);

		mListImageDirPopupWindow = new ListImageDirPopupWindow(
				LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7),
				mImageFloders, LayoutInflater.from(getApplicationContext())
						.inflate(R.layout.list_dir, null));

		mListImageDirPopupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// 设置背景颜色变暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1.0f;
				getWindow().setAttributes(lp);
			}
		});
		// 设置选择文件夹的回调
		mListImageDirPopupWindow.setOnImageDirSelected(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_multi_img_main);

		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		mScreenHeight = outMetrics.heightPixels;

		MultiImagePreviewActivity.setAdpaterInterface(this);

		initView();
		getImages();
		initEvent();

	}

	/**
	 * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
	 */
	private void getImages() {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this, "暂无外部存储。", Toast.LENGTH_SHORT).show();
			return;
		}
		// 显示进度条
		// mProgressDialog = ProgressDialog.show(this, null, "正在加载...");

		// 添加所有图片

		new Thread(new Runnable() {
			@Override
			public void run() {

				String firstImage = null;

				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = MultiImageActivity.this
						.getContentResolver();

				// 只查询jpeg和png的图片
				Cursor mCursor = mContentResolver.query(mImageUri, null,
						MediaStore.Images.Media.MIME_TYPE + "=? or "
								+ MediaStore.Images.Media.MIME_TYPE + "=?",
						new String[] { "image/jpeg", "image/png" },
						MediaStore.Images.Media.DATE_MODIFIED);

				// Log.e("TAG", mCursor.getCount() + "");

				while (mCursor.moveToNext()) {
					// 获取图片的路径
					String path = mCursor.getString(mCursor
							.getColumnIndex(MediaStore.Images.Media.DATA));
					// 拿到第一张图片的路径
					if (firstImage == null)
						firstImage = path;
					// 获取该图片的父路径名
					File file = new File(path);
					if (!file.exists()) {
						continue;
					}

					File parentFile = new File(path).getParentFile();
					if (parentFile == null)
						continue;
					String dirPath = parentFile.getAbsolutePath();
					ImageFloder imageFloder = null;
					// 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
					if (mDirPaths.contains(dirPath)) {
						continue;

					} else {
						mDirPaths.add(dirPath);
						// 初始化imageFloder
						imageFloder = new ImageFloder();
						imageFloder.setDir(dirPath);
						imageFloder.setFirstImagePath(path);
					}

					int picSize = parentFile.list(new FilenameFilter() {
						@Override
						public boolean accept(File dir, String filename) {
							if (filename.endsWith(".jpg")
									|| filename.endsWith(".png")
									|| filename.endsWith(".jpeg")) {
								return true;
							} else {
								return false;
							}
						}
					}).length;

					totalCount += picSize;
					imageFloder.setCount(picSize);
					mImageFloders.add(imageFloder);
					if (picSize > mPicsSize) {
						mPicsSize = picSize;
						mImgDir = parentFile;
					}
				}
				mCursor.close();

				// 扫描完成，辅助的HashSet也就可以释放内存了
				mDirPaths = null;

				// 通知Handler扫描图片完成
				mHandler.sendEmptyMessage(0);

			}
		}).start();

	}

	/**
	 * 初始化View
	 */
	private void initView() {
		// leftbutton.setText("预览");
		// leftbutton.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// Intent intent = new Intent(getApplicationContext(),
		// MultiImagePreviewActivity.class);
		// startActivity(intent);
		// }
		// });

		btnComfirm = (Button) findViewById(R.id.picture_sure);
		btnComfirm.setText("确定");
		btnComfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
				// MultiImgsProcessActivity.class);
				// fengyi modify to go to publish activity 20150428
				// PublishedActivity.class);
						ImgMainActivity.class);

				// Intent intent = new Intent(getApplicationContext(),
				// PhotoActivity.class);
				// Bundle bundle = new Bundle();
				// bundle.putStringArrayList("files", MyAdapter.mSelectedImage);
				// intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		mGirdView = (GridView) findViewById(R.id.id_gridView);
		all_pics_layout = (RelativeLayout) findViewById(R.id.all_pics_layout);
		btnChooseDir = (TextView) findViewById(R.id.btn_choose_dir);
		btnReviewImages = (Button) findViewById(R.id.btn_review_images);
		mBottomLy = (RelativeLayout) findViewById(R.id.id_bottom_ly);
		btnReviewImages.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),
						MultiImagePreviewActivity.class);
				startActivity(intent);
			}
		});

	}

	private void initEvent() {
		/**
		 * 为底部的布局设置点击事件，弹出popupWindow
		 */
		all_pics_layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mListImageDirPopupWindow
						.setAnimationStyle(R.style.anim_popup_dir);
				mListImageDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);

				// 设置背景颜色变暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = .3f;
				getWindow().setAttributes(lp);
			}
		});
	}

	@Override
	public void selected(ImageFloder floder) {

		if (floder.getName().equalsIgnoreCase("所有图片")) {
			mAdapter = new MyAdapter(getApplicationContext(), mImgs,
					R.layout.grid_item, "");
		} else {
			mImgDir = new File(floder.getDir());
			List<String> mImgs = Arrays.asList(mImgDir
					.list(new FilenameFilter() {
						@Override
						public boolean accept(File dir, String filename) {
							if (filename.endsWith(".jpg")
									|| filename.endsWith(".png")
									|| filename.endsWith(".jpeg"))
								return true;
							return false;
						}
					}));
			/**
			 * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
			 */
			mAdapter = new MyAdapter(getApplicationContext(), mImgs,
					R.layout.grid_item, mImgDir.getAbsolutePath());
		}

		mGirdView.setAdapter(mAdapter);
		// mImageCount.setText(floder.getCount() + "张");
		btnChooseDir.setText(floder.getName());
		mAdapter.setAdapterItemListener(this);
		mListImageDirPopupWindow.dismiss();

	}

	@Override
	public void onAdapterItemClick(int mSelectedImageNum) {
		// TODO Auto-generated method stub
		if (mSelectedImageNum > 0) {
			btnComfirm.setText("完成(" + mSelectedImageNum + ")");
			btnReviewImages.setEnabled(true);
		} else {
			btnComfirm.setText("完成");
			btnReviewImages.setEnabled(false);
		}
	}

	private void goToCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		m_strCameraImgPathName = ImgMainActivity.mImgSavePath + File.separator
				+ "img.jpg";

		intent.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(new File(m_strCameraImgPathName)));

		startActivityForResult(intent, RESULT_CAMERA_IMAGE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RESULT_CAMERA_IMAGE) {
			if (resultCode == RESULT_OK) {
				MyAdapter.mSelectedImage.add(m_strCameraImgPathName);
				Intent intent = new Intent(getApplicationContext(),
						MultiImagePreviewActivity.class);
				startActivity(intent);
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onCameraEnter() {
		goToCamera();
	}

	@Override
	protected void onDestroy() {
		MyAdapter.mSelectedImage.clear();
		super.onDestroy();
	}

	@Override
	public void hasChaged(int code) {
		mAdapter.notifyDataSetChanged();
		onAdapterItemClick(MyAdapter.mSelectedImage.size());
	}

}
