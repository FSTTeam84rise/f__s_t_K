package com.fstrise.ilovekara.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.MediaRecorder;
import android.os.Environment;
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.callback.ImageOptions;
import com.fstrise.ilovekara.MainActivity;
import com.fstrise.ilovekara.R;
import com.fstrise.ilovekara.config.Conf;

public class Utils {
public static final String getListKara = "http://fstrise.com/api.aspx?page=";
public static final String searchListKara = "http://fstrise.com/api.aspx?page=";
	public static String getData(String url) {

		// Creating HTTP client
		HttpClient httpClient = new DefaultHttpClient();
		// Creating HTTP Post
		HttpGet httpGet = new HttpGet(url);
		// Making HTTP Request
		try {
			HttpResponse response = httpClient.execute(httpGet);
			// writing response to log
			HttpEntity entity = response.getEntity();
			InputStream is1 = entity.getContent();
			String programlist = convertStreamToString(is1);
			return programlist;
		} catch (ClientProtocolException e) {
			// writing exception to log
			e.printStackTrace();
		} catch (IOException e) {
			// writing exception to log
			e.printStackTrace();

		}
		return "";
	}

	private static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public static void DisplayImage(final String url,
			final ImageView imageView, final int type) {
		MainActivity.aq.id(imageView).image(url, true, true, 0, AQuery.FADE_IN);
		//

	}

	public static String UppercaseFirstLetters(String str) {
		boolean prevWasWhiteSp = true;
		char[] chars = str.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (Character.isLetter(chars[i])) {
				if (prevWasWhiteSp) {
					chars[i] = Character.toUpperCase(chars[i]);
				}
				prevWasWhiteSp = false;
			} else {
				prevWasWhiteSp = Character.isWhitespace(chars[i]);
			}
		}
		return new String(chars);
	}

	private static MediaRecorder myAudioRecorder;
	private static String outputFile = null;

	@SuppressLint("InlinedApi")
	public static void preRecordAudio(String filename) {
		File f = new File(Environment.getExternalStorageDirectory() + "/"
				+ Conf.folderSave + "/mysong/");
		if (!f.isDirectory()) {
			f.mkdir();
		}
		outputFile = filename;

		myAudioRecorder = new MediaRecorder();
		myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
		myAudioRecorder.setOutputFile(outputFile);
	}

	public static void startRecord() {
		try {
			myAudioRecorder.prepare();
			myAudioRecorder.start();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void stopRecord() {
		myAudioRecorder.stop();
		myAudioRecorder.release();
		myAudioRecorder = null;
	}

	public static String convTime(long time) {
		Period period = new Period(time, new DateTime().getMillis());

		// PeriodFormatter formatter = new PeriodFormatterBuilder()
		// .appendSeconds().appendSuffix(" seconds ago\n")
		// .appendMinutes().appendSuffix(" minutes ago\n")
		// .appendHours().appendSuffix(" hours ago\n")
		// .appendDays().appendSuffix(" days ago\n")
		// .appendWeeks().appendSuffix(" weeks ago\n")
		// .appendMonths().appendSuffix(" months ago\n")
		// .appendYears().appendSuffix(" years ago\n").printZeroNever().toFormatter();
		String elapsed = PeriodFormat.getDefault().print(period).split(",")[0];
		if (elapsed.contains("years") || elapsed.contains("year"))
			elapsed = elapsed.replace("years", "năm trước").replace("year",
					"phút trước");
		else if (elapsed.contains("months") || elapsed.contains("month"))
			elapsed = elapsed.replace("months", "tháng trước").replace("month",
					"phút trước");
		else if (elapsed.contains("weeks") || elapsed.contains("week"))
			elapsed = elapsed.replace("weeks", "tuần trước").replace("week",
					"phút trước");
		else if (elapsed.contains("days") || elapsed.contains("day"))
			elapsed = elapsed.replace("days", "ngày trước").replace("day",
					"phút trước");
		else if (elapsed.contains("hours") || elapsed.contains("hour"))
			elapsed = elapsed.replace("hours", "giờ trước").replace("hour",
					"phút trước");
		else if (elapsed.contains("minutes") || elapsed.contains("minute"))
			elapsed = elapsed.replace("minutes", "phút trước").replace(
					"minute", "phút trước");
		else if (elapsed.contains("second"))
			elapsed = elapsed.split(" ")[0] + " giây trước";
		return elapsed;
	}

	public static String formatT(int totalSecs) {
		int hours = totalSecs / 3600;
		int minutes = (totalSecs % 3600) / 60;
		int seconds = totalSecs % 60;
		return String.format("%02d:%02d", minutes, seconds);
	}

	@SuppressLint("NewApi")
	public static void moveView(Object flayout, String xory,
			int position, int speed) {
		try {
			ObjectAnimator animation2 = ObjectAnimator.ofFloat(flayout, xory,
					position);
			animation2.setDuration(speed);
			animation2.start();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
