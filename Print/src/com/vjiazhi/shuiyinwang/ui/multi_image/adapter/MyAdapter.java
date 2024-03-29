package com.vjiazhi.shuiyinwang.ui.multi_image.adapter;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import com.vjiazhi.yinji.R;
import com.vjiazhi.shuiyinwang.ui.multi_image.ViewHolder;

public class MyAdapter extends CommonAdapter<String> {

	/**
	 * 用户选择的图片，存储为图片的完整路径
	 */
	public static List<String> mSelectedImage = new LinkedList<String>();

	/**
	 * 文件夹路径
	 */
	private String mDirPath;

	/**
	 * set msg handler fengyi add
	 */
	public interface AdapterItemListener {
		void onAdapterItemClick(int mSelectedImageNum);

		void onCameraEnter();
	}

	AdapterItemListener adapterItemListener;

	public AdapterItemListener getAdapterItemListener() {
		return adapterItemListener;
	}

	public void setAdapterItemListener(AdapterItemListener adapterItemListener) {
		this.adapterItemListener = adapterItemListener;
	}

	public MyAdapter(Context context, List<String> mDatas, int itemLayoutId,
			String dirPath) {
		super(context, mDatas, itemLayoutId);
		this.mDirPath = dirPath;
	}

	@Override
	public void convert(final ViewHolder helper, final String item) {
		// 设置no_pic
		helper.setImageResource(R.id.id_item_image, R.drawable.pictures_no);
		// 设置no_selected
//		helper.setImageResource(R.id.id_item_select,
//				R.drawable.pics_unselected);

		String tempFileString = "";
		if (mDirPath.equalsIgnoreCase("")) {
			tempFileString = item;
		} else {
			tempFileString = mDirPath + "/" + item;
		}
		final String dirFileString=tempFileString;
		// 设置图片
		if (item.equals("capture")) {
			helper.setImageResource(R.id.id_item_image, R.drawable.ic_capture);
		} else {

			helper.setImageByUrl(R.id.id_item_image, dirFileString);
		}

		final ImageView mImageView = helper.getView(R.id.id_item_image);
		final ImageView mSelect = helper.getView(R.id.id_item_select);

		if (mSelectedImage.contains(dirFileString)) {
			mSelect.setBackgroundResource(R.drawable.pics_selected);
			mImageView.setColorFilter(Color.parseColor("#77000000"));
		} else {
			mSelect.setBackgroundResource(R.drawable.pics_unselected);
		}

		mImageView.setColorFilter(null);
		// 设置ImageView的点击事件
		mImageView.setOnClickListener(new OnClickListener() {
			// 选择，则将图片变暗，反之则反之
			@Override
			public void onClick(View v) {
				if (item.equals("capture")) {
					adapterItemListener.onCameraEnter();
					return;
				}
				
				if (mSelectedImage.contains(dirFileString)) {
					mSelectedImage.remove(dirFileString);
					mSelect.setBackgroundResource(R.drawable.pics_unselected);
					mImageView.setColorFilter(null);
				} else
				{
					mSelectedImage.add(dirFileString);
					mSelect.setBackgroundResource(R.drawable.pics_selected);
					mImageView.setColorFilter(Color.parseColor("#77000000"));
				}
				if (adapterItemListener != null) {
					adapterItemListener.onAdapterItemClick(mSelectedImage
							.size());
				}
			}
		});

		/**
		 * 已经选择过的图片，显示出选择过的效果
		 */
		if (mSelectedImage.contains(mDirPath + "/" + item)) {
			mSelect.setBackgroundResource(R.drawable.pics_selected);
			mImageView.setColorFilter(Color.parseColor("#77000000"));
		}
	}
}
