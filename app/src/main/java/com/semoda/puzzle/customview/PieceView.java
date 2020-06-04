/*
 * com.zeronz.puzzle.customview.PieceView
 *
 * Created on 2011. 4. 4.
 * 
 * Copyright (c) 2009-2011 zerOnz Co., Ltd. All Rights Reserved.
 */
package com.semoda.puzzle.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.appcompat.widget.AppCompatImageView;

import com.semoda.puzzle.R;

/**
 * 
 * PieceView
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
public class PieceView extends AppCompatImageView {
	
	private Context 		mContext;
	/** 최초의 위치인덱스 (완료상태의 위치) */
	private int	 			mOriLocIdx;
	/** 현재의 위치인덱스  */
	private int	 			mImgIdx;
	
	private Bitmap 			mImage;
	
	public PieceView(Context context, Bitmap bImage, int locIdx) {
		super(context);
		mContext = context;
		mOriLocIdx = mImgIdx = locIdx;
		setImage(bImage);
	}
	
	/**
	 * 이미지  인덱스 입력
	 * @param locIdx
	 */
	public void setImageIdx(int locIdx){
		mImgIdx  = locIdx;
	}
	
	/**
	 * 이미지 인덱스 반환
	 * @return
	 */
	public int getImageIdx(){
		return mImgIdx;
	}
	
	/**
	 * 위치인덱스 반환
	 * @return
	 */
	public int getLocationIdx(){
		return mOriLocIdx;
	}

	/**
	 * 이미지세팅 
	 * @param bImage
	 */
	public void setImage(Bitmap bImage){
		
		mImage = bImage;
		
		if(bImage == null){
			setBackgroundColor(Color.TRANSPARENT);
			setImageBitmap(null);
		}else{
			setBackgroundResource(R.drawable.image_border); //테두리 선 세팅 
			setImageBitmap(bImage);
		}
		
	}
	
	/**
	 * 뷰의 현재 이미지 반환
	 * @return Bitmap mImage
	 */
	public Bitmap getImage(){
		return mImage;
	}
	
	/**
	 * 현재 위치가 최초의 위치인지 확인
	 * @return boolean 
	 */
	public boolean getOriPosition(){
		if(mOriLocIdx == mImgIdx){
			return true;
		}
		return false;
	}
}
