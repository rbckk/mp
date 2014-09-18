package com.mp.android.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadUtil {
	public static int downLoadFile(String requestURL, String toPath) {
		InputStream in = null;
		FileOutputStream out = null;
		HttpURLConnection conn = null;
		int res =0;
		try {
			URL url = new URL(requestURL);
			conn = (HttpURLConnection) url.openConnection();
			conn.connect();

			res = conn.getResponseCode();
			if (res == 200) {
				String[] names = requestURL.split("_");
				String fileName = names[names.length - 1];

				in = new BufferedInputStream(conn.getInputStream());
				out = new FileOutputStream(new File(toPath + File.separator + fileName));
				int len = 0;
				byte[] b = new byte[1024];
				while ((len = in.read(b)) != -1) {
					out.write(b, 0, len);
				}
				out.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
				conn.disconnect();
			} catch (Exception t) {
				t.printStackTrace();
			}
		}
		
		return res;
	}

	public static String upload(File file, String requestUrl) {
		HttpURLConnection conn = null;
		OutputStream out = null;
		DataInputStream in = null;
		String result = null;
		try {
			URL url = new URL(requestUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setChunkedStreamingMode(1024 * 1024);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("Charsert", "UTF-8");
			conn.setRequestProperty("Content-Type", "multipart/form-data;file=" + file.getName());
			conn.setRequestProperty("filename", file.getName());

			out = new DataOutputStream(conn.getOutputStream());
			in = new DataInputStream(new FileInputStream(file));
			int len = 0;
			byte[] buff = new byte[1024];
			while ((len = in.read(buff)) != -1) {
				out.write(buff, 0, len);
			}
			out.flush();

			int res = conn.getResponseCode();
			if (res == 200) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line = null;
				StringBuffer sb = new StringBuffer();
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
				if (sb != null && sb.length() > 0) {
					result = sb.toString();
				}
			}
		} catch (Exception e) {
			System.out.println("发送POST请求出现异常！" + e);
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
				conn.disconnect();
			} catch (Exception t) {
				t.printStackTrace();
			}
		}
		System.out.println(result);
		return result;
	}

}