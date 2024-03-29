﻿package com.vjiazhi.shuiyinwang.utils;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

//里面的具体参数，待调试稳定后使用宏定义

public class ImageProcessor {

	final static int WATERMARK1_X = 0;
	final static int WATERMARK1_Y = 30;
	final static float WATERMARK_FONT_SIZE_BASE = 50;// 图片宽度为800时使用字体尺寸40比较合适
	final static float WATERMARK_SRC_WIDTH_BASE = 800;// 原始图片的参考尺寸
	final static String WATERMARK_FONT_NAME = "宋体";
	static float mWaterMarkFontSize = WATERMARK_FONT_SIZE_BASE;
	final static int WATERMARK_EXTRA_EDGE = 15; // 水印图像，基于文字尺寸再加上一些余量
	static int mPaddingToEdge = 15; // 页边距性质

	// 入口参数：src代表源图像，strText为水印文字，isUser是否用户的水印（默认为false）
	// 出口参数：合成后的图像
	public static Bitmap createFinalBitmap(Context context,Bitmap src, String strText) {

		if (src == null) {
			return null;
		}
		int nSrcwidth = src.getWidth();
		int nSrcHeight = src.getHeight();

		Paint p = new Paint();
		Rect rect = new Rect();

		// 根据原始图片的尺寸，自动调整字体大小，使得比例相对协调一些（图片小字体就小）
		mWaterMarkFontSize = WATERMARK_FONT_SIZE_BASE * nSrcwidth
				/ WATERMARK_SRC_WIDTH_BASE;

		float fTextSize = (float) (mWaterMarkFontSize * MyConfig.getNewSize() / 100.0);
		String familyName = WATERMARK_FONT_NAME;
		Typeface font = Typeface.create(familyName, Typeface.NORMAL);
		/*
		 * p.setColor(Color.rgb(200, 200, 200));//浅灰色（接近白色） p.setTypeface(font);
		 * p.setTextSize(WATERMARK_FONT_SIZE); canvasTemp.drawText(strText,
		 * WATERMARK1_X, WATERMARK1_Y, p);
		 */
		p.setColor(MyConfig.getNewColor(context));
		p.setTypeface(font);
		p.setTextSize(fTextSize);

		// 返回包围整个字符串的最小的一个Rect区域，这里length是否合适？
		p.getTextBounds(strText, 0, strText.length(), rect);

		// 后续可能需要描边
		int nMarkwidth = rect.width(); // 原先是(int)(nSrcwidth*9.0/10.0);
		int nMarkHeight = rect.height(); // 原先是(int)(nSrcHeight*1.0/10.0);

		// 乘以缩放比例系数,加上余量（2）
		nMarkwidth = (int) (nMarkwidth * MyConfig.getNewSize() / 100.0)
				+ WATERMARK_EXTRA_EDGE;
		nMarkHeight = (int) (nMarkHeight * MyConfig.getNewSize() / 100.0)
				+ WATERMARK_EXTRA_EDGE;

		Bitmap bmpWaterMark = Bitmap.createBitmap(nMarkwidth, nMarkHeight,
				Config.ARGB_8888);
		Canvas canvasTemp = new Canvas(bmpWaterMark);

		// canvasTemp.saveLayerAlpha(0, 0, w, h, 0x99, LAYER_FLAGS);
		canvasTemp.drawColor(Color.TRANSPARENT);
		// canvasTemp.restore();

		canvasTemp.drawText(strText, 0, nMarkHeight - WATERMARK_EXTRA_EDGE, p);
		// 水印图像创建end

		
		//加一层白色背景
		p.setColor(Color.DKGRAY);
		float fShadeSize =  mWaterMarkFontSize/20; //默认参考值2.5
		canvasTemp.drawText(strText,fShadeSize, nMarkHeight - WATERMARK_EXTRA_EDGE +fShadeSize, p);
		
		// create the new blank bitmap
		Bitmap newb = Bitmap.createBitmap(nSrcwidth, nSrcHeight,
				Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
		Canvas cv = new Canvas(newb);

		// draw src into
		cv.drawBitmap(src, 0, 0, null);// 在 0，0坐标开始画入src

		int nWatermarkLeft, nWatermarkTop;

		int nPos = MyConfig.getNewPosition(context);// 0-8 对应9个位置

		switch (nPos) {
		case 0:
			nWatermarkLeft = mPaddingToEdge;
			nWatermarkTop = mPaddingToEdge;
			break;
		case 1:
			nWatermarkLeft = (int) (nSrcwidth / 2 - nMarkwidth / 2);
			nWatermarkTop = mPaddingToEdge;
			break;
		case 2:
			nWatermarkLeft = (int) (nSrcwidth - nMarkwidth) - mPaddingToEdge;
			nWatermarkTop = mPaddingToEdge;
			break;
		case 3:
			nWatermarkLeft = mPaddingToEdge;
			nWatermarkTop = nSrcHeight / 2 - nMarkHeight / 2;
			break;
		case 4:
			nWatermarkLeft = (int) (nSrcwidth / 2 - nMarkwidth / 2);
			nWatermarkTop = nSrcHeight / 2 - nMarkHeight / 2;
			break;
		case 5:
			nWatermarkLeft = (int) (nSrcwidth - nMarkwidth) - mPaddingToEdge;
			nWatermarkTop = nSrcHeight / 2 - nMarkHeight;
			break;
		case 6:
			nWatermarkLeft = mPaddingToEdge;
			nWatermarkTop = nSrcHeight - nMarkHeight - mPaddingToEdge;
			break;
		case 7:
			nWatermarkLeft = (int) (nSrcwidth / 2 - nMarkwidth / 2);
			nWatermarkTop = nSrcHeight - nMarkHeight - mPaddingToEdge;
			break;
		case 8:
		default:
			nWatermarkLeft = (int) (nSrcwidth - nMarkwidth) - mPaddingToEdge;
			nWatermarkTop = nSrcHeight - nMarkHeight - mPaddingToEdge;
			break;
		}

		cv.drawBitmap(bmpWaterMark, nWatermarkLeft, nWatermarkTop, null);// 在src的右下角画入水印
		cv.save(Canvas.ALL_SAVE_FLAG);// 保存
		cv.restore();// 存储

		if (bmpWaterMark != null && !bmpWaterMark.isRecycled()) {
			bmpWaterMark.recycle();
			System.gc();
		}

		return newb;
	}

	/*
	 * 下面的代码是为了利用BitmapFactory.decodeStream方法压缩图片，节省内存 // 将Bitmap转换成InputStream
	 * public InputStream Bitmap2InputStream(Bitmap bm, int quality) {
	 * ByteArrayOutputStream baos = new ByteArrayOutputStream();
	 * bm.compress(Bitmap.CompressFormat.PNG, quality, baos); InputStream is =
	 * new ByteArrayInputStream(baos.toByteArray()); return is; }
	 * 
	 * // 以最省内存的方式读取图片 / public static Bitmap readBitMap(InputStream is) {
	 * BitmapFactory.Options opt = new BitmapFactory.Options();
	 * opt.inPreferredConfig = Bitmap.Config.RGB_565; opt.inPurgeable = true;
	 * opt.inInputShareable = true;
	 * 
	 * // 解码图片 return BitmapFactory.decodeStream(is, null, opt); }
	 */
}
