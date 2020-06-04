/*
 * com.zeronz.puzzle.Puzzle
 *
 * Created on 2011. 4. 4.
 * 
 * Copyright (c) 2009-2011 zerOnz Co., Ltd. All Rights Reserved.
 */
package com.semoda.puzzle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.semoda.puzzle.customview.PuzzleLayout;
import com.semoda.puzzle.utils.ImageUtils;

/**
 * 
 * Puzzle class
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
public class Puzzle extends Activity implements OnClickListener, SensorEventListener{

	private PuzzleLayout 	mPuzzleLayout;
	private int 			mImageResID;
	private int 			mImageType;
	private String 			mImagePath;

	private long lastTime; 
	private float speed; 
	private float lastX; 
	private float lastY; 
	private float lastZ; 

	private float x, y, z; 
	private static final int SHAKE_THRESHOLD = 2000;

	private static final int DATA_X = SensorManager.DATA_X; 
	private static final int DATA_Y = SensorManager.DATA_Y; 
	private static final int DATA_Z = SensorManager.DATA_Z; 

	private SensorManager sensorManager; 
	private Sensor accelerormeterSensor; 


	/* Menu Item ID */
	private final int 		IMAGE_MENU 	= 0;
	private final int 		TYPE_MENU 	= 1;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.puzzle);

		// 센서 사용을 위한 등록
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE); 
        accelerormeterSensor = sensorManager 
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER); 

		
		mImageResID = getIntent().getIntExtra("resID", R.drawable.doeun_1);
		int type 	= getIntent().getIntExtra("type", 3);
		mImageType 	= getIntent().getIntExtra("imageType", Const.IMAGE_TYPE_DEFAULT);
		mImagePath 	= getIntent().getStringExtra("imagePath");

		// 단말기 해상도 구하기 
		Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();  
		int screenWidth = display.getWidth();
		int screenHeight = display.getHeight();

		LinearLayout puzzlelayout = (LinearLayout)findViewById(R.id.puzzlelayout);
		puzzlelayout.setBackgroundColor(Color.WHITE);

		LinearLayout mainlayout = (LinearLayout)findViewById(R.id.mainlayout);

		mPuzzleLayout = new PuzzleLayout(this);
		mPuzzleLayout.setInfo((TextView)findViewById(R.id.timer), type, type, mImageResID, mImagePath, mImageType, screenWidth, screenHeight);
		mainlayout.addView(mPuzzleLayout);

		Button btnShuffle = (Button)findViewById(R.id.btnShuffle);
		btnShuffle.setOnClickListener(this);

		setBackimage(mainlayout, screenWidth, screenHeight);
	}

	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btnShuffle:
			mPuzzleLayout.shuffle();
			break;
		}
	}

	/**
	 * 흑백이미지 백그라운드 적용
	 *
	 * @param mainlayout
	 */
	private void setBackimage(LinearLayout mainlayout, int width, int height){

		Drawable drawable 	= null;
		Bitmap tempBit      = null;
		if(mImageType == Const.IMAGE_TYPE_GALLERY){
			tempBit = ImageUtils.getDrawableFromUri(this, mImagePath);
		}else{
			tempBit = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(this.getResources(), mImageResID), width, height, false); 
		}

		Canvas canvas = new Canvas(tempBit);

		ColorMatrix cm = new ColorMatrix(); 
		cm.setSaturation(0); 

		Paint paint = new Paint(); 
		paint.setColorFilter(new ColorMatrixColorFilter(cm)); 
		canvas.drawBitmap(tempBit, 0, 0, paint);

		drawable = (Drawable)(new BitmapDrawable(tempBit));
		mainlayout.setBackgroundDrawable(drawable);

		tempBit = null;
	}

	/**
	 * 롤업메뉴 세팅
	 */
	public boolean onCreateOptionsMenu(Menu menu) {

		boolean result = super.onCreateOptionsMenu(menu);

		menu.add(0, IMAGE_MENU, Menu.NONE, "Select Image");
		menu.add(0, TYPE_MENU, Menu.NONE, "Select Type");
		return result;
	}

	/**
	 * 롤업메뉴 동작 세팅ㄴ	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch(item.getItemId()){

		case IMAGE_MENU:
			intent = new Intent(this, SelectPuzzleImage.class);
			startActivity(intent);
			finish();
			break;

		case TYPE_MENU:
			intent = new Intent(this, SelectPuzzleType.class);
			intent.putExtra("imagePath", mImagePath);
			intent.putExtra("resID", mImageResID);
			intent.putExtra("imageType", mImageType);
			startActivity(intent);
			finish();
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ( event.getRepeatCount() == 0 ) {
			if (keyCode == KeyEvent.KEYCODE_BACK){
				Intent intent = new Intent(this, SelectPuzzleType.class);
				intent.putExtra("imagePath", mImagePath);
				intent.putExtra("resID", mImageResID);
				intent.putExtra("imageType", mImageType);
				startActivity(intent);
				finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	public void onStart() { 
		super.onStart();

		if (accelerormeterSensor != null) 
			sensorManager.registerListener(this, accelerormeterSensor, 
					SensorManager.SENSOR_DELAY_GAME); 
	} 

	public void onStop() { 
		super.onStop(); 


		if (sensorManager != null) 
			sensorManager.unregisterListener(this); 
	} 

	public void onAccuracyChanged(Sensor sensor, int accuracy) { 
	} 

	public void onSensorChanged(SensorEvent event) { 
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) { 
			long currentTime = System.currentTimeMillis(); 
			long gabOfTime = (currentTime - lastTime); 


			if (gabOfTime > 100) { 
				lastTime = currentTime; 


				x = event.values[SensorManager.DATA_X]; 
				y = event.values[SensorManager.DATA_Y]; 
				z = event.values[SensorManager.DATA_Z]; 


				speed = Math.abs(x + y + z - lastX - lastY - lastZ) /

				gabOfTime * 10000;



				if (speed > SHAKE_THRESHOLD) { 
					// 이벤트 발생!! 
					mPuzzleLayout.shuffle();
				} 
				lastX = event.values[DATA_X]; 
				lastY = event.values[DATA_Y]; 
				lastZ = event.values[DATA_Z]; 
			}
		}
	}

}