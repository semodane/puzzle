/*
 * com.zeronz.puzzle.customview.PuzzleLayout
 *
 * Created on 2011. 4. 4.
 * 
 * Copyright (c) 2009-2011 zerOnz Co., Ltd. All Rights Reserved.
 */
package com.semoda.puzzle.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.semoda.puzzle.Const;
import com.semoda.puzzle.R;
import com.semoda.puzzle.utils.ImageUtils;

import java.util.HashMap;

/**
 * 
 * PuzzleLayout
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
public class PuzzleLayout extends RelativeLayout implements OnTouchListener, AnimationListener{
	
	/* MessageID */
	/** MessageID timer start */
	private static final int MSG_TIMER_START		= 00000001;
	/** MessageID timer stop */
	private static final int MSG_TIMER_STOP			= 00000002;
	/** MessageID timer text init */
	private static final int MSG_TIMER_INIT			= 00000003;
	
	/* 이미지위치 검색시 해시맵 타입 */
	private static final int ORIPOS				= 0;
	private static final int MOVEPOS			= 1;
	
	
	/* Move type*/
	private static final int MOVE_UP			= 0;
	private static final int MOVE_DOWN			= 1;
	private static final int MOVE_LEFT			= 2;
	private static final int MOVE_RIGHT			= 3;

	private Context 			mContext;
	private Bitmap 				mPic, mSPic;
	private int 				mSizeX = 0, mSizeY = 0;
	private PieceView 			mPiece[];
	private PieceView 			mClickview;
	private int		 			mBlankIdx;
	private int 				mOldX, mOldY, mDeltaX, mDeltaY;
	private int 				mPuzX, mPuzY;
	private Point 				mPtMeStart;
	private boolean 			mShuffle, mStart;
	private TextView 			mTimer_text;
	private int		 			mTimer_sec;
	
	private HashMap<Integer, Bitmap> mImage = new HashMap<Integer, Bitmap>();
	

	private	Handler			mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch(msg.what) {

			case MSG_TIMER_START:

				if(mTimer_sec < 60){
					if(mTimer_sec < 10){
						mTimer_text.setText("0:0"+mTimer_sec);						
					}else{
						mTimer_text.setText("0:"+mTimer_sec);						
					}
				}else if (mTimer_sec > 60){
					if(mTimer_sec%60 < 10){
						mTimer_text.setText(mTimer_sec/60 +":"+ "0"+mTimer_sec%60);					
					}else{
						mTimer_text.setText(mTimer_sec/60 +":"+ mTimer_sec%60);						
					}
				}

				mTimer_sec++;
				
				Message tmag = mHandler.obtainMessage();
				tmag.what = MSG_TIMER_START;
    			mHandler.sendMessageDelayed(tmag, 1000 ); 
				
				break;
				
			case MSG_TIMER_STOP:
				mHandler.removeMessages(MSG_TIMER_START);
				
				Message imag = mHandler.obtainMessage();
				imag.what = MSG_TIMER_INIT;
    			mHandler.sendMessageDelayed(imag, 1000 ); 
    			
				mStart = false;
				break;
			
			case MSG_TIMER_INIT:
				mHandler.removeMessages(MSG_TIMER_START);
				mTimer_text.setText("0:00");
				
				break;
			}
		}
	};

	public PuzzleLayout(Context context) {
		super(context);

		mContext = context;
	}
	
	/** 
	 * Screen Init
	 * 
	 * @param resID
	 * @param imagePath
	 * @param imageType
	 * @param screenWidth
	 * @param screenHeight
	 */
	private void init(int resID, String imagePath, int imageType, int screenWidth, int screenHeight){
		
		mPiece 	= new PieceView[mPuzX * mPuzY];
		
		MarginLayoutParams margin = new MarginLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		this.setLayoutParams(new LinearLayout.LayoutParams(margin));
		
		this.setBackgroundColor(Color.argb(170, 255, 255, 255)); // 반투명 백그라운드 적용

		if(imageType == Const.IMAGE_TYPE_GALLERY){
			mPic = ImageUtils.getDrawableFromUri(mContext, imagePath);
		}else{
			mPic = BitmapFactory.decodeResource(mContext.getResources(), resID); 
		}
			
		mShuffle = false;
		mStart = false;

		mSizeX = screenWidth - 20;  // 좌우 패딩영역 제외
		mSizeY = (screenHeight/8)*7 -20 ; // 전체에서 8/7 칸틈 계산후 패딩영역 제외 

		pieceCreate();
	}
	
	/**
	 * 관련 데이터 세팅 
	 * 
	 * @param tv
	 * @param puz_x
	 * @param puz_y
	 * @param resID
	 * @param imagePath
	 * @param imageType
	 * @param screenWidth
	 * @param screenHeight
	 */
	public void setInfo(TextView tv, int puz_x, int puz_y, int resID, String imagePath, int imageType, int screenWidth, int screenHeight){
		mTimer_text = tv;
		mPuzX = puz_x;
		mPuzY = puz_y;
		
		init(resID, imagePath, imageType, screenWidth, screenHeight);
	}

	/**
	 * 선택이미지 분할하여 Piece 생성
	 */
	private void pieceCreate() {

		mSPic = Bitmap.createScaledBitmap(mPic, mSizeX, mSizeY, false); 

		int startx, starty, xwidth, ywidth;
		Bitmap imsiBitmap;  
		MarginLayoutParams margin = null;
		
		for(int i=0; i<mPuzY; i++){
			for(int j=0; j<mPuzX; j++){
				startx = mSizeX / mPuzX * j; 
				starty = mSizeY / mPuzY * i;
				xwidth = mSizeX / mPuzX;  
				ywidth = mSizeY / mPuzY;
				imsiBitmap = Bitmap.createBitmap(mSPic, startx, starty, xwidth, ywidth);
		
				mPiece[mPuzX * i + j] = new PieceView(mContext, imsiBitmap, mPuzX * i + j); 
				
				
				margin = new MarginLayoutParams(new LayoutParams(xwidth, ywidth));
				margin.setMargins(startx, starty, 0, 0);
				mPiece[mPuzX * i + j].setLayoutParams(new LayoutParams(margin));
				mPiece[mPuzX * i + j].setOnTouchListener(this);
				
				if((mPuzX * i + j != mPuzX * mPuzY -1)){
					this.addView(mPiece[mPuzX * i + j]);
					mImage.put(mPuzX * i + j,imsiBitmap);
				}else{
					
					mPiece[mPuzX * i + j].setImage(null);
					this.addView(mPiece[mPuzX * i + j]);
					mBlankIdx = mPuzX * i + j;
					mImage.put(mPuzX * i + j, null);
				}
			}
		}
		
		imsiBitmap 	= null;
		mPic 		= null;
		mSPic 		= null;
	}
	
	public boolean onTouch(View view, MotionEvent event) {
		
		PieceView clickview = (PieceView)view;
		
		if(clickview.getLocationIdx() == mBlankIdx){
			return true;
		}
		
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			mOldX = (int)event.getX();
			mOldY = (int)event.getY();
			mPtMeStart = getSlotByPixelPoint(mOldX, mOldY); // 터치시작점을 저장해놓고 ACTION_UP에서 사용
		}
		if(event.getAction() == MotionEvent.ACTION_UP){
			mDeltaX = (int)event.getX() - mOldX;
			mDeltaY = (int)event.getY() - mOldY;

			Point ptDirec = getMoveDirec(mDeltaX, mDeltaY);
			Point ptCheck = new Point(ptDirec.x+mPtMeStart.x, ptDirec.y+mPtMeStart.y);

			if(mBlankIdx == clickview.getLocationIdx() + (mPuzX * ptCheck.y + ptCheck.x)){
				if(ptDirec.x == 1){
					moveRight(clickview);
				}
				else if(ptDirec.x == -1){
					moveLeft(clickview);
				}
				else if(ptDirec.y == 1){
					moveDown(clickview);
				}
				else if(ptDirec.y == -1){
					moveUp(clickview);
				}
			}        
		}
		return true;  
	}
	
	/** 
	 * 터치 지점의  포인트 반환
	 * 
	 * @param pixx
	 * @param pixy
	 * @return Point
	 */
	Point getSlotByPixelPoint(int pixx, int pixy){
		Point ptReturn = new Point( pixx  *mPuzX / mSizeX, pixy * mPuzY / mSizeY);
		return ptReturn;
	}
	
	/**
	 * 터치 이벤트로 입력된 이동방향 계산
	 * 
	 * @param deltax
	 * @param deltay
	 * @return
	 */
	Point getMoveDirec(int deltax, int deltay){
		Point ptDirec = new Point(0,0);
		if( Math.abs(deltax) > Math.abs(deltay) ){
			if(deltax>0){
				ptDirec.x = 1; 
			}else{ 
				ptDirec.x = -1;
			}
		}
		if( Math.abs(deltay) > Math.abs(deltax) ){
			if(deltay>0){
				ptDirec.y = 1;
			}else{ 
				ptDirec.y = -1;
			}
		}
		return ptDirec;
	}
	
	/**
	 * 순서 섞기
	 */
	public void shuffle(){
		
		if(mStart == true){  // 퍼즐이 진행중이면..
			
			mStart = false;
			mTimer_sec = 0;
			mTimer_text.setText("0:00");
			
			mHandler.removeMessages(MSG_TIMER_START);
		}
		
		HashMap<Integer, Integer> oripos = getPosition(ORIPOS); 
		
		imageShuffle(); // 이미지 섞기
		
		HashMap<Integer, Integer> movepos = getPosition(MOVEPOS); 
		
		//애니메이션효과 
		for(int j=0; j<movepos.entrySet().size() ; j++){
			
			int pos = oripos.get(movepos.get(j));
			
			Animation ani =  new TranslateAnimation(
					0,
					((j%mPuzX) - (pos%mPuzX)) * mPiece[pos].getWidth(), 
					0, 
					((j/mPuzX) - (pos/mPuzX)) * mPiece[pos].getHeight());
			ani.setDuration(300);
			mPiece[pos].startAnimation(ani);
		}
		
		mShuffle = true;
		invalidate();
	}
	
	
	/**
	 * Piece의 그려질이미지를 빈칸을 기준으로 섞는다.
	 */
	private void imageShuffle(){
		
		int direc;
		PieceView tempView = null;
		
		for(int i=0; i<1000; i++){
			direc = (int)(Math.random() * 4);
			boolean moveOk = false;
			switch(direc){
			case MOVE_UP: 
				tempView = getNearBlank(MOVE_UP);
				if(tempView != null && tempView.getLocationIdx() / mPuzX != 0){
					moveOk = true;
				}
				break;
				
			case MOVE_DOWN: 
				tempView = getNearBlank(MOVE_DOWN);
				if(tempView != null && tempView.getLocationIdx() / mPuzX != mPuzY-1){
					moveOk = true;
				}
				break;
				
			case MOVE_LEFT: 
				tempView = getNearBlank(MOVE_LEFT);
				if(tempView != null && tempView.getLocationIdx() % mPuzX != 0){
					moveOk = true;
				}
				break;
				
			case MOVE_RIGHT: 
				tempView = getNearBlank(MOVE_RIGHT);
				if(tempView != null && tempView.getLocationIdx() % mPuzX != mPuzX-1){
					moveOk = true;
				}
				break;
			}
			
			if(moveOk){
				
				PieceView blankview = mPiece[mBlankIdx];
				
				int blankImageIdx = blankview.getImageIdx();
				
				blankview.setImageIdx(tempView.getImageIdx());
				blankview.setImage(tempView.getImage());

				tempView.setImageIdx(blankImageIdx);
				tempView.setImage(null);
				
				mBlankIdx = tempView.getLocationIdx();
			}
			
		}
	}
	
	
	/**
	 * 이미지 인덱스와 위치인덱스를 해시맵에 담아  반환
	 * 
	 * @param type
	 * @return HashMap<Integer, Integer>
	 */
	private HashMap<Integer, Integer> getPosition(int type){
		
		HashMap<Integer, Integer> temp = new HashMap<Integer, Integer>(); 
		
		for(int j=0; j<mPiece.length ; j++){
			int locIdx = mPiece[j].getLocationIdx();
			int imgIdx = mPiece[j].getImageIdx();
			if(type == ORIPOS){
				temp.put(imgIdx, locIdx);
			}else if (type == MOVEPOS){
				temp.put(locIdx, imgIdx);
			}
		}
		
		return temp;
	}
	
	/**
	 * 빈칸의 주위의 Piece뷰을 리턴
	 * 
	 * @param nType
	 * @return PieceView
	 */
	private PieceView getNearBlank(int nType){
		
		int movePoint = 0;;
		
		switch(nType){

		case MOVE_UP: 
			movePoint = -mPuzX;
				break;
		case MOVE_DOWN: 
			movePoint = mPuzX;
				break;
		case MOVE_LEFT: 
			movePoint = -1;
				break;
		case MOVE_RIGHT: 
			movePoint = 1;
				break;
		}
		
		for(int j=0; j<mPiece.length; j++){
			if(mBlankIdx == mPiece[j].getLocationIdx() + movePoint){
				return mPiece[j];
			}
		}
		
		return null;
	}
	
	/**
	 * 완료 여부 판단
	 * 
	 * @return boolean
	 */
	boolean checkCompleted(){
		for(int i=0; i<mPiece.length ; i++){
			if(mPiece[i].getOriPosition() == false){
				return false;
			}
		}
		return true; 
	}
	
	/**
	 * 왼쪽으로 이동
	 * 
	 * @param clickview
	 */
	void moveLeft(PieceView clickview){
		Animation ani = AnimationUtils.loadAnimation(mContext, R.anim.translate_left);
		ani.setAnimationListener(this);
		mClickview = clickview;
		clickview.startAnimation(ani);
	}

	/**
	 * 오른쪽으로 이동
	 * 
	 * @param clickview
	 */
	void moveRight(PieceView clickview){
		Animation ani = AnimationUtils.loadAnimation(mContext, R.anim.translate_right);
		ani.setAnimationListener(this);
		mClickview = clickview;
		clickview.startAnimation(ani);
	}

	/**
	 * 아래로 이동
	 * 
	 * @param clickview
	 */
	void moveDown(PieceView clickview){
		Animation ani = AnimationUtils.loadAnimation(mContext, R.anim.translate_down);
		ani.setAnimationListener(this);
		mClickview = clickview;
		clickview.startAnimation(ani);
	}

	/**
	 * 위로 이동
	 * 
	 * @param clickview
	 */
	void moveUp(PieceView clickview){
		Animation ani = AnimationUtils.loadAnimation(mContext, R.anim.translate_up);
		ani.setAnimationListener(this);
		mClickview = clickview;
		clickview.startAnimation(ani);
	}
	
	/**
	 * 이동시킬 Piece뷰를 빈칸으로 이동시키고 위치값 재입력
	 * 
	 * @param clickview
	 */
	private void moveMent(PieceView clickview){
		
		PieceView blankview = mPiece[mBlankIdx];
		
		int blankImageIdx = blankview.getImageIdx();
		
		blankview.setImageIdx(clickview.getImageIdx());
		blankview.setImage(clickview.getImage());

		clickview.setImageIdx(blankImageIdx);
		clickview.setImage(null);
		
		mBlankIdx = clickview.getLocationIdx();
		
		if(mShuffle && mStart == false){ // 퍼즐시작
			
			mTimer_sec 	= 0;
			mStart    	= true;
			
			Message tmsg = Message.obtain(mHandler);
			tmsg.what = MSG_TIMER_START;
			mHandler.sendMessage(tmsg);
		}

		if(mShuffle && checkCompleted()){ // 퍼즐완료
			
			Message tmsg = Message.obtain(mHandler);
			tmsg.what = MSG_TIMER_STOP;
			mHandler.sendMessage(tmsg);
			
			Toast.makeText(mContext, "완료   < " + mTimer_text.getText() + " >" , Toast.LENGTH_LONG).show();
			mShuffle = false;
			
		}
	}

	public void onAnimationEnd(Animation animation) {
		moveMent(mClickview);
		mClickview = null;
	}

	public void onAnimationRepeat(Animation animation) {
	}

	public void onAnimationStart(Animation animation) {
	}
}

