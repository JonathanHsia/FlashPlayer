package com.jonthanhsia.flashplayer.factory;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;

import com.jonthanhsia.flashplayer.base.pager.DownloadResource;
import com.jonthanhsia.flashplayer.base.pager.impl.DownloadAlbumPic;
import com.jonthanhsia.flashplayer.base.pager.impl.DownloadLyric;

/**
 * 下载歌曲资源的工厂类
 * 
 * @author JonathanHsia
 */
public class DownloadResourcFactory {
	public static final int LYRIC_MODE = 0;
	public static final int Album_PIC_MODE = 1;
//	private static DownloadResource dr;
	private static Map<Integer, DownloadResource> map = new HashMap<Integer, DownloadResource>();
	public static DownloadResource getDownloadResource(Activity act, int mode) {
		DownloadResource dr = map.get(mode);
		if (dr == null) {
			switch (mode) {
			case LYRIC_MODE:
				dr = new DownloadLyric(act, mode);
				break;
			case Album_PIC_MODE:
				dr = new DownloadAlbumPic(act, mode);
				break;
			}
			map.put(mode, dr);
		}
		return dr;
	}
}
