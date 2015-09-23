package com.fstrise.ilovekara;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.FrameLayout;

import com.androidquery.AQuery;
import com.androidquery.util.AQUtility;
import com.fstrise.ilovekara.classinfo.Song;
import com.fstrise.ilovekara.config.Cals;
import com.fstrise.ilovekara.config.Conf;
import com.fstrise.ilovekara.customtoast.Effects;
import com.fstrise.ilovekara.customtoast.NiftyNotificationView;
import com.fstrise.ilovekara.data.SqlLiteDbHelper;
import com.fstrise.ilovekara.fragment.ListKara5Fragment;

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
	public String fileNameDL;
	public int positionPage = 0;
	private FragmentHome mFragmentHome;

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

		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
			cacheDir = new File(
					android.os.Environment.getExternalStorageDirectory(),
					Conf.folderSave);
		else
			cacheDir = this.getCacheDir();
		if (!cacheDir.exists())
			cacheDir.mkdirs();
		AQUtility.setCacheDir(cacheDir);
		aq = new AQuery(this);

		MaterialMenu menu = new MaterialMenu();

		// create intent for settings activity
		Intent i = new Intent(this, Settings.class);
		mFragmentHome = new FragmentHome(MainActivity.this);
		this.newSection(getResources().getString(R.string.home), this
				.getResources().getDrawable(R.drawable.ic_myhome),
				mFragmentHome, false, menu);
		this.newDevisor(menu);
		// section with own in click listener
		this.newLabel(getResources().getString(R.string.ikara), false, menu);
		MaterialSection<?> section7 = this.newSection(
				getResources().getString(R.string.cakhuccuatoi), this
						.getResources().getDrawable(R.drawable.ic_mymusic),
				new FragmentMySong(MainActivity.this), false, menu);
		section7.setNotifications(0);
		//
		MaterialSection<?> section8 = this.newSection(
				getResources().getString(R.string.myfavorite), this
						.getResources().getDrawable(R.drawable.ic_love),
				new FragmentVideoFavorite(MainActivity.this), false, menu);
		section8.setNotifications(0);
		MaterialSection<?> section9 = this.newSection(
				getResources().getString(R.string.singdownload), this
						.getResources().getDrawable(R.drawable.ic_dl),
				new FragmentVideoDownload(MainActivity.this), false, menu);
		section9.setNotifications(0);
		this.newDevisor(menu);
		// section with own in click listener
		this.newLabel(getResources().getString(R.string.karaquan), false, menu);
		this.newSection(getResources().getString(R.string.karaquan5so), this
				.getResources().getDrawable(R.drawable.ic_5so),
				new ListKara5Fragment(MainActivity.this), false, menu);
		this.newSection(getResources().getString(R.string.karaquan6so), this
				.getResources().getDrawable(R.drawable.ic_6so),
				new FragmentIndex(), false, menu);
		// section with own in click listener
		this.newLabel(getResources().getString(R.string.khac), false, menu);

		this.newSection(getResources().getString(R.string.feedback), this
				.getResources().getDrawable(R.drawable.ic_fb),
				new FragmentIndex(), false, menu);
		this.newSection(getResources().getString(R.string.huongdan), this
				.getResources().getDrawable(R.drawable.ic_info),
				new FragmentIndex(), false, menu);
		this.newSection(getResources().getString(R.string.about), this
				.getResources().getDrawable(R.drawable.ic_help),
				new FragmentIndex(), false, menu);
		// use bitmap and make a circle photo
		final Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.logo_small);
		final RoundedCornersDrawable drawableAppIcon = new RoundedCornersDrawable(
				getResources(), bitmap);

		// create Head Item
		MaterialHeadItem headItem = new MaterialHeadItem(this, getResources()
				.getString(R.string.ilovekara), getResources().getString(
				R.string.mysing), drawableAppIcon, R.drawable.karaoke, menu, 0);

		// add head Item (menu will be loaded automatically)
		this.addHeadItem(headItem);
		de.madcyph3r.materialnavigationdrawer.tools.ProgressWheel2 mainLoading = (de.madcyph3r.materialnavigationdrawer.tools.ProgressWheel2) findViewById(R.id.mainLoading);
		mainLoading.setLayoutParams(new FrameLayout.LayoutParams(Cals.w80,
				Cals.w80, Gravity.CENTER));
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		String hasdb = preferences.getString("hasdb", null);
		if (hasdb == null) {
			Log.i("Kar", "Add Item : hasdb null");
			new getDatafromAssetFolder().execute("");
		}

	}

	public void showToast(String msg) {
		NiftyNotificationView.build(this, msg, Effects.jelly, R.id.mLyout)
				.setIcon(R.drawable.messages).show();
	}

	public void enableLoading(boolean status) {
		FrameLayout frame_mainloading = (FrameLayout) findViewById(R.id.frame_mainloading);

		if (status) {
			frame_mainloading.setVisibility(View.VISIBLE);
		} else {
			frame_mainloading.setVisibility(View.GONE);
		}

	}

	public void onBackPressed() {
		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle(
						this.getApplicationContext().getString(
								R.string.msgcloseapp1))
				.setMessage(
						this.getApplicationContext().getString(
								R.string.msgcloseapp))
				.setPositiveButton(
						this.getApplicationContext().getString(R.string.co),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								finish();
							}

						})
				.setNegativeButton(
						this.getApplicationContext().getString(R.string.khong),
						null).show();
	}

	// Progress Dialog
	private ProgressDialog pDialog;

	// Progress dialog type (0 - for Horizontal progress bar)
	public static final int progress_bar_type = 0;

	/**
	 * Showing Dialog
	 * */
	@SuppressWarnings("deprecation")
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case progress_bar_type: // we set this to 0
			pDialog = new ProgressDialog(this);
			pDialog.setMessage("Đang tải video, vui lòng đợi...");
			pDialog.setIndeterminate(false);
			pDialog.setMax(100);
			pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pDialog.setCancelable(false);
			pDialog.setCanceledOnTouchOutside(false);
			pDialog.setButton("Hủy tải video",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							File f = new File(Environment
									.getExternalStorageDirectory()
									+ "/"
									+ Conf.folderSave
									+ "/dlvideo/"
									+ fileNameDL.replace(" ", "_") + ".mp4");
							if (f.exists())
								f.delete();
							cancelDownload = true;
							pDialog.dismiss();
							return;
						}
					});
			pDialog.show();

			return pDialog;
		default:
			return null;
		}
	}

	private boolean cancelDownload;

	class DownloadFileFromURL extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Bar Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			cancelDownload = false;
			showDialog(progress_bar_type);
		}

		/**
		 * Downloading file in background thread
		 * */
		@Override
		protected String doInBackground(String... f_url) {
			int count;
			try {
				URL url = new URL(f_url[0]);
				URLConnection conection = url.openConnection();
				conection.connect();
				// this will be useful so that you can show a tipical 0-100%
				// progress bar
				int lenghtOfFile = conection.getContentLength();

				// download the file
				InputStream input = new BufferedInputStream(url.openStream(),
						8192);

				File f = new File(Environment.getExternalStorageDirectory()
						+ "/" + Conf.folderSave + "/dlvideo/");
				if (!f.isDirectory()) {
					f.mkdir();
				}
				// Output stream
				OutputStream output = new FileOutputStream(Environment
						.getExternalStorageDirectory().toString()
						+ "/"
						+ Conf.folderSave
						+ "/dlvideo/"
						+ fileNameDL.replace(" ", "_") + ".mp4");

				byte data[] = new byte[1024];

				long total = 0;

				while ((count = input.read(data)) != -1) {
					total += count;
					// publishing the progress....
					// After this onProgressUpdate will be called
					publishProgress("" + (int) ((total * 100) / lenghtOfFile));

					// writing data to file
					output.write(data, 0, count);
					if (cancelDownload)
						break;
				}

				// flushing output
				output.flush();

				// closing streams
				output.close();
				input.close();

			} catch (Exception e) {
				Log.e("Error: ", e.getMessage());
			}

			return null;
		}

		/**
		 * Updating progress bar
		 * */
		protected void onProgressUpdate(String... progress) {
			// setting progress percentage
			pDialog.setProgress(Integer.parseInt(progress[0]));
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		@Override
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after the file was downloaded
			String imagePath = Environment.getExternalStorageDirectory()
					.toString()
					+ "/"
					+ Conf.folderSave
					+ "/dlvideo/"
					+ fileNameDL.replace(" ", "_") + ".mp4";
			dismissDialog(progress_bar_type);

		}
	}

	public void downloadVideo(String urlYTB) {
		new DownloadFileFromURL().execute(urlYTB);
	}

	protected boolean hasMicrophone() {
		PackageManager pmanager = this.getPackageManager();
		return pmanager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
	}

	private class getDatafromAssetFolder extends
			AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			Log.i("KarData", "Begin add Data");
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			Log.i("KarData", "doInBackground Data");
			// String tContents = "";
			//
			// try {
			// InputStream stream = getAssets().open(Conf.dbName);
			//
			// int size = stream.available();
			// byte[] buffer = new byte[size];
			// stream.read(buffer);
			// stream.close();
			// tContents = new String(buffer);
			// } catch (IOException e) {
			// // Handle exceptions here
			// }
			SqlLiteDbHelper db = new SqlLiteDbHelper(getApplicationContext());
			String contents = "";
			InputStream is = null;
			BufferedReader reader = null;
			try {
				is = getAssets().open(Conf.dbName);
				reader = new BufferedReader(new InputStreamReader(is));
				contents = reader.readLine();
				String line = null;
				while ((line = reader.readLine()) != null) {
					// 42,"không cảm xúc","Bao nhiêu lâu ta không gặp nhau,Bao nhiêu lâu ta không thấy...",57066,48,"Nguyễn Đình Vũ",""
					String[] arrItem = line.split(",");
					int id = Integer.parseInt(arrItem[0]);
					String title = arrItem[1].replace("\"", "");
					String lyric = arrItem[2].replace("\"", "");
					int code = Integer.parseInt(arrItem[3]);
					int vol = Integer.parseInt(arrItem[4]);
					String singer = arrItem[5].replace("\"", "");
					String composer = arrItem[6].replace("\"", "");
					Song obj = new Song(id, title, lyric, code, vol, singer,
							composer, 0);
					// Conf.arListKar.add(obj);
					// Log.i("KarData", obj.getCode()+" - " + obj.getTitle());
					db.insertSong(obj);
					contents += '\n' + line;
				}
			} catch (final Exception e) {
				e.printStackTrace();
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException ignored) {
					}
				}
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException ignored) {
					}
				}
			}
			return contents;

		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Log.i("KarData", "Finish Data");
			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());
			Editor edit = preferences.edit();
			edit.putString("hasdb", "1");
			edit.commit();
			Log.i("Kar", "Add Item : hasdb 1");
		}

		@Override
		protected void onProgressUpdate(String... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			Log.i("KarData", "onProgressUpdate Data");
		}

	}

	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		// Inflate menu to add items to action bar if it is present.
		inflater.inflate(R.menu.menu_main, menu);
		// Associate searchable configuration with the SearchView
		// SearchManager searchManager = (SearchManager)
		// getSystemService(Context.SEARCH_SERVICE);
		// SearchView searchView = (SearchView) menu.findItem(R.id.menu_search)
		// .getActionView();
		// searchView.setSearchableInfo(searchManager
		// .getSearchableInfo(getComponentName()));
		MenuItem searchItem = menu.findItem(R.id.menu_search);
		mSearchView = (SearchView) searchItem.getActionView();
		mSearchView.setOnQueryTextListener(this);
		// this.ms

		return true;
	}

	@Override
	public boolean onQueryTextChange(String arg0) {
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String arg0) {

		mFragmentHome.mPageHot.searchSong(arg0);
		return false;
	}

}
