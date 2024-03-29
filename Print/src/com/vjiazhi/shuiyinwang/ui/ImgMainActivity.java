package com.vjiazhi.shuiyinwang.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import android.app.FragmentTransaction;

import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.socialize.bean.RequestType;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;
import com.vjiazhi.yinji.R;
import com.vjiazhi.shuiyinwang.ui.adapter.GridAdapter;
import com.vjiazhi.shuiyinwang.ui.adapter.GridViewAdapter;
import com.vjiazhi.shuiyinwang.ui.adapter.HorizontalListViewAdapter;
import com.vjiazhi.shuiyinwang.ui.adapter.HorizontalOutListViewAdapter;
import com.vjiazhi.shuiyinwang.ui.multi_image.ImageLoader;
import com.vjiazhi.shuiyinwang.ui.multi_image.MultiImageActivity;
import com.vjiazhi.shuiyinwang.ui.multi_image.adapter.MyAdapter;
import com.vjiazhi.shuiyinwang.utils.ImageProcessor;
import com.vjiazhi.shuiyinwang.utils.ImgFileUtils;
import com.vjiazhi.shuiyinwang.utils.LogPrint;
import com.vjiazhi.shuiyinwang.utils.MyConfig;
import com.vjiazhi.shuiyinwang.widgets.ZoomableImageView;
import android.support.v4.widget.SlidingPaneLayout;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
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
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.AdapterView.OnItemClickListener;

import com.vjiazhi.shuiyinwang.ui.view.*;

@TargetApi(9)
public class ImgMainActivity extends Activity implements
		ContentFragment.InterfaceContentView, MenuFragment.UmengInterface {

	Button m_btnLoadImg, m_btnSaveShare;
	Button m_layText, m_layColor, m_layPostion, m_shuiYing;
	RelativeLayout shezhi_Layout;
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

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			// 透明状态栏
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// 透明导航栏

		}

		setContentView(R.layout.slidingpane_main_layout);
		slidingPaneLayout = (ContentEnablePanelLayout) findViewById(R.id.slidingpanellayout);
		menuFragment = new MenuFragment();
		contentFragment = new ContentFragment(this);
		menuFragment.setUmengInterface(this);
		mUmengFeedBack = new FeedbackAgent(this);
		mUmengFeedBack.sync();
		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.slidingpane_menu, menuFragment);
		transaction.replace(R.id.slidingpane_content, contentFragment);
		transaction.commit();
		maxMargin = displayMetrics.heightPixels / 10;
		slidingPaneLayout.setEnabled(false);
		m_strWaterMarkInfo = MyConfig.getNewText(ImgMainActivity.this);

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

	/**
	 * 下面的函数没有被使用，标注！
	 */
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
		mContext = this;
		contentView = contentFragment.getContentView();
		m_btnLoadImg = (Button) contentView.findViewById(R.id.btn_load_img);
		m_shuiYing = (Button) contentView.findViewById(R.id.shuiying);
		shezhi_Layout = (RelativeLayout) contentView
				.findViewById(R.id.shezhilayout);
		mImageView = (ZoomableImageView) contentView
				.findViewById(R.id.zoomable_imageview);
		prepareVjiazhiFolder();
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
				startImgFIleListActivity();
				slidingPaneLayout.openPane();

			}
		});
		m_btnSaveShare = (Button) contentView.findViewById(R.id.btn_save_share);
		m_btnSaveShare.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// 分享图片到微信朋友圈
				showShareDialog();
			}
		});
		m_layText = (Button) contentView.findViewById(R.id.text);
		m_layText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				LayoutInflater inflater = LayoutInflater.from(mContext);
				final View textEntryView = inflater.inflate(
						R.layout.watermark_input_layout, null);
				final EditText edtInput = (EditText) textEntryView
						.findViewById(R.id.edtInput);
				final AlertDialog.Builder builder = new AlertDialog.Builder(
						mContext);
				builder.setCancelable(true);
				builder.setView(textEntryView);
				final AlertDialog dialog = builder.show();
				edtInput.setText(MyConfig.getNewText(ImgMainActivity.this));
				Button sureButton = (Button) textEntryView
						.findViewById(R.id.sure);
				sureButton.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						m_strWaterMarkInfo = edtInput.getText().toString();
						if ((!m_strWaterMarkInfo.isEmpty())) {

							MyConfig.setNewText(ImgMainActivity.this,
									m_strWaterMarkInfo);
							if (mFirstLoadedImg != null) {
								mCurrentImg = ImageProcessor.createFinalBitmap(
										ImgMainActivity.this, mFirstLoadedImg,
										m_strWaterMarkInfo);
								mImageView.setImageBitmap(mCurrentImg);
								mIsImgUpdated = true;
							}
							if (dialog != null) {
								dialog.dismiss();
							}
						} else {
							Toast.makeText(ImgMainActivity.this, "内容不能为空！",
									Toast.LENGTH_SHORT).show();
						}

					}
				});

				Button cancelButton = (Button) textEntryView
						.findViewById(R.id.cancel);
				cancelButton.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if (dialog != null) {
							dialog.dismiss();
						}
					}
				});
			}
		});

		m_layColor = (Button) contentView.findViewById(R.id.color);
		m_layColor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showColorDialog();
			}
		});

		m_layPostion = (Button) contentView.findViewById(R.id.position);
		m_layPostion.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showShezhiDialog();
			}
		});

		m_shuiYing.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (MyAdapter.mSelectedImage.size() == 0) {
					Toast.makeText(ImgMainActivity.this, "请先点击+添加图片",
							Toast.LENGTH_SHORT).show();
				} else {
					new MyImgsProcessTask(ImgMainActivity.this)
							.execute(MyAdapter.mSelectedImage);
				}
			}
		});

		initViews();
	}

	/******************************************************
	 * 下面是分享图片的代码 *********************************
	 */
	AlertDialog mDlgChooseShare = null;

	private void showShareDialog() {
		mDlgChooseShare = new AlertDialog.Builder(this).setNegativeButton(
				android.R.string.cancel, null).create();
		Window windowDlg = mDlgChooseShare.getWindow();

		// 使用自定义的网格布局
		String[] name = { "微信好友", "微信朋友圈", "更多方式" };

		int[] iconarray = { R.drawable.umeng_socialize_wechat,
				R.drawable.umeng_socialize_wxcircle, R.drawable.logo_more };

		LayoutInflater factory = LayoutInflater.from(mContext);
		View view = factory.inflate(R.layout.share_alert_layout, null);
		GridView grView = (GridView) view
				.findViewById(R.id.main_menu_grid_view);

		GridViewAdapter gridAdapter = new GridViewAdapter(this, name, iconarray);
		grView.setAdapter(gridAdapter);
		grView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				shareCurrent(position);
			}
		});

		windowDlg
				.setType(android.view.WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		mDlgChooseShare.setView(view);
		windowDlg.setGravity(Gravity.BOTTOM);
		mDlgChooseShare.show();
	}

	private void shareCurrent(int which) {
		MobclickAgent.onEvent(this, "share_event");
		final UMSocialService mController = UMServiceFactory
				.getUMSocialService("com.umeng.share", RequestType.SOCIAL);

		String appID = "wx4e79aee91524b279";
		String appSecret = "6419aaed2e10049d1304c0c72d1267b1";

		if (which == 0) {

			// 添加微信平台 UMWXHandler
			UMWXHandler wxHandler = new UMWXHandler(this, appID, appSecret);
			wxHandler.setToCircle(false);
			wxHandler.addToSocialSDK();

			// 设置微信好友分享内容
			WeiXinShareContent weixinContent = new WeiXinShareContent(); // 设置分享文字
			weixinContent.setTitle(mCurrTitle); //
			// weixinContent.setTargetUrl(mStrImageUrl);

			UMImage uMImgBitmap = new UMImage(this, mCurrentImg);
			weixinContent.setShareImage(uMImgBitmap);
			mController.setShareMedia(weixinContent);

			mController.postShare(this, SHARE_MEDIA.WEIXIN,
					new SnsPostListener() {

						@Override
						public void onComplete(SHARE_MEDIA arg0, int arg1,
								SocializeEntity arg2) {
						}

						@Override
						public void onStart() {
						}
					});

		} else if (which == 1) {// 朋友圈
			UMWXHandler wxCircleHandler = new UMWXHandler(this, appID,
					appSecret);
			wxCircleHandler.setToCircle(true);
			wxCircleHandler.addToSocialSDK();

			// 设置微信朋友圈分享内容
			// String mShareContent = getShortTextFromData();
			CircleShareContent circleMedia = new CircleShareContent();
			// circleMedia.setShareContent(mShareContent);
			// 设置朋友圈title
			circleMedia.setTitle(mCurrTitle);
			UMImage uMImgBitmap = new UMImage(this, mCurrentImg);
			circleMedia.setShareImage(uMImgBitmap);
			// circleMedia.setShareImage(new UMImage().set)
			mController.setShareMedia(circleMedia);

			mController.postShare(this, SHARE_MEDIA.WEIXIN_CIRCLE,
					new SnsPostListener() {

						@Override
						public void onComplete(SHARE_MEDIA arg0, int arg1,
								SocializeEntity arg2) {
						}

						@Override
						public void onStart() {
						}
					});
		} else {// 使用其他分享方式
			showWaitingDialog();
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					try {
						Intent inSend = new Intent(Intent.ACTION_SEND);
						String filePath = "";

						filePath = saveCurrentBitmap(false);// 这里保存不提示

						hideWaitingDialog();

						if (!filePath.isEmpty()) {
							File file = new File(filePath);
							if (file != null && file.exists() && file.isFile()) {
								inSend.setType("image/jpg");
								Uri u = Uri.fromFile(file);
								inSend.putExtra(Intent.EXTRA_STREAM, u);
							}

						}
						// 当用户选择短信时使用sms_body取得文字
						inSend.putExtra(Intent.EXTRA_SUBJECT, mCurrTitle);
						inSend.putExtra("sms_body", mCurrTitle + "\n"
								+ mBlogUrl + "\n【来自秦刚观点】");
						inSend.putExtra(Intent.EXTRA_TEXT, mCurrTitle + "\n"
								+ mBlogUrl + "\n【来自秦刚观点】");
						inSend.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						inSend.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
						try {
							startActivity(Intent
									.createChooser(inSend, "分享自印记。"));
						} catch (android.content.ActivityNotFoundException ex) {
							// if no app handles it, do nothing
						}

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}, 100);

		}
		mDlgChooseShare.dismiss();
	}

	// 设置默认的图片
	public void setPreviewView(int position) {
		String picturePath = MyAdapter.mSelectedImage.get(position);
		mFirstLoadedImg = BitmapFactory.decodeFile(picturePath);
		mCurrentImg = ImageProcessor.createFinalBitmap(ImgMainActivity.this,
				mFirstLoadedImg, m_strWaterMarkInfo);
		mImageView.setImageBitmap(mCurrentImg);
	}

	private void initViews() {
		hListview = (HorizontalListView) contentView
				.findViewById(R.id.img_horizon_listview);
		hListview2 = (HorizontalListView) contentView
				.findViewById(R.id.img_horizon_listview2);

		hListViewAdapter2 = new HorizontalListViewAdapter(
				getApplicationContext(), MyAdapter.mSelectedImage);

		if (MyAdapter.mSelectedImage.size() > 0) {
			String picturePath = MyAdapter.mSelectedImage.get(0);
			mFirstLoadedImg = BitmapFactory.decodeFile(picturePath);
			mCurrentImg = ImageProcessor.createFinalBitmap(
					ImgMainActivity.this, mFirstLoadedImg, m_strWaterMarkInfo);
			mImageView.setImageBitmap(mCurrentImg);
		}
		hListview2.setAdapter(hListViewAdapter2);
		hListview2.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == MyAdapter.mSelectedImage.size()) {
					startImgsFIleListActivity();
				} else {
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

	// process task
	class MyImgsProcessTask extends AsyncTask<List, Integer, Boolean> {
		ProgressDialog pdialog;
		Bitmap loadedImg;
		Bitmap savedImg;

		Context mContext;

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
					e.printStackTrace();
				}
				String imgPath = (String) mlist[0].get(i);
				loadedImg = ImageLoader.decodeSampledBitmapFromResource(
						imgPath, 720, 1280);// 缩放图片显示

				savedImg = ImageProcessor.createFinalBitmap(
						ImgMainActivity.this, loadedImg, m_strWaterMarkInfo);

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
		mUmengFeedBack.startFeedbackActivity();
	}

	/************************************************************
	 * 如下代码实现选择颜色的自定义Dialog
	 * **********************************************************
	 */

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

	private void showColorDialog() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		final View textEntryView = inflater.inflate(R.layout.color_selector,
				null);
		final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setCancelable(true);

		builder.setView(textEntryView);
		final AlertDialog dialog = builder.show();
		dialog.getWindow().setGravity(Gravity.CENTER);
		dialog.getWindow().setLayout(
				android.view.WindowManager.LayoutParams.WRAP_CONTENT,
				android.view.WindowManager.LayoutParams.WRAP_CONTENT);

		myButton_01 = (ToggleButton) textEntryView.findViewById(R.id.button_01);
		// myButton_01.setBackgroundColor(Color.RED);
		mColorMap.put(R.id.button_01, 0xFFFFFFCC);
		myButton_01.setOnClickListener(new myColorOnClickListener());

		myButton_02 = (ToggleButton) textEntryView.findViewById(R.id.button_02);
		mColorMap.put(R.id.button_02, 0xFF66FFCC);
		myButton_02.setOnClickListener(new myColorOnClickListener());

		myButton_03 = (ToggleButton) textEntryView.findViewById(R.id.button_03);
		mColorMap.put(R.id.button_03, 0xFFCCFFFF);
		myButton_03.setOnClickListener(new myColorOnClickListener());

		myButton_04 = (ToggleButton) textEntryView.findViewById(R.id.button_04);
		mColorMap.put(R.id.button_04, 0xFFFFCCFF);
		myButton_04.setOnClickListener(new myColorOnClickListener());

		myButton_05 = (ToggleButton) textEntryView.findViewById(R.id.button_05);
		mColorMap.put(R.id.button_05, 0xFF0099FF);
		myButton_05.setOnClickListener(new myColorOnClickListener());

		myButton_06 = (ToggleButton) textEntryView.findViewById(R.id.button_06);
		mColorMap.put(R.id.button_06, 0xFFFF9966);
		myButton_06.setOnClickListener(new myColorOnClickListener());

		myButton_07 = (ToggleButton) textEntryView.findViewById(R.id.button_07);
		mColorMap.put(R.id.button_07, 0xFFFFFF99);
		myButton_07.setOnClickListener(new myColorOnClickListener());

		myButton_08 = (ToggleButton) textEntryView.findViewById(R.id.button_08);
		mColorMap.put(R.id.button_08, 0xFF33FF99);
		myButton_08.setOnClickListener(new myColorOnClickListener());

		myButton_09 = (ToggleButton) textEntryView.findViewById(R.id.button_09);
		mColorMap.put(R.id.button_09, 0xFF99FFFF);
		myButton_09.setOnClickListener(new myColorOnClickListener());

		myButton_10 = (ToggleButton) textEntryView.findViewById(R.id.button_10);
		mColorMap.put(R.id.button_10, 0xFFFF99FF);
		myButton_10.setOnClickListener(new myColorOnClickListener());

		myButton_11 = (ToggleButton) textEntryView.findViewById(R.id.button_11);
		mColorMap.put(R.id.button_11, 0xFF0066FF);
		myButton_11.setOnClickListener(new myColorOnClickListener());

		myButton_12 = (ToggleButton) textEntryView.findViewById(R.id.button_12);
		mColorMap.put(R.id.button_12, 0xFFFF6633);
		myButton_12.setOnClickListener(new myColorOnClickListener());

		myButton_13 = (ToggleButton) textEntryView.findViewById(R.id.button_13);
		mColorMap.put(R.id.button_13, 0xFFFFFF00);
		myButton_13.setOnClickListener(new myColorOnClickListener());

		myButton_14 = (ToggleButton) textEntryView.findViewById(R.id.button_14);
		mColorMap.put(R.id.button_14, 0xFF00FF00);
		myButton_14.setOnClickListener(new myColorOnClickListener());

		myButton_15 = (ToggleButton) textEntryView.findViewById(R.id.button_15);
		mColorMap.put(R.id.button_15, 0xFF00FFFF);
		myButton_15.setOnClickListener(new myColorOnClickListener());

		myButton_16 = (ToggleButton) textEntryView.findViewById(R.id.button_16);
		mColorMap.put(R.id.button_16, 0xFFFF66FF);
		myButton_16.setOnClickListener(new myColorOnClickListener());

		myButton_17 = (ToggleButton) textEntryView.findViewById(R.id.button_17);
		mColorMap.put(R.id.button_17, 0xFF0000FF);
		myButton_17.setOnClickListener(new myColorOnClickListener());

		myButton_18 = (ToggleButton) textEntryView.findViewById(R.id.button_18);
		mColorMap.put(R.id.button_18, 0xFFFF3300);
		myButton_18.setOnClickListener(new myColorOnClickListener());

		myButton_19 = (ToggleButton) textEntryView.findViewById(R.id.button_19);
		mColorMap.put(R.id.button_19, 0xFFCCCC00);
		myButton_19.setOnClickListener(new myColorOnClickListener());

		myButton_20 = (ToggleButton) textEntryView.findViewById(R.id.button_20);
		mColorMap.put(R.id.button_20, 0xFF009900);
		myButton_20.setOnClickListener(new myColorOnClickListener());

		myButton_21 = (ToggleButton) textEntryView.findViewById(R.id.button_21);
		mColorMap.put(R.id.button_21, 0xFF00CCFF);
		myButton_21.setOnClickListener(new myColorOnClickListener());

		myButton_22 = (ToggleButton) textEntryView.findViewById(R.id.button_22);
		mColorMap.put(R.id.button_22, 0xFFCC33FF);
		myButton_22.setOnClickListener(new myColorOnClickListener());

		myButton_23 = (ToggleButton) textEntryView.findViewById(R.id.button_23);
		mColorMap.put(R.id.button_23, 0xFF0000CC);
		myButton_23.setOnClickListener(new myColorOnClickListener());

		myButton_24 = (ToggleButton) textEntryView.findViewById(R.id.button_24);
		mColorMap.put(R.id.button_24, 0xFFCC3300);
		myButton_24.setOnClickListener(new myColorOnClickListener());

		myButton_25 = (ToggleButton) textEntryView.findViewById(R.id.button_25);
		mColorMap.put(R.id.button_25, 0xFF999900);
		myButton_25.setOnClickListener(new myColorOnClickListener());

		myButton_26 = (ToggleButton) textEntryView.findViewById(R.id.button_26);
		mColorMap.put(R.id.button_26, 0xFF006633);
		myButton_26.setOnClickListener(new myColorOnClickListener());

		myButton_27 = (ToggleButton) textEntryView.findViewById(R.id.button_27);
		mColorMap.put(R.id.button_27, 0xFF0099FF);
		myButton_27.setOnClickListener(new myColorOnClickListener());

		myButton_28 = (ToggleButton) textEntryView.findViewById(R.id.button_28);
		mColorMap.put(R.id.button_28, Color.rgb(166, 0, 221));// ԭ�е���ɫ����
		myButton_28.setOnClickListener(new myColorOnClickListener());

		myButton_29 = (ToggleButton) textEntryView.findViewById(R.id.button_29);
		mColorMap.put(R.id.button_29, 0xFF000066);
		myButton_29.setOnClickListener(new myColorOnClickListener());

		myButton_30 = (ToggleButton) textEntryView.findViewById(R.id.button_30);
		mColorMap.put(R.id.button_30, 0xFF990000);
		myButton_30.setOnClickListener(new myColorOnClickListener());

		myButton_31 = (ToggleButton) textEntryView.findViewById(R.id.button_31);
		mColorMap.put(R.id.button_31, 0xFFFFFFFF);
		myButton_31.setOnClickListener(new myColorOnClickListener());

		myButton_32 = (ToggleButton) textEntryView.findViewById(R.id.button_32);
		mColorMap.put(R.id.button_32, 0xFFCCCCCC);
		myButton_32.setOnClickListener(new myColorOnClickListener());

		myButton_33 = (ToggleButton) textEntryView.findViewById(R.id.button_33);
		mColorMap.put(R.id.button_33, 0xFF999999);
		myButton_33.setOnClickListener(new myColorOnClickListener());

		myButton_34 = (ToggleButton) textEntryView.findViewById(R.id.button_34);
		mColorMap.put(R.id.button_34, 0xFF666666);
		myButton_34.setOnClickListener(new myColorOnClickListener());

		myButton_35 = (ToggleButton) textEntryView.findViewById(R.id.button_35);
		mColorMap.put(R.id.button_35, 0xFF333333);
		myButton_35.setOnClickListener(new myColorOnClickListener());

		myButton_36 = (ToggleButton) textEntryView.findViewById(R.id.button_36);
		mColorMap.put(R.id.button_36, 0xFF000000);
		myButton_36.setOnClickListener(new myColorOnClickListener());

		myCancel = (Button) textEntryView.findViewById(R.id.cancel);

		myConfirm = (Button) textEntryView.findViewById(R.id.confirm);
		myConfirm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				MyConfig.setNewColor(ImgMainActivity.this, mColor);
				if ((!m_strWaterMarkInfo.isEmpty())
						&& (mFirstLoadedImg != null)) {
					mCurrentImg = ImageProcessor.createFinalBitmap(
							ImgMainActivity.this, mFirstLoadedImg,
							m_strWaterMarkInfo);
					mImageView.setImageBitmap(mCurrentImg);
					mIsImgUpdated = true;
				}
				if (dialog != null) {
					dialog.dismiss();
				}
			}
		});

		myCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (dialog != null) {
					dialog.dismiss();
				}
			}
		});

		setColorCheckedButton(MyConfig.getNewColor(ImgMainActivity.this));

	}

	class myColorOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			changeColor(v);

		}
	}

	private void setColorCheckedButton(int position) {
		Iterator iter = mColorMap.entrySet().iterator();
		int key;
		int val;
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			key = (Integer) entry.getKey();
			val = (Integer) entry.getValue();
			if (val == position) {
				setViewChecked(myButton_01, key);
				setViewChecked(myButton_02, key);
				setViewChecked(myButton_03, key);
				setViewChecked(myButton_04, key);
				setViewChecked(myButton_05, key);
				setViewChecked(myButton_06, key);
				setViewChecked(myButton_07, key);
				setViewChecked(myButton_08, key);
				setViewChecked(myButton_09, key);
				setViewChecked(myButton_10, key);
				setViewChecked(myButton_11, key);
				setViewChecked(myButton_12, key);
				setViewChecked(myButton_13, key);
				setViewChecked(myButton_14, key);
				setViewChecked(myButton_15, key);
				setViewChecked(myButton_16, key);
				setViewChecked(myButton_17, key);
				setViewChecked(myButton_18, key);
				setViewChecked(myButton_19, key);
				setViewChecked(myButton_20, key);
				setViewChecked(myButton_21, key);
				setViewChecked(myButton_22, key);
				setViewChecked(myButton_23, key);
				setViewChecked(myButton_24, key);
				setViewChecked(myButton_25, key);
				setViewChecked(myButton_26, key);
				setViewChecked(myButton_27, key);
				setViewChecked(myButton_28, key);
				setViewChecked(myButton_29, key);
				setViewChecked(myButton_30, key);
				setViewChecked(myButton_31, key);
				setViewChecked(myButton_32, key);
				setViewChecked(myButton_33, key);
				setViewChecked(myButton_34, key);
				setViewChecked(myButton_35, key);
				setViewChecked(myButton_36, key);
				break;
			}
		}
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

	/*************************************************************
	 * 以下代碼為位置設置 ***********************************************************
	 */

	private ToggleButton myButton_011 = null;
	private ToggleButton myButton_021 = null;
	private ToggleButton myButton_031 = null;
	private ToggleButton myButton_041 = null;
	private ToggleButton myButton_051 = null;
	private ToggleButton myButton_061 = null;
	private ToggleButton myButton_071 = null;
	private ToggleButton myButton_081 = null;
	private ToggleButton myButton_091 = null;

	private Button myCancel1 = null;
	private Button myConfirm1 = null;

	int mPosIndex = 8;// 0-8,
	private HashMap<Integer, Integer> mColorMap1 = new HashMap<Integer, Integer>();

	private void showShezhiDialog() {

		LayoutInflater inflater = LayoutInflater.from(mContext);
		final View textEntryView = inflater.inflate(R.layout.position_selector,
				null);
		final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setCancelable(true);

		builder.setView(textEntryView);
		final AlertDialog dialog = builder.show();
		dialog.getWindow().setGravity(Gravity.CENTER);
		dialog.getWindow().setLayout(
				android.view.WindowManager.LayoutParams.WRAP_CONTENT,
				android.view.WindowManager.LayoutParams.WRAP_CONTENT);

		myButton_011 = (ToggleButton) textEntryView
				.findViewById(R.id.button_01);
		mColorMap1.put(R.id.button_01, 0);
		myButton_011.setOnClickListener(new MyShezhiOnClickListener());
		myButton_021 = (ToggleButton) textEntryView
				.findViewById(R.id.button_02);
		mColorMap1.put(R.id.button_02, 1);
		myButton_021.setOnClickListener(new MyShezhiOnClickListener());
		myButton_031 = (ToggleButton) textEntryView
				.findViewById(R.id.button_03);
		mColorMap1.put(R.id.button_03, 2);
		myButton_031.setOnClickListener(new MyShezhiOnClickListener());
		myButton_041 = (ToggleButton) textEntryView
				.findViewById(R.id.button_04);
		mColorMap1.put(R.id.button_04, 3);
		myButton_041.setOnClickListener(new MyShezhiOnClickListener());
		myButton_051 = (ToggleButton) textEntryView
				.findViewById(R.id.button_05);
		mColorMap1.put(R.id.button_05, 4);
		myButton_051.setOnClickListener(new MyShezhiOnClickListener());
		myButton_061 = (ToggleButton) textEntryView
				.findViewById(R.id.button_06);
		mColorMap1.put(R.id.button_06, 5);
		myButton_061.setOnClickListener(new MyShezhiOnClickListener());
		myButton_071 = (ToggleButton) textEntryView
				.findViewById(R.id.button_07);
		mColorMap1.put(R.id.button_07, 6);
		myButton_071.setOnClickListener(new MyShezhiOnClickListener());
		myButton_081 = (ToggleButton) textEntryView
				.findViewById(R.id.button_08);
		mColorMap1.put(R.id.button_08, 7);
		myButton_081.setOnClickListener(new MyShezhiOnClickListener());
		myButton_091 = (ToggleButton) textEntryView
				.findViewById(R.id.button_09);
		mColorMap1.put(R.id.button_09, 8);
		myButton_091.setOnClickListener(new MyShezhiOnClickListener());
		myCancel1 = (Button) textEntryView.findViewById(R.id.cancel);
		myCancel1.setOnClickListener(new MyShezhiOnClickListener());
		myConfirm1 = (Button) textEntryView.findViewById(R.id.confirm);
		myConfirm1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				MyConfig.setNewPosition(ImgMainActivity.this, mPosIndex);
				if ((!m_strWaterMarkInfo.isEmpty())
						&& (mFirstLoadedImg != null)) {
					mCurrentImg = ImageProcessor.createFinalBitmap(
							ImgMainActivity.this, mFirstLoadedImg,
							m_strWaterMarkInfo);
					mImageView.setImageBitmap(mCurrentImg);
					mIsImgUpdated = true;
				}

				if (dialog != null) {
					dialog.dismiss();
				}
			}
		});

		myCancel1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (dialog != null) {
					dialog.dismiss();
				}
			}
		});

		setCheckedButton(MyConfig.getNewPosition(ImgMainActivity.this));

	}

	private void setCheckedButton(int position) {
		Iterator iter = mColorMap1.entrySet().iterator();
		int key;
		int val;
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			key = (Integer) entry.getKey();
			val = (Integer) entry.getValue();
			if (val == position) {
				setViewChecked(myButton_011, key);
				setViewChecked(myButton_021, key);
				setViewChecked(myButton_031, key);
				setViewChecked(myButton_041, key);
				setViewChecked(myButton_051, key);
				setViewChecked(myButton_061, key);
				setViewChecked(myButton_071, key);
				setViewChecked(myButton_081, key);
				setViewChecked(myButton_091, key);
				break;
			}
		}
	}

	class MyShezhiOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View arg0) {
			changePos(arg0);
		}
	}

	private void setViewChecked(View v, int id) {
		if (v.getId() == id) {
			((ToggleButton) v).setChecked(true);
		} else {
			((ToggleButton) v).setChecked(false);
		}
	}

	private void changePos(View v) {
		myButton_011.setChecked(false);
		myButton_021.setChecked(false);
		myButton_031.setChecked(false);
		myButton_041.setChecked(false);
		myButton_051.setChecked(false);
		myButton_061.setChecked(false);
		myButton_071.setChecked(false);
		myButton_081.setChecked(false);
		myButton_091.setChecked(false);

		((ToggleButton) v).setChecked(true);
		mPosIndex = mColorMap1.get(v.getId());
	}

}
