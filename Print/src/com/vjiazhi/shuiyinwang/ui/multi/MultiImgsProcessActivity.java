package com.vjiazhi.shuiyinwang.ui.multi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.vjiazhi.yinji.R;
import com.vjiazhi.shuiyinwang.ui.ImgMainActivity;
import com.vjiazhi.shuiyinwang.ui.multi_image.ImageLoader;
import com.vjiazhi.shuiyinwang.ui.multi_image.adapter.MyAdapter;
import com.vjiazhi.shuiyinwang.utils.ImageProcessor;
import com.vjiazhi.shuiyinwang.utils.ImgFileUtils;

public class MultiImgsProcessActivity extends Activity {
	ListView listView;
	ArrayList<String> listfile = new ArrayList<String>();
	ArrayList<String> listfileOut = new ArrayList<String>();

	/*
	 * fengyi add image compress quality percent set 50% for temporary
	 */
	private final static int IMAGE_QUALITY_PERCENT = 50;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_multi_process);
		listView = (ListView) findViewById(R.id.listView1);
		Bundle bundle = getIntent().getExtras();

		// if (bundle != null) {
		// if (bundle.getStringArrayList("files") != null) {
		// / listfile = bundle.getStringArrayList("files");
		// listView.setVisibility(View.VISIBLE);
		// ArrayAdapter<String> arryAdapter = new ArrayAdapter<String>(
		// this, android.R.layout.simple_list_item_1, listfile);
		// listView.setAdapter(arryAdapter);
		// start processtask
		new MyImgsProcessTask(this).execute(MyAdapter.mSelectedImage);
		// combineSavedFileName(listfile.get(0));
		// }
		// }

	}

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

				savedImg = ImageProcessor.createFinalBitmap(
						MultiImgsProcessActivity.this, loadedImg,
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
		listView.setVisibility(View.VISIBLE);
		ArrayAdapter<String> arryAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, listfileOut);
		listView.setAdapter(arryAdapter);
	}
}
