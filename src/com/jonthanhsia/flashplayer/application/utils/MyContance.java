package com.jonthanhsia.flashplayer.application.utils;

import android.os.Environment;

/**
 * 保存一些固定的字符串名称
 * @author JonathanHsia
 */
public class MyContance {
	/**
	 * SharedPreference的名称
	 */
	public static final String SP_NAME = "config";
	/**
	 * 音乐播放的进度
	 */
	public static final String MUSIC_PROGRESS = "musicProgress";//当前进度
	public static final String MUSIC_TOTAL_PG = "totalProgress";//总进度
	public static final String IS_MUSIC_PROGRESS_COMEBACK = "isMusicProgressComeback";//是否是数据回显的播放
	
	/**
	 * 音乐播放器是否已经设置了播放资源
	 * 0:之前设置了资源
	 * 1:之前没有设置资源
	 * 2:当前正在播放音乐
	 */
	public static final String PLAYER_SETED_SOURCE = "playerSetedSource";
	/**
	 * 音乐播放进度的handler处理的标记
	 */
	public static final int MUSIC_PLAY_COMPLETE = 1;
	public static final int MUSIC_PROCESS_CONTROL = 0;//音乐进度条更新的通知
	
	/**
	 * 音乐列表的handler中处理消息的分类
	 */
	public static final int LOAD_SONG_LIST_COMPLETED = 0;//加载音乐列表进度完成的通知
	/**
	 * 当前正在播放歌曲的在lv的item中的id
	 */
	public static final String CURRENT_PLAYSONG_ID = "currentPlaysongId";//当前正在播放歌曲的在lv的item中的id
	/**
	 * 是否是下一首歌
	 */
	public static final String IS_AUTO_NEXT_MUSIC = "isNextSong";
	/**
	 * seekbar给定的进度
	 */
	public static final String SEEK_PROGRESS = "seekProgress";
	/**
	 * 专辑图片停止旋转时的角度
	 */
	public static final String ALBUM_STOP_ROTATION = "albumStopRotation";
	/**
	 * 播放模式
	 */
	public static final String PLAY_MODE = "playMode";
	
	/**
	 * 歌词的工作路径
	 */
	public static final String LYRIC_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Flash/Lyrics/";
	/**
	 * 歌词缓存路径
	 */
	public static final String LYRIC_TEMP_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Flash/Lyrics/Temps/";
	/**
	 * 专辑图片的路径
	 */
	public static final String ALBUM_PIC_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Flash/AlbumPic/";
	/**
	 * 专辑缓存图片
	 */
	public static final String ALBUM_PIC_TEMP_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Flash/AlbumPic/Temps/";

	/**
	 * 歌曲列表路径
	 */
	public static final String SONGLIST_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Flash/SongList";
	/**
	 * 网络歌词搜索根路径路径
	 */
	public static final String NetLyricBaseURL = "http://geci.me/api/lyric/";
	/**
	 * 根据歌手编号获取歌手信息(暂时只有歌手名)的URL
	 */
	public static final String NetArtistInfoBaseURL = "http://geci.me/api/artist/";
	/**
	 * 根据专辑编号获取专辑封面URL
	 */
	public static final String NetSongConverBaseURL = "http://geci.me/api/cover/";
}
