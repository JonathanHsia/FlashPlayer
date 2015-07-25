package com.jonthanhsia.flashplayer.domain;

/**
 * 专辑图片的json bean对象
 * @author JonathanHsia
 *
 */
public class AlbumPicJsonBean {

	public int code;
	public int count;
	public Result result;

	public class Result {
		public String cover;
		public String thumb;
	}
}