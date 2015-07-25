package com.jonthanhsia.flashplayer.domain;

/**
 * 歌手信息的json bean
 * @author JonathanHsia
 *
 */
public class ArtistInfoJsonBean {

	public int code;
	public int count;
	public Result result;

	public class Result {
		public String area;
		public String birthday;
		public String constellation;
		public String profile;
		public String name;
		public String alias;
	}
}