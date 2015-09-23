package com.fstrise.ilovekara.page;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Telephony.Mms;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.fstrise.ilovekara.MainActivity;
import com.fstrise.ilovekara.PlayerActivity;
import com.fstrise.ilovekara.PlayerViewDemoActivity;
import com.fstrise.ilovekara.R;
import com.fstrise.ilovekara.adapter.VideoPagingAdaper;
import com.fstrise.ilovekara.classinfo.video;
import com.fstrise.ilovekara.custom.SafeAsyncTask;
import com.fstrise.ilovekara.data.SqlLiteDbHelper;
import com.fstrise.ilovekara.paginglistview.PagingListView;
import com.fstrise.ilovekara.quickaction.ActionItem;
import com.fstrise.ilovekara.quickaction.QuickAction;
import com.fstrise.ilovekara.utils.Utils;

import de.madcyph3r.materialnavigationdrawer.MaterialNavigationDrawer;

@SuppressLint("ValidFragment")
public class PageHot extends Fragment {
	public PageHot(Context context, MainActivity ma) {
		mConetxt = context;
		mActivity = ma;
	}

	private MainActivity mActivity;
	private PagingListView listView;
	private VideoPagingAdaper adapter;
	private int pager = 0;
	private List<String> firstList;
	private List<String> secondList;
	private List<String> thirdList;
	private List<video> listVideo;
	private static final int ID_PLAY = 1;
	private static final int ID_FAVORITE = 2;
	private static final int ID_DOWNLOAD = 3;
	private int mSelectedRow = 0;
	private String itemSelectedID;
	private String itemSelectedURL;
	private String itemSelectedTitle;
	private String itemSelectedLyrics;
	private String itemSelectedSinger;
	private Context mConetxt;

	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mActivity.positionPage = 1;
		View rootView = inflater.inflate(R.layout.pager_songhot, container,
				false);
		listView = (PagingListView) rootView
				.findViewById(R.id.paging_list_view_hot);
		adapter = new VideoPagingAdaper();
		// initData();
		//
		listView.setAdapter(adapter);
		listView.setHasMoreItems(true);
		listView.setPagingableListener(new PagingListView.Pagingable() {
			@Override
			public void onLoadMoreItems() {
				if (pager < 20) {
					new loadVideoAsyncTask().execute();
				} else {
					listView.onFinishLoading(false, null);
				}
			}
		});
		ActionItem addItem = new ActionItem(ID_PLAY, "Xem & Thu Âm",
				getResources().getDrawable(R.drawable.ic_action_playback_play));
		ActionItem acceptItem = new ActionItem(ID_FAVORITE, "Thích",
				getResources().getDrawable(R.drawable.ic_action_heart));
		ActionItem uploadItem = new ActionItem(ID_DOWNLOAD, "Tải về máy",
				getResources().getDrawable(R.drawable.ic_action_download));

		final QuickAction mQuickAction = new QuickAction(getActivity()
				.getBaseContext());

		mQuickAction.addActionItem(addItem);
		mQuickAction.addActionItem(acceptItem);
		mQuickAction.addActionItem(uploadItem);

		// setup the action item click listener
		mQuickAction
				.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
					@Override
					public void onItemClick(QuickAction quickAction, int pos,
							int actionId) {
						// ActionItem actionItem =
						// quickAction.getActionItem(pos);

						if (actionId == ID_PLAY) { // Add item selected
							// Intent it = new Intent(mActivity,
							// PlayerActivity.class);
							// mActivity.startActivity(it);
							// mActivity.overridePendingTransition(
							// R.anim.open_next, R.anim.close_main);
							Intent it = new Intent(mActivity,
									PlayerViewDemoActivity.class);
							it.putExtra("id", itemSelectedID);
							it.putExtra("url", itemSelectedURL);
							it.putExtra("title", itemSelectedTitle);
							mActivity.startActivity(it);
							mActivity.overridePendingTransition(
									R.anim.open_next, R.anim.close_main);
							Toast.makeText(
									getActivity().getBaseContext(),
									"Add item selected on row " + mSelectedRow
											+ " T: " + itemSelectedTitle,
									Toast.LENGTH_SHORT).show();
						} else if (actionId == ID_FAVORITE) {
							// getFavVideoByCode
							SqlLiteDbHelper sqlD = new SqlLiteDbHelper(mConetxt);
							if (sqlD.getFavVideoByCode(itemSelectedID) == null) {
								video obj = new video();
								obj.setVideo_code(itemSelectedID);
								obj.setLyrics(itemSelectedLyrics);
								obj.setTitle(itemSelectedTitle);
								obj.setUrl(itemSelectedURL);
								obj.setSinger(itemSelectedSinger);
								obj.setType(1);
								sqlD.insertFavVideo(obj);
								mActivity
										.showToast("Đã thêm vào video yêu thích.");
							} else {
								mActivity
										.showToast("Video này đã tồn tại trong danh sách yêu thích.");
							}
						} else if (actionId == ID_DOWNLOAD) {
							new taskGetLinkDL()
									.execute("https://www.youtube.com/watch?v="
											+ itemSelectedURL);

						}
					}
				});

		// setup on dismiss listener, set the icon back to normal
		mQuickAction.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				try {
					mSelectedRow = position; // set the selected row
					TextView txtID = (TextView) view.findViewById(R.id.txtID);
					if (txtID != null) {
						itemSelectedID = txtID.getText().toString();
						TextView txtUrl = (TextView) view
								.findViewById(R.id.txtUrl);
						itemSelectedURL = txtUrl.getText().toString();
						TextView txtTitle = (TextView) view
								.findViewById(R.id.txtTitle);
						itemSelectedTitle = txtTitle.getText().toString();
						TextView txtLyrics = (TextView) view
								.findViewById(R.id.txtLyrics);
						itemSelectedLyrics = txtLyrics.getText().toString();
						TextView txtSinger = (TextView) view
								.findViewById(R.id.txtSinger);
						itemSelectedSinger = txtSinger.getText().toString();
						mQuickAction.show(view);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				// change the right arrow icon to selected state
			}
		});
		return rootView;
	}

	private class loadVideoAsyncTask extends SafeAsyncTask<String> {

		@Override
		public String call() throws Exception {
			String result = Utils.getData("http://fstrise.com/api.aspx?page="
					+ (pager * 20));
			Thread.sleep(1000);
			return result;
		}

		@Override
		protected void onSuccess(String newItems) throws Exception {
			super.onSuccess(newItems);
			pager++;
			listVideo = new ArrayList<video>();
			try {
				JSONObject obj = new JSONObject(newItems);
				JSONArray objArr = new JSONArray(obj.getString("rows"));
				JSONObject e3 = null;
				for (int i = 0; i < objArr.length(); i++) {
					e3 = objArr.getJSONObject(i);
					video objvideo = new video();
					objvideo.setTitle(e3.getString("title"));
					objvideo.setLyrics(e3.getString("lyrics"));
					objvideo.setVideo_code(e3.getString("video_code"));
					objvideo.setUrl(e3.getString("url"));
					objvideo.setView(e3.getInt("view"));
					objvideo.setSinger(e3.getString("singer"));
					objvideo.setTotal_dl(e3.getInt("total_dl"));
					listVideo.add(objvideo);
				}

			} catch (JSONException e3) {
				e3.printStackTrace();
			}
			listView.onFinishLoading(true, listVideo);
		}
	}

	private class taskGetLinkDL extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			mActivity.enableLoading(true);
		}

		@Override
		protected String doInBackground(String... params) {
			String result = "";
			try {
				result = Utils.getData("http://fstrise.com/dlyoutube.aspx?url="
						+ params[0]);
			} catch (Exception ex) {

			}
			return result;

		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			mActivity.enableLoading(false);
			if (result.contains("http")) {
				mActivity.fileNameDL = itemSelectedTitle;
				mActivity.downloadVideo(result);
			}
		}

	}

	public void searchSong(String query) {
		adapter.removeAllItems();
		listView.setAdapter(adapter);
		listVideo = new ArrayList<video>();
		pager = 0;
		keyword = query;
		new searchVideoAsyncTask().execute();
		Log.i("search",
				"result: " + query + " ___ "
						+ mActivity.currentSection.getTitle() + " - "
						+ mActivity.currentSection.getTargetIntent() + " - "
						+ mActivity.currentSection.getTarget());
	}

	private String keyword;

	private class searchVideoAsyncTask extends SafeAsyncTask<String> {

		@Override
		public String call() throws Exception {
			String result = Utils.getData("http://fstrise.com/api.aspx?page="
					+ (pager * 20) + "&query=" + Uri.decode(keyword));
			Thread.sleep(1000);
			return result;
		}

		@Override
		protected void onSuccess(String newItems) throws Exception {
			super.onSuccess(newItems);
			pager++;
			listVideo = new ArrayList<video>();
			try {
				JSONObject obj = new JSONObject(newItems);
				JSONArray objArr = new JSONArray(obj.getString("rows"));
				JSONObject e3 = null;
				for (int i = 0; i < objArr.length(); i++) {
					e3 = objArr.getJSONObject(i);
					video objvideo = new video();
					objvideo.setTitle(e3.getString("title"));
					objvideo.setLyrics(e3.getString("lyrics"));
					objvideo.setVideo_code(e3.getString("video_code"));
					objvideo.setUrl(e3.getString("url"));
					objvideo.setView(e3.getInt("view"));
					objvideo.setSinger(e3.getString("singer"));
					objvideo.setTotal_dl(e3.getInt("total_dl"));
					listVideo.add(objvideo);
				}

			} catch (JSONException e3) {
				e3.printStackTrace();
			}
			listView.onFinishLoading(true, listVideo);
		}
	}

}
