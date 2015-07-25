package com.jonthanhsia.flashplayer.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.jonthanhsia.flashplayer.application.utils.MyContance;
import com.jonthanhsia.flashplayer.domain.Mp3Info;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

/**
 * 音乐信息类容提供者
 * @author JonathanHsia
 */
public class Mp3InfoProviderBySystem {
	/**
	 * 获取系统中的所有Mp3文件的信息
	 * 
	 * @param context
	 * @return
	 */
	public static List<Mp3Info> getMp3Infos(Context context) {
		Cursor cursor = context.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
				MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		List<Mp3Info> mp3Infos = new ArrayList<Mp3Info>();
		for (int i = 0; i < cursor.getCount(); i++) {
			Mp3Info mp3Info = new Mp3Info();
			cursor.moveToNext();
//			long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID)); // 音乐id
			String title = cursor.getString((cursor
					.getColumnIndex(MediaStore.Audio.Media.TITLE)));// 音乐标题
			String artist = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.ARTIST));// 艺术家
			long duration = cursor.getLong(cursor
					.getColumnIndex(MediaStore.Audio.Media.DURATION));// 时长
			long size = cursor.getLong(cursor
					.getColumnIndex(MediaStore.Audio.Media.SIZE)); // 文件大小
			String url = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.DATA)); // 文件路径
			String album = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.ALBUM));//歌曲所属的专辑
			
			String lrcUrl = MyContance.LYRIC_PATH+title+"_"+artist+".lrc";//通过歌曲的路径,获得lrc文件的路径
			String artistPicPath = MyContance.ALBUM_PIC_PATH+album+"_"+artist+".jpg";
//			System.out.println("lruUrl: "+lrcUrl);
//			System.out.println("artistPicPath: "+artistPicPath);
			
			int isMusic = cursor.getInt(cursor
					.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));// 是否为音乐
			
			if (isMusic != 0) { // 只把音乐添加到集合当中
				mp3Info.id = i;
				mp3Info.title = title;
				mp3Info.artist = artist;
				mp3Info.duration = duration;
				mp3Info.size = size;
				mp3Info.url = url;
				mp3Info.album = album;
				mp3Info.lrcUrl = lrcUrl;//歌词设置进去
				mp3Info.artistPicPath = artistPicPath;
				mp3Infos.add(mp3Info);
			}
		}
		return mp3Infos;
	}

	/**
	 * 获取替换指定路径后的文件名
	 * 
	 * @param songPath
	 * @param
	 * @return
	 */
	private static String getPathUrlBySong(String songPath, String suffix) {
		// int dotIndex = songPath.lastIndexOf(".");
		// int spratorIndex = songPath.lastIndexOf(File.separator);
		// String songName = songPath.substring(spratorIndex,dotIndex);
		// //
		// System.out.println("歌词路径: lrcPath-->"+MyContance.LYRIC_PATH+songName+".lrc");
		// return Path+songName+suffix;
		if (!songPath.endsWith(".mp3")) {
			return "";
		}
		String tempPath = songPath.replace(".mp3", suffix);
		
		return tempPath.substring(tempPath.lastIndexOf(File.separator));
	}
}
