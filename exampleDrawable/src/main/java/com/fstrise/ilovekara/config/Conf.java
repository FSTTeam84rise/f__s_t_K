package com.fstrise.ilovekara.config;

import android.content.Context;
import android.graphics.Typeface;

public class Conf {
	public static int textSize21 = 21;
	public static int textSize22 = 22;
	public static int textSize23 = 23;
	public static int textSize25 = 25;
	public static int textSize30 = 30;
	public static int textSize20 = 20;
	public static int textSize15 = 15;
	public static int textSize13 = 13;
	public static int textSize10 = 10;
	public static int textSize17 = 17;
	public static int textSize12 = 12;
	public static int textSize18 = 18;
	public static int textSize40 = 40;
	public static int textSize35 = 35;
	public static int textSize36 = 36;
	
	public static Typeface Roboto_Thin;
	public static Typeface Roboto_LightItalic;
	public static Typeface Roboto_Regular;
	public static Typeface Roboto_Bold;

	public Conf(int w, Context mContext) {
		
		textSize10 = (w * 10) / 1280;
		textSize12 = (w * 12) / 1280;
		textSize13 = (w * 13) / 1280;
		textSize15 = (w * 15) / 1280;
		textSize20 = (w * 20) / 1280;
		textSize21 = (w * 21) / 1280;
		textSize22 = (w * 22) / 1280;
		textSize23 = (w * 23) / 1280;
		textSize25 = (w * 25) / 1280;
		textSize30 = (w * 30) / 1280;
		textSize17 = (w * 17) / 1280;
		textSize18 = (w * 18) / 1280;
		textSize35 = (w * 35) / 1280;
		textSize36 = (w * 36) / 1280;
		textSize40 = (w * 40) / 1280;
		
		Roboto_Thin = Typeface.createFromAsset(mContext.getAssets(),
				"fonts/Roboto-Thin.ttf");
		Roboto_LightItalic = Typeface.createFromAsset(mContext.getAssets(),
				"fonts/Roboto-LightItalic.ttf");
		Roboto_Regular = Typeface.createFromAsset(mContext.getAssets(),
				"fonts/Roboto-Regular.ttf");
		Roboto_Bold = Typeface.createFromAsset(mContext.getAssets(),
				"fonts/Roboto-Bold.ttf");


	}
}
