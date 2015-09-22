package com.fstrise.ilovekara.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import com.fstrise.ilovekara.classinfo.Song;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;

public class SqlLiteDbHelper extends SQLiteOpenHelper {
	private int abc;
	// All Static variables
	// Database Versiondd
	private static final int DATABASE_VERSION = 120;
	private static final String DATABASE_PATH = "/data/data/com.fstrise.ilovekara/databases/";
	// Database Name
	private static final String DATABASE_NAME = "songdb";

	private SQLiteDatabase db;
	// favorite table name
	private static final String TABLE_SONG = "Song";
	// favorite Columns names
	private static final String KEY_ID_ = "id_";
	private static final String KEY_ID = "id";
	private static final String KEY_TITLE = "title";
	private static final String KEY_TITLE_S = "titles";
	private static final String KEY_LANG = "lang";
	private static final String KEY_LYRIC = "lyric";
	private static final String KEY_SOURCE = "source";
	private static final String KEY_FAV = "fav";
	//

	Context ctx;

	public SqlLiteDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		ctx = context;
	}

	// Getting single contact
	public ArrayList<Song> get_Song(String name) {
		SQLiteDatabase db = this.getReadableDatabase();
		//
		ArrayList<Song> aSong = new ArrayList<Song>();
		Cursor cursor = db.query(TABLE_SONG, new String[] { KEY_ID, KEY_TITLE,
				KEY_TITLE_S, KEY_LANG, KEY_LYRIC, KEY_SOURCE, KEY_FAV }, null,
				null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				Song obj = new Song();
				obj.setId(cursor.getInt(1));
				obj.setTitle(cursor.getString(2));
				obj.setTitle_simple(cursor.getString(3));
				obj.setLang(cursor.getString(4));
				obj.setLyric(cursor.getString(5));
				obj.setSource(cursor.getString(6));
				aSong.add(obj);
			} while (cursor.moveToNext());
		}
		return aSong;
	}

	public ArrayList<Song> get_SongByRow(String kw) {

		ArrayList<Song> aSong = new ArrayList<Song>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_SONG + " WHERE " + kw
				+ " ORDER BY titles asc ";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		try {
			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					Song obj = new Song();
					obj.setId(cursor.getInt(1));
					obj.setTitle(cursor.getString(3));
					obj.setTitle_simple(cursor.getString(4));
					obj.setLang(cursor.getString(5));
					obj.setLyric(cursor.getString(6));
					obj.setSource(cursor.getString(7));
					obj.setFav(cursor.getInt(8));
					aSong.add(obj);
				} while (cursor.moveToNext());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			cursor.close();
			if (db.isOpen())
				db.close();
		}

		return aSong;
	}

	public ArrayList<Song> get_SongByFav() {

		ArrayList<Song> aSong = new ArrayList<Song>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_SONG
				+ " WHERE fav = 1 ORDER BY titles asc ";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		try {
			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					Song obj = new Song();
					obj.setId(cursor.getInt(1));
					obj.setTitle(cursor.getString(3));
					obj.setTitle_simple(cursor.getString(4));
					obj.setLang(cursor.getString(5));
					obj.setLyric(cursor.getString(6));
					obj.setSource(cursor.getString(7));
					obj.setFav(cursor.getInt(8));
					aSong.add(obj);
				} while (cursor.moveToNext());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			cursor.close();
			if (db.isOpen())
				db.close();
		}

		return aSong;
	}

	public int getRowFav() {

		ArrayList<Song> aSong = new ArrayList<Song>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_SONG
				+ " WHERE fav = 1 ORDER BY titles asc ";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		try {
			// looping through all rows and adding to list
			return cursor.getCount();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			cursor.close();
			if (db.isOpen())
				db.close();
		}
		return 0;
	}

	public int updateSong(int fav, int id) {

		// 1. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();

		// 2. create ContentValues to add key "column"/value
		ContentValues values = new ContentValues();
		values.put("fav", fav); // get title

		// 3. updating row
		int i = db.update(TABLE_SONG, // table
				values, // column/value
				KEY_ID + " = ?", // selections
				new String[] { String.valueOf(id) }); // selection
														// args

		// 4. close
		db.close();

		return i;

	}
	
	public ArrayList<Song> getAllSong() {

		ArrayList<Song> aSong = new ArrayList<Song>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_SONG
				+ " ORDER BY titles asc ";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		try {
			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					Song obj = new Song();
					obj.setId(cursor.getInt(1));
					obj.setTitle(cursor.getString(3));
					obj.setTitle_simple(cursor.getString(4));
					obj.setLang(cursor.getString(5));
					obj.setLyric(cursor.getString(6));
					obj.setSource(cursor.getString(7));
					obj.setFav(cursor.getInt(8));
					aSong.add(obj);
				} while (cursor.moveToNext());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			cursor.close();
			if (db.isOpen())
				db.close();
		}

		return aSong;
	}

	public void CopyDataBaseFromAsset(Context cont) throws IOException {
		InputStream in = ctx.getAssets().open(DATABASE_NAME);
		Log.e("sample", "Starting copying");
		String outputFileName = DATABASE_PATH + DATABASE_NAME;
		File databaseFile = new File(
				"/data/data/com.fstrise.ilovekara/databases");
		// check if databases folder exists, if not create one and its
		// subfolders
		if (!databaseFile.exists()) {
			databaseFile.mkdir();
		}

		OutputStream out = new FileOutputStream(outputFileName);

		byte[] buffer = new byte[1024];
		int length;

		while ((length = in.read(buffer)) > 0) {
			out.write(buffer, 0, length);
		}
		Log.e("sample", "Completed");
		out.flush();
		out.close();
		in.close();
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(cont);
		Editor edit = preferences.edit();
		edit.putString("hasdb", "1");
		edit.commit();

	}

	public void openDataBase() throws SQLException {
		String path = DATABASE_PATH + DATABASE_NAME;
		db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.NO_LOCALIZED_COLLATORS
						| SQLiteDatabase.CREATE_IF_NECESSARY);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
}
