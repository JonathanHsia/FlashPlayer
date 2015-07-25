package com.jonthanhsia.flashplayer.base.pager.impl;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.jonthanhsia.flashplayer.R;
import com.jonthanhsia.flashplayer.base.pager.BasePager;
import com.jonthanhsia.flashplayer.base.pager.DownloadResource;
import com.jonthanhsia.flashplayer.factory.DownloadResourcFactory;
import com.jonthanhsia.flashplayer.view.ShowLyricView;

/**
 * 歌词显示的fragment
 * 
 * @author JonathanHsia
 */
public class LyricShowPager extends BasePager implements OnClickListener {
	private View view;// 歌词显示页面
	public static ShowLyricView lrcView;// 显示歌词的自定义view
//	private ControlFragment mControlFragment;
	private TextView tvSearchLyric;// 点击下载歌词的控件

//	private EditText etAlbumName;// 专辑名
//	private EditText etSongName;// 歌曲名
//	private EditText etArtistName;// 歌手名
//	private Button ibSearch;// 在线搜索的按钮
	
//	private Mp3Info mCurrentMp3Info;//当前播放歌曲的信息
//	
//	private String album;
//	private String title;
//	private String artist;
//	
//	private String lrcUrl;
//	private List<File> lrcFileList;
//	private ListView lyricListView;
//
//	private TextView headerView;//listview的提示头布局
	public LyricShowPager(Activity act) {
		super(act);
//		mControlFragment = ((MainUI) act).getControlFragment();
	}

	@Override
	public View initView() {
		view = View.inflate(mActivity, R.layout.pager_song_lyric, null);

		// 自定义歌词显示控件
		lrcView = (ShowLyricView) view.findViewById(R.id.lrcShowView);
		// ControlFragment.sLrcView = lrcView;// 赋值给主界面,让主界面去操作
		System.out.println("Fragement:     lrcView===>" + lrcView);
		// 下载歌词按钮
		tvSearchLyric = (TextView) view.findViewById(R.id.tv_serarch_lyric);

		return view;
	}

	@Override
	public void initData() {
		// 设置歌词按钮监听器
		tvSearchLyric.setOnClickListener(this);
	}

	/**
	 * 设置搜索歌词按钮的显示
	 * 
	 * @param visible
	 */
	public void setSearchLrcButtonVisible(int visible) {
		tvSearchLyric.setVisibility(visible);
	}

	/**
	 * 歌词按钮的点击事件
	 */
	@Override
	public void onClick(View v) {
		// Toast.makeText(mActivity, "下载歌词喽", 0).show();
		// mActivity.startActivity(new
		// Intent(mActivity,DownloadLrcActivity.class));//不是用activity使用弹窗试试
		switch (v.getId()) {
		case R.id.tv_serarch_lyric:
//			showDownPopWindow();
//			new DownloadResource(mActivity,Down);
			DownloadResource downloadLyric = DownloadResourcFactory.getDownloadResource(mActivity,DownloadResource.LYRIC_MODE);
			downloadLyric.showDownloadDialog();
			break;
//		case R.id.ib_pop_search_artist_search:
//			//显示下载弹窗的默认显示信息
//			llLoadingLyric.setVisibility(View.VISIBLE);//现在加载弹窗
//			tvNoLyric.setVisibility(View.INVISIBLE);//默认是不显示没有歌词的
//			lyricListView.setVisibility(View.INVISIBLE);
//			showPopDefInfo();
//			
//			break;
		}
		
	}
	
	/**
	 * 显示下载弹窗
	 */
//	private void showDownPopWindow() {
//		downView = initDownView();
//		downPop = new PopupWindow(downView,
//				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//		// 设置背景,背景必须设置,要不然无法显示
//		downPop.setOutsideTouchable(false);//点击边缘不退出
//		downPop.setBackgroundDrawable(new BitmapDrawable());
//		downPop.setFocusable(true);
//		
//		// 显示弹窗
//		downPop.showAtLocation(rootView, Gravity.CENTER, 0, 0);
//	}
//
//	/**
//	 * 初始化下载布局
//	 * 
//	 * @return
//	 */
//	private View initDownView() {
//		View view = View.inflate(mActivity, R.layout.pop_window_search_for_net,
//				null);
//		etAlbumName = (EditText) view
//				.findViewById(R.id.et_pop_search_album_name);
//		etSongName = (EditText) view.findViewById(R.id.et_pop_search_song_name);
//		etArtistName = (EditText) view
//				.findViewById(R.id.et_pop_search_artist_name);
//		ibSearch = (Button) view
//				.findViewById(R.id.ib_pop_search_artist_search);
//		lyricListView = (ListView) view
//				.findViewById(R.id.lv_pop_search_show_lyric_list);
//		llLoadingLyric = (LinearLayout) view.findViewById(R.id.ll_pop_search_loading_lyric);
//		tvNoLyric = (TextView) view.findViewById(R.id.tv_pop_search_no_resource);
//		
//		//初始化头布局
//		headerView = new TextView(mActivity);
//		headerView.setTextSize(18);
//		headerView.setText("点击查看,长按使用");
//		headerView.setTextColor(Color.WHITE);
//		headerView.setGravity(Gravity.CENTER);
//		lyricListView.addHeaderView(headerView);//添加提示头
//		
//		
//		// 初始化控件数据
//		initDownPopViewData();
//		return view;
//	}

//	/**
//	 * 初始化下载弹窗的view数据
//	 */
//	private void initDownPopViewData() {
//		mCurrentMp3Info = ControlFragment.sCurrentPlayMp3Info;
//		// 设置控件的默认值,为当前正在播放的歌曲
//		etAlbumName.setText(mCurrentMp3Info.album);
//		etSongName.setText(mCurrentMp3Info.title);
//		etArtistName.setText(mCurrentMp3Info.artist);
//
//		ibSearch.setOnClickListener(this);
//		//设置listview的item点击事件监听器
//		lyricListView.setOnItemClickListener(this);
//		lyricListView.setOnItemLongClickListener(this);
//	}
//	/**
//	 * 显示下载弹窗的默认信息
//	 */
//	private void showPopDefInfo() {
//		//搜索歌词的时候获取最新的输入信息
//		album = etAlbumName.getText().toString().trim();
//		title = etSongName.getText().toString().trim();
//		artist = etArtistName.getText().toString().trim();
//		if(TextUtils.isEmpty(title)){
//			Toast.makeText(mActivity, "歌曲名不能为空", 0).show();
//			return;
//		}
//		lrcUrl = MyContance.NetLyricBaseURL+title;
//		
//		if(!TextUtils.isEmpty(artist)){
//			//如果歌手名不为空的话
//			lrcUrl = lrcUrl+File.separator+artist;
//		}
//		
//		System.out.println("歌曲要的歌词地址: "+mCurrentMp3Info.lrcUrl);
//		
//		//联网获取歌词的json数据
//		getLrcJsonDataFromNet(lrcUrl);
//	}

	
	/**
	 * 联网获取歌词信息
	 * @param lrcUrl2
	 */
//	private void getLrcJsonDataFromNet(String url) {
//		HttpUtils httpUtils = new HttpUtils();
//		httpUtils.send(HttpMethod.GET, url, new RequestCallBack<String>() {
//			@Override
//			public void onSuccess(ResponseInfo<String> responseInfo) {
//				System.out.println("歌词数据获取成功json数据如下\n"+responseInfo.result);
//				processData(responseInfo.result);
//			}
//			@Override
//			public void onFailure(HttpException error, String msg) {
//				System.out.println("搜索歌词失败!");
//				Toast.makeText(mActivity, "歌词搜索失败", 0).show();
//			}
//		});
//	}
	
	/**
	 * 解析结果数据
	 * @param result
	 */
//	protected void processData(String result) {
//		//获得歌词的json的bean
//		LyricJsonBean lyricBean = parseJson(result,LyricJsonBean.class);
//		if(lyricBean.count<=0){
//			tvNoLyric.setVisibility(View.VISIBLE);//显示没有搜索到歌词
//			llLoadingLyric.setVisibility(View.INVISIBLE);//隐藏加载进度条
//			lyricListView.setVisibility(View.INVISIBLE);//当没有歌词的时候如果如果之前有搜索结果的话就清空一下
//			Toast.makeText(mActivity, "没有搜索到歌词", 0).show();
//		}
//		//下载歌词
//		downloadLyric(lyricBean.result);
//	}
	
	/**
	 * 下载歌词
	 * @param result
	 */
//	private void downloadLyric(List<Result> result) {
//		//初始化歌曲文件路径列表
//		lrcFileList = new ArrayList<File>();
//		
//		System.out.println("下载歌词啦...");
//		HttpUtils httpUtils = new HttpUtils();
//		int length = result.size()<=3?result.size():3;//只下载三个歌词数据
//		lyricCount = length;
//		String target = null;//下载路径
//		for (int i = 0; i < length; i++) {
//			target=MyContance.LYRIC_TEMP_PATH+(title+"_"+artist+i+".lrc");
////			if(length==1){//如果只有一首歌词的话,就直接下载到歌词目录
////				target = MyContance.LYRIC_PATH+title+"_"+artist+".lrc";
////			}
//			//有多个歌词的话,让用户选择哪一个
//			System.out.println("target:"+target);
//			httpUtils.download(result.get(i).lrc, target, new RequestCallBack<File>() {
//				@Override
//				public void onSuccess(ResponseInfo<File> responseInfo) {
//					System.out.println("下载成功.....+"+responseInfo.result.getAbsolutePath());
////					Toast.makeText(mActivity, "歌词下载成功", 0).show();
//					//添加歌词到集合中
//					lrcFileList.add(responseInfo.result);
//					//展现歌词到ListView中让用户选择
//					handler.sendEmptyMessage(1);
//				}
//				@Override
//				public void onFailure(HttpException error, String msg) {
//					Toast.makeText(mActivity, "歌词下载失败,请重试", 0).show();
//				}
//			});
//		}
////		initLyricListView();
//	}
//	/**
//	 * 定义handler下载歌词的显示
//	 */
//	private Handler handler = new Handler(){
//		int num = 0;
//		public void handleMessage(android.os.Message msg) {
//			num++;
//			if(num == lyricCount){//当三条信息都执行完成后才显示
//				lyricListView.setVisibility(View.VISIBLE);
//				System.out.println("设置适配器成功!");
//				myAdapter = new MyLyricAdapter();
//				lyricListView.setAdapter(myAdapter);
//				tvNoLyric.setVisibility(View.INVISIBLE);//有歌词一定要隐藏没有歌词的提示不管有没有
//				llLoadingLyric.setVisibility(View.INVISIBLE);//隐藏加载进度
////				System.out.println("通知数据更新了");
////				myAdapter.notifyDataSetChanged();
//				num=0;
//			}
//		};
//	};
//	
//	/**
//	 * 初始化歌词展示ListView
//	 */
//	private void initLyricListView() {
//		System.out.println("初始化listview了");
//	}
//	private MyLyricAdapter myAdapter;//歌词的适配器
//	private int lyricCount;//歌词的数量
//	private View downView;
//	private LinearLayout llLoadingLyric;
//	private TextView tvNoLyric;
//	private PopupWindow downPop;
//	/**
//	 * 歌词的数据适配器
//	 * @author JonathanHsia
//	 */
//	private class MyLyricAdapter extends BaseAdapter{
//		@Override
//		public int getCount() {
//			return lrcFileList.size();
//		}
//		@Override
//		public File getItem(int position) {
//			if(position==0){
//				return null;
//			}
//			return lrcFileList.get(position);
//		}
//		@Override
//		public long getItemId(int position) {
//			return position;
//		}
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			View view = View.inflate(mActivity, R.layout.listitem_download_lyric, null);
//			TextView tvLrcTitle = (TextView) view.findViewById(R.id.tv_down_lyric_title);
//			TextView tvLrcSize = (TextView) view.findViewById(R.id.tv_down_lyric_size);
//			File item = lrcFileList.get(position);
////			System.out.println("getView    item: "+item.getAbsolutePath());
//			tvLrcTitle.setText(item.getName());
//			tvLrcSize.setText(Formatter.formatFileSize(mActivity, item.length()));
//			return view;
//		}
//	}
//	/**
//	 * listview的条路点击事件监听器
//	 */
//	@Override
//	public void onItemClick(AdapterView<?> parent, View view, int position,
//			long id) {
//		if(position == 0){//点击了头布局
//			System.out.println("点击了头布局");
//			return;
//		}
//		//item点击显示一个弹窗,显示歌词的预览
//		File item = lrcFileList.get(position-1);
//		
//		System.out.println("file item:"+item.getAbsolutePath());
//		
//		showLyric(item,view);
//	}
//	/**
//	 * 显示歌词
//	 * @param item
//	 */
//	private void showLyric(File item,View view) {
//		View lrcView = View.inflate(mActivity, R.layout.pop_window_show_lyric, null);
//		TextView tvLyric = (TextView) lrcView.findViewById(R.id.tv_pop_window_show_lyric);
//		//读取歌词,设置到控件中
////		LrcProcess processLrc = new LrcProcess();
//		LrcParser lrcParser = new LrcParser();
//		try {
//			lrcParser.parser(item.getAbsolutePath());
//		} catch (Exception e) {
//			System.out.println("解析歌词异常");
//			e.printStackTrace();
//		}
//		String lrcStr = lrcParser.getLrcStr();
//		
////		String lrc = processLrc.readLRC(item.getAbsolutePath());
//		//歌词集合
////		List<LrcContentBean> lrcList = processLrc.getLrcList();
////		StringBuilder sb = new StringBuilder();
////		for (int i = 0; i < lrcList.size(); i++) {//转录歌词
////			sb.append(lrcList.get(i).lrcStr+"\n");
////		}
////		
////		tvLyric.setText(sb.toString());
//		tvLyric.setText(lrcStr);
//		//显示歌词对话框
//		showLyricDialog(item, lrcView);
//	}
//
//	private void showLyricDialog(final File item, View lrcView) {
//		//对话框显示歌词
//		Builder abd = new AlertDialog.Builder(mActivity);
//		abd.setNegativeButton("使用", new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				//使用当前的歌词
//				useLyric(item);
//			}
//		});
//		abd.setPositiveButton("取消", null);//取消按钮
//		
//		AlertDialog dialog = abd.create();
//		dialog.setView(lrcView, 0, 0, 0, 0);
//		dialog.setTitle(item.getName());
//		//初始化按钮
//		
//		dialog.show();
//	}
//	
//	/**
//	 * 确定使用指定的歌词
//	 * @param item
//	 */
//	private void useLyric(final File item) {
//		System.out.println("执行了歌词的复制操作了.......");
//		//复制当前歌词到歌词目录,删除缓存文件
//		FileUtils.copy(item.getAbsolutePath(), mCurrentMp3Info.lrcUrl, true);//复制操作
//		//删除缓存操作
////		File parentFile = item.getParentFile();
//		List<File> data = new ArrayList<File>();
//		FileUtils.getFileListBySuffix(item, ".lrc", data  );
//		System.out.println("fileData:  "+data);
//		
//		FileUtils.deleteFileByFileList(data);//删除缓存操作
//		
//		//通知更新歌词
//		ControlFragment.sIMusicControl.iniLrc();//初始化歌词控件让它重新的加载歌词
//		//隐藏下载歌词按钮
//		setSearchLrcButtonVisible(View.INVISIBLE);
//		
//		downPop.dismiss();//隐藏下载歌词栏
//		Toast.makeText(mActivity, "歌词使用成功", 0).show();
//	}
//	
//	
//	/**
//	 * 条目长按点击监听器
//	 */
//	@Override
//	public boolean onItemLongClick(AdapterView<?> parent, View view,
//			int position, long id) {
//		if(position == 0){//点击了头布局
//			System.out.println("长按了头布局");
//			return false;
//		}
//		//item长按点击使用选择的歌词
//		File item = lrcFileList.get(position-1);
//		
//		useLyric(item);
//		return true;
//	}
	
	
//	/**
//	 * 解析json数据返回对应的bean
//	 * @param <T>
//	 * @param result
//	 * @return
//	 */
//	private <T> T parseJson(String json,Class<T> clazz) {
//		Gson gson = new Gson();
//		T t = gson.fromJson(json, clazz);
//		return t;
//	}
	
}
