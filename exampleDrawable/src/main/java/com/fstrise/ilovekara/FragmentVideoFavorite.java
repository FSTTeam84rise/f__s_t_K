package com.fstrise.ilovekara;

import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.fstrise.ilovekara.adapter.VideoFavoriteAdapter;
import com.fstrise.ilovekara.classinfo.video;
import com.fstrise.ilovekara.data.SqlLiteDbHelper;
import com.fstrise.ilovekara.quickaction.ActionItem;
import com.fstrise.ilovekara.quickaction.QuickAction;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("ValidFragment")
public class FragmentVideoFavorite extends Fragment {
	private MainActivity mActivity;
	private Context mConetxt;
	private ListView listviewFav;
	private List<video> listFav;
	private static final int ID_PLAY = 1;
	private static final int ID_FAVORITE = 2;
	private static final int ID_DOWNLOAD = 3;
	private int mSelectedRow = 0;
	private String itemSelectedID;
	private String itemSelectedURL;
	private String itemSelectedTitle;
	private String itemSelectedLyrics;
	private String itemSelectedSinger;

	public FragmentVideoFavorite(MainActivity ma) {
		mActivity = ma;
		mConetxt = mActivity.getBaseContext();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_video_favorite,
				container, false);
		listviewFav = (ListView) rootView.findViewById(R.id.listviewFav);
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				loadDataFav();
			}
		}, 250);

		return rootView;

	}

	private void loadDataFav() {
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB_MR1) {
			new loadFavData().execute("");
		} else {
			new loadFavData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
					"");
		}

	}

	private class loadFavData extends AsyncTask<String, Integer, String> {
		@Override
		protected String doInBackground(String... params) {
			String result = "";
			try {
				SqlLiteDbHelper sqlD = new SqlLiteDbHelper(mConetxt);
				listFav = sqlD.getAllFavVideo();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			VideoFavoriteAdapter favVideoAdapter = new VideoFavoriteAdapter(
					mConetxt, listFav);
			listviewFav.setAdapter(favVideoAdapter);
			ActionItem addItem = new ActionItem(ID_PLAY, "Xem & Thu Âm",
					getResources().getDrawable(
							R.drawable.ic_action_playback_play));
			ActionItem acceptItem = new ActionItem(ID_FAVORITE, "Xóa",
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
						public void onItemClick(QuickAction quickAction,
								int pos, int actionId) {
							ActionItem actionItem = quickAction
									.getActionItem(pos);

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
										"Add item selected on row "
												+ mSelectedRow + " T: "
												+ itemSelectedID,
										Toast.LENGTH_SHORT).show();
							} else if (actionId == ID_FAVORITE) {
								// getFavVideoByCode
								SqlLiteDbHelper sqlD = new SqlLiteDbHelper(
										mConetxt);
								sqlD.deleteFavVideo(itemSelectedID, 1);
								loadDataFav();
								mActivity.showToast("Đã xóa video "
										+ itemSelectedTitle
										+ " ra khỏi danh sách yêu thích.");
							} else if (actionId == ID_DOWNLOAD) {

							}

						}
					});

			// setup on dismiss listener, set the icon back to normal
			mQuickAction
					.setOnDismissListener(new PopupWindow.OnDismissListener() {
						@Override
						public void onDismiss() {
						}
					});

			listviewFav.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					try {
						mSelectedRow = position; // set the selected row
						TextView txtID = (TextView) view
								.findViewById(R.id.txtID);
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
		}
	}

}
