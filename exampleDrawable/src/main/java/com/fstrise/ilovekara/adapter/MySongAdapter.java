package com.fstrise.ilovekara.adapter;

import java.lang.reflect.Method;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.fstrise.ilovekara.R;
import com.fstrise.ilovekara.classinfo.mysong;
import com.fstrise.ilovekara.config.Cals;
import com.fstrise.ilovekara.config.Conf;
import com.fstrise.ilovekara.custom.PopupMenuCompat;
import com.fstrise.ilovekara.utils.Utils;

public class MySongAdapter extends BaseAdapter {

	Context mContext;
	List<mysong> listItem;
	private int selectedPos = -1;

	public void setSelectedPosition(int pos) {
		selectedPos = pos;
		// inform the view of this change
		notifyDataSetChanged();
	}

	public int getSelectedPosition() {
		return selectedPos;
	}

	public MySongAdapter(Context context, List<mysong> list) {
		listItem = list;
		mContext = context;

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listItem.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listItem.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View row = convertView;
		try {
			if (convertView == null) {
				row = inflater.inflate(R.layout.item_song_row, parent, false);
			}
		} catch (Exception ex) {
			Log.i("", "");
		}

		mysong objv = listItem.get(position);
		//
		TextView itemtext = (TextView) row.findViewById(R.id.txtTitle);
		itemtext.setText(Utils.UppercaseFirstLetters(objv.getTitle()));
		itemtext.setTypeface(Conf.Roboto_Regular);
		//

		TextView txtSinger = (TextView) row.findViewById(R.id.txtSinger);
		txtSinger.setText(Utils.UppercaseFirstLetters(objv.getSize()) + " | "
				+ Utils.convTime(objv.getLasttime()));
		txtSinger.setTypeface(Conf.Roboto_Thin);
		

		return row;
	}

}
