package com.fstrise.ilovekara.fragment;

import java.util.ArrayList;

import com.fstrise.ilovekara.R;
import com.fstrise.ilovekara.adapter.FavAdapter;
import com.fstrise.ilovekara.classinfo.Song;
import com.fstrise.ilovekara.config.Cals;
import com.fstrise.ilovekara.config.Conf;
import com.fstrise.ilovekara.data.SqlLiteDbHelper;
import com.fstrise.ilovekara.listAnima.SpeedScrollListener;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class Q_YeuThich extends Fragment {
	private static SpeedScrollListener listener;
	public static FavAdapter favAdapter;
	public static ListView listv;
	private static ArrayList<Song> arList;
	private static Context context;
	
	LinearLayout layoutInfo;
	TextView txtMaso;
	TextView txtTenBH;

	public Q_YeuThich() {
		// mConetxt = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_yeuthich, container,
				false);
		
		layoutInfo =(LinearLayout) rootView.findViewById(R.id.layoutInfo);
		LinearLayout.LayoutParams paramsInfo = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,Cals.h70);
		paramsInfo.gravity = Gravity.CENTER_VERTICAL;
		layoutInfo.setLayoutParams(paramsInfo);
		
		txtMaso =(TextView) rootView.findViewById(R.id.txtMaso);
		LinearLayout.LayoutParams lpCasi = new LinearLayout.LayoutParams(
				Cals.w100 + Cals.w70, LayoutParams.WRAP_CONTENT);
		lpCasi.gravity= Gravity.CENTER;
		txtMaso.setLayoutParams(lpCasi);
//		txtMaso.setTextSize((float) CalS.textSize17);
		txtMaso.setTypeface(Conf.Roboto_Bold);
		
		txtTenBH =(TextView) rootView.findViewById(R.id.txtTenBH);
//		txtTenBH.setTextSize((float) CalS.textSize17);
		txtTenBH.setTypeface(Conf.Roboto_Bold);
		
		listener = new SpeedScrollListener();
		listv = (ListView) rootView.findViewById(R.id.listv);
		Q_YeuThich.context = getActivity();
		
		return rootView;
	}

	public void loadFavorite() {
		SqlLiteDbHelper dbHelper = new SqlLiteDbHelper(getActivity());
		dbHelper.openDataBase();
		arList = dbHelper.get_SongByFav();
		favAdapter = new FavAdapter(getActivity(), listener, arList);
		listv.setAdapter(favAdapter);
	}

	public static void loadData(int position) {
	
		SqlLiteDbHelper dbHelper = new SqlLiteDbHelper(context);
		dbHelper.openDataBase();
		dbHelper.updateSong(0, arList.get(position).getId());
		arList.remove(position);

		favAdapter = new FavAdapter(context, listener, arList);
		listv.setAdapter(favAdapter);

	}

}
