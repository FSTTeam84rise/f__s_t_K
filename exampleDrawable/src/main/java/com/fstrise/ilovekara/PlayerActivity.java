/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fstrise.ilovekara;

import java.io.File;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar.OnProgressChangeListener;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.CaptioningManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.FrameLayout.LayoutParams;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.fstrise.ilovekara.config.Cals;
import com.fstrise.ilovekara.config.Conf;
import com.fstrise.ilovekara.player.DashRendererBuilder;
import com.fstrise.ilovekara.player.DefaultRendererBuilder;
import com.fstrise.ilovekara.player.DemoPlayer;
import com.fstrise.ilovekara.player.DemoPlayer.RendererBuilder;
import com.fstrise.ilovekara.player.DemoUtil;
import com.fstrise.ilovekara.player.EventLogger;
import com.fstrise.ilovekara.player.HlsRendererBuilder;
import com.fstrise.ilovekara.player.SmoothStreamingRendererBuilder;
import com.fstrise.ilovekara.player.SmoothStreamingTestMediaDrmCallback;
import com.fstrise.ilovekara.player.UnsupportedDrmException;
import com.fstrise.ilovekara.player.WidevineTestMediaDrmCallback;
import com.fstrise.ilovekara.utils.NotificationUtils;
import com.fstrise.ilovekara.utils.StorageUtils;
import com.fstrise.ilovekara.utils.Utils;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.VideoSurfaceView;
import com.google.android.exoplayer.metadata.TxxxMetadata;
import com.google.android.exoplayer.text.CaptionStyleCompat;
import com.google.android.exoplayer.text.SubtitleView;
import com.google.android.exoplayer.util.Util;
import com.google.android.exoplayer.util.VerboseLogUtil;

/**
 * An activity that plays media using {@link DemoPlayer}.
 */
@SuppressLint("NewApi")
public class PlayerActivity extends Activity implements SurfaceHolder.Callback,
		OnClickListener, DemoPlayer.Listener, DemoPlayer.TextListener,
		DemoPlayer.Id3MetadataListener {

	public static final String CONTENT_TYPE_EXTRA = "content_type";
	public static final String CONTENT_ID_EXTRA = "content_id";

	private static final String TAG = "PlayerActivity";

	private static final float CAPTION_LINE_HEIGHT_RATIO = 0.0533f;
	private static final int MENU_GROUP_TRACKS = 1;
	private static final int ID_OFFSET = 2;

	private EventLogger eventLogger;
	// private MediaController mediaController;
	private View debugRootView;
	private View shutterView;
	private VideoSurfaceView surfaceView;
	private TextView debugTextView;
	private TextView playerStateTextView;
	private SubtitleView subtitleView;
	private Button videoButton;
	private Button audioButton;
	private Button textButton;
	private Button retryButton;
	private LinearLayout layoutOSD;

	private DemoPlayer player;
	private boolean playerNeedsPrepare;

	private long playerPosition;
	private boolean enableBackgroundAudio;

	private Uri contentUri;
	private int contentType;
	private String contentId;
	private String title;
	private boolean isShowOSD;
	private boolean isPause;
	private Button btnPlay;
	private Button btnRecord;
	private DiscreteSeekBar seekBarPlayer;
	private TextView txtStatus;
	private TextView txtST;
	private TextView txtET;
	private TextView txtTitle;
	private Timer mTimer;
	private boolean isSeeking;
	private boolean allowRecord;
	private int timeSeek;
	private int durationPlay;
	private String fileName;

	// Activity lifecycle

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		contentUri = Uri
				.parse("/storage/emulated/0/iLoveKara/dlvideo/Anh_Ba_Khía.mp4");

		contentType = 2;
		contentId = "dizzy";
		Intent intent = getIntent();
		if (null != intent) { // Null Checking
			contentUri = Uri.parse(intent.getStringExtra("url"));
			title = intent.getStringExtra("title");

		}
		//
		// record ui
		if (!StorageUtils.checkExternalStorageAvailable()) {
			NotificationUtils.showInfoDialog(this,
					"External storage is unavailable");
			return;
		}

		fileName = android.os.Environment.getExternalStorageDirectory() + "/"
				+ Conf.folderSave + "/mysong/" + title.replace(" ", "_")
				+ ".mp3";

		for (int i = 1; i <= 50; i++) {
			File checkExist = new File(fileName);
			if (checkExist.exists()) {
				fileName = android.os.Environment.getExternalStorageDirectory()
						+ "/" + Conf.folderSave + "/mysong/"
						+ title.replace(" ", "_") + "_" + i + ".mp3";
			} else {
				break;
			}
		}
		// fileNameTemp = android.os.Environment.getExternalStorageDirectory()
		// + "/" + Conf.folderSave + "/mysong/temp.wav";
		Utils.preRecordAudio(fileName);
		//
		setContentView(R.layout.player_activity);
		View root = findViewById(R.id.root);
		root.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
					// toggleControlsVisibility();
					if (!isShowOSD) {
						isShowOSD = true;
						layoutOSD.setVisibility(View.VISIBLE);
					} else {
						isShowOSD = false;
						layoutOSD.setVisibility(View.GONE);
					}

				} else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
					view.performClick();
				}
				return true;
			}
		});

		shutterView = findViewById(R.id.shutter);
		debugRootView = findViewById(R.id.controls_root);

		surfaceView = (VideoSurfaceView) findViewById(R.id.surface_view);
		surfaceView.getHolder().addCallback(this);
		debugTextView = (TextView) findViewById(R.id.debug_text_view);

		playerStateTextView = (TextView) findViewById(R.id.player_state_view);
		subtitleView = (SubtitleView) findViewById(R.id.subtitles);

		// mediaController = new MediaController(this);
		// mediaController.setAnchorView(root);
		retryButton = (Button) findViewById(R.id.retry_button);
		retryButton.setOnClickListener(this);
		videoButton = (Button) findViewById(R.id.video_controls);
		audioButton = (Button) findViewById(R.id.audio_controls);
		textButton = (Button) findViewById(R.id.text_controls);
		//
		layoutOSD = (LinearLayout) findViewById(R.id.layoutOSD);
		layoutOSD.setVisibility(View.VISIBLE);
		//
		LinearLayout.LayoutParams lpBtn = new LinearLayout.LayoutParams(
				Cals.w100, Cals.w100);
		lpBtn.leftMargin = Cals.w10;
		btnPlay = (Button) findViewById(R.id.btnPlay);
		btnPlay.setLayoutParams(lpBtn);
		btnPlay.setOnClickListener(playClick);
		btnPlay.setVisibility(View.VISIBLE);
		btnRecord = (Button) findViewById(R.id.btnRecord);
		btnRecord.setLayoutParams(lpBtn);
		btnRecord.setOnClickListener(playClick);
		btnRecord.setVisibility(View.VISIBLE);
		//
		txtStatus = (TextView) findViewById(R.id.txtStatus);
		txtStatus.setTextSize((float) Conf.textSize17);
		txtStatus.setTypeface(Conf.Roboto_LightItalic);
		//
		txtTitle = (TextView) findViewById(R.id.txtTitle);
		txtTitle.setTextSize((float) Conf.textSize17);
		txtTitle.setTypeface(Conf.Roboto_Thin);
		txtTitle.setText(title);
		//
		txtST = (TextView) findViewById(R.id.txtST);
		txtST.setTextSize((float) Conf.textSize20);
		txtST.setTypeface(Conf.Roboto_Bold);
		FrameLayout.LayoutParams lpST = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lpST.leftMargin = Cals.w30;
		txtST.setLayoutParams(lpST);
		txtST.setText("00:00");
		//
		txtET = (TextView) findViewById(R.id.txtET);
		txtET.setTextSize((float) Conf.textSize20);
		txtET.setTypeface(Conf.Roboto_Bold);
		FrameLayout.LayoutParams lpET = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				Gravity.RIGHT);
		lpET.rightMargin = Cals.w80;
		txtET.setLayoutParams(lpET);
		txtET.setText("00:00");

		//
		seekBarPlayer = (DiscreteSeekBar) findViewById(R.id.discrete3);
		LinearLayout.LayoutParams lpSB = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lpSB.topMargin = -Cals.h20;
		lpSB.rightMargin = Cals.w50;
		seekBarPlayer.setLayoutParams(lpSB);
		onTouchSeekbar();
		DemoUtil.setDefaultCookieManager();
	}

	private void onTouchSeekbar() {
		seekBarPlayer
				.setOnProgressChangeListener(new OnProgressChangeListener() {

					@Override
					public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
						isSeeking = false;
						player.seekTo(timeSeek * 1000);
					}

					@Override
					public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
						isSeeking = true;
					}

					@Override
					public void onProgressChanged(DiscreteSeekBar seekBar,
							int value, boolean fromUser) {
						timeSeek = (value * durationPlay) / 3600;
						seekBarPlayer.setIndicatorFormatter(formatT(timeSeek));

					}
				});

	}

	@Override
	public void onResume() {
		super.onResume();
		isShowOSD = true;
		setmTimer(new Timer());
		getmTimer().schedule(new TimerTask() {
			@Override
			public void run() {
				TimerM();
			}
		}, 0, 1000);

		configureSubtitleView();
		if (player == null) {
			preparePlayer();
		} else if (player != null) {
			player.setBackgrounded(false);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		getmTimer().cancel();
		if (!enableBackgroundAudio) {
			releasePlayer();
		} else {
			player.setBackgrounded(true);

		}
		shutterView.setVisibility(View.VISIBLE);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		releasePlayer();
	}

	// OnClickListener methods

	@Override
	public void onClick(View view) {
		if (view == retryButton) {
			preparePlayer();
		}
	}

	// Internal methods

	private RendererBuilder getRendererBuilder() {
		String userAgent = DemoUtil.getUserAgent(this);
		switch (contentType) {
		case DemoUtil.TYPE_SS:
			return new SmoothStreamingRendererBuilder(userAgent,
					contentUri.toString(), contentId,
					new SmoothStreamingTestMediaDrmCallback(), debugTextView);
		case DemoUtil.TYPE_DASH:
			return new DashRendererBuilder(userAgent, contentUri.toString(),
					contentId, new WidevineTestMediaDrmCallback(contentId),
					debugTextView);
		case DemoUtil.TYPE_HLS:
			return new HlsRendererBuilder(userAgent, contentUri.toString(),
					contentId);
		default:
			return new DefaultRendererBuilder(this, contentUri, debugTextView);
		}
	}

	private void preparePlayer() {
		if (player == null) {
			player = new DemoPlayer(getRendererBuilder());
			player.addListener(this);
			player.setTextListener(this);
			player.setMetadataListener(this);
			player.seekTo(playerPosition);
			playerNeedsPrepare = true;
			// mediaController.setMediaPlayer(player.getPlayerControl());
			// mediaController.setEnabled(true);
			eventLogger = new EventLogger();
			eventLogger.startSession();
			player.addListener(eventLogger);
			player.setInfoListener(eventLogger);
			player.setInternalErrorListener(eventLogger);
		}
		if (playerNeedsPrepare) {
			player.prepare();
			playerNeedsPrepare = false;
			updateButtonVisibilities();
		}
		player.setSurface(surfaceView.getHolder().getSurface());
		player.setPlayWhenReady(true);
	}

	private void releasePlayer() {
		if (player != null) {
			playerPosition = player.getCurrentPosition();
			player.release();
			player = null;
			eventLogger.endSession();
			eventLogger = null;
		}
	}

	// DemoPlayer.Listener implementation

	@Override
	public void onStateChanged(boolean playWhenReady, int playbackState) {
		if (playbackState == ExoPlayer.STATE_ENDED) {
			showControls();
		}
		String text = "playWhenReady=" + playWhenReady + ", playbackState=";
		switch (playbackState) {
		case ExoPlayer.STATE_BUFFERING:
			text += "buffering";
			break;
		case ExoPlayer.STATE_ENDED:
			text += "ended";
			videoEnd();
			break;
		case ExoPlayer.STATE_IDLE:
			text += "idle";
			break;
		case ExoPlayer.STATE_PREPARING:
			text += "preparing";
			break;
		case ExoPlayer.STATE_READY:
			text += "ready";
			break;
		default:
			text += "unknown";
			break;
		}
		playerStateTextView.setText(text);
		updateButtonVisibilities();
	}

	@Override
	public void onError(Exception e) {
		if (e instanceof UnsupportedDrmException) {
			// Special case DRM failures.
			UnsupportedDrmException unsupportedDrmException = (UnsupportedDrmException) e;
			int stringId = unsupportedDrmException.reason == UnsupportedDrmException.REASON_NO_DRM ? R.string.drm_error_not_supported
					: unsupportedDrmException.reason == UnsupportedDrmException.REASON_UNSUPPORTED_SCHEME ? R.string.drm_error_unsupported_scheme
							: R.string.drm_error_unknown;
			Toast.makeText(getApplicationContext(), stringId, Toast.LENGTH_LONG)
					.show();
		}
		playerNeedsPrepare = true;
		updateButtonVisibilities();
		showControls();
	}

	@Override
	public void onVideoSizeChanged(int width, int height,
			float pixelWidthAspectRatio) {
		shutterView.setVisibility(View.GONE);
		surfaceView.setVideoWidthHeightRatio(height == 0 ? 1
				: (width * pixelWidthAspectRatio) / height);
	}

	// User controls

	private void updateButtonVisibilities() {
		retryButton
				.setVisibility(playerNeedsPrepare ? View.VISIBLE : View.GONE);
		videoButton
				.setVisibility(haveTracks(DemoPlayer.TYPE_VIDEO) ? View.VISIBLE
						: View.GONE);
		audioButton
				.setVisibility(haveTracks(DemoPlayer.TYPE_AUDIO) ? View.VISIBLE
						: View.GONE);
		textButton
				.setVisibility(haveTracks(DemoPlayer.TYPE_TEXT) ? View.VISIBLE
						: View.GONE);
	}

	private boolean haveTracks(int type) {
		return player != null && player.getTracks(type) != null;
	}

	public void showVideoPopup(View v) {
		PopupMenu popup = new PopupMenu(this, v);
		configurePopupWithTracks(popup, null, DemoPlayer.TYPE_VIDEO);
		popup.show();
	}

	public void showAudioPopup(View v) {
		PopupMenu popup = new PopupMenu(this, v);
		Menu menu = popup.getMenu();
		menu.add(Menu.NONE, Menu.NONE, Menu.NONE,
				R.string.enable_background_audio);
		final MenuItem backgroundAudioItem = menu.findItem(0);
		backgroundAudioItem.setCheckable(true);
		backgroundAudioItem.setChecked(enableBackgroundAudio);
		OnMenuItemClickListener clickListener = new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if (item == backgroundAudioItem) {
					enableBackgroundAudio = !item.isChecked();
					return true;
				}
				return false;
			}
		};
		configurePopupWithTracks(popup, clickListener, DemoPlayer.TYPE_AUDIO);
		popup.show();
	}

	public void showTextPopup(View v) {
		PopupMenu popup = new PopupMenu(this, v);
		configurePopupWithTracks(popup, null, DemoPlayer.TYPE_TEXT);
		popup.show();
	}

	public void showVerboseLogPopup(View v) {
		PopupMenu popup = new PopupMenu(this, v);
		Menu menu = popup.getMenu();
		menu.add(Menu.NONE, 0, Menu.NONE, R.string.logging_normal);
		menu.add(Menu.NONE, 1, Menu.NONE, R.string.logging_verbose);
		menu.setGroupCheckable(Menu.NONE, true, true);
		menu.findItem((VerboseLogUtil.areAllTagsEnabled()) ? 1 : 0).setChecked(
				true);
		popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if (item.getItemId() == 0) {
					VerboseLogUtil.setEnableAllTags(false);
				} else {
					VerboseLogUtil.setEnableAllTags(true);
				}
				return true;
			}
		});
		popup.show();
	}

	private void configurePopupWithTracks(PopupMenu popup,
			final OnMenuItemClickListener customActionClickListener,
			final int trackType) {
		if (player == null) {
			return;
		}
		String[] tracks = player.getTracks(trackType);
		if (tracks == null) {
			return;
		}
		popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				return (customActionClickListener != null && customActionClickListener
						.onMenuItemClick(item))
						|| onTrackItemClick(item, trackType);
			}
		});
		Menu menu = popup.getMenu();
		// ID_OFFSET ensures we avoid clashing with Menu.NONE (which equals 0)
		menu.add(MENU_GROUP_TRACKS, DemoPlayer.DISABLED_TRACK + ID_OFFSET,
				Menu.NONE, R.string.off);
		if (tracks.length == 1 && TextUtils.isEmpty(tracks[0])) {
			menu.add(MENU_GROUP_TRACKS, DemoPlayer.PRIMARY_TRACK + ID_OFFSET,
					Menu.NONE, R.string.on);
		} else {
			for (int i = 0; i < tracks.length; i++) {
				menu.add(MENU_GROUP_TRACKS, i + ID_OFFSET, Menu.NONE, tracks[i]);
			}
		}
		menu.setGroupCheckable(MENU_GROUP_TRACKS, true, true);
		menu.findItem(player.getSelectedTrackIndex(trackType) + ID_OFFSET)
				.setChecked(true);
	}

	private boolean onTrackItemClick(MenuItem item, int type) {
		if (player == null || item.getGroupId() != MENU_GROUP_TRACKS) {
			return false;
		}
		player.selectTrack(type, item.getItemId() - ID_OFFSET);
		return true;
	}

	private void toggleControlsVisibility() {
		// if (mediaController.isShowing()) {
		// mediaController.hide();
		// debugRootView.setVisibility(View.GONE);
		// } else {
		// showControls();
		// }
	}

	private void showControls() {
		// mediaController.show(0);
		debugRootView.setVisibility(View.VISIBLE);
	}

	// DemoPlayer.TextListener implementation

	@Override
	public void onText(String text) {
		if (TextUtils.isEmpty(text)) {
			subtitleView.setVisibility(View.INVISIBLE);
		} else {
			subtitleView.setVisibility(View.VISIBLE);
			subtitleView.setText(text);
		}
	}

	// DemoPlayer.MetadataListener implementation

	@Override
	public void onId3Metadata(Map<String, Object> metadata) {
		for (int i = 0; i < metadata.size(); i++) {
			if (metadata.containsKey(TxxxMetadata.TYPE)) {
				TxxxMetadata txxxMetadata = (TxxxMetadata) metadata
						.get(TxxxMetadata.TYPE);
				Log.i(TAG, String.format(
						"ID3 TimedMetadata: description=%s, value=%s",
						txxxMetadata.description, txxxMetadata.value));
			}
		}
	}

	// SurfaceHolder.Callback implementation

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (player != null) {
			player.setSurface(holder.getSurface());
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// Do nothing.
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (player != null) {
			player.blockingClearSurface();
		}
	}

	private void configureSubtitleView() {
		CaptionStyleCompat captionStyle;
		float captionTextSize = getCaptionFontSize();
		if (Util.SDK_INT >= 19) {
			captionStyle = getUserCaptionStyleV19();
			captionTextSize *= getUserCaptionFontScaleV19();
		} else {
			captionStyle = CaptionStyleCompat.DEFAULT;
		}
		subtitleView.setStyle(captionStyle);
		subtitleView.setTextSize(captionTextSize);
	}

	private float getCaptionFontSize() {
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		Point displaySize = new Point();
		display.getSize(displaySize);
		return Math
				.max(getResources().getDimension(
						R.dimen.subtitle_minimum_font_size),
						CAPTION_LINE_HEIGHT_RATIO
								* Math.min(displaySize.x, displaySize.y));
	}

	@TargetApi(19)
	private float getUserCaptionFontScaleV19() {
		CaptioningManager captioningManager = (CaptioningManager) getSystemService(Context.CAPTIONING_SERVICE);
		return captioningManager.getFontScale();
	}

	@TargetApi(19)
	private CaptionStyleCompat getUserCaptionStyleV19() {
		CaptioningManager captioningManager = (CaptioningManager) getSystemService(Context.CAPTIONING_SERVICE);
		return CaptionStyleCompat.createFromCaptionStyle(captioningManager
				.getUserStyle());
	}

	private Timer getmTimer() {
		return mTimer;
	}

	private void setmTimer(Timer mTimer) {
		this.mTimer = mTimer;
	}

	private OnClickListener playClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnPlay:
				playvideo();
				break;
			case R.id.btnRecord:
				if (!allowRecord) {
					new AlertDialog.Builder(PlayerActivity.this)
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setTitle("Cảnh báo")
							.setMessage(
									"Bạn có chắc muốn thu âm ca khúc này hay không?")
							.setPositiveButton("Thu âm",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											allowRecord = true;
											btnRecord
													.setBackgroundResource(R.drawable.btn_stop);
											startRecord();
										}

									}).setNegativeButton("Để lần sau", null)
							.show();
				} else if (allowRecord) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							PlayerActivity.this);
					builder.setTitle("Bạn muốn chuyển hướng?");
					builder.setItems(
							new CharSequence[] { "Lưu bài hát này & thoát",
									"Thu âm lại bài hát này",
									"Không lưu, thoát luôn." },
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									switch (which) {
									case 0:
										btnPlay.setVisibility(View.VISIBLE);
										if (!isPause) {
											btnPlay.setBackgroundResource(R.drawable.btn_play);
											isPause = true;
										}
										btnRecord
												.setBackgroundResource(R.drawable.btn_record);
										endRecord();
										PlayerActivity.this.finish();
										break;
									case 1:
										Utils.stopRecord();
										player.seekTo(0);
										Utils.startRecord();
										break;
									case 2:
										btnPlay.setVisibility(View.VISIBLE);
										if (!isPause) {
											btnPlay.setBackgroundResource(R.drawable.btn_play);
											isPause = true;
										}
										btnRecord
												.setBackgroundResource(R.drawable.btn_record);
										endRecord();
										File fdelete = new File(fileName);
										if (fdelete.exists()) {
											fdelete.delete();
										}
										PlayerActivity.this.finish();
										break;

									}
								}
							});
					builder.create().show();
				}

				break;
			default:
				break;
			}

		}
	};

	private void TimerM() {

		this.runOnUiThread(Timer_Tick);
	}

	private Runnable Timer_Tick = new Runnable() {
		public void run() {
			try {// cal time line
				int currDuration = (int) player.getCurrentPosition() / 1000;
				durationPlay = (int) player.getDuration() / 1000;
				if (currDuration > 0) {
					txtET.setText(formatT(durationPlay));
					txtST.setText(formatT(currDuration));
					if (!isSeeking) {
						int curPosSeek = (currDuration * 3600) / durationPlay;
						seekBarPlayer.setProgress(curPosSeek);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	};

	private String formatT(int totalSecs) {
		int hours = totalSecs / 3600;
		int minutes = (totalSecs % 3600) / 60;
		int seconds = totalSecs % 60;
		return String.format("%02d:%02d", minutes, seconds);
	}

	private void playvideo() {

		if (!isPause) {
			btnPlay.setBackgroundResource(R.drawable.btn_play);
			isPause = true;
			player.setPlayWhenReady(false);

		} else {
			btnPlay.setBackgroundResource(R.drawable.btn_pause);
			isPause = false;
			player.setPlayWhenReady(true);
		}

	}

	private void startRecord() {
		btnPlay.setVisibility(View.GONE);
		seekBarPlayer.setEnabled(false);
		Utils.startRecord();
		// visualizerView.setVisibility(View.VISIBLE);
		txtStatus.setText("Đang thu âm...");

	}

	private void videoEnd() {
		txtStatus.setText("Finish...");
		btnPlay.setBackgroundResource(R.drawable.btn_play);
		endRecord();
	}

	private void endRecord() {
		allowRecord = false;
		Utils.stopRecord();
		// recordStop();
		// visualizerView.setVisibility(View.GONE);
	}

}
