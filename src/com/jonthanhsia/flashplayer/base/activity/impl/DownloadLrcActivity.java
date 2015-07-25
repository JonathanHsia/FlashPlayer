package com.jonthanhsia.flashplayer.base.activity.impl;
import java.io.File;
import java.util.List;

import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jonthanhsia.flashplayer.R;
import com.jonthanhsia.flashplayer.application.utils.MyContance;
import com.jonthanhsia.flashplayer.base.activity.BaseActivity;
import com.jonthanhsia.flashplayer.base.fragment.impl.ControlFragment;
import com.jonthanhsia.flashplayer.domain.LyricJsonBean;
import com.jonthanhsia.flashplayer.domain.LyricJsonBean.Result;
import com.jonthanhsia.flashplayer.domain.Mp3Info;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
/**
 * 下载歌词页面
 * @author JonathanHsia
 */
public class DownloadLrcActivity extends BaseActivity implements OnClickListener{

	private EditText etAlbumName;//专辑名
	private EditText etSongName;//歌曲名
	private EditText etArtistName;//歌手名
	private ImageButton ibSearch;//在线搜索的按钮
	private ViewPager vpShowLrc;//显示歌词预览的vp
	private Mp3Info mCurrentMp3Info;
	private String album;
	private String title;
	private String artist;
	private String lrcUrl;

	@Override
	protected void initViewPager() {
//		vpShowLrc = (ViewPager) this.findViewById(R.id.vp_pop_search_show_lyric);
	}

	@Override
	protected void initView() {
		this.setContentView(R.layout.pop_window_search_for_net);
		etAlbumName = (EditText) this.findViewById(R.id.et_pop_search_album_name);
		etSongName = (EditText) this.findViewById(R.id.et_pop_search_song_name);
		etArtistName = (EditText) this.findViewById(R.id.et_pop_search_artist_name);
		ibSearch = (ImageButton) this.findViewById(R.id.ib_pop_search_artist_search);
	}

	@Override
	protected void initData() {
		mCurrentMp3Info = ControlFragment.sCurrentPlayMp3Info;
		//设置控件的默认值,为当前正在播放的歌曲
		etAlbumName.setText(mCurrentMp3Info.album);
		etSongName.setText(mCurrentMp3Info.title);
		etArtistName.setText(mCurrentMp3Info.artist);
		
		ibSearch.setOnClickListener(this);
		
	}
	
	/**
	 * 搜索按钮的点击事件
	 */
	@Override
	public void onClick(View v) {
		//搜索歌词的时候获取最新的输入信息
		album = etAlbumName.getText().toString().trim();
		title = etSongName.getText().toString().trim();
		artist = etArtistName.getText().toString().trim();
		if(TextUtils.isEmpty(title)){
			Toast.makeText(this, "歌曲名不能为空", 0).show();
			return;
		}
		lrcUrl = MyContance.NetLyricBaseURL+title;
		
		if(!TextUtils.isEmpty(artist)){
			//如果歌手名不为空的话
			lrcUrl = lrcUrl+File.separator+artist;
		}
		
		System.out.println("歌曲要的歌词地址: "+mCurrentMp3Info.lrcUrl);
		
		//联网获取歌词的json数据
		getLrcJsonDataFromNet(lrcUrl);
		
	}
	
	/**
	 * 联网获取歌词信息
	 * @param lrcUrl2
	 */
	private void getLrcJsonDataFromNet(String url) {
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.GET, url, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				System.out.println("歌词数据获取成功json数据如下\n"+responseInfo.result);
				processData(responseInfo.result);
			}
			@Override
			public void onFailure(HttpException error, String msg) {
				System.out.println("搜索歌词失败!");
				Toast.makeText(getApplicationContext(), "歌词搜索失败", 0).show();
			}
		});
	}
	
	/**
	 * 解析结果数据
	 * @param result
	 */
	protected void processData(String result) {
		//获得歌词的json的bean
		LyricJsonBean lyricBean = parseJson(result,LyricJsonBean.class);
		if(lyricBean.count<=0){
			Toast.makeText(this, "没有搜索到歌词", 0).show();
		}
		//下载歌词
		downloadLyric(lyricBean.result);
	}
	
	
	/**
	 * 下载歌词
	 * @param result
	 */
	private void downloadLyric(List<Result> result) {
		System.out.println("下载歌词啦...");
		HttpUtils httpUtils = new HttpUtils();
		int length = result.size()<=3?result.size():3;//只下载三个歌词数据
		String target = null;//下载路径
		for (int i = 0; i < length; i++) {
			target=MyContance.LYRIC_TEMP_PATH+(title+"_"+artist+i+".lrc");
			if(length==1){//如果只有一首歌词的话,就直接下载到歌词目录
				target = MyContance.LYRIC_PATH+title+"_"+artist+".lrc";
			}
			//有多个歌词的话,让用户选择哪一个
			System.out.println("target:"+target);
			httpUtils.download(result.get(i).lrc, target, new RequestCallBack<File>() {
				@Override
				public void onSuccess(ResponseInfo<File> responseInfo) {
					System.out.println("下载成功.....+"+responseInfo.result.getAbsolutePath());
					Toast.makeText(getApplicationContext(), "歌词下载成功", 0).show();
					
					//展现歌词让用户选择
//					\
				}

				@Override
				public void onFailure(HttpException error, String msg) {
					Toast.makeText(getApplicationContext(), "歌词下载失败,请重试", 0).show();
				}
			});
		}
	}

	/**
	 * 解析json数据返回对应的bean
	 * @param <T>
	 * @param result
	 * @return
	 */
	private <T> T parseJson(String json,Class<T> clazz) {
		Gson gson = new Gson();
		T t = gson.fromJson(json, clazz);
		return t;
	}
}
