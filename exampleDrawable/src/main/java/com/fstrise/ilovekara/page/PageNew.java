package com.fstrise.ilovekara.page;

import com.fstrise.ilovekara.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PageNew extends Fragment {
	public PageNew() {
		// mConetxt = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.pager_songnew, container,
				false);
		return rootView;
	}
}
