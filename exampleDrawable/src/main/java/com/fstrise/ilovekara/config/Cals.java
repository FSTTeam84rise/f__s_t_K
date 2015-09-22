package com.fstrise.ilovekara.config;

public class Cals {

	// width
	public static int w1;
	public static int w2;
	public static int w3;
	public static int w4;
	public static int w5;
	public static int w6;
	public static int w7;
	public static int w8;
	public static int w9;
	public static int w10;
	public static int w20;
	public static int w30;
	public static int w40;
	public static int w50;
	public static int w60;
	public static int w70;
	public static int w80;
	public static int w90;
	public static int w100;

	// height
	public static int h1;
	public static int h2;
	public static int h3;
	public static int h4;
	public static int h5;
	public static int h6;
	public static int h7;
	public static int h8;
	public static int h9;
	public static int h10;
	public static int h20;
	public static int h30;
	public static int h40;
	public static int h50;
	public static int h60;
	public static int h70;
	public static int h80;
	public static int h90;
	public static int h100;


	public Cals(int width, int height, int mWidth, int mHeight) {
		int tempw = width;
		if (width < height) {
			width = height;
			height = tempw;
		}
		
		
		// height

		w1 = (int) (0.0013888888888889 * height);
		w2 = (int) (0.0027777777777778 * height);
		w3 = (int) (0.0041666666666667 * height);
		w4 = (int) (0.0055555555555556 * height);
		w5 = (int) (0.0069444444444444 * height);
		w6 = (int) (0.0083333333333333 * height);
		w7 = (int) (0.0097222222222222 * height);
		w8 = (int) (0.0111111111111111 * height);
		w9 = (int) (0.0125 * height);
		w10 = (int) (0.013888888888889 * height);
		w20 = (int) (0.027777777777778 * height);
		w30 = (int) (0.041666666666667 * height);
		w40 = (int) (0.055555555555556 * height);
		w50 = (int) (0.069444444444444 * height);
		w60 = (int) (0.083333333333333 * height);
		w70 = (int) (0.097222222222222 * height);
		w80 = (int) (0.111111111111111 * height);
		w90 = (int) (0.125 * height);
		w100 = (int) (0.13888888888889 * height);
	
		
		h1 = (int) (0.00078125 * width);
		h2 = (int) (0.0015625 * width);
		h3 = (int) (0.00234375 * width);
		h4 = (int) (0.003125 * width);
		h5 = (int) (0.00390625 * width);
		h6 = (int) (0.0046875 * width);
		h7 = (int) (0.00546875 * width);
		h8 = (int) (0.00625 * width);
		h9 = (int) (0.00703125 * width);
		h10 = (int) (0.0078125 * width);
		h20 = (int) (0.015625 * width);
		h30 = (int) (0.0234375 * width);
		h40 = (int) (0.03125 * width);
		h50 = (int) (0.0390625 * width);
		h60 = (int) (0.046875 * width);
		h70 = (int) (0.0546875 * width);
		h80 = (int) (0.0625 * width);
		h90 = (int) (0.0703125 * width);
		h100 = (int) (0.078125 * width);

	}
}
