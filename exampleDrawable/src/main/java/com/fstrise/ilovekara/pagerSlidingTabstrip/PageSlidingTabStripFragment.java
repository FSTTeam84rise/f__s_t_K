package com.fstrise.ilovekara.pagerSlidingTabstrip;

import com.fstrise.ilovekara.R;
import com.fstrise.ilovekara.config.Cals;
import com.fstrise.ilovekara.fragment.Q_KarAriang;
import com.fstrise.ilovekara.fragment.Q_YeuThich;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;


public class PageSlidingTabStripFragment extends Fragment {
	Q_YeuThich mYeuThich;
	Q_KarAriang mKarAriang;
	PagerSlidingTabStrip tabs;
	public static final String TAG = PageSlidingTabStripFragment.class
			.getSimpleName();

	public static PageSlidingTabStripFragment newInstance() {
		return new PageSlidingTabStripFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.pager, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, Cals.h60);
		tabs.setLayoutParams(params);
		tabs.setWidthPageTab(Cals.w100*3 + Cals.w60);
		
		tabs.setTextColor(Color.parseColor("#ffffff"));
		ViewPager pager = (ViewPager) view.findViewById(R.id.pager);
		MyPagerAdapter adapter = new MyPagerAdapter(getChildFragmentManager());
		pager.setAdapter(adapter);
		tabs.setViewPager(pager);

		pager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub
				tabs.notifyDataSetChanged();
				if (position == 0) {
					mKarAriang.loadAllSong();
				} else if (position == 1) {
					mYeuThich.loadFavorite();
				}
				Log.i("Kar", "onPageSelected");
			}

			@Override
			public void onPageScrolled(int position, float arg1, int arg2) {
				// TODO Auto-generated method stub
				Log.i("Kar", "onPageScrolled");
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				Log.i("Kar", "onPageScrollStateChanged");
			}
		});

	}

	public class MyPagerAdapter extends FragmentPagerAdapter {

		public MyPagerAdapter(android.support.v4.app.FragmentManager fm) {
			super(fm);
		}

		private final String[] TITLES = { "Danh Sach Nhac", "Bai Yeu Thich" };

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLES[position];
		}

		@Override
		public int getCount() {
			return TITLES.length;
		}

		@Override
		public Fragment getItem(int position) {
			Fragment frag = null;
			if (position == 0) {
				mKarAriang = new Q_KarAriang();
				frag = mKarAriang;
			} else {
				mYeuThich = new Q_YeuThich();
				frag = mYeuThich;
			}

			return frag;// SuperAwesomeCardFragment.newInstance(position);
		}

	}

}
