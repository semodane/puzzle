/*
 * com.zeronz.puzzle.utils.ImageUtils
 *
 * Created on 2011. 4. 4.
 * 
 * Copyright (c) 2009-2011 zerOnz Co., Ltd. All Rights Reserved.
 */
package com.semoda.puzzle.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * 
 * ImageUtils Class
 * 
 * Create Date 2011. 4. 4.
 * @version	1.00 2011. 4. 4.
 * @since   1.00
 * @see
 * @author	jylee(jylee@zeronz.com)
 * Revision History
 * who          when         	what
 * jylee                2011. 4. 4.    최초 작성
 */
public class ImageUtils {
	
	/**
	 * 사진 컨텐츠 Path를 받아 Bitmap을 생성 반환
	 * @param context
	 * @param imagePath
	 * @return Bitmap
	 */
	public static Bitmap getDrawableFromUri(Context context, String imagePath) {
		Bitmap bitmap = null;
		BitmapFactory.Options option = new BitmapFactory.Options();
		option.inSampleSize = 4;
		Bitmap src = BitmapFactory.decodeFile(imagePath, option);
		bitmap = Bitmap.createScaledBitmap(src, 480, 800, true);

		src = null;
		return bitmap;
	}
}
