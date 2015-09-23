package com.fstrise.ilovekara.fragment;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import com.fstrise.ilovekara.R;
import com.fstrise.ilovekara.adapter.KarAriangAdapter;
import com.fstrise.ilovekara.classinfo.Song;
import com.fstrise.ilovekara.config.Cals;
import com.fstrise.ilovekara.config.Conf;
import com.fstrise.ilovekara.data.SqlLiteDbHelper;
import com.fstrise.ilovekara.listAnima.SpeedScrollListener;

import android.support.v4.app.Fragment;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint("NewApi")
public class Q_KarAriang extends Fragment {
	public static ListView listv;
	private SpeedScrollListener listener;

	public static KarAriangAdapter plusAdapter;
	LinearLayout layoutInfo;
	TextView txtMaso;
	TextView txtTenBH;
	// Context mConetxt;
	private Timer mTimer;

	public Q_KarAriang() {
		// mConetxt = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_karariang,
				container, false);
		layoutInfo = (LinearLayout) rootView.findViewById(R.id.layoutInfo);
		LinearLayout.LayoutParams paramsInfo = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, Cals.h70);
		paramsInfo.gravity = Gravity.CENTER_VERTICAL;
		layoutInfo.setLayoutParams(paramsInfo);

		txtMaso = (TextView) rootView.findViewById(R.id.txtMaso);
		LinearLayout.LayoutParams lpCasi = new LinearLayout.LayoutParams(
				Cals.w100 + Cals.w70, LayoutParams.WRAP_CONTENT);
		lpCasi.gravity = Gravity.CENTER;
		txtMaso.setLayoutParams(lpCasi);
		// txtMaso.setTextSize((float) CalS.textSize17);
		txtMaso.setTypeface(Conf.Roboto_Bold);

		txtTenBH = (TextView) rootView.findViewById(R.id.txtTenBH);
		// txtTenBH.setTextSize((float) CalS.textSize17);
		txtTenBH.setTypeface(Conf.Roboto_Bold);

		listener = new SpeedScrollListener();
		listv = (ListView) rootView.findViewById(R.id.listv);
		listv.setOnScrollListener(listener);

		countTimer = 0;
		mTimer = new Timer();
		mTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				TimerSplashM();
			}
		}, 0, 1000);

		return rootView;
	}

	private class LoadData extends AsyncTask<String, Integer, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			// q.showLoading(1);

		}

		@Override
		protected String doInBackground(String... urls) {

			return "";
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try {
				loadAllSong();
			} catch (Exception ex) {
				ex.printStackTrace();

			}

		}
	}

	private void TimerSplashM() {
		getActivity().runOnUiThread(TimerSplashM_Tick);
	}

	int countTimer = 0;
	private Runnable TimerSplashM_Tick = new Runnable() {
		public void run() {
			if (countTimer == 1) {
				if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB_MR1) {
					new LoadData().execute("");
				} else {
					new LoadData().executeOnExecutor(
							AsyncTask.THREAD_POOL_EXECUTOR, "");
				}
				mTimer.cancel();
			}
			countTimer++;
		}

	};

	public void loadAllSong() {
		try {
			// if (Conf.arListKar.size() == 0) {
			SqlLiteDbHelper dbHelper = new SqlLiteDbHelper(getActivity());
			dbHelper.openDataBase();
			Conf.arListKar = dbHelper.getAllSong();
			List<Song> listfav= dbHelper.get_SongByFav();
			// }

			plusAdapter = new KarAriangAdapter(getActivity(), listener,
					Conf.arListKar);
			listv.setAdapter(plusAdapter);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
