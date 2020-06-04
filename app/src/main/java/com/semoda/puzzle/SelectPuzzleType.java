/*
 * com.zeronz.puzzle.SelectPuzzleType
 *
 * Created on 2011. 4. 4.
 * 
 * Copyright (c) 2009-2011 zerOnz Co., Ltd. All Rights Reserved.
 */
package com.semoda.puzzle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * 
 * SelectPuzzleType Class
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
public class SelectPuzzleType extends Activity implements OnClickListener{
    
	private int 	mImageResID;
	private int 	mImageType;
	private String 	mImagePath;
	
	/* Menu Item ID */
	private final int 		IMAGE_MENU 	= 0;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_type);
        
        mImageType = getIntent().getIntExtra("imageType", Const.IMAGE_TYPE_DEFAULT);
        mImageResID = getIntent().getIntExtra("resID", R.drawable.doeun_1);
        mImagePath = getIntent().getStringExtra("imagePath");
        
        Button btn_3x3 = (Button)findViewById(R.id.btn_3x3);
        btn_3x3.setOnClickListener(this);
        Button btn_4x4 = (Button)findViewById(R.id.btn_4x4);
        btn_4x4.setOnClickListener(this);
        Button btn_5x5 = (Button)findViewById(R.id.btn_5x5);
        btn_5x5.setOnClickListener(this);
    }

	public void onClick(View v) {
		Intent intent = new Intent(this, Puzzle.class);
		
		switch(v.getId()){
		case R.id.btn_3x3:
			switch(mImageType){
			case Const.IMAGE_TYPE_GALLERY:
				intent.putExtra("imagePath", mImagePath);
				break;
			case Const.IMAGE_TYPE_DEFAULT:
				intent.putExtra("resID", mImageResID);
				break;
			case Const.IMAGE_TYPE_NUMBER:
				intent.putExtra("resID", R.drawable.num3);
				break;
			}
			
			intent.putExtra("type", 3);
			break;
			
		case R.id.btn_4x4:
			switch(mImageType){
			case Const.IMAGE_TYPE_GALLERY:
				intent.putExtra("imagePath", mImagePath);
				break;
			case Const.IMAGE_TYPE_DEFAULT:
				intent.putExtra("resID", mImageResID);
				break;
			case Const.IMAGE_TYPE_NUMBER:
				intent.putExtra("resID", R.drawable.num4);
				break;
			}
			intent.putExtra("type", 4);
			break;
			
		case R.id.btn_5x5:
			switch(mImageType){
			case Const.IMAGE_TYPE_GALLERY:
				intent.putExtra("imagePath", mImagePath);
				break;
			case Const.IMAGE_TYPE_DEFAULT:
				intent.putExtra("resID", mImageResID);
				break;
			case Const.IMAGE_TYPE_NUMBER:
				intent.putExtra("resID", R.drawable.num5);
				break;
			}
			intent.putExtra("type", 5);
			break;
		}
		
		intent.putExtra("imageType", mImageType);
		startActivity(intent);
		finish();
	}
	
	/**
	 * 롤업메뉴 세팅
	 */
	public boolean onCreateOptionsMenu(Menu menu) {

		boolean result = super.onCreateOptionsMenu(menu);
	
		menu.add(0, IMAGE_MENU, Menu.NONE, "Select Image");
		return result;
	}
	
	/**
	 * 롤업메뉴 클릭 이벤트 
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch(item.getItemId()){
		
		case IMAGE_MENU:
			intent = new Intent(this, SelectPuzzleImage.class);
			startActivity(intent);
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * back key 처리
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ( event.getRepeatCount() == 0 ) {
			if (keyCode == KeyEvent.KEYCODE_BACK){
				Intent intent = new Intent(this, SelectPuzzleImage.class);
				startActivity(intent);
				finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
