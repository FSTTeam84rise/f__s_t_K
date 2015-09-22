package com.fstrise.ilovekara.adapter;

import java.util.ArrayList;

import com.fstrise.ilovekara.R;
import com.fstrise.ilovekara.classinfo.Song;
import com.fstrise.ilovekara.config.Cals;
import com.fstrise.ilovekara.config.Conf;
import com.fstrise.ilovekara.config.ImageDownloader;
import com.fstrise.ilovekara.data.SqlLiteDbHelper;
import com.fstrise.ilovekara.fragment.Q_KarAriang;
import com.fstrise.ilovekara.listAnima.GPlusListAdapter;
import com.fstrise.ilovekara.listAnima.SpeedScrollListener;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class KarAriangAdapter extends GPlusListAdapter {
	private final ImageDownloader imageDownloader = new ImageDownloader();
	private ArrayList<Song> aSong;

	private Context context;
//	private QMain qMain;

	public KarAriangAdapter(Context context,
			SpeedScrollListener scrollListener, ArrayList<Song> arSong) {
		super(context, scrollListener, arSong);
		this.context = context;
//		qMain = (QMain) context;
		aSong = arSong;
	}

	@Override
	protected View getRowView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.layout_kar_ariang, parent, false);

			holder = new ViewHolder();
			holder.imgButton = (ImageButton) convertView
					.findViewById(R.id.imgButton);

			holder.frameInfo = (FrameLayout) convertView
					.findViewById(R.id.frameInfo);
			LinearLayout.LayoutParams lpCasi = new LinearLayout.LayoutParams(
					Cals.w100 + Cals.w70, LayoutParams.MATCH_PARENT);
			holder.frameInfo.setLayoutParams(lpCasi);

			holder.layoutInfo = (LinearLayout) convertView
					.findViewById(R.id.layoutInfo);
			holder.layoutInfo.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, Cals.h80));
			holder.layoutMaso = (LinearLayout) convertView
					.findViewById(R.id.layoutMaso);
			//
			holder.txtMaso = (TextView) convertView.findViewById(R.id.txtMaso);
			// holder.txtMaso.setTextSize((float) CalS.textSize20);
			holder.txtMaso.setTypeface(Conf.Roboto_Bold);

			holder.imgFavorite = (ImageView) convertView
					.findViewById(R.id.imgFavorite);
			LinearLayout.LayoutParams lpTitle = new LinearLayout.LayoutParams(
					Cals.h40 + Cals.h5, Cals.h40 + Cals.h5);
			lpTitle.gravity = Gravity.CENTER_HORIZONTAL;
			holder.imgFavorite.setLayoutParams(lpTitle);
			//
			holder.layoutTen = (LinearLayout) convertView
					.findViewById(R.id.layoutTen);
			LinearLayout.LayoutParams lpTen = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			holder.layoutTen.setLayoutParams(lpTen);
			holder.layoutTen.setPadding(Cals.w10, 0, 0, 0);
			//
			holder.txtTenBH = (TextView) convertView
					.findViewById(R.id.txtTenBH);
			// holder.txtTenBH.setTextSize((float) CalS.textSize17);
			holder.txtTenBH.setTypeface(Conf.Roboto_Bold);

			holder.txtDes = (TextView) convertView.findViewById(R.id.txtDes);
			// holder.txtDes.setTextSize((float) CalS.textSize13);
			holder.txtDes.setTypeface(Conf.Roboto_LightItalic);

			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		// imageDownloader.download(URLS[position], holder.image);

		final Song obj = aSong.get(position);
		// holder.text.setText("0" + obj.getId());
		holder.txtMaso.setText(obj.getId() + "");
		holder.txtTenBH.setText(obj.getTitle());
		holder.txtDes.setText(obj.getLyric());

		if (obj.getFav() == 0) {
			holder.imgFavorite.setBackgroundResource(R.drawable.un_heart);
		} else {
			holder.imgFavorite.setBackgroundResource(R.drawable.heart);
		}
		final int idS = obj.getId();
		final int fav = obj.getFav();
		final int pos = position;

		holder.imgButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				SqlLiteDbHelper dbHelper = new SqlLiteDbHelper(context);
				dbHelper.openDataBase();
				if (fav == 0) {
					dbHelper.updateSong(1, idS);
				} else {
					dbHelper.updateSong(0, idS);
				}
				if (fav == 0) {

					aSong.get(pos).setFav(1);
				} else {
					aSong.get(pos).setFav(0);
				}
				Q_KarAriang.plusAdapter.notifyDataSetChanged();

			}
		});
		return convertView;
	}

	static class ViewHolder {
		ImageView imgFavorite;
		TextView txtMaso;
		TextView txtTenBH;
		TextView txtDes;
		LinearLayout layoutTen;
		LinearLayout layoutMaso;
		LinearLayout layoutInfo;
		ImageButton imgButton;
		FrameLayout frameInfo;
	}
}