package com.fstrise.ilovekara.customdialog;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar.OnProgressChangeListener;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fstrise.ilovekara.R;
import com.fstrise.ilovekara.customdialog.effect.BaseEffects;
import com.fstrise.ilovekara.utils.Utils;

/**
 * Created by lee on 2014/7/30.
 */
public class NiftyPlayerMusicDialogBuilder extends Dialog implements
		DialogInterface {

	private final String defTextColor = "#FFFFFFFF";

	private final String defDividerColor = "#11000000";

	private final String defMsgColor = "#FFFFFFFF";

	private final String defDialogColor = "#FFE74C3C";

	private static Context tmpContext;

	private Effectstype type = null;

	private LinearLayout mLinearLayoutView;

	private RelativeLayout mRelativeLayoutView;

	private LinearLayout mLinearLayoutMsgView;

	private LinearLayout mLinearLayoutTopView;

	private FrameLayout mFrameLayoutCustomView;

	private View mDialogView;

	private View mDivider;

	private TextView mTitle;

	// private TextView mMessage;

	private ImageView mIcon;

	private Button mButton1;

	private Button mButton2;
	private Button mButton3;
	private int mDuration = -1;

	private static int mOrientation = 1;

	private boolean isCancelable = true;

	private static NiftyPlayerMusicDialogBuilder instance;
	//
	MediaPlayer mediaPlayer;
	private int stateMediaPlayer;
	private final int stateMP_Error = 0;
	private final int stateMP_NotStarter = 1;
	private final int stateMP_Playing = 2;
	private final int stateMP_Pausing = 3;
	private Button btnPlayMusic;
	private boolean isPlaymusic;
	private TextView txtStartTime;
	private TextView txtEndTime;
	private DiscreteSeekBar seekbarMusic;
	private boolean isSeeking;
	private int durationPlay;
	private int duration;
	private int timeSeek;

	public NiftyPlayerMusicDialogBuilder(Context context) {
		super(context);
		init(context);

	}

	public NiftyPlayerMusicDialogBuilder(Context context, int theme) {
		super(context, theme);
		init(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.height = ViewGroup.LayoutParams.MATCH_PARENT;
		params.width = ViewGroup.LayoutParams.MATCH_PARENT;
		getWindow().setAttributes(
				(android.view.WindowManager.LayoutParams) params);

	}

	public static NiftyPlayerMusicDialogBuilder getInstance(Context context) {

		if (instance == null || !tmpContext.equals(context)) {
			synchronized (NiftyPlayerMusicDialogBuilder.class) {
				if (instance == null || !tmpContext.equals(context)) {
					instance = new NiftyPlayerMusicDialogBuilder(context,
							R.style.dialog_untran);
				}
			}
		}
		tmpContext = context;
		return instance;

	}

	private void init(Context context) {

		mDialogView = View.inflate(context, R.layout.layout_playmusic, null);

		mLinearLayoutView = (LinearLayout) mDialogView
				.findViewById(R.id.parentPanel);
		mRelativeLayoutView = (RelativeLayout) mDialogView
				.findViewById(R.id.main);
		mLinearLayoutTopView = (LinearLayout) mDialogView
				.findViewById(R.id.topPanel);
		mLinearLayoutMsgView = (LinearLayout) mDialogView
				.findViewById(R.id.contentPanel);
		mFrameLayoutCustomView = (FrameLayout) mDialogView
				.findViewById(R.id.customPanel);

		mTitle = (TextView) mDialogView.findViewById(R.id.alertTitle);
		// mMessage = (TextView) mDialogView.findViewById(R.id.message);
		mIcon = (ImageView) mDialogView.findViewById(R.id.icon);
		mDivider = mDialogView.findViewById(R.id.titleDivider);
		mButton1 = (Button) mDialogView.findViewById(R.id.button1);
		mButton2 = (Button) mDialogView.findViewById(R.id.button2);
		mButton3 = (Button) mDialogView.findViewById(R.id.button3);
		btnPlayMusic = (Button) mDialogView.findViewById(R.id.btnPlayMusic);
		txtStartTime = (TextView) mDialogView.findViewById(R.id.txtStartTime);
		txtEndTime = (TextView) mDialogView.findViewById(R.id.txtEndTime);
		seekbarMusic = (DiscreteSeekBar) mDialogView
				.findViewById(R.id.seekbarMusic);
		seekbarMusic
				.setOnProgressChangeListener(new OnProgressChangeListener() {

					@Override
					public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
						isSeeking = false;
						mediaPlayer.seekTo(timeSeek * 1000);
					}

					@Override
					public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
						isSeeking = true;

					}

					@Override
					public void onProgressChanged(DiscreteSeekBar seekBar,
							int value, boolean fromUser) {
						timeSeek = (value * duration) / 3600;
						seekbarMusic.setIndicatorFormatter(Utils
								.formatT(timeSeek));

					}
				});
		//
		btnPlayMusic.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!isPlaymusic) {
					isPlaymusic = true;
					mediaPlayer.start();
					btnPlayMusic.setBackgroundResource(R.drawable.btnpause);
				} else {
					isPlaymusic = false;
					mediaPlayer.pause();
					btnPlayMusic.setBackgroundResource(R.drawable.btnplay);
				}

			}
		});
		this.setCancelable(false);
		this.setCanceledOnTouchOutside(false);
		setContentView(mDialogView);

		this.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialogInterface) {

				mLinearLayoutView.setVisibility(View.VISIBLE);
				if (type == null) {
					type = Effectstype.Slidetop;
				}
				start(type);

			}
		});

		mRelativeLayoutView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (isCancelable)
					dismiss();
			}
		});
	}

	public void toDefault() {
		mTitle.setTextColor(Color.parseColor(defTextColor));
		mDivider.setBackgroundColor(Color.parseColor(defDividerColor));
		// mMessage.setTextColor(Color.parseColor(defMsgColor));
		mLinearLayoutView.setBackgroundColor(Color.parseColor(defDialogColor));
	}

	public NiftyPlayerMusicDialogBuilder withDividerColor(String colorString) {
		mDivider.setBackgroundColor(Color.parseColor(colorString));
		return this;
	}

	public NiftyPlayerMusicDialogBuilder withDividerColor(int color) {
		mDivider.setBackgroundColor(color);
		return this;
	}

	public NiftyPlayerMusicDialogBuilder withTitle(CharSequence title) {
		toggleView(mLinearLayoutTopView, title);
		mTitle.setText(title);
		return this;
	}

	public NiftyPlayerMusicDialogBuilder withTitleColor(String colorString) {
		mTitle.setTextColor(Color.parseColor(colorString));
		return this;
	}

	public NiftyPlayerMusicDialogBuilder withTitleColor(int color) {
		mTitle.setTextColor(color);
		return this;
	}

	public NiftyPlayerMusicDialogBuilder withMessage(int textResId) {
		toggleView(mLinearLayoutMsgView, textResId);
		// mMessage.setText(textResId);
		return this;
	}

	public NiftyPlayerMusicDialogBuilder withMessage(CharSequence msg) {
		toggleView(mLinearLayoutMsgView, msg);
		// mMessage.setText(msg);
		return this;
	}

	public NiftyPlayerMusicDialogBuilder withMessageColor(String colorString) {
		// mMessage.setTextColor(Color.parseColor(colorString));
		return this;
	}

	public NiftyPlayerMusicDialogBuilder withMessageColor(int color) {
		// mMessage.setTextColor(color);
		return this;
	}

	public NiftyPlayerMusicDialogBuilder withDialogColor(String colorString) {
		mLinearLayoutView.getBackground().setColorFilter(
				ColorUtils.getColorFilter(Color.parseColor(colorString)));
		return this;
	}

	public NiftyPlayerMusicDialogBuilder withDialogColor(int color) {
		mLinearLayoutView.getBackground().setColorFilter(
				ColorUtils.getColorFilter(color));
		return this;
	}

	public NiftyPlayerMusicDialogBuilder withIcon(int drawableResId) {
		mIcon.setImageResource(drawableResId);
		return this;
	}

	public NiftyPlayerMusicDialogBuilder withIcon(Drawable icon) {
		mIcon.setImageDrawable(icon);
		return this;
	}

	public NiftyPlayerMusicDialogBuilder withDuration(int duration) {
		this.mDuration = duration;
		return this;
	}

	public NiftyPlayerMusicDialogBuilder withEffect(Effectstype type) {
		this.type = type;
		return this;
	}

	public NiftyPlayerMusicDialogBuilder withButtonDrawable(int resid) {
		mButton1.setBackgroundResource(resid);
		mButton2.setBackgroundResource(resid);
		return this;
	}

	public NiftyPlayerMusicDialogBuilder withButton1Text(CharSequence text) {
		mButton1.setVisibility(View.VISIBLE);
		// mButton1.setText(text);

		return this;
	}

	public NiftyPlayerMusicDialogBuilder withButton2Text(CharSequence text) {
		mButton2.setVisibility(View.VISIBLE);
		// mButton2.setText(text);
		return this;
	}

	public NiftyPlayerMusicDialogBuilder withURLPlay(String text) {
		initMediaPlayer(text);
		return this;
	}

	public NiftyPlayerMusicDialogBuilder setButton1Click(
			View.OnClickListener click) {
		mButton1.setOnClickListener(click);
		return this;
	}

	public NiftyPlayerMusicDialogBuilder setButton2Click(
			View.OnClickListener click) {
		mButton2.setOnClickListener(click);
		return this;
	}

	public NiftyPlayerMusicDialogBuilder setButton3Click(
			View.OnClickListener click) {
		mButton3.setOnClickListener(click);
		return this;
	}

	public NiftyPlayerMusicDialogBuilder setCustomView(int resId,
			Context context) {
		View customView = View.inflate(context, resId, null);
		if (mFrameLayoutCustomView.getChildCount() > 0) {
			mFrameLayoutCustomView.removeAllViews();
		}
		mFrameLayoutCustomView.addView(customView);
		return this;
	}

	public NiftyPlayerMusicDialogBuilder setCustomView(View view,
			Context context) {
		if (mFrameLayoutCustomView.getChildCount() > 0) {
			mFrameLayoutCustomView.removeAllViews();
		}
		mFrameLayoutCustomView.addView(view);

		return this;
	}

	public NiftyPlayerMusicDialogBuilder isCancelableOnTouchOutside(
			boolean cancelable) {
		this.isCancelable = cancelable;
		this.setCanceledOnTouchOutside(cancelable);
		return this;
	}

	public NiftyPlayerMusicDialogBuilder isCancelable(boolean cancelable) {
		this.isCancelable = cancelable;
		this.setCancelable(cancelable);
		return this;
	}

	private void toggleView(View view, Object obj) {
		if (obj == null) {
			view.setVisibility(View.GONE);
		} else {
			view.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void show() {
		super.show();
	}

	private void start(Effectstype type) {
		BaseEffects animator = type.getAnimator();
		if (mDuration != -1) {
			animator.setDuration(Math.abs(mDuration));
		}
		animator.start(mRelativeLayoutView);
	}

	@Override
	public void dismiss() {
		super.dismiss();
		mButton1.setVisibility(View.GONE);
		mButton2.setVisibility(View.GONE);
	}

	private void initMediaPlayer(String path) {
		String PATH_TO_FILE = path;
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				isPlaymusic = false;
				btnPlayMusic.setBackgroundResource(R.drawable.btnplay);
			}
		});
		try {
			mediaPlayer.setDataSource(PATH_TO_FILE);
			mediaPlayer.prepare();
			Toast.makeText(tmpContext, PATH_TO_FILE, Toast.LENGTH_LONG).show();
			stateMediaPlayer = stateMP_NotStarter;
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(tmpContext, e.toString(), Toast.LENGTH_LONG).show();
			stateMediaPlayer = stateMP_Error;
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(tmpContext, e.toString(), Toast.LENGTH_LONG).show();
			stateMediaPlayer = stateMP_Error;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(tmpContext, e.toString(), Toast.LENGTH_LONG).show();
			stateMediaPlayer = stateMP_Error;
		}
		isPlaymusic = true;
		mediaPlayer.start();
		btnPlayMusic.setBackgroundResource(R.drawable.btnpause);
	}

	public void timerplaying() {
		if (mediaPlayer != null && !isSeeking) {
			duration = mediaPlayer.getDuration() / 1000;
			durationPlay = mediaPlayer.getCurrentPosition() / 1000;
			txtStartTime.setText(Utils.formatT(durationPlay));
			txtEndTime.setText(Utils.formatT(duration));
			//
			int curPosSeek = (durationPlay * 3600) / duration;
			seekbarMusic.setProgress(curPosSeek);
		}
	}

	public void stopMediaPlayer() {
		if (mediaPlayer != null) {
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	@Override
	protected void onStop() {
		stopMediaPlayer();
		super.onStop();
	}
}
