package com.jonthanhsia.flashplayer.view;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
/**
 * 自定义的开关控件, 继承于View
 * @author poplar
 * 
 * Android 绘制流程
 *	  测量		   放置			 绘制
 *	measure  ->   layout    ->   draw 
 *
 * 	onMeasure ->  onLayout  ->  onDraw
 * 
 *  ViewGroup
 *  onMeasure(子控件, 自身控件宽高) ->  onLayout (摆放子控件) ->  onDraw -> (绘制)
 *  
 *  View
 *  onMeasure(自身控件宽高) ->  onDraw -> (绘制)
 */
public class ToggleView extends View {
	private boolean currentState; // 当前开关状态
	private Bitmap switchBackgroundBitmap; // 背景图片
	private Bitmap slideButtonBitmap;// 滑块图片
	private float currentX; // 当前按下位置
	/**
	 * 用于在java代码中初始化View对象
	 * @param context 上下文
	 */
	public ToggleView(Context context) {
		super(context);
	}

	/**
	 * 用于在xml初始化View对象
	 * @param context 上下文
	 * @param attrs 配置的一些属性
	 */
	public ToggleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		// 拿到属性中配置的值
		String namespace = "http://schemas.android.com/apk/res/com.jonthanhsia.flashplayer";
		int switchBackgroundResource = attrs.getAttributeResourceValue(namespace , "switchBackground", -1);
		int slideButtonResource = attrs.getAttributeResourceValue(namespace , "slideButton", -1);
		currentState = attrs.getAttributeBooleanValue(namespace, "toggleState", false);
		
		// 把获取到的资源id转换成bitmap
		setSwitchBackgroundResource(switchBackgroundResource);
		setSlideButtonResource(slideButtonResource);
		
	}

	/**
	 * 用于在xml初始化View对象, 指定样式
	 * @param context 上下文
	 * @param attrs 属性
	 * @param defStyle 指定样式
	 */
	public ToggleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * 将开关的背景图片设置进去
	 * @param switchBackground 图片资源id
	 */
	public void setSwitchBackgroundResource(int switchBackground) {
		switchBackgroundBitmap = BitmapFactory.decodeResource(getResources(), switchBackground);
	}

	public void setSlideButtonResource(int slideButton) {
		slideButtonBitmap = BitmapFactory.decodeResource(getResources(), slideButton);
	}

	public void setSwitchState(boolean b) {
		currentState = b;
	}
	
	
	/**
	 * 测量控件的宽高
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// Mode , Size
		// 设置当前控件的宽高
		setMeasuredDimension(switchBackgroundBitmap.getWidth(), switchBackgroundBitmap.getHeight());
	}
	
	/**
	 * 重写此方法, 绘制想要展示的内容, canvas 画画板, 画板
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		
		// 1. 绘制一个背景图片
		canvas.drawBitmap(switchBackgroundBitmap, 0, 0, null);
		
		
		// 2. 绘制滑动开关按钮
		// 当手指滑动时, 绘制
		if(isTouchSliding){
			// 触摸滑动模式, 根据当前CurrentX绘制界面
			int newLeft = (int) (currentX - slideButtonBitmap.getWidth() * 0.5f);
			System.out.println("currentX: " + currentX + " newLeft: " + newLeft);
			// 获取滑块右边界滑动最大值
			int maxLeft = switchBackgroundBitmap.getWidth() - slideButtonBitmap.getWidth();
			if(newLeft < 0){
				newLeft = 0;
			}else if (newLeft > maxLeft) {
				newLeft = maxLeft;
			}
			canvas.drawBitmap(slideButtonBitmap, newLeft, 0, null);
		}else {
			// 非触摸滑动模式, 根据当前的开关状态 绘制
			if(currentState){
				int newLeft = switchBackgroundBitmap.getWidth() - slideButtonBitmap.getWidth();
				canvas.drawBitmap(slideButtonBitmap, newLeft, 0, null);
			}else {
				canvas.drawBitmap(slideButtonBitmap, 0, 0, null);
			}
		}
		
	}
	
	boolean isTouchSliding = false;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			isTouchSliding = true;
			// 按下时触发调用
			System.out.println("按下");
			currentX = event.getX();
			
			break;
		case MotionEvent.ACTION_MOVE:
			// 移动时触发调用
			System.out.println("移动");
			currentX = event.getX();
			
			break;
		case MotionEvent.ACTION_UP:
			isTouchSliding = false;
			// 抬起时触发调用
			System.out.println("抬起");
			currentX = event.getX();
			
			int center = switchBackgroundBitmap.getWidth() / 2;
			boolean state = currentX > center;
			
			if(onToggleStateChangeListener != null && currentState != state){
				// 状态有更新才调用
				onToggleStateChangeListener.onToggleStateChange(state);
			}
			currentState = state;
			
			break;

		default:
			break;
		}
		// 每调用一次重绘界面, 引发onDraw被调用
		invalidate();
		return true;
	}
	private OnToggleStateChangeListener onToggleStateChangeListener;
	
	public void setOnToggleStateChangeListener(OnToggleStateChangeListener listener){
		this.onToggleStateChangeListener = listener;
	}
	
	public interface OnToggleStateChangeListener{
		
		void onToggleStateChange(boolean state);
		
	}

}
