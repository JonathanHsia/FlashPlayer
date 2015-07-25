package com.jonthanhsia.flashplayer.utils;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import com.jonthanhsia.flashplayer.domain.Lyric;


/**
 * 解析歌词
 * @author Administrator
 *
 */
public class LyricUtils {
	/**
	 * 解析好的歌词列表
	 */
	private ArrayList<Lyric> lyrics;
	
	public ArrayList<Lyric> getLyrics() {
		return lyrics;
	}
	private boolean isExistLyric = false;

	/**
	 * 判断是否有歌词
	 * @return
	 */
	public boolean isExistLyric() {
		return isExistLyric;
	}

	public void readLyricFile(File file) throws IOException{
		
		if(file == null || !file.exists()){
			//歌词文件不存在
			lyrics = null;
			isExistLyric = false;
		}else{
			//有歌词文件-解析
			isExistLyric = true;
			lyrics = new ArrayList<Lyric>();
			
			//1.解析歌词
			//一行一行的去读
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			BufferedReader reader = new BufferedReader(new InputStreamReader(bis, getCharset(file)));
			String line = "";
			while((line = reader.readLine())!=null){
				line = analyzeLyric(line);
			}
			
			//2.歌词排序
			Collections.sort(lyrics, new Sort());
			
			
			
			//3.计算每句歌词高亮显示多长时间
			for(int i=0; i<lyrics.size(); i++){
				Lyric oneLyric = lyrics.get(i);
				
				if(i+1 < lyrics.size()){
					Lyric twoLyric = lyrics.get(i+1);
					oneLyric.setSleepTime(twoLyric.getTimePoint()-oneLyric.getTimePoint());
				}
				
			}
			
		}
		
	}
	/**
	 * 判断文件编码
	 * @param file 文件
	 * @return 编码：GBK,UTF-8,UTF-16LE
	 */
	public String getCharset(File file) {
		String charset = "UTF-8";
		byte[] first3Bytes = new byte[3];
		try {
			boolean checked = false;
			BufferedInputStream bis = new BufferedInputStream(
					new FileInputStream(file));
			bis.mark(0);
			int read = bis.read(first3Bytes, 0, 3);
			if (read == -1)
				return charset;
			if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
				charset = "UTF-16LE";
				checked = true;
			} else if (first3Bytes[0] == (byte) 0xFE
					&& first3Bytes[1] == (byte) 0xFF) {
				charset = "UTF-16BE";
				checked = true;
			} else if (first3Bytes[0] == (byte) 0xEF
					&& first3Bytes[1] == (byte) 0xBB
					&& first3Bytes[2] == (byte) 0xBF) {
				charset = "UTF-8";
				checked = true;
			}
			bis.reset();
			if (!checked) {
				int loc = 0;
				while ((read = bis.read()) != -1) {
					loc++;
					if (read >= 0xF0)
						break;
					if (0x80 <= read && read <= 0xBF)
						break;
					if (0xC0 <= read && read <= 0xDF) {
						read = bis.read();
						if (0x80 <= read && read <= 0xBF)
							continue;
						else
							break;
					} else if (0xE0 <= read && read <= 0xEF) {
						read = bis.read();
						if (0x80 <= read && read <= 0xBF) {
							read = bis.read();
							if (0x80 <= read && read <= 0xBF) {
								charset = "UTF-8";
								break;
							} else
								break;
						} else
							break;
					}
				}
			}
			bis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return charset;
	}
	
	//歌词排序
	private class Sort implements Comparator<Lyric>{

		@Override
		public int compare(Lyric lyr1, Lyric lyr2) {
			if(lyr1.getTimePoint() < lyr2.getTimePoint()){
				return -1;
			}else if(lyr1.getTimePoint() > lyr2.getTimePoint()){
				return 1;
			}else{
				return 0;
			}
		}
		
	}

	/**
	 * 解析每一行歌词，并且把解析好的歌词放到列表中
	 * @param line ： [02:04.12][03:37.32][00:59.73]我在这里欢笑
	 * @return
	 */
	private String analyzeLyric(String line) {
		//1.得到左边第一个括号的位置和得到右边第一个括号的位置
		int pos1 = line.indexOf("[");//如果没有找到返回-1 当前 0
		int pos2 = line.indexOf("]");//9;
		if(pos1 ==0&& pos2 != -1){
			//设置装时间点的集合
			long[] timePoints = new long[getCountTag(line)];
			//第0的下标开始赋值
			String timPointStr = line.substring(pos1+1, pos2);//02:04.12
			timePoints[0] = timeStrToLong(timPointStr);
			if(timePoints[0]==-1){
				return "";
			}
			String content = line;//[02:04.12][03:37.32][00:59.73]我在这里欢笑
			int i = 1;
			while (pos1 ==0&& pos2 != -1) {
				content  = content.substring(pos2+1);//我在这里欢笑
				pos1 = content.indexOf("[");//如果没有找到返回-1 当前 0
				pos2 = content.indexOf("]");//9;
				if(pos2 != -1){
					timPointStr = content.substring(pos1+1, pos2);
					timePoints[i] = timeStrToLong(timPointStr);
					
					if(timePoints[i] ==-1){
						return "";
					}
					i++;
				}
			}
			
			Lyric lyric = new Lyric();
			//歌词的内容和时间点进行关联
			for(int j=0;j<timePoints.length;j++){
				
				if(timePoints[j] != 0){
					lyric.setContent(content);
					lyric.setTimePoint(timePoints[j]);
					//添加到集合里面
					lyrics.add(lyric);
					lyric = new Lyric();
				}
			}
		}

		
		return null;
	}

	/**
	 * 02:04.12转换成毫秒并且是Long类型
	 * @param timPointStr
	 * @return
	 */
	private long timeStrToLong(String timPointStr) {
		long result = 0;
		try {
			//1.把02:04.12 切成02和04.12；
			//2.把04.12切成04和12
			String[] s1 = timPointStr.split(":");
			String[] s2 = s1[1].split("\\.");
			
			//分
			int min = Integer.valueOf(s1[0]);
			
			//秒
			int second = Integer.valueOf(s2[0]);
			//毫秒
			int mil = Integer.valueOf(s2[1]);
			result = min*60*1000 + second*1000+mil*10;
			
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = -1;
		}catch(ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
			result = -1;
		}
		return result;
	}
	/**
	 * [02:04.12][03:37.32][00:59.73]我在这里欢笑
	 * 根据这一行分析有多少句歌词
	 * @param line
	 * 至少是个1
	 * @return
	 */
	private int getCountTag(String line) {
		int result = 0;
		String[] s1 = line.split("\\[");//3
		String[] s2 = line.split("\\]");//3
		if(s1.length ==0&&s2.length==0){
			result = 1;
		}else if(s1.length >s2.length){
			result = s1.length;
		}else{
			result = s2.length;
		}
		return result;
	}
}
