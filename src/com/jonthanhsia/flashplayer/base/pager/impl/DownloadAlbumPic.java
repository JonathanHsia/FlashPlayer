package com.jonthanhsia.flashplayer.base.pager.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.jonthanhsia.flashplayer.application.utils.FileUtils;
import com.jonthanhsia.flashplayer.application.utils.MyContance;
import com.jonthanhsia.flashplayer.base.activity.impl.MainUI;
import com.jonthanhsia.flashplayer.base.pager.DownloadResource;
import com.jonthanhsia.flashplayer.domain.AlbumPicJsonBean;
import com.jonthanhsia.flashplayer.domain.LyricJsonBean;
import com.jonthanhsia.flashplayer.domain.LyricJsonBean.Result;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 下载专辑图片
 * 
 * @author JonathanHsia
 */
public class DownloadAlbumPic extends DownloadResource {
	// 获取专辑图片地址
	protected static final int GET_COVER_URL = 0;
	//获取专辑图片的bitmap		
	protected static final int GET_ALBUM_PIC = 1;

	public DownloadAlbumPic(Activity act, int downMode) {
		super(act, downMode);

	}

	/**
	 * 处理歌词bean
	 */
	@Override
	protected void processLyricBean(LyricJsonBean lyricBean) {
		System.out.println("专辑下载页弹出了,来处理数据了");
		List<Result> result = lyricBean.result;
		// 专辑图片集合
		covers = new ArrayList<Bitmap>();
		// 专辑图片的url地址
		converUrl = new ArrayList<File>();
		int length = result.size() <= 3 ? result.size() : 3;// 只下载三个装机封面数据,(最多)
		// 专辑的个数
		coverLength = length;
		String albumUrl = null;
		for (int i = 0; i < length; i++) {
			albumUrl = MyContance.NetSongConverBaseURL + result.get(i).aid;
			httpUtils.send(HttpMethod.GET, albumUrl,
					new RequestCallBack<String>() {
						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							System.out.println("专辑地址获取成功");
//							converUrl.add(responseInfo.result);
							Message msg = handler.obtainMessage();
							msg.what = GET_COVER_URL;
							msg.obj = responseInfo.result;
							handler.sendMessage(msg);
						}

						@Override
						public void onFailure(HttpException error, String msg) {
							System.out.println("专辑地址获取失败!...");
						}
					});

		}

	}

	private List<Bitmap> covers;
	private List<File> converUrl;
	private int coverLength;
	/**
	 * 定义handler下载歌词的显示
	 */
	private Handler handler = new Handler() {
		 int num = -1;//下载图片的标签
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case GET_COVER_URL:
				 num++;
				// if(num == coverLength){
				// 专辑地址获取完成
				System.out.println("专辑地址信息:  converUrl  =====>" + converUrl);
				// 转换地址为json bean对象集合
				// List<AlbumPicJsonBean> albumBeanList =
				// paserJsonListToBeanList(converUrl);
				String converUrl = (String) msg.obj;
				System.out.println("当前专辑图片的url + "+ converUrl);
				//专辑地址的bean 
				AlbumPicJsonBean bean = parseJson(converUrl, AlbumPicJsonBean.class);
				//调用下载专辑图片的功能
				downloadAlbumPicJsonBean(bean,num);
				// num=0;
				// }
				if(num == coverLength){
					num=0;
				}
				break;
			case GET_ALBUM_PIC:
				/**
				 * 专辑图片全都下载完成了
				 */
				if(covers.size()==coverLength){
					System.out.println("\\\\\\\\\\\\\\\\\\\\\\图片全部下载完成//////////////////////");
					vpShowAlbumPic.setVisibility(View.VISIBLE);//显示vp
					lyricListView.setVisibility(View.INVISIBLE);
					tvNoLyric.setVisibility(View.INVISIBLE);// 有数据一定要隐藏没有歌词的提示不管有没有
					llLoadingLyric.setVisibility(View.INVISIBLE);// 隐藏加载进度
					MyViewPagerAdatper vpAdapter = new  MyViewPagerAdatper();
					vpShowAlbumPic.setAdapter(vpAdapter);
				}
				
				break;
			}
		};
	};
	/**
	 * 显示专辑图片的ViewPager的适配器
	 * @author JonathanHsia
	 */
	private class MyViewPagerAdatper extends PagerAdapter{
		@Override
		public int getCount() {
			return covers.size();
		}
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0==arg1;
		}
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView iv = new ImageView(mActivity);
			final int ps = position;
			iv.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					//长按图片设置专辑图片
					setSongAlbumPic(ps);
					
					return true;
				}
			});
			Bitmap bitmap = covers.get(position);
			iv.setImageBitmap(bitmap);
			
			container.addView(iv);
			return iv;
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
		
	}
	/**
	 * 设置专辑图片
	 */
	protected void setSongAlbumPic(int position) {
		File item = converUrl.get(position);
		//赋值选中的图片
		if(item.exists()){
			FileUtils.copy(item.getAbsolutePath(), mCurrentMp3Info.artistPicPath, true);
		}
		//删除缓存中的图片
		
		File parentFile = item.getParentFile();
		System.out.println("filte item:" + item.getAbsolutePath());
		List<File> data = new ArrayList<File>();
		FileUtils.getFileListBySuffix(parentFile, ".jpg", data);
		System.out.println("fileData:  " + data);

		FileUtils.deleteFileByFileList(data);// 删除缓存操作
		//刷新专辑图片
		((MainUI)mActivity).getControlFragment().getmPlayPager().setAlbumAtrtistPic();
		
		downDialog.dismiss();// 隐藏下载歌词栏
		Toast.makeText(mActivity, "图片使用成功", 0).show();
	}
	/**
	 * 下载专辑图片
	 * @param bean
	 */
	protected void downloadAlbumPicJsonBean(AlbumPicJsonBean bean,int num) {
		System.out.println("开始下载专辑:  "+num);
		final String target = MyContance.ALBUM_PIC_TEMP_PATH+album+"_"+artist+num+".jpg";
		httpUtils.download(bean.result.cover, target, new RequestCallBack<File>() {
			@Override
			public void onSuccess(ResponseInfo<File> responseInfo) {
				//专辑图片下载成功
				System.out.println("图片下载成功=====>"+target);
				//发送下载成功信息到handler中处理
//				Message msg = handler.obtainMessage();
//				msg.what= GET_ALBUM_PIC;
//				msg.obj = responseInfo.result;
				//添加文件的路径的集合中
				converUrl.add(responseInfo.result);
				covers.add(BitmapFactory.decodeFile(responseInfo.result.getAbsolutePath()));
				handler.sendEmptyMessage(GET_ALBUM_PIC);
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				System.out.println("专辑图下载失败");
			}
		});
	}

	

	/**
	 * 转换json数据集合到bean集合
	 * 
	 * @param converUrl2
	 * @return
	 */
	protected List<AlbumPicJsonBean> paserJsonListToBeanList(
			List<String> converUrl2) {
		List<AlbumPicJsonBean> beanList = new ArrayList<AlbumPicJsonBean>();
		String tempStr = null;
		for (int i = 0; i < converUrl2.size(); i++) {
			tempStr = converUrl2.get(i);
			AlbumPicJsonBean bean = parseJson(tempStr, AlbumPicJsonBean.class);
			beanList.add(bean);
		}
		return beanList;
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {

		return false;

	}

}
