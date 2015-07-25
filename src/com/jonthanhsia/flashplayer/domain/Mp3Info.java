package com.jonthanhsia.flashplayer.domain;
public class Mp3Info {
	public long id; // 歌曲ID 
	public String title; // 歌曲名称 
	public String album; // 专辑 
	public long albumId;// 专辑ID
//	public String displayName; // 显示名称 
	public String artist; // 歌手名称 
	public long duration; // 歌曲时长 
	public long size; // 歌曲大小 
	public String url; // 歌曲路径 
	public String artistPicPath;//专辑图片路径
	
	public String lrcUrl; // 歌词路径
	public String lrcSize; // 歌词大小
	@Override
	public String toString() {
		return "Mp3Info [id=" + id + ", title=" + title + ", album=" + album
				+ ", albumId=" + albumId + ", artist=" + artist + ", duration="
				+ duration + ", size=" + size + ", url=" + url
				+ ", artistPicPath=" + artistPicPath + ", lrcUrl=" + lrcUrl
				+ ", lrcSize=" + lrcSize + "]";
	}

	
}