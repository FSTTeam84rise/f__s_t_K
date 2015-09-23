package com.fstrise.ilovekara;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.fstrise.ilovekara.config.Cals;
import com.fstrise.ilovekara.config.Conf;
import com.fstrise.ilovekara.matchview.MatchTextView;
import com.fstrise.ilovekara.utils.Utils;

@SuppressLint("NewApi")
public class FirstPage extends Activity {

	private LinearLayout ll_logo;
	private ImageView img_logo;
	private FrameLayout framTview;
	 MatchTextView tvKar;

	public static int widthScre;
	public static int heightScre;
	public int varWidthScre;
	public int varHeightScree;
	public static int realHeight;
	public static int realWidth;

	private Timer splashTimer;

	private Timer splashTimer1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		DisplayMetrics displayMetrics = Resources.getSystem()
				.getDisplayMetrics();
		heightScre = metrics.heightPixels;
		widthScre = metrics.widthPixels;
		varWidthScre = (int) (displayMetrics.widthPixels
				/ displayMetrics.density + 0.5);
		varHeightScree = (int) (displayMetrics.heightPixels
				/ displayMetrics.density + 0.5);

		Display display = getWindowManager().getDefaultDisplay();
		Method mGetRawH = null, mGetRawW = null;

		try {
			// For JellyBeans and onward
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
				display.getRealMetrics(metrics);
				realHeight = metrics.heightPixels;
				realWidth = metrics.widthPixels;
				if (realHeight == 800) {
					realWidth = widthScre;
					realHeight = heightScre;
				}
				// objCS = new CalScreen(realWidth, realHeight, varWidthScre,
				// varHeightScree);

			} else {
				mGetRawH = Display.class.getMethod("getRawHeight");
				mGetRawW = Display.class.getMethod("getRawWidth");

				try {
					realHeight = (Integer) mGetRawH.invoke(display);
					realWidth = (Integer) mGetRawW.invoke(display);
					if (realHeight == 800) {
						realWidth = widthScre;
						realHeight = heightScre;
					}

				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (Exception x) {

		}

		// if (realWidth < realHeight) {
		// int rw = realWidth;
		// realWidth = realHeight;
		// realHeight = rw;
		// }

		// if (varHeightScree < varWidthScre) {
		// int vw = varHeightScree;
		// varHeightScree = varWidthScre;
		// varWidthScre = vw;
		// }

		new Cals(realWidth, realHeight, varHeightScree, varWidthScre);
		new Conf(realWidth, FirstPage.this);

		Log.i("Screen", realWidth + "-" + realHeight + "-" + varHeightScree
				+ "-" + varWidthScre);
		setContentView(R.layout.layout_firstpage);

		ll_logo = (LinearLayout) findViewById(R.id.ll_logo);
		moveLayout(ll_logo, "y", realHeight, 0);
		img_logo = (ImageView) findViewById(R.id.img_logo);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				Cals.w100 * 4 + Cals.w60, Cals.h100 * 2 + Cals.h70 + Cals.h8);
		params.gravity = Gravity.CENTER;
		img_logo.setLayoutParams(params);

		 tvKar = (MatchTextView) findViewById(R.id.tvKar);
		// tvKar.setVisibility(View.GONE);

		framTview = (FrameLayout) findViewById(R.id.framTview);
		setSplashTimer(new Timer());
		getSplashTimer().schedule(new TimerTask() {
			@Override
			public void run() {
				TimerSplashM();
			}
		}, 0, 1000);
	}

	private void TimerSplashM() {
		this.runOnUiThread(TimerSplashM_Tick);
	}

	private int countdelay = 0;
	private Runnable TimerSplashM_Tick = new Runnable() {
		public void run() {
			countdelay++;
			if (countdelay == 2) {
				moveLayoutNoCondition(ll_logo, "y", realHeight / 2 - Cals.h100
						* 2, 1500);
				splashTimer.cancel();
			}
		}
	};

	public void setSplashTimer(Timer splashTimer) {
		this.splashTimer = splashTimer;
	}

	public Timer getSplashTimer() {
		return splashTimer;
	}

	@SuppressLint("NewApi")
	public void moveLayoutNoCondition(Object flayout, String xory,
			int position, int speed) {
		try {
			ObjectAnimator animation2 = ObjectAnimator.ofFloat(flayout, xory,
					position);
			animation2.setDuration(speed);
			animation2.start();

			animation2.addListener(new AnimatorListener() {

				@Override
				public void onAnimationStart(Animator animation) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationRepeat(Animator animation) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationEnd(Animator animation) {
					splashTimer1 = new Timer();
					splashTimer1.schedule(new TimerTask() {
						@Override
						public void run() {
							TimerSplashM1();
						}
					}, 0, 1000);

				}

				@Override
				public void onAnimationCancel(Animator animation) {
					// TODO Auto-generated method stub

				}
			});
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	@SuppressLint("NewApi")
	public void moveLayout(Object flayout, String xory, int position, int speed) {
		try {
			ObjectAnimator animation2 = ObjectAnimator.ofFloat(flayout, xory,
					position);
			animation2.setDuration(speed);
			animation2.start();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private void TimerSplashM1() {
		this.runOnUiThread(TimerSplashM_Tick1);
	}

	private int count = 0;
	private Runnable TimerSplashM_Tick1 = new Runnable() {
		public void run() {
			count++;
			if (count == 4) {
				splashTimer1.cancel();
				Intent it = new Intent(FirstPage.this, MainActivity.class);
				FirstPage.this.startActivity(it);
				overridePendingTransition(R.anim.open_next, R.anim.close_main);
				FirstPage.this.finish();
			}
		}
	};

	public void onBackPressed() {

	}
}
