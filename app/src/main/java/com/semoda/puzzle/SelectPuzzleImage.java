/*
 * com.zeronz.puzzle.SelectPuzzleImage
 *
 * Created on 2011. 4. 4.
 * 
 * Copyright (c) 2009-2011 zerOnz Co., Ltd. All Rights Reserved.
 */
package com.semoda.puzzle;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.semoda.puzzle.popup.Popup_SelectDefaultImage;

/**
 *
 * SelectPuzzleImage Class 
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
public class SelectPuzzleImage extends Activity implements OnClickListener{

	private PopupWindow 	mPopup;
	private LinearLayout 	mImgSelLayout;

	private final int 		REQUEST_CODE_GALLERY = 10001;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_image_type);

		mImgSelLayout = (LinearLayout)findViewById(R.id.imgSelLayout);
		mImgSelLayout.setBackgroundResource(R.drawable.ssss);

		Button btn_gallery = (Button)findViewById(R.id.btn_gallery);
		btn_gallery.setOnClickListener(this);
		Button btn_default_img = (Button)findViewById(R.id.btn_default_img);
		btn_default_img.setOnClickListener(this);
		Button btn_number = (Button)findViewById(R.id.btn_number);
		btn_number.setOnClickListener(this);

	}

	public void onClick(View v) {

		Intent intent;
		switch(v.getId()){
			case R.id.btn_gallery: // 갤러리 이미지 선택


				intent = new Intent(Intent.ACTION_PICK);
				intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
				intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(intent, REQUEST_CODE_GALLERY);

				break;

			case R.id.btn_default_img: // 기본이미지로 선택

				Popup_SelectDefaultImage popview = new Popup_SelectDefaultImage(this);

				LinearLayout imagelayout_1 = (LinearLayout)popview.findViewById(R.id.imagelayout_1);
				imagelayout_1.setOnClickListener(this);
				LinearLayout imagelayout_2 = (LinearLayout)popview.findViewById(R.id.imagelayout_2);
				imagelayout_2.setOnClickListener(this);
				LinearLayout imagelayout_3 = (LinearLayout)popview.findViewById(R.id.imagelayout_3);
				imagelayout_3.setOnClickListener(this);
				LinearLayout imagelayout_4 = (LinearLayout)popview.findViewById(R.id.imagelayout_4);
				imagelayout_4.setOnClickListener(this);

				mPopup = new PopupWindow(popview, mImgSelLayout.getWidth(), mImgSelLayout.getHeight(), true);
				popview.setPopupWindow(mPopup);

				mPopup.showAtLocation(mImgSelLayout, Gravity.CENTER, 0, 0);

				break;

			case R.id.btn_number: //숫자퍼즐 선택
				intent = new Intent(this, SelectPuzzleType.class);
				intent.putExtra("imageType", Const.IMAGE_TYPE_NUMBER);
				startActivity(intent);
				finish();
				break;

			case R.id.imagelayout_1: //기본이미지 1번
				intent = new Intent(this, SelectPuzzleType.class);
				intent.putExtra("imageType", Const.IMAGE_TYPE_DEFAULT);
				intent.putExtra("resID", R.drawable.doeun_1);
				startActivity(intent);
				mPopup.dismiss();
				finish();
				break;

			case R.id.imagelayout_2: //기본이미지 2번
				intent = new Intent(this, SelectPuzzleType.class);
				intent.putExtra("imageType", Const.IMAGE_TYPE_DEFAULT);
				intent.putExtra("resID", R.drawable.doeun_2);
				startActivity(intent);
				mPopup.dismiss();
				finish();
				break;

			case R.id.imagelayout_3: //기본이미지 3번
				intent = new Intent(this, SelectPuzzleType.class);
				intent.putExtra("imageType", Const.IMAGE_TYPE_DEFAULT);
				intent.putExtra("resID", R.drawable.doeun_3);
				startActivity(intent);
				mPopup.dismiss();
				finish();
				break;

			case R.id.imagelayout_4: //기본이미지 4번
				intent = new Intent(this, SelectPuzzleType.class);
				intent.putExtra("imageType", Const.IMAGE_TYPE_DEFAULT);
				intent.putExtra("resID", R.drawable.doeun_4);
				startActivity(intent);
				mPopup.dismiss();
				finish();
				break;
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch(requestCode){
			case REQUEST_CODE_GALLERY:
				if(resultCode == RESULT_OK){
					Intent intent = new Intent(this, SelectPuzzleType.class);
					intent.putExtra("imagePath", getRealImagePath(data.getData()));
					intent.putExtra("imageType", Const.IMAGE_TYPE_GALLERY);
					startActivity(intent);
					finish();
				}
		}
	}

	/**
	 * URI로 부터 실제 파일 경로를 가져온다.
	 * @param uriPath URI : URI 경로
	 * @return String : 실제 파일 경로
	 */
	private String getRealImagePath(Uri uriPath){

		String[] proj = {MediaStore.Images.Media.DATA};
		Cursor cursor = managedQuery(uriPath, proj, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

		cursor.moveToFirst();

		String path = cursor.getString(column_index);
		Log.e("JYL","path - " + path);
//		path = path.substring(5);

		return path;
	}
}
