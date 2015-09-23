package com.fstrise.ilovekara;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fstrise.ilovekara.customtoast.Effects;
import com.fstrise.ilovekara.customtoast.NiftyNotificationView;
import com.fstrise.ilovekara.page.PageHot;
import com.fstrise.ilovekara.page.PageNew;
import com.fstrise.ilovekara.pagertab.PagerSlidingTabStrip;

@SuppressLint("ValidFragment")
public class FragmentHome extends Fragment {
	private PagerSlidingTabStrip pagerSlidingTabStrip1;
	private ViewPager viewPager1;
	private MainActivity mActivity;
	private Context mConetxt;
	public PageHot mPageHot;
	private PageNew mPageNew;

	public FragmentHome(MainActivity ma) {
		mActivity = ma;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_home, container,
				false);
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
				mPageHot = new PageHot(mActivity.getBaseContext(), mActivity);
				frag = mPageHot;
			} else {
				mPageNew = new PageNew();
				frag = mPageNew;
			}
			return frag;// SuperAwesomeCardFragment.newInstance(position);
		}

	}

	
}
