package com.vjiazhi.shuiyinwang.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import android.app.FragmentTransaction;

import com.umeng.fb.FeedbackAgent;
import com.vjiazhi.shuiyinwang.R;
import com.vjiazhi.shuiyinwang.ui.adapter.HorizontalListViewAdapter;
import com.vjiazhi.shuiyinwang.ui.adapter.HorizontalOutListViewAdapter;
import com.vjiazhi.shuiyinwang.ui.multi_image.ImageLoader;
import com.vjiazhi.shuiyinwang.ui.multi_image.MultiImageActivity;
import com.vjiazhi.shuiyinwang.ui.multi_image.adapter.MyAdapter;
import com.vjiazhi.shuiyinwang.utils.ImageProcessor;
import com.vjiazhi.shuiyinwang.utils.ImgFileUtils;
import com.vjiazhi.shuiyinwang.utils.L;
import com.vjiazhi.shuiyinwang.utils.LogPrint;
import com.vjiazhi.shuiyinwang.utils.MyConfig;
import com.vjiazhi.shuiyinwang.widgets.ZoomableImageView;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v4.widget.SlidingPaneLayout.PanelSlideListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.vjiazhi.shuiyinwang.ui.view.*;

@TargetApi(9)
public class ImgMainActivity extends Activity implements
		ContentFragment.InterfaceContentView, MenuFragment.UmengInterface {

	Button m_btnLoadImg, m_btnSaveShare;
	// LinearLayout m_layText, m_layColor, m_layPostion;
	Button m_layText, m_layColor, m_layPostion, m_shuiYing;
	AlertDialog m_dlgLoadImg;
	Context mContext;
	private ZoomableImageView mImageView = null;

	private Bitmap mCurrentImg = null;
	private Bitmap mFirstLoadedImg = null;

	ProgressDialog mWaitingDialog = null;

	String mCurrTitle = "";
	String mBlogUrl = "";

	String m_strWaterMarkInfo = "印记Print";
	String m_strCameraImgPathName = "";
	boolean mIsImgUpdated = false;
	String mFileNameToSave = "";

	final int LOAD_FROM_GALLERY = 0; // 图库
	final int LOAD_FROM_CAMERA = 1; // 拍照
	final int LOAD_MULTI_GALLERY = 2; // 多图

	private static final int RESULT_GALLERY_IMAGE = 100;
	private static final int RESULT_CAMERA_IMAGE = 101;
	private static final int WATER_MARK_MAX_LENGTH_BYTES = 33;
	private static final int COLOR_REQUEST_CODE = 102;
	private static final int POS_REQUEST_CODE = 103;

	// fengyi add
	public static String mImgSavePath = Environment
			.getExternalStorageDirectory() + "/vjiazhi";

	public static String mMultiImgsSavePath = mImgSavePath + "/out";

	// the sliderFragment
	private ContentEnablePanelLayout slidingPaneLayout;
	private MenuFragment menuFragment;
	private ContentFragment contentFragment;
	private DisplayMetrics displayMetrics = new DisplayMetrics();
	private int maxMargin = 0;

	HorizontalListView hListview;
	HorizontalListView hListview2;
	HorizontalOutListViewAdapter hListViewAdapter;
	HorizontalListViewAdapter hListViewAdapter2;

	FeedbackAgent mUmengFeedBack = null; // 友盟用户反馈组件

	private View contentView = null;

	// --------下面就是多图

	// ================
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		setContentView(R.layout.slidingpane_main_layout);
		slidingPaneLayout = (ContentEnablePanelLayout) findViewById(R.id.slidingpanellayout);
		menuFragment = new MenuFragment();
		contentFragment = new ContentFragment(this);

		menuFragment.setUmengInterface(this);

		mUmengFeedBack = new FeedbackAgent(this);
		// check if the app developer has replied to the feedback or not.
		mUmengFeedBack.sync();

		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.slidingpane_menu, menuFragment);
		transaction.replace(R.id.slidingpane_content, contentFragment);
		transaction.commit();
		maxMargin = displayMetrics.heightPixels / 10;
		slidingPaneLayout.setEnabled(false);
		// slidingPaneLayout.onTouchEvent(arg0)
		slidingPaneLayout.setPanelSlideListener(new PanelSlideListener() {

			@Override
			public void onPanelSlide(View panel, float slideOffset) {
//				int contentMargin = (int) (slideOffset * maxMargin);
//				FrameLayout.LayoutParams contentParams = contentFragment
//						.getCurrentViewParams();
//				contentParams.setMargins(0, contentMargin, 0, contentMargin);
//				contentFragment.setCurrentViewPararms(contentParams);
//
//				float scale = 1 - ((1 - slideOffset) * maxMargin * 3)
//						/ (float) displayMetrics.heightPixels;
//				menuFragment.getCurrentView().setScaleX(scale);// 设置缩放的基准点
//				menuFragment.getCurrentView().setScaleY(scale);// 设置缩放的基准点
//				menuFragment.getCurrentView().setPivotX(0);// 设置缩放和选择的点
//				menuFragment.getCurrentView().setPivotY(
//						displayMetrics.heightPixels / 2);
//				menuFragment.getCurrentView().setAlpha(slideOffset);
//				contentFragment.getCurrentView().setAlpha(1);

				// slidingPaneLayout.setEnabled(false);
				// slidingPaneLayout.setFocusable(false);
			}

			@Override
			public void onPanelOpened(View panel) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPanelClosed(View panel) {
				// TODO Auto-generated method stub

			}
		});
		// setContentView(R.layout.activity_img_main);

	}

	/**
	 * @return the slidingPaneLayout
	 */
	public SlidingPaneLayout getSlidingPaneLayout() {
		return slidingPaneLayout;
	}

	/**
	 * fengyi add
	 * 
	 * @time 2015/02/23 13:47
	 */
	private void prepareVjiazhiFolder() {
		File f = new File(mImgSavePath);
		if (!f.exists()) {
			f.mkdirs();
		} else {
			// System.out.println("VJIAZHI_PATH exists!");
			LogPrint.logd(this, "VJIAZHI_PATH exists!");
		}
	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) {
	 * getMenuInflater().inflate(R.menu.activity_img_main, menu); return true; }
	 */

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {

			if (requestCode == RESULT_GALLERY_IMAGE && null != data) {
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };

				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String picturePath = cursor.getString(columnIndex);
				cursor.close();

				mFirstLoadedImg = BitmapFactory.decodeFile(picturePath);
				mImageView.setImageBitmap(mFirstLoadedImg);

				mIsImgUpdated = true;

			} else if (requestCode == RESULT_CAMERA_IMAGE) {
				mFirstLoadedImg = BitmapFactory
						.decodeFile(m_strCameraImgPathName);
				mCurrentImg = mFirstLoadedImg; // xinghe add
				mImageView.setImageBitmap(mFirstLoadedImg);
				mIsImgUpdated = true;
			} else if (requestCode == COLOR_REQUEST_CODE) {
				try {
					Bundle extras = data.getExtras();
					MyConfig.setNewColor(extras.getInt("color"));
					if ((!m_strWaterMarkInfo.isEmpty())
							&& (mFirstLoadedImg != null)) {
						mCurrentImg = ImageProcessor.createFinalBitmap(
								mFirstLoadedImg, m_strWaterMarkInfo);
						mImageView.setImageBitmap(mCurrentImg);
						mIsImgUpdated = true;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (requestCode == POS_REQUEST_CODE) {
				try {
					Bundle extras = data.getExtras();
					MyConfig.setNewPosition(extras.getInt("position"));

					if ((!m_strWaterMarkInfo.isEmpty())
							&& (mFirstLoadedImg != null)) {
						mCurrentImg = ImageProcessor.createFinalBitmap(
								mFirstLoadedImg, m_strWaterMarkInfo);
						mImageView.setImageBitmap(mCurrentImg);
						mIsImgUpdated = true;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public String saveCurrentBitmap(boolean bNeedToast) throws IOException {

		LogPrint.logd(mContext, "saveCurrentBitmap,mIsImgUpdated:"
				+ mIsImgUpdated);
		LogPrint.logd(mContext, "saveCurrentBitmap,mFileNameToSave:"
				+ mFileNameToSave);

		if (!mIsImgUpdated) {
			if (!mFileNameToSave.isEmpty()) {
				String savedMessage = getString(R.string.img_already_saved,
						mImgSavePath + mFileNameToSave);
				hideWaitingDialog();

				if (bNeedToast) {
					Toast.makeText(mContext, savedMessage, Toast.LENGTH_LONG)
							.show();
				}
				return mImgSavePath + mFileNameToSave;
			} else {
				mFileNameToSave = createFileName(false);
			}
		} else {
			mFileNameToSave = createFileName(true);
		}

		File fileImgToSave = new File(mImgSavePath + mFileNameToSave);
		File dirForFileCache = new File(mImgSavePath);

		if (fileImgToSave.exists()) {
			hideWaitingDialog();
			if (bNeedToast) {
				String savedMessage = getString(R.string.img_already_saved,
						mFileNameToSave);
				Toast.makeText(mContext, savedMessage, Toast.LENGTH_SHORT)
						.show();
			}
			return mImgSavePath + mFileNameToSave;
		}

		if (!dirForFileCache.exists()) {
			if (!dirForFileCache.mkdirs()) {

				hideWaitingDialog();
				Toast.makeText(mContext, R.string.failed_to_save_img,
						Toast.LENGTH_SHORT).show();
				return "";
			}
		}

		FileOutputStream fOut = null;
		try {
			fileImgToSave.createNewFile();
			fOut = new FileOutputStream(fileImgToSave);
		} catch (FileNotFoundException e) {
			hideWaitingDialog();

			if (bNeedToast) {
				Toast.makeText(mContext, R.string.failed_to_save_img,
						Toast.LENGTH_SHORT).show();
			}
			e.printStackTrace();
			return "";
		}

		mCurrentImg.compress(Bitmap.CompressFormat.PNG, 100, fOut);

		try {
			fOut.flush();

			String savedMessage = getString(R.string.img_already_saved,
					mImgSavePath + mFileNameToSave);
			hideWaitingDialog();

			if (bNeedToast) {
				Toast.makeText(mContext, savedMessage, Toast.LENGTH_LONG)
						.show();
			}
		} catch (IOException e) {
			hideWaitingDialog();

			if (bNeedToast) {
				Toast.makeText(mContext, R.string.failed_to_save_img,
						Toast.LENGTH_SHORT).show();
			}
			return "";
		}

		try {
			fOut.close();
		} catch (IOException e) {
			hideWaitingDialog();
			return "";
		}
		mIsImgUpdated = false;

		return mImgSavePath + mFileNameToSave;
	}

	public String createFileName(boolean bUpdated) {
		String fileName = mFileNameToSave;

		if (bUpdated) {
			Time t = new Time();
			t.setToNow();
			int year = t.year;
			int month = t.month + 1;
			int monthDay = t.monthDay;
			int hour = t.hour;
			int minute = t.minute;
			int second = t.second;

			String syear = String.valueOf(year);
			String smonth = String.valueOf(month);
			String smonthDay = String.valueOf(monthDay);
			String shour = String.valueOf(hour);
			String sminute = String.valueOf(minute);
			String ssecond = String.valueOf(second);
			if (month < 10)
				smonth = "0" + smonth;
			else
				;

			if (monthDay < 10)
				smonthDay = "0" + smonthDay;
			else
				;

			if (hour < 10)
				shour = "0" + shour;
			else
				;

			if (minute < 10)
				sminute = "0" + sminute;
			else
				;

			if (second < 10)
				ssecond = "0" + ssecond;
			else
				;

			fileName = "Img_" + syear + smonth + smonthDay + shour + sminute
					+ ssecond + ".jpg";

		}
		return fileName;
	}

	public void showWaitingDialog() {
		if (mWaitingDialog == null) {
			mWaitingDialog = new ProgressDialog(this);
		} else {
			mWaitingDialog.dismiss();
		}
		mWaitingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mWaitingDialog.setMessage(getResources().getString(R.string.waiting));
		mWaitingDialog.setIndeterminate(false);
		mWaitingDialog.setCancelable(true);
		mWaitingDialog.show();
	}

	public void hideWaitingDialog() {
		if (mWaitingDialog != null) {
			mWaitingDialog.dismiss();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mCurrentImg != null && !mCurrentImg.isRecycled()) {
			mCurrentImg.recycle();
			System.gc();
		}

		if (this.mFirstLoadedImg != null && !mFirstLoadedImg.isRecycled()) {
			mFirstLoadedImg.recycle();
			System.gc();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	void showDialogLoadImg() {
		if (m_dlgLoadImg == null) {
			m_dlgLoadImg = new AlertDialog.Builder(mContext)
					.setTitle(R.string.load_img_title)
					.setItems(R.array.load_type_items,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									if (which == LOAD_FROM_GALLERY) {
										Intent i = new Intent(
												Intent.ACTION_PICK,
												android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

										startActivityForResult(i,
												RESULT_GALLERY_IMAGE);
									} else if (which == LOAD_FROM_CAMERA) {
										Intent intent = new Intent(
												MediaStore.ACTION_IMAGE_CAPTURE);

										m_strCameraImgPathName = mImgSavePath
												+ File.separator + "img.jpg";

										intent.putExtra(
												MediaStore.EXTRA_OUTPUT,
												Uri.fromFile(new File(
														m_strCameraImgPathName)));

										startActivityForResult(intent,
												RESULT_CAMERA_IMAGE);
									} else if (which == LOAD_MULTI_GALLERY) {
										startImgFIleListActivity();
									}
								}
							}).create();
		}

		Window windowDlg = m_dlgLoadImg.getWindow();
		windowDlg.setGravity(Gravity.CENTER);

		m_dlgLoadImg.show();
	}

	/**
	 * intent to ImgFIleListActivity
	 * 
	 * @author fengyi
	 */
	private void startImgFIleListActivity() {
	}

	private void startImgsFIleListActivity() {
		Intent intent = new Intent(ImgMainActivity.this,
				MultiImageActivity.class);//
		startActivity(intent);
	}

	@Override
	public void hasDone() {
		contentView = contentFragment.getContentView();
		m_btnLoadImg = (Button) contentView.findViewById(R.id.btn_load_img);
		m_shuiYing = (Button) contentView.findViewById(R.id.shuiying);
		mImageView = (ZoomableImageView) contentView
				.findViewById(R.id.zoomable_imageview);
		prepareVjiazhiFolder();
		mContext = this;
		StorageManager sm = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
		try {
			Class smClass;
			smClass = Class.forName("android.os.storage.StorageManager");
			Log.i("WiFiDirectActivity.TAG", "hexiang-test" + smClass.toString());
			Method meths[] = smClass.getMethods();
			Method getDefaultPath = null;
			for (int i = 0; i < meths.length; i++) {
				if (meths[i].getName().endsWith("getDefaultPath"))
					getDefaultPath = meths[i];
			}

			mImgSavePath = (String) getDefaultPath.invoke(sm, null);
			mImgSavePath = mImgSavePath + "/" + "vjiazhi/";

			mMultiImgsSavePath = mImgSavePath + "out";
		} catch (Exception e) {
			e.printStackTrace();
		}
		m_btnLoadImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// showDialogLoadImg();
				// go to image list add by fengyi
				startImgFIleListActivity();
				slidingPaneLayout.openPane();

			}
		});
		m_btnSaveShare = (Button) contentView.findViewById(R.id.btn_save_share);
		m_btnSaveShare.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// 分享图片到微信朋友圈
			}
		});

		m_layText = (Button) contentView.findViewById(R.id.text);
		m_layText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (mFirstLoadedImg != null) {
					LayoutInflater inflater = LayoutInflater.from(mContext);
					final View textEntryView = inflater.inflate(
							R.layout.watermark_input_layout, null);
					final EditText edtInput = (EditText) textEntryView
							.findViewById(R.id.edtInput);
					final AlertDialog.Builder builder = new AlertDialog.Builder(
							mContext);
					builder.setCancelable(false);

					builder.setTitle(R.string.title_watermark_txt);
					builder.setView(textEntryView);
					builder.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									m_strWaterMarkInfo = edtInput.getText()
											.toString();
									if ((!m_strWaterMarkInfo.isEmpty())
											&& (mFirstLoadedImg != null)) {
										mCurrentImg = ImageProcessor
												.createFinalBitmap(
														mFirstLoadedImg,
														m_strWaterMarkInfo);
										mImageView.setImageBitmap(mCurrentImg);
										mIsImgUpdated = true;
									}
								}
							});
					builder.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

								}
							});
					builder.show();
				}
			}
		});

		m_layColor = (Button) contentView.findViewById(R.id.color);
		m_layColor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(mContext, ColorSetActivity.class);
				startActivityForResult(i, COLOR_REQUEST_CODE);
			}
		});

		m_layPostion = (Button) contentView.findViewById(R.id.position);
		m_layPostion.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(mContext, PositionSetActivity.class);
				startActivityForResult(i, POS_REQUEST_CODE);
			}
		});

		m_shuiYing.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new MyImgsProcessTask(ImgMainActivity.this)
						.execute(MyAdapter.mSelectedImage);
			}
		});

		initViews();
	}

	// 设置默认的图片
	public void setPreviewView(int position) {
		String picturePath = MyAdapter.mSelectedImage.get(position);
		mFirstLoadedImg = BitmapFactory.decodeFile(picturePath);
		mCurrentImg = ImageProcessor.createFinalBitmap(mFirstLoadedImg,
				m_strWaterMarkInfo);
		mImageView.setImageBitmap(mCurrentImg);
	}

	private void initViews() {
		hListview = (HorizontalListView) contentView
				.findViewById(R.id.img_horizon_listview);
		hListview2 = (HorizontalListView) contentView
				.findViewById(R.id.img_horizon_listview2);

		hListViewAdapter2 = new HorizontalListViewAdapter(
				getApplicationContext(), MyAdapter.mSelectedImage);

		// hListview.setAdapter(hListViewAdapter);

		String picturePath = "";
		if (MyAdapter.mSelectedImage.size() > 0) {
			picturePath = MyAdapter.mSelectedImage.get(0);
			mFirstLoadedImg = BitmapFactory.decodeFile(picturePath);
			mCurrentImg = ImageProcessor.createFinalBitmap(mFirstLoadedImg,
					m_strWaterMarkInfo);
			mImageView.setImageBitmap(mCurrentImg);
		}

		// hListview.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view,
		// int position, long id) {
		// // TODO Auto-generated method stub
		// if (position == MyAdapter.mSelectedImage.size()) {
		// startImgsFIleListActivity();
		// }
		// }
		// });

		hListview2.setAdapter(hListViewAdapter2);

		hListview2.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (position == MyAdapter.mSelectedImage.size()) {
					startImgsFIleListActivity();
				} else {
					// Intent intent = new Intent(getApplicationContext(),
					// MultiImagePreviewActivity.class);
					// startActivity(intent);
					setPreviewView(position);
				}

			}
		});
	}

	// =================下面是点击水印后的效果==========================
	ArrayList<String> listfile = new ArrayList<String>();
	ArrayList<String> listfileOut = new ArrayList<String>(); // 输出后的地址列表
	ArrayList<String> selectHashMap = new ArrayList<String>();
	private final static int IMAGE_QUALITY_PERCENT = 50; // 压缩的是

	private boolean saveImgsAndOut(Bitmap bitmap, String path, String name) {
		File savePath = new File(path);
		if (!savePath.exists()) {
			savePath.mkdirs();
		}

		if (!path.endsWith(File.separator)) {
			path = path + File.separator;
		}

		String filePath = path + name;

		File fileImgToSave = new File(filePath);
		if (fileImgToSave.exists()) {
			return false;
		}

		listfileOut.add(filePath);
		selectHashMap.add("yes");

		FileOutputStream fOut = null;
		try {
			fileImgToSave.createNewFile();
			fOut = new FileOutputStream(fileImgToSave);

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		bitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY_PERCENT, fOut);

		try {
			fOut.flush();
			fOut.close();
		} catch (IOException e) {
			return false;
		} finally {
			bitmap.recycle();
		}

		return true;
	}

	private String combineSavedFileName(String fromPath) {
		int dot = fromPath.lastIndexOf(".");

		String preName = fromPath.substring(0, dot);
		String suffixName = fromPath.substring(dot, fromPath.length());

		return preName + "_out" + suffixName;
	}

	// private String getFileName(String fromPath){
	// int sep = fromPath.lastIndexOf(File.separator);
	//
	//
	// return fromPath.substring(sep);
	// }

	// process task
	class MyImgsProcessTask extends AsyncTask<List, Integer, Boolean> {
		ProgressDialog pdialog;
		Bitmap loadedImg;
		Bitmap savedImg;

		Context mContext;

		// mFirstLoadedImg = BitmapFactory
		// .decodeFile(m_strCameraImgPathName);
		public MyImgsProcessTask(Context mcontext) {
			this.mContext = mcontext;
			pdialog = new ProgressDialog(mcontext);
			pdialog.setTitle(mcontext.getString(R.string.str_processing));
			pdialog.setProgress(0);
			pdialog.setMax(listfile.size());
			pdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pdialog.show();
		}

		@Override
		protected Boolean doInBackground(List... mlist) {
			final int sum = mlist[0].size();
			for (int i = 0; i < sum; i++) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String imgPath = (String) mlist[0].get(i);
				loadedImg = ImageLoader.decodeSampledBitmapFromResource(
						imgPath, 720, 1280);// 缩放图片显示

				savedImg = ImageProcessor.createFinalBitmap(loadedImg,
						mContext.getString(R.string.str_syw_test));

				saveImgsAndOut(savedImg, ImgMainActivity.mMultiImgsSavePath,
						ImgFileUtils.createFileName());
				// progress
				publishProgress((int) (i * 100.0 / sum));
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			pdialog.setProgress(values[0]);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pdialog.dismiss();
			showListOutPath();
		}
	}

	private void showListOutPath() {
		// ArrayAdapter<String> arryAdapter = new ArrayAdapter<String>(this,
		// android.R.layout.simple_list_item_1, listfileOut);
		hListViewAdapter = new HorizontalOutListViewAdapter(
				getApplicationContext(), listfileOut);
		hListViewAdapter.setSelectHashMap(selectHashMap);
		hListview.setAdapter(hListViewAdapter);

		hListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				if (arg2 == listfileOut.size()) {
					return;
				}
				if (selectHashMap.get(arg2).equalsIgnoreCase("yes")) {
					selectHashMap.add(arg2, "no");
				} else {
					selectHashMap.add(arg2, "yes");
				}
				selectHashMap.remove(arg2 + 1);
				hListViewAdapter.setSelectHashMap(selectHashMap);
				hListViewAdapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public void onClickPosition(int position) {
		// TODO Auto-generated method stub
		mUmengFeedBack.startFeedbackActivity();
	}
}
