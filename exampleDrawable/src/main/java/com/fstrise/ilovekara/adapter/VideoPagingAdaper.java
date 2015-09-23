package com.fstrise.ilovekara.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fstrise.ilovekara.R;
import com.fstrise.ilovekara.classinfo.video;
import com.fstrise.ilovekara.config.Cals;
import com.fstrise.ilovekara.config.Conf;
import com.fstrise.ilovekara.paginglistview.PagingBaseAdapter;
import com.fstrise.ilovekara.utils.Utils;

public class VideoPagingAdaper extends PagingBaseAdapter<video> {

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public video getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) parent.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View row = convertView;
		try {
			if (convertView == null) {
				row = inflater.inflate(R.layout.item_video_row, parent, false);
			}
		} catch (Exception ex) {
			Log.i("", "");
		}
		video objv = getItem(position);
		//
		TextView itemtext = (TextView) row.findViewById(R.id.txtTitle);
		itemtext.setText(Utils.UppercaseFirstLetters(objv.getTitle()));
		itemtext.setTypeface(Conf.Roboto_Regular);
		//
		TextView txtID = (TextView) row.findViewById(R.id.txtID);
		txtID.setPadding(Cals.w4, 0, Cals.w4, 0);
		txtID.setTypeface(Conf.Roboto_Regular);
		txtID.setText(objv.getVideo_code().toString());
		//
		TextView txtUrl = (TextView) row.findViewById(R.id.txtUrl);
		txtUrl.setText(objv.getUrl());
		//
		TextView txtLyrics = (TextView) row.findViewById(R.id.txtLyrics);
		txtLyrics.setText(Utils.UppercaseFirstLetters(objv.getLyrics()));
		// txtLyrics.setTextSize((float) Conf.textSize21);
		txtLyrics.setTypeface(Conf.Roboto_LightItalic);
		//
		TextView txtSinger = (TextView) row.findViewById(R.id.txtSinger);
		txtSinger.setText(Utils.UppercaseFirstLetters(objv.getSinger()));
		txtSinger.setTypeface(Conf.Roboto_Thin);
		//
		ImageView imgVideo = (ImageView) row.findViewById(R.id.imgVideo);
		imgVideo.setLayoutParams(new LinearLayout.LayoutParams(Cals.w100 * 2,
				Cals.h100 + Cals.h20));
		if (!objv.getUrl().equals("")) {
			// http://i.ytimg.com/vi/K6JTLRBewxk/0.jpg
			String tempUrl = "http://i.ytimg.com/vi/" + objv.getUrl()
					+ "/0.jpg";
			Utils.DisplayImage(tempUrl, imgVideo, 10);
			imgVideo.setScaleType(ScaleType.FIT_XY);
		}
		return row;
	}

}
