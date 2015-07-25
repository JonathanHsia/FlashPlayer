package com.jonthanhsia.flashplayer.base.activity.impl;
import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

import com.jonthanhsia.flashplayer.R;
import com.jonthanhsia.flashplayer.application.utils.MyContance;
/**
 * 闪屏页面
 * @author JonathanHsia
 */
public class SplashActivity extends Activity {
	protected static final int GO_HOME = 0;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        //发送延时的消失进入主页面
        handler.sendEmptyMessageDelayed(GO_HOME, 1000);
        
        //初始化本地目录和音乐数据库
        initData();
    }
	/**
	 * 初始化本地目录和音乐数据库
	 */
    private void initData() {
    	//创建本地工作目录
    	createPath(MyContance.LYRIC_PATH);
    	createPath(MyContance.ALBUM_PIC_PATH);
    	createPath(MyContance.SONGLIST_PATH);
    	createPath(MyContance.LYRIC_TEMP_PATH);
    	createPath(MyContance.ALBUM_PIC_TEMP_PATH);
    	
	}
    /**
     * 创建指定目录
     * @param lyricPath
     */
	private void createPath(String path) {
		File filePath = new File(path);
		if(!filePath.exists()){//如果目录不存在就创建目录
			filePath.mkdirs();
			System.out.println("创建目录成功!! "+filePath.getAbsolutePath());
		}else{
			System.out.println("目录已经存在,不创建了");
		}
	}
	private Handler handler = new Handler(){
    	public void handleMessage(android.os.Message msg) {
    		switch (msg.what) {
			case GO_HOME:
				//跳转到主页面
				startActivity(new Intent(getApplicationContext(),MainUI.class));
				finish();
				break;
    		}
    	};
    };
    
    
}
