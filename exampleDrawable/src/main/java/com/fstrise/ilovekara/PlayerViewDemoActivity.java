/*
 * Copyright 2012 Google Inc. All Rights Reserved.
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
import java.util.Timer;
import java.util.TimerTask;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar.OnProgressChangeListener;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fstrise.ilovekara.config.Cals;
import com.fstrise.ilovekara.config.Conf;
import com.fstrise.ilovekara.utils.NotificationUtils;
import com.fstrise.ilovekara.utils.StorageUtils;
import com.fstrise.ilovekara.utils.Utils;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.ErrorReason;
import com.google.android.youtube.player.YouTubePlayer.PlaybackEventListener;
import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener;
import com.google.android.youtube.player.YouTubePlayer.PlayerStyle;
import com.google.android.youtube.player.YouTubePlayerView;

/**
 * A simple YouTube Android API demo application which shows how to create a
 * simple application that displays a YouTube Video in a
 * {@link YouTubePlayerView}.
 * <p>
 * Note, to use a {@link YouTubePlayerView}, your activity must extend
 * {@link YouTubeBaseActivity}.
 */
@SuppressLint("DefaultLocale")
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class PlayerViewDemoActivity extends YouTubeFailureRecoveryActivity
		implements View.OnClickListener, TextView.OnEditorActionListener,
		CompoundButton.OnCheckedChangeListener,
		AdapterView.OnItemSelectedListener {
	private YouTubePlayer mplayer;
	
	private boolean isPlay;
	private boolean isPause;
	private boolean isSeeking;
	private boolean isLoaded;
	private boolean allowRecord;
	private String url;
	private String title;
	private int videocode;
	private int durationPlay;

	private int timeSeek;
	private MyPlayerStateChangeListener playerStateChangeListener;
	private MyPlaybackEventListener playbackEventListener;
	private Button btnPlay;
	private Button btnRecord;
	private DiscreteSeekBar seekBarPlayer;
	private TextView txtStatus;
	private TextView txtST;
	private TextView txtET;
	private TextView txtTitle;
	// record ui
	// private VisualizerView visualizerView;
	// private AudioRecordingThread recordingThread;
	private boolean startRecording = true;
	private static String fileName = null;

	// private static String fileNameTemp = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		View decorView = getWindow().getDecorView();
		int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
		decorView.setSystemUiVisibility(uiOptions);
		setContentView(R.layout.playerview_demo);
		Bundle b = getIntent().getExtras();
		videocode = b.getInt("id");
		url = b.getString("url");
		title = b.getString("title");
		//
		YouTubePlayerView youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
		FrameLayout.LayoutParams lpPlayer = new FrameLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, Cals.h100 * 6,
				Gravity.CENTER_HORIZONTAL);
		// lpPlayer.topMargin = Cals.h40;
		youTubeView.setLayoutParams(lpPlayer);
		youTubeView.initialize(DeveloperKey.DEVELOPER_KEY, this);
		playerStateChangeListener = new MyPlayerStateChangeListener();
		playbackEventListener = new MyPlaybackEventListener();

		//
		LinearLayout.LayoutParams lpBtn = new LinearLayout.LayoutParams(
				Cals.w100, Cals.w100);
		lpBtn.leftMargin = Cals.w10;
		btnPlay = (Button) findViewById(R.id.btnPlay);
		btnPlay.setLayoutParams(lpBtn);
		btnPlay.setOnClickListener(playClick);
		btnPlay.setVisibility(View.GONE);
		btnRecord = (Button) findViewById(R.id.btnRecord);
		btnRecord.setLayoutParams(lpBtn);
		btnRecord.setOnClickListener(playClick);
		btnRecord.setVisibility(View.GONE);
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

	}

	// private void setupVisualizer() {
	// Paint paint = new Paint();
	// paint.setStrokeWidth(5f);
	// paint.setAntiAlias(true);
	// paint.setColor(Color.argb(200, 227, 69, 53));
	// BarGraphRenderer barGraphRendererBottom = new BarGraphRenderer(2,
	// paint, false);
	// visualizerView.addRenderer(barGraphRendererBottom);
	// }
	//
	// private void releaseVisualizer() {
	// if (visualizerView != null) {
	// visualizerView.release();
	// visualizerView = null;
	// }
	// }

	// private void startRecording() {
	//
	// recordingThread = new AudioRecordingThread(fileNameTemp,
	// new AudioRecordingHandler() {
	// @Override
	// public void onFftDataCapture(final byte[] bytes) {
	// runOnUiThread(new Runnable() {
	// public void run() {
	// if (visualizerView != null) {
	// visualizerView.updateVisualizerFFT(bytes);
	// }
	// }
	// });
	// }
	//
	// @Override
	// public void onRecordSuccess() {
	// }
	//
	// @Override
	// public void onRecordingError() {
	// runOnUiThread(new Runnable() {
	// public void run() {
	// recordStop();
	// NotificationUtils.showInfoDialog(
	// PlayerViewDemoActivity.this,
	// "Error recording audio");
	// }
	// });
	// }
	//
	// @Override
	// public void onRecordSaveError() {
	// runOnUiThread(new Runnable() {
	// public void run() {
	// recordStop();
	// NotificationUtils.showInfoDialog(
	// PlayerViewDemoActivity.this,
	// "Error saving audio record");
	// }
	// });
	// }
	// });
	// recordingThread.start();
	// }
	//
	// private void stopRecording() {
	// if (recordingThread != null) {
	// recordingThread.stopRecording();
	// recordingThread = null;
	// }
	// }

	// private void record() {
	// if (startRecording) {
	// recordStart();
	// } else {
	// recordStop();
	// }
	// }

	// private void recordStart() {
	// visualizerView = (VisualizerView) findViewById(R.id.visualizerView);
	// setupVisualizer();
	// startRecording();
	// startRecording = false;
	// txtStatus.setText("Start Record");
	// // recordBtn.setText(R.string.stopRecordBtn);
	// // playBtn.setEnabled(false);
	// }

	// private void recordStop() {
	// stopRecording();
	// startRecording = true;
	// txtStatus.setText("Stop Record");
	// // recordBtn.setText(R.string.recordBtn);
	// // playBtn.setEnabled(true);
	// }

	@Override
	public void onInitializationSuccess(YouTubePlayer.Provider provider,
			YouTubePlayer player, boolean wasRestored) {
		if (!wasRestored) {
			player.cueVideo(url);
			player.setPlayerStateChangeListener(playerStateChangeListener);
			player.setPlaybackEventListener(playbackEventListener);
			player.setPlayerStyle(PlayerStyle.CHROMELESS);
			mplayer = player;
		}
	}

	@Override
	protected YouTubePlayer.Provider getYouTubePlayerProvider() {
		return (YouTubePlayerView) findViewById(R.id.youtube_view);
	}

	public Timer getmTimer() {
		return mTimer;
	}

	public void setmTimer(Timer mTimer) {
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
				if (isPlay && !allowRecord) {
					new AlertDialog.Builder(PlayerViewDemoActivity.this)
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
											mplayer.seekToMillis(1000);
											allowRecord = true;
											btnRecord
													.setBackgroundResource(R.drawable.btn_stop);
											startRecord();
										}

									}).setNegativeButton("Để lần sau", null)
							.show();
				} else if (startRecording) {
					new AlertDialog.Builder(PlayerViewDemoActivity.this)
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
											playvideo();
										}

									}).setNegativeButton("Để lần sau", null)
							.show();
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							PlayerViewDemoActivity.this);
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
											mplayer.pause();

										}
										btnRecord
												.setBackgroundResource(R.drawable.btn_record);
										endRecord();
										PlayerViewDemoActivity.this.finish();
										break;
									case 1:
										Utils.stopRecord();
										mplayer.seekToMillis(1000);
										Utils.startRecord();
										break;
									case 2:
										btnPlay.setVisibility(View.VISIBLE);
										if (!isPause) {
											btnPlay.setBackgroundResource(R.drawable.btn_play);
											isPause = true;
											mplayer.pause();

										}
										btnRecord
												.setBackgroundResource(R.drawable.btn_record);
										endRecord();
										File fdelete = new File(fileName);
										if (fdelete.exists()) {
											fdelete.delete();
										}
										PlayerViewDemoActivity.this.finish();
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

	private void playvideo() {
		if (isPlay) {
			if (!isPause) {
				btnPlay.setBackgroundResource(R.drawable.btn_play);
				isPause = true;
				mplayer.pause();

			} else {
				btnPlay.setBackgroundResource(R.drawable.btn_play);
				isPause = false;
				mplayer.play();
			}
		}
		if (!isPlay) {
			btnPlay.setBackgroundResource(R.drawable.btn_pause);
			isPlay = true;
			mplayer.play();
			setmTimer(new Timer());
			getmTimer().schedule(new TimerTask() {
				@Override
				public void run() {
					TimerM();
				}
			}, 0, 1000);
		}
	}

	private Timer mTimer;

	private void TimerM() {
		this.runOnUiThread(Timer_Tick);
	}

	private Runnable Timer_Tick = new Runnable() {
		public void run() {
			if (isPlay) {
				try {// cal time line
					int currDuration = mplayer.getCurrentTimeMillis() / 1000;
					if (currDuration > 0) {
						txtET.setText(formatT(durationPlay));
						txtST.setText(formatT(currDuration));
						if (!isSeeking) {
							int curPosSeek = (currDuration * 3600)
									/ durationPlay;
							seekBarPlayer.setProgress(curPosSeek);
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			}
		}
	};

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}

	private final class MyPlaybackEventListener implements
			PlaybackEventListener {
		String playbackState = "NOT_PLAYING";
		String bufferingState = "";

		@Override
		public void onPlaying() {
			playbackState = "PLAYING";
			// updateText();
			// log("\tPLAYING " + getTimesText());
		}

		@Override
		public void onBuffering(boolean isBuffering) {
			bufferingState = isBuffering ? "(BUFFERING)" : "";
			// updateText();
			// log("\t\t" + (isBuffering ? "BUFFERING " : "NOT BUFFERING ") +
			// getTimesText());
		}

		@Override
		public void onStopped() {
			playbackState = "STOPPED";
			// updateText();
			isPlay = false;
			isPause = false;
			getmTimer().cancel();
			// releaseVisualizer();
			log("\tSTOPPED");
		}

		@Override
		public void onPaused() {
			playbackState = "PAUSED";
			// updateText();
			log("\tPAUSED " + mplayer.getDurationMillis());
		}

		@Override
		public void onSeekTo(int endPositionMillis) {
			log(String.format("\tSEEKTO: (%s/%s)",
					formatTime(endPositionMillis),
					formatTime(mplayer.getDurationMillis())));
		}
	}

	private final class MyPlayerStateChangeListener implements
			PlayerStateChangeListener {
		String playerState = "UNINITIALIZED";

		@Override
		public void onLoading() {
			playerState = "LOADING";
			// txtStatus.setText("Loading...");
			log(playerState);
		}

		@Override
		public void onLoaded(String videoId) {
			playerState = String.format("LOADED %s", videoId);
			// updateText();
			durationPlay = mplayer.getDurationMillis() / 1000;
			btnRecord.setVisibility(View.VISIBLE);
			btnPlay.setVisibility(View.VISIBLE);
			txtStatus.setText("Ready...");
			isLoaded = true;
			log(playerState);
		}

		@Override
		public void onAdStarted() {
			playerState = "AD_STARTED";
			// updateText();
			log(playerState);
		}

		@Override
		public void onVideoStarted() {
			playerState = "VIDEO_STARTED";
			seekBarPlayer.setEnabled(true);
			if (allowRecord) {
				startRecord();
			} else {
				txtStatus.setText("Đang xem...");
			}

			log(playerState);
		}

		@Override
		public void onVideoEnded() {
			playerState = "VIDEO_ENDED";
			// updateText();
			videoEnd();
			log(playerState);
		}

		@Override
		public void onError(ErrorReason reason) {
			playerState = "ERROR (" + reason + ")";
			if (reason == ErrorReason.UNEXPECTED_SERVICE_DISCONNECTION) {
				// When this error occurs the player is released and can no
				// longer be used.
				// player = null;
				// setControlsEnabled(false);
			}
			// updateText();
			log(playerState);
		}

	}

	private void startRecord() {
		startRecording = false;
		btnPlay.setVisibility(View.GONE);
		// record();
		seekBarPlayer.setEnabled(false);
		Utils.startRecord();
		// visualizerView.setVisibility(View.VISIBLE);
		txtStatus.setText("Đang thu âm...");
		if (!isPlay) {
			btnPlay.setBackgroundResource(R.drawable.btn_pause);
			setmTimer(new Timer());
			getmTimer().schedule(new TimerTask() {
				@Override
				public void run() {
					TimerM();
				}
			}, 0, 1000);
		}
	}

	private void videoEnd() {
		txtStatus.setText("Finish...");
		btnPlay.setBackgroundResource(R.drawable.btn_play);
		isPlay = false;
		endRecord();
	}

	private void endRecord() {
		allowRecord = false;
		startRecording = true;
		Utils.stopRecord();
		// recordStop();
		// visualizerView.setVisibility(View.GONE);
	}

	private void log(String mg) {
		Log.i("player", mg);
	}

	private String formatTime(int millis) {
		int seconds = millis / 1000;
		int minutes = seconds / 60;
		int hours = minutes / 60;

		return (hours == 0 ? "" : hours + ":")
				+ String.format("%02d:%02d", minutes % 60, seconds % 60);
	}

	private void onTouchSeekbar() {
		seekBarPlayer
				.setOnProgressChangeListener(new OnProgressChangeListener() {

					@Override
					public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
						isSeeking = false;
						mplayer.seekToMillis(timeSeek * 1000);
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

	private String formatT(int totalSecs) {
		int hours = totalSecs / 3600;
		int minutes = (totalSecs % 3600) / 60;
		int seconds = totalSecs % 60;
		return String.format("%02d:%02d", minutes, seconds);
	}

	public void onBackPressed() {
		if (!startRecording) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					PlayerViewDemoActivity.this);
			builder.setTitle("Bạn muốn chuyển hướng?");
			builder.setItems(new CharSequence[] { "Lưu bài hát này & thoát",
					"Thu âm lại bài hát này", "Không lưu, thoát luôn." },
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case 0:
								btnPlay.setVisibility(View.VISIBLE);
								if (!isPause) {
									btnPlay.setBackgroundResource(R.drawable.btn_play);
									isPause = true;
									mplayer.pause();

								}
								btnRecord
										.setBackgroundResource(R.drawable.btn_record);
								endRecord();
								PlayerViewDemoActivity.this.finish();
								break;
							case 1:
								Utils.stopRecord();
								mplayer.seekToMillis(1000);
								Utils.startRecord();
								break;
							case 2:
								btnPlay.setVisibility(View.VISIBLE);
								if (!isPause) {
									btnPlay.setBackgroundResource(R.drawable.btn_play);
									isPause = true;
									mplayer.pause();

								}
								btnRecord
										.setBackgroundResource(R.drawable.btn_record);
								endRecord();
								File fdelete = new File(fileName);
								if (fdelete.exists()) {
									fdelete.delete();
								}
								PlayerViewDemoActivity.this.finish();
								break;

							}
						}
					});
			builder.create().show();
		} else {
			PlayerViewDemoActivity.this.finish();
		}
	}
}
