package com.jonthanhsia.flashplayer.domain;
/**
 * 代表一句歌词
 * @author Administrator
 *
 */
public class Lyric {
	//时间点
	private long timePoint;
	
	//高亮显示多长时间
	private long sleepTime;
	
	//歌词内容
	private String content;

	public long getTimePoint() {
		return timePoint;
	}

	public void setTimePoint(long timePoint) {
		this.timePoint = timePoint;
	}

	public long getSleepTime() {
		return sleepTime;
	}

	public void setSleepTime(long sleepTime) {
		this.sleepTime = sleepTime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "Lyric [timePoint=" + timePoint + ", sleepTime=" + sleepTime
				+ ", content=" + content + "]";
	}
	
	
	

}
