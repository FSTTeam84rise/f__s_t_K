package com.fstrise.ilovekara;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.fstrise.ilovekara.adapter.MySongAdapter;
import com.fstrise.ilovekara.classinfo.mysong;
import com.fstrise.ilovekara.classinfo.video;
import com.fstrise.ilovekara.config.Conf;
import com.fstrise.ilovekara.customdialog.Effectstype;
import com.fstrise.ilovekara.customdialog.NiftyPlayerMusicDialogBuilder;
import com.fstrise.ilovekara.quickaction.ActionItem;
import com.fstrise.ilovekara.quickaction.QuickAction;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("ValidFragment")
public class FragmentMySong extends Fragment {
	private MainActivity mActivity;
	private Context mConetxt;
	private ListView listviewSong;
	private List<video> listFav;
	private static final int ID_MUSIC = 1;
	private static final int ID_UPLOAD = 2;
	private static final int ID_SHARE = 3;
	private static final int ID_DELETE = 4;
	private int mSelectedRow = 0;
	private String itemSelectedID;
	private String itemSelectedTitle;
	private String itemSelectedPath;
	private ArrayList<mysong> listmyS;
	private Timer mTimer;
	private NiftyPlayerMusicDialogBuilder dialogBuilder;

	public FragmentMySong(MainActivity ma) {
		mActivity = ma;
		mConetxt = mActivity.getBaseContext();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_my_song, container,
				false);
		listviewSong = (ListView) rootView.findViewById(R.id.listviewSong);
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				loadData();
			}
		}, 250);

		return rootView;

	}

	private void getDATA() {
		String folderName = android.os.Environment
				.getExternalStorageDirectory()
				+ "/"
				+ Conf.folderSave
				+ "/mysong/";
		List<File> files = getListFiles(new File(folderName));
		listmyS = new ArrayList<mysong>();
		for (File file : files) {
			mysong obj = new mysong();
			obj.setPath(file.getPath());
			obj.setTitle(file.getName().replace("_", " "));
			obj.setSize((file.length() / 1024) + "KB");
			obj.setLasttime(file.lastModified());
			listmyS.add(obj);
		}
		MySongAdapter mySongAdapter = new MySongAdapter(mConetxt, listmyS);
		listviewSong.setAdapter(mySongAdapter);
	}

	private void loadData() {
		getDATA();
		ActionItem addItem = new ActionItem(ID_MUSIC, "Nghe", getResources()
				.getDrawable(R.drawable.ic_action_headphones));
		ActionItem acceptItem = new ActionItem(ID_UPLOAD, "Đăng nhạc",
				getResources().getDrawable(R.drawable.ic_action_upload));
		ActionItem uploadItem = new ActionItem(ID_SHARE, "Chia sẽ",
				getResources().getDrawable(R.drawable.ic_action_share));
		ActionItem deleteItem = new ActionItem(ID_DELETE, "Xóa", getResources()
				.getDrawable(R.drawable.ic_action_cancel));

		final QuickAction mQuickAction = new QuickAction(getActivity()
				.getBaseContext());

		mQuickAction.addActionItem(addItem);
		mQuickAction.addActionItem(acceptItem);
		mQuickAction.addActionItem(uploadItem);
		mQuickAction.addActionItem(deleteItem);
		// setup the action item click listener
		mQuickAction
				.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
					@Override
					public void onItemClick(QuickAction quickAction, int pos,
							int actionId) {
						if (actionId == ID_MUSIC) { // Add item selected

							dialogBuilder = NiftyPlayerMusicDialogBuilder
									.getInstance(mActivity);

							dialogBuilder
									.withTitle(
											listmyS.get(mSelectedRow)
													.getTitle())
									// .withTitle(null) no title
									.withTitleColor("#FFFFFF")
									// def
									.withDividerColor("#11000000")
									.withDialogColor("#FFE74C3C")
									// def | withDialogColor(int resid) //def
									.withIcon(
											getResources()
													.getDrawable(
															R.drawable.ic_action_headphones))
									.isCancelableOnTouchOutside(true)
									// def | isCancelable(true)
									.withDuration(500)
									// def
									.withEffect(Effectstype.SlideBottom)
									// def Effectstype.Slidetop
									.withButton1Text("OK")
									// def gone
									.withButton2Text("Cancel")
									.withURLPlay(
											listmyS.get(mSelectedRow).getPath())
									.setButton1Click(
											new View.OnClickListener() {
												@Override
												public void onClick(View v) {
													mActivity
															.showToast("Chức năng này sẽ được cập nhật trong phiên bản sau");
												}
											})
									.setButton2Click(
											new View.OnClickListener() {
												@Override
												public void onClick(View v) {
													mActivity
															.showToast("Chức năng này sẽ được cập nhật trong phiên bản sau");
												}
											})
									.setButton3Click(
											new View.OnClickListener() {
												@Override
												public void onClick(View v) {
													dialogBuilder.dismiss();
												}
											}).show();
						} else if (actionId == ID_UPLOAD) {
							mActivity
									.showToast("Chức năng này sẽ được cập nhật trong phiên bản sau");
						} else if (actionId == ID_SHARE) {

							mActivity
									.showToast("Chức năng này sẽ được cập nhật trong phiên bản sau");
						} else if (actionId == ID_DELETE) {
							File file = new File(listmyS.get(mSelectedRow)
									.getPath());
							boolean deleted = file.delete();
							if (deleted) {
								getDATA();
								mActivity.showToast("Xóa ca khúc thành công");
							} else {
								mActivity
										.showToast("Không xóa được có lỗi xảy ra!");
							}
						}

					}
				});

		// setup on dismiss listener, set the icon back to normal
		mQuickAction.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
			}
		});
		listviewSong.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				mSelectedRow = position;
				mQuickAction.show(view);

			}
		});

	}

	private List<File> getListFiles(File parentDir) {
		ArrayList<File> inFiles = new ArrayList<File>();
		File[] files = parentDir.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				inFiles.addAll(getListFiles(file));
			} else {
				if (file.getName().endsWith(".mp3")) {
					inFiles.add(file);
				}
			}
		}
		return inFiles;
	}

	private Timer getmTimer() {
		return mTimer;
	}

	private void setmTimer(Timer mTimer) {
		this.mTimer = mTimer;
	}

	private void TimerM() {

		mActivity.runOnUiThread(Timer_Tick);
	}

	private Runnable Timer_Tick = new Runnable() {
		public void run() {
			if (dialogBuilder != null) {
				dialogBuilder.timerplaying();
			}
		}
	};

	@Override
	public void onResume() {
		setmTimer(new Timer());
		getmTimer().schedule(new TimerTask() {
			@Override
			public void run() {
				TimerM();
			}
		}, 0, 1000);

		super.onResume();
	}

	@Override
	public void onPause() {
		getmTimer().cancel();
		super.onPause();
	}
}
