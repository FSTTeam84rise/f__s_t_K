package com.fstrise.ilovekara;

import java.io.File;
import java.io.IOException;

import com.androidquery.AQuery;
import com.androidquery.util.AQUtility;
import com.fstrise.ilovekara.data.SqlLiteDbHelper;
import com.fstrise.ilovekara.pagerSlidingTabstrip.PageSlidingTabStripFragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import de.madcyph3r.materialnavigationdrawer.MaterialNavigationDrawer;
import de.madcyph3r.materialnavigationdrawer.head.MaterialHeadItem;
import de.madcyph3r.materialnavigationdrawer.menu.MaterialMenu;
import de.madcyph3r.materialnavigationdrawer.menu.item.MaterialSection;
import de.madcyph3r.materialnavigationdrawer.tools.RoundedCornersDrawable;

public class MainActivity extends MaterialNavigationDrawer<Object> {

	MaterialNavigationDrawer<?> drawer = null;
	SqlLiteDbHelper dbHelper = new SqlLiteDbHelper(this);
	private File cacheDir;
	public static AQuery aq;

	@Override
	public int headerType() {
		// set type. you get the available constant from
		// MaterialNavigationDrawer class
		return MaterialNavigationDrawer.DRAWERHEADER_HEADITEMS;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void init(Bundle savedInstanceState) {

		drawer = this;
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		String hasdb = preferences.getString("hasdb", null);
		if (hasdb == null) {
			dbHelper = new SqlLiteDbHelper(this);
			try {
				dbHelper.CopyDataBaseFromAsset(this);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
			cacheDir = new File(
					android.os.Environment.getExternalStorageDirectory(),
					"iLoveKara");
		else
			cacheDir = this.getCacheDir();
		if (!cacheDir.exists())
			cacheDir.mkdirs();
		AQUtility.setCacheDir(cacheDir);
		aq = new AQuery(this);

		MaterialMenu menu = new MaterialMenu();

		// create intent for settings activity
		Intent i = new Intent(this, Settings.class);

		this.newSection(
				"Start Fragment",
				this.getResources().getDrawable(
						R.drawable.ic_favorite_black_36dp),
				new FragmentIndex(), false, menu);
		this.newSection("Start Activity",
				this.getResources().getDrawable(R.drawable.ic_list_black_36dp),
				i, false, menu);
		// add devisor
		this.newDevisor(menu);
		// section with own in click listener
		this.newLabel("label", false, menu);
		this.newSection("On Click listener",
				this.getResources().getDrawable(R.drawable.ic_list_black_36dp),
				i, false, menu);

		MaterialSection<?> section7 = this.newSection(
				"Start Fragment Notification",
				this.getResources().getDrawable(
						R.drawable.ic_favorite_black_36dp),
				new FragmentIndex(), false, menu).setSectionColor(
				Color.parseColor("#ff0858"));
		section7.setNotifications(20);

		this.newSection("Start Fragment No Icon", new FragmentIndex(), false,
				menu);

		this.newSection("Start Fragment Red Color", new FragmentIndex(), false,
				menu).setSectionColor(Color.parseColor("#ff0000"));

		this.newDevisor(menu);
		// section with own in click listener
		this.newLabel("label", false, menu);
		this.newSection(
				"List Karaoke",
				this.getResources().getDrawable(
						R.drawable.ic_favorite_black_36dp),
				new PageSlidingTabStripFragment(), false, menu);

		// use bitmap and make a circle photo
		final Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.app_drawer_icon);
		final RoundedCornersDrawable drawableAppIcon = new RoundedCornersDrawable(
				getResources(), bitmap);

		// create Head Item
		MaterialHeadItem headItem = new MaterialHeadItem(this, "F HeadItem",
				"F Subtitle", drawableAppIcon, R.drawable.mat5, menu, 0);

		// add head Item (menu will be loaded automatically)
		this.addHeadItem(headItem);
	}

}
