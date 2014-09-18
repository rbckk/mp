package com.mp.android.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StrikethroughSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.mp.android.R;
import com.mp.android.RenRenData;

//import com.mp.android.emoticons.EmoticonsHelper;
//import com.mp.android.emoticons.EmoticonsResponseBean;
//import com.mp.android.emoticons.EmoticonsResult;
import com.mp.android.statusnet.MustardDbAdapter;
//import com.mp.android.user.FriendPage;

/**
 * �ı�������
 * 
 * @author rendongwei
 * 
 */
public class Text_Util {
	/**
	 * ������ʽ
	 * 
	 * @return
	 */
	private Pattern buildPattern() {
		/**
		 * �鿴�����������Ƿ����,��������ӱ��ض�ȡJson,������
		 */
		/*
		if (RenRenData.mEmoticonsResults.size() == 0
				|| RenRenData.mEmoticonsResults == null) {
			String json = readFromFile(EmoticonsResponseBean.FILENAME,
					EmoticonsResponseBean.FILEPATH);
			if (json != null) {
				EmoticonsHelper helper = new EmoticonsHelper();
				RenRenData.mEmoticonsResults = helper.Resolve(json);
			}
		}

		StringBuilder patternString = new StringBuilder(
				RenRenData.mEmoticonsResults.size() * 3);
		patternString.append('(');
		for (EmoticonsResult result : RenRenData.mEmoticonsResults) {
			String s = result.getEmotion();
			patternString.append(Pattern.quote(s));
			patternString.append('|');
		}
		patternString.replace(patternString.length() - 1,
				patternString.length(), ")");
		return Pattern.compile(patternString.toString());*/
		return null;
	}

	/**
	 * ���ı��еı������滻Ϊ����ͼƬ
	 * 
	 * @param text
	 *            ��Ҫת�����ַ�
	 * @return ���б�����ַ�
	 */
	public CharSequence replace(CharSequence text) {
		try {
			SpannableStringBuilder builder = new SpannableStringBuilder(text);
			Pattern pattern = buildPattern();
			Matcher matcher = pattern.matcher(text);
			while (matcher.find()) {
				Bitmap bitmap = getLoaclEmoticons(matcher.group());
				ImageSpan span = new ImageSpan(bitmap);
				builder.setSpan(span, matcher.start(), matcher.end(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			return builder;
		} catch (Exception e) {
			return text;
		}
	}

	/**
	 * ��SD���и�ݱ����Ż�ȡ����ͼƬ
	 * 
	 * @param imageName
	 *            ��������
	 * @return �����Bitmap
	 */
	private Bitmap getLoaclEmoticons(String imageName) {
		File dir = new File(MustardDbAdapter.PATH_ETC);
		if (!dir.exists() || dir == null) {
			dir.mkdirs();
		}
		File[] cacheFiles = dir.listFiles();
		int i = 0;
		if (cacheFiles != null) {
			for (; i < cacheFiles.length; i++) {
				if (imageName.equals(cacheFiles[i].getName())) {
					break;
				}
			}
		}
		if (i < cacheFiles.length) {
			/**
			 * �����ͼƬ��С,�����ﷵ����һ��60*60��Bitmap,����ֵ�ɸ���������
			 */
			return Bitmap.createScaledBitmap(BitmapFactory
					.decodeFile(MustardDbAdapter.PATH_ETC
							+ imageName), 60, 60, true);
		}
		return null;
	}

	/**
	 * �����ı���SD����
	 * 
	 * @param context
	 *            ������
	 * @param fileName
	 *            �ļ������
	 * @param filePath
	 *            �ļ���·��
	 * @param stringToWrite
	 *            д����ַ�
	 */
	public void savedToText(String fileName, String filePath,
			String stringToWrite) {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			String foldername = MustardDbAdapter.PATH_PKG + filePath;

			File folder = new File(foldername);
			if (folder == null || !folder.exists()) {
				folder.mkdirs();
			}
			File targetFile = new File(foldername + "/" + fileName);
			OutputStreamWriter osw;

			try {

				if (!targetFile.exists()) {
					targetFile.createNewFile();
					osw = new OutputStreamWriter(new FileOutputStream(
							targetFile), "UTF-8");
					osw.write(stringToWrite);
					osw.close();

				} else {
					/**
					 * �ٴ�д��ʱ������ƴ�ӵķ���,��������д
					 */
					osw = new OutputStreamWriter(new FileOutputStream(
							targetFile, false), "UTF-8");
					osw.write(stringToWrite);
					osw.flush();
					osw.close();
				}
			} catch (Exception e) {

			}
		}
	}

	/**
	 * ��ȡSD�����ļ�
	 * 
	 * @param fileName
	 *            �ļ������
	 * @param filePath
	 *            �ļ���·��
	 * @return �ļ��е��ַ�
	 */
	public String readFromFile(String fileName, String filePath) {

		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {

			String foldername = MustardDbAdapter.PATH_PKG + filePath + "/";
			File folder = new File(foldername);

			if (folder == null || !folder.exists()) {
				folder.mkdirs();
			}

			File targetFile = new File(foldername + "/" + fileName);
			String readedStr = "";

			try {
				if (!targetFile.exists()) {
					targetFile.createNewFile();
				} else {
					InputStream in = new BufferedInputStream(
							new FileInputStream(targetFile));
					BufferedReader br = new BufferedReader(
							new InputStreamReader(in, "UTF-8"));
					String tmp;
					while ((tmp = br.readLine()) != null) {
						readedStr += tmp;
					}
					br.close();
					in.close();
					return readedStr;
				}
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}

	/**
	 * ��ӳ����ӵ���������
	 * 
	 * @param context
	 *            ������
	 * @param activity
	 *            Activity
	 * @param view
	 *            ��ʾ��TextView
	 * @param text
	 *            �޸ĵ��ַ�
	 * @param start
	 *            ��ӳ����ӵĳ�ʼλ��
	 * @param end
	 *            ��ӳ����ӵĽ���λ��
	 * @param uid
	 *            �ò���ΪFriendInfo�������,��������ʹ��
	 */
	public void addIntentLinkToFriendInfo(final Context context,
			final Activity activity, final TextView view,
			final CharSequence text, final int start, final int end,
			final int uid) {
		SpannableString sp = new SpannableString(text);
		sp.setSpan(new IntentSpan(new OnClickListener() {

			public void onClick(View view) {

				//Intent userIntent = new Intent();
				//userIntent.setClass(context, FriendPage.class);
				//userIntent.putExtra("uid", uid);
				//context.startActivity(userIntent);
				//activity.overridePendingTransition(R.anim.roll_up, R.anim.roll);
			}
		}), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		sp.setSpan(
				new ForegroundColorSpan(context.getResources().getColor(
						R.color.link_color)), start, end,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		view.setText(sp);
		view.setMovementMethod(LinkMovementMethod.getInstance());
	}

	/**
	 * �����ӵ���־
	 * 
	 * @param context
	 *            ������
	 * @param activity
	 *            Activity
	 * @param view
	 *            ��ʾ��TextView
	 * @param text
	 *            �޸ĵ��ַ�
	 * @param start
	 *            ��ӳ����ӵĳ�ʼλ��
	 * @param end
	 *            ��ӳ����ӵĽ���λ��
	 * @param id
	 *            ��־��ID
	 * @param uid
	 *            ������ID
	 * @param type
	 *            ����������(user,page)
	 */
	public void addIntentLinkToBlog(final Context context,
			final Activity activity, final TextView view,
			final CharSequence text, final int start, final int end,
			final int id, final int uid, final String name,
			final String description, final String type, final int count) {
		SpannableString sp = new SpannableString(text);
		sp.setSpan(new IntentSpan(new OnClickListener() {

			public void onClick(View view) {

				Intent userIntent = new Intent();
				//userIntent.setClass(context, Blog.class);
				userIntent.putExtra("id", id);
				userIntent.putExtra("uid", uid);
				userIntent.putExtra("name", name);
				userIntent.putExtra("description", description);
				userIntent.putExtra("type", type);
				userIntent.putExtra("count", count);
				//context.startActivity(userIntent);
				//activity.overridePendingTransition(R.anim.roll_up, R.anim.roll);
			}
		}), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		sp.setSpan(
				new ForegroundColorSpan(context.getResources().getColor(
						R.color.link_color)), start, end,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		view.setText(sp);
		view.setMovementMethod(LinkMovementMethod.getInstance());
	}

	public void addStrikethrough(final TextView view, CharSequence text,
			final int start, final int end) {
		SpannableString sp = new SpannableString(text);
		sp.setSpan(new StrikethroughSpan(), start, end,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		view.setText(sp);
	}

	public String getCharacterPinYin(char c) {
		HanyuPinyinOutputFormat format = null;
		format = new HanyuPinyinOutputFormat();
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		String[] pinyin = null;
		try {
			pinyin = PinyinHelper.toHanyuPinyinStringArray(c, format);
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
		}
	
		if (pinyin == null)
			return null;
	
		return pinyin[0];
	}


	public String getStringPinYin(String str) {
		StringBuilder sb = new StringBuilder();
		String tempPinyin = null;
		for (int i = 0; i < str.length(); ++i) {
			tempPinyin = getCharacterPinYin(str.charAt(i));
			if (tempPinyin == null) {
				sb.append(str.charAt(i));
			} else {
				sb.append(tempPinyin);
			}
		}
		return sb.toString();
	}
}
