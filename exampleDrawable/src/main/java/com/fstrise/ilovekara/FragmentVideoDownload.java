package com.fstrise.ilovekara;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.fstrise.ilovekara.adapter.MySongAdapter;
import com.fstrise.ilovekara.classinfo.mysong;
import com.fstrise.ilovekara.classinfo.video;
import com.fstrise.ilovekara.config.Conf;
import com.fstrise.ilovekara.quickaction.ActionItem;
import com.fstrise.ilovekara.quickaction.QuickAction;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("ValidFragment")
public class FragmentVideoDownload extends Fragment {
	private MainActivity mActivity;
	private Context mConetxt;
	private ListView listviewSong;
	private List<video> listFav;
	private static final int ID_MUSIC = 1;
	private static final int ID_UPLOAD = 2;
	private static final int ID_SHARE = 3;
	private static final int ID_DELETE = 4;
	private int mSelectedRow = 0;
	private ArrayList<mysong> listmyS;

	public FragmentVideoDownload(MainActivity ma) {
		mActivity = ma;
		mConetxt = mActivity.getBaseContext();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_my_download,
				container, false);
		listviewSong = (ListView) rootView.findViewById(R.id.listviewDownload);
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				loadData();
			}
		}, 250);

		return rootView;

	}

	@SuppressWarnings("deprecation")
	private void loadData() {
		String folderName = android.os.Environment
				.getExternalStorageDirectory()
				+ "/"
				+ Conf.folderSave
				+ "/dlvideo/";
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
		//
		MySongAdapter mySongAdapter = new MySongAdapter(mConetxt, listmyS);
		listviewSong.setAdapter(mySongAdapter);
		ActionItem addItem = new ActionItem(ID_MUSIC, "Xem & Thu Âm",
				getResources().getDrawable(R.drawable.ic_action_playback_play));

		ActionItem deleteItem = new ActionItem(ID_DELETE, "Xóa", getResources()
				.getDrawable(R.drawable.ic_action_cancel));

		final QuickAction mQuickAction = new QuickAction(getActivity()
				.getBaseContext());

		mQuickAction.addActionItem(addItem);
		mQuickAction.addActionItem(deleteItem);
		// setup the action item click listener
		mQuickAction
				.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
					@Override
					public void onItemClick(QuickAction quickAction, int pos,
							int actionId) {
						if (actionId == ID_MUSIC) { // Add item selected
							Intent it = new Intent(mActivity,
									PlayerActivity.class);
							// Bundle extras = it.getExtras();
							// extras.putString("url",
							// listmyS.get(mSelectedRow).getPath());
							// extras.putString("title",
							// listmyS.get(mSelectedRow).getTitle());
							Bundle mBundle = new Bundle();
							mBundle.putString("url", listmyS.get(mSelectedRow)
									.getPath());
							mBundle.putString("title", listmyS
									.get(mSelectedRow).getTitle());
							it.putExtras(mBundle);
							mActivity.startActivity(it);
							mActivity.overridePendingTransition(
									R.anim.open_next, R.anim.close_main);
						} else if (actionId == ID_DELETE) {

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
			public void onItemClick(AdapterView<?> arg0, View view, int pos,
					long arg3) {
				mSelectedRow = pos;
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
				if (file.getName().endsWith(".mp4")) {
					inFiles.add(file);
				}
			}
		}
		return inFiles;
	}

}
