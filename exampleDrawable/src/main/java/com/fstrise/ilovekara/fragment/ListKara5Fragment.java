package com.fstrise.ilovekara.fragment;

import com.fstrise.ilovekara.MainActivity;
import com.fstrise.ilovekara.R;
import com.fstrise.ilovekara.FragmentHome.MyPagerAdapter;
import com.fstrise.ilovekara.config.Conf;
import com.fstrise.ilovekara.page.PageHot;
import com.fstrise.ilovekara.page.PageNew;
import com.fstrise.ilovekara.pagertab.PagerSlidingTabStrip;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ListKara5Fragment extends Fragment {
	private PagerSlidingTabStrip pagerSlidingTabStrip1;
	private ViewPager viewPager1;
	private MainActivity mActivity;
	private Context mConetxt;
	Q_YeuThich mYeuThich;
	Q_KarAriang mKarAriang;

	public ListKara5Fragment(MainActivity ma) {
		mActivity = ma;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_ariang5number,
				container, false);
		pagerSlidingTabStrip1 = (PagerSlidingTabStrip) rootView
				.findViewById(R.id.slidingTabStrip_1);
		viewPager1 = (ViewPager) rootView.findViewById(R.id.viewPager_1);
		init(0, pagerSlidingTabStrip1, viewPager1);
		return rootView;

	}

	private void init(int index, PagerSlidingTabStrip pagerSlidingTabStrip,
			ViewPager viewPager) {
		// int length = pagerSlidingTabStrip.getTabCount();
		// List<View> views = new ArrayList<View>(length);
		// Random random = new Random();
		// for (int w = 0; w < length; w++) {
		// views.add(getContentView(colors[Math.abs(random.nextInt())
		// % colors.length]));
		// }
		// viewPager.setAdapter(new ViewPagerAdapter(views));
		// viewPager.setCurrentItem(index < length ? index : length);
		MyPagerAdapter adapter = new MyPagerAdapter(getChildFragmentManager());
		viewPager.setAdapter(adapter);
		pagerSlidingTabStrip.setViewPager(viewPager);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub
				// tabs.notifyDataSetChanged();
				if (position == 0) {
					// mKarAriang.loadAllSong();
					if (Conf.arListKar.size() != 0)
						Q_KarAriang.plusAdapter.notifyDataSetChanged();
					else
						mKarAriang.loadAllSong();
				} else if (position == 1) {
					mYeuThich.loadFavorite();
				}
				// Log.i("Kar", "onPageSelected");
			}

			@Override
			public void onPageScrolled(int position, float arg1, int arg2) {
				// TODO Auto-generated method stub
				// Log.i("Kar", "onPageScrolled");
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				// Log.i("Kar", "onPageScrollStateChanged");
			}
		});
	}

	public class MyPagerAdapter extends FragmentPagerAdapter {
		public MyPagerAdapter(android.support.v4.app.FragmentManager fm) {
			super(fm);
		}

		private final String[] TITLES = { "", "" };

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
