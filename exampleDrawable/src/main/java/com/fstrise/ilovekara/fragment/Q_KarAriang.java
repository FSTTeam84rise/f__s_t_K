package com.fstrise.ilovekara.fragment;

import java.util.ArrayList;

import com.fstrise.ilovekara.R;
import com.fstrise.ilovekara.adapter.KarAriangAdapter;
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
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

public class Q_KarAriang extends Fragment {
	public static ListView listv;
	private SpeedScrollListener listener;
	private static ArrayList<Song> arList;
	public static KarAriangAdapter plusAdapter;
	LinearLayout layoutInfo;
	TextView txtMaso;
	TextView txtTenBH;
//	Context mConetxt;
	

	public Q_KarAriang() {
//		mConetxt = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_karariang,
				container, false);
		layoutInfo =(LinearLayout) rootView.findViewById(R.id.layoutInfo);
		LinearLayout.LayoutParams paramsInfo = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,Cals.h70);
		paramsInfo.gravity = Gravity.CENTER_VERTICAL;
		layoutInfo.setLayoutParams(paramsInfo);
		
		txtMaso =(TextView) rootView.findViewById(R.id.txtMaso);
		LinearLayout.LayoutParams lpCasi = new LinearLayout.LayoutParams(
				Cals.w100+Cals.w70, LayoutParams.WRAP_CONTENT);
		lpCasi.gravity= Gravity.CENTER;
		txtMaso.setLayoutParams(lpCasi);
//		txtMaso.setTextSize((float) CalS.textSize17);
		txtMaso.setTypeface(Conf.Roboto_Bold);
		
		txtTenBH =(TextView) rootView.findViewById(R.id.txtTenBH);
//		txtTenBH.setTextSize((float) CalS.textSize17);
		txtTenBH.setTypeface(Conf.Roboto_Bold);
		
		listener = new SpeedScrollListener();
		listv = (ListView) rootView.findViewById(R.id.listv);
		listv.setOnScrollListener(listener);
		loadAllSong();
		return rootView;
	}

	public void loadAllSong() {
		try {
			SqlLiteDbHelper dbHelper = new SqlLiteDbHelper(getActivity());
			dbHelper.openDataBase();
			arList = dbHelper.getAllSong();
			plusAdapter = new KarAriangAdapter(getActivity(), listener, arList);
			listv.setAdapter(plusAdapter);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
