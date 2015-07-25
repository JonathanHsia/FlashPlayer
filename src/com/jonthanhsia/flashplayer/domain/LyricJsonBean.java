package com.jonthanhsia.flashplayer.domain;

import java.util.List;
/**
 * 歌词 json 传输的bean
 * @author JonathanHsia
 */
public class LyricJsonBean {

	public int code;
	public int count;
	public List<Result> result;
	public class Result{
		public String song;
		public String lrc;
		public int aid;
		public int artist_id;
		public int sid;
	}
}