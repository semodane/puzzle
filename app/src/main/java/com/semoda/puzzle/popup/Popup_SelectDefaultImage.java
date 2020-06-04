/*
 * com.zeronz.puzzle.popup.Popup_SelectDefaultImage
 *
 * Created on 2011. 4. 4.
 * 
 * Copyright (c) 2009-2011 zerOnz Co., Ltd. All Rights Reserved.
 */
package com.semoda.puzzle.popup;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.semoda.puzzle.R;

/**
 * 
 * Popup SelectDefaultImage Class
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
public class Popup_SelectDefaultImage extends RelativeLayout{

	private	Context		mContext = null;
	private	PopupWindow mPopview;
	
	public Popup_SelectDefaultImage(Context context) {
		super(context);
		mContext = context;
		Init();
	}
	
	/**
	 * 화면 구성
	 */
	private void Init(){
		this.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		this.setBackgroundColor(Color.TRANSPARENT);
		this.setGravity(Gravity.CENTER);
		this.setPadding(20, 20, 20, 20);
		
		LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = vi.inflate(R.layout.select_image, null);
		
		this.addView(view);
	}
	
	/**
	 * 팝업종료를 위한 PopupWindow 세팅
	 * 
	 * @param popview
	 */
	public void setPopupWindow(PopupWindow popview){
		mPopview = popview;
	}
	
	/**
	 * Back key 종료처리
	 */
	public boolean dispatchKeyEvent(KeyEvent event) {
		
		if ( event.getRepeatCount() == 0 ) {
			if (event.getKeyCode() == KeyEvent.KEYCODE_BACK){ 
				mPopview.dismiss();
			}
		}
		return super.dispatchKeyEvent(event);
	}
}
