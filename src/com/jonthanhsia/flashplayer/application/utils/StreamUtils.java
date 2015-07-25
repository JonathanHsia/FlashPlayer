package com.jonthanhsia.flashplayer.application.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 流工具
 * 
 * @author JonathanHsia
 */
public class StreamUtils {
	/**
	 * 将字节输入流转换成字符串
	 * 
	 * @param is
	 * @return
	 */
	public static String stream2String(InputStream is) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		int len = -1;
		byte[] b = new byte[1024];
		try {
			while ((len = is.read(b)) != -1) {
				bos.write(b, 0, len);
			}

			String result = new String(bos.toByteArray());
			return result;
		} catch (IOException e) {

			e.printStackTrace();
		}
		return "";

	}
}
