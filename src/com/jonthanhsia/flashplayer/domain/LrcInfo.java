package com.jonthanhsia.flashplayer.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * 用来封装歌词信息的类
 * @author Administrator
 *
 */
public class LrcInfo {
    public String title;//歌曲名
	public String singer;//演唱者
	public String album;//专辑	
	public Map<Long,String> infos;//保存歌词信息和时间点一一对应的Map
   //以下为getter()  setter()
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSinger() {
		return singer;
	}
	public void setSinger(String singer) {
		this.singer = singer;
	}
	public String getAlbum() {
		return album;
	}
	public void setAlbum(String album) {
		this.album = album;
	}
	public Map<Long, String> getInfos() {
		return infos;
	}
	public void setInfos(Map<Long, String> infos) {
		this.infos = infos;
	}
}
