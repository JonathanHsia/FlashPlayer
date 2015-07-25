package com.jonthanhsia.flashplayer.service;
import com.jonthanhsia.flashplayer.domain.Mp3Info;
/**
 * 音乐控制对象
 * @author JonathanHsia
 */
public interface IMusicControl {
	void playMusic(Mp3Info song,int playState);
	void pauseMusic();
//	void nextSong(Mp3Info song);//一个播放音乐方法,上一曲下一曲全部搞定
	void stopMusic();
	boolean isPlaying();
	void resetMusic();
	void startMusic();
	void seekProgress(int progress);
	
	void startUpdatePd();
	void stopUpdatePd();
	
	void startUpdateLrc();
	void stopUpdateLrc();
	void iniLrc();
	
//	void showLyric(Mp3Info song);
}
