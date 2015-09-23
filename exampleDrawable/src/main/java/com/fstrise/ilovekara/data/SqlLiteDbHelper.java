package com.fstrise.ilovekara.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

import com.fstrise.ilovekara.classinfo.Song;
import com.fstrise.ilovekara.classinfo.video;
import com.fstrise.ilovekara.config.Conf;

public class SqlLiteDbHelper extends SQLiteOpenHelper {
	private int abc;
	// All Static variables
	// Database Versiondd
	private static final int DATABASE_VERSION = 123;
	private static final String DATABASE_PATH = "/data/data/com.fstrise.ilovekara/databases/";
	// Database Name
	private static final String DATABASE_NAME = "songdb";
	
	private SQLiteDatabase db;
	// favorite table name
	private static final String TABLE_SONG = "Song";
	// favorite Columns names
	private static final String K_ID_ = "id_";
	private static final String K_ID = "id";
	private static final String K_TITLE = "title";
	private static final String K_LYRIC = "lyric";
	private static final String K_CODE = "code";
	private static final String K_VOL = "vol";
	private static final String K_SINGER = "singer";
	private static final String K_COMPOSER = "composer";
	private static final String K_FAV = "fav";
	//
	private static final String TABLE_Video = "videos";
	private static final String VIDEO_ID_ = "id_";
	private static final String VIDEO_CODE = "code";
	private static final String VIDEO_TITLE = "title";
	private static final String VIDEO_LYRICS = "lyrics";
	private static final String VIDEO_URL = "url";
	private static final String VIDEO_SINGER = "singer";
	private static final String VIDEO_TYPE = "type";
	// type = 1 fav : 2 download : 3 my song

	Context ctx;
	// colums Video item
	private static final String[] COLUMNS_VIDEOITEM = { VIDEO_ID_, VIDEO_CODE,
			VIDEO_TITLE, VIDEO_LYRICS, VIDEO_URL, VIDEO_SINGER, VIDEO_TYPE };

	// colums Kar item
	private static final String[] COLUMNS_KARITEM = { K_ID_, K_ID, K_TITLE,
			K_LYRIC, K_CODE, K_VOL, K_SINGER, K_COMPOSER, K_FAV };

	// insert fav video
	public void insertFavVideo(video itemL) {
		try {
			// 1. get reference to writable DB
			SQLiteDatabase db = this.getWritableDatabase();
			// 2. create ContentValues to add key "column"/value
			ContentValues values = new ContentValues();
			values.put(VIDEO_CODE, itemL.getVideo_code());
			values.put(VIDEO_TITLE, itemL.getTitle());
			values.put(VIDEO_LYRICS, itemL.getLyrics());
			values.put(VIDEO_URL, itemL.getUrl());
			values.put(VIDEO_SINGER, itemL.getSinger());
			values.put(VIDEO_TYPE, itemL.getType());
			// 3. insert
			db.insert(TABLE_Video, // table
					null, // nullColumnHack
					values); // key/value -> keys = column names/ values =
								// column values
			// 4. close
			db.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// get channel Item by channel id
	public video getFavVideoByCode(String videcode) {
		// type: 1 live, 2 movie
		// 1. get reference to readable DB
		SQLiteDatabase db = this.getReadableDatabase();
		// 2. build query
		Cursor cursor = db.query(TABLE_Video, // a. table
				COLUMNS_VIDEOITEM, // b. column names
				"  code = ?", // c. selections
				new String[] { videcode }, // d.
											// selections
											// args
				null, // e. group by
				null, // f. having
				null, // g. order by
				null); // h. limit
		// 3. if we got results get the first one
		video obj = null;
		if (cursor.moveToFirst()) {
			do {
				obj = new video();
				obj.setVideo_code(cursor.getString(1));
				obj.setTitle(cursor.getString(2));
				obj.setLyrics(cursor.getString(3));
				obj.setUrl(cursor.getString(4));
				obj.setSinger(cursor.getString(5));
				obj.setType(cursor.getInt(6));
			} while (cursor.moveToNext());
		}
		if (db.isOpen()) {
			cursor.close();
			db.close();
		}
		return obj;
	}

	// get list fav
	public List<video> getAllFavVideo() {
		List<video> arrItem = new ArrayList<video>();
		// 1. build the query
		String query = "SELECT  * FROM " + TABLE_Video;
		// 2. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		// 3. go over each row, build fav and add it to list
		video obj = null;
		if (cursor.moveToFirst()) {
			do {
				obj = new video();
				obj.setVideo_code(cursor.getString(1));
				obj.setTitle(cursor.getString(2));
				obj.setLyrics(cursor.getString(3));
				obj.setUrl(cursor.getString(4));
				obj.setSinger(cursor.getString(5));
				obj.setType(cursor.getInt(6));
				arrItem.add(obj);
			} while (cursor.moveToNext());
		}
		if (db.isOpen()) {
			cursor.close();
			db.close();
		}
		return arrItem;
	}

	// delete fav item by id
	public void deleteFavVideo(String id, int type) {
		try {
			// 1. get reference to writable DB
			SQLiteDatabase db = this.getWritableDatabase();
			// 2. delete
			db.delete(TABLE_Video, // table name
					VIDEO_CODE + " = ? AND " + VIDEO_TYPE + " = " + type, // selections
					new String[] { id }); // selections
											// args
			// 3. close
			db.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public SqlLiteDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		ctx = context;
	}
	
	// insert fav video
		public void insertSong(Song itemL) {
			try {
				// 1. get reference to writable DB
				SQLiteDatabase db = this.getWritableDatabase();
				// 2. create ContentValues to add key "column"/value
				ContentValues values = new ContentValues();
				values.put(K_ID, itemL.getId());
				values.put(K_TITLE, itemL.getTitle());
				values.put(K_LYRIC, itemL.getLyric());
				values.put(K_CODE, itemL.getCode());
				values.put(K_VOL, itemL.getSinger());
				values.put(K_SINGER, itemL.getSinger());
				values.put(K_COMPOSER, itemL.getComposer());
				values.put(K_FAV, itemL.getFav());
				// 3. insert
				db.insert(TABLE_SONG, // table
						null, // nullColumnHack
						values); // key/value -> keys = column names/ values =
									// column values
				// 4. close
				db.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		

	// Getting single contact
	public ArrayList<Song> get_Song(String name) {
		SQLiteDatabase db = this.getReadableDatabase();
		//
		ArrayList<Song> aSong = new ArrayList<Song>();
		Cursor cursor = db.query(TABLE_SONG, new String[] { K_ID, K_TITLE,
				K_LYRIC, K_CODE, K_SINGER, K_COMPOSER, K_FAV }, null, null,
				null, null, null);
		if (cursor.moveToFirst()) {
			do {
				// id, title, lyrics, code, vol, singer, composer
				Song obj = new Song();
				obj.setId(cursor.getInt(1));
				obj.setTitle(cursor.getString(2));
				obj.setLyric(cursor.getString(3));
				obj.setCode(cursor.getInt(4));
				obj.setVol(cursor.getInt(5));
				obj.setSinger(cursor.getString(6));
				obj.setComposer(cursor.getString(7));
				obj.setFav(cursor.getInt(8));

				aSong.add(obj);
			} while (cursor.moveToNext());
		}
		return aSong;
	}

	public ArrayList<Song> get_SongByRow(String kw) {

		ArrayList<Song> aSong = new ArrayList<Song>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_SONG + " WHERE " + kw
				+ " ORDER BY  " +K_TITLE+"  asc ";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		try {
			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					Song obj = new Song();
					obj.setId(cursor.getInt(1));
					obj.setTitle(cursor.getString(2));
					obj.setLyric(cursor.getString(3));
					obj.setCode(cursor.getInt(4));
					obj.setVol(cursor.getInt(5));
					obj.setSinger(cursor.getString(6));
					obj.setComposer(cursor.getString(7));
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
				+ " WHERE fav = 1 ORDER BY " + K_TITLE+" asc ";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		try {
			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					Song obj = new Song();
					obj.setId(cursor.getInt(1));
					obj.setTitle(cursor.getString(2));
					obj.setLyric(cursor.getString(3));
					obj.setCode(cursor.getInt(4));
					obj.setVol(cursor.getInt(5));
					obj.setSinger(cursor.getString(6));
					obj.setComposer(cursor.getString(7));
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
				+ " WHERE fav = 1 ORDER BY  " +K_TITLE+"  asc ";
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
				K_ID + " = ?", // selections
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
				+ " ORDER BY " +K_TITLE+" asc ";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		try {
			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					Song obj = new Song();
					obj.setId(cursor.getInt(1));
					obj.setTitle(cursor.getString(2));
					obj.setLyric(cursor.getString(3));
					obj.setCode(cursor.getInt(4));
					obj.setVol(cursor.getInt(5));
					obj.setSinger(cursor.getString(6));
					obj.setComposer(cursor.getString(7));
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

		InputStream in = ctx.getAssets().open(Conf.dbName);
		Log.e("sample", "Starting copying");
		String outputFileName = DATABASE_PATH + DATABASE_NAME;
		File databaseFile = new File(
				"/data/data/com.fstrise.ilovekara/databases");
		// check if databases folder exists, if not create one and its
		// subfolders
		if (!databaseFile.exists()) {
			databaseFile.mkdir();
		}

		// OutputStream out = new FileOutputStream(outputFileName);

		byte[] buffer = new byte[1024];
		int length;
		String item = "";
		while ((length = in.read(buffer)) > 0) {
			// out.write(buffer, 0, length);

			item = new String(buffer);
		}
		Log.e("sample", "Completed");
		// out.flush();
		// out.close();
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
		String CREATE_VIDEOS_TABLE = "CREATE TABLE " + TABLE_Video + " ( "
				+ VIDEO_ID_ + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ VIDEO_CODE + "  TEXT, " + VIDEO_TITLE + " TEXT, "
				+ VIDEO_LYRICS + " TEXT, " + VIDEO_URL + " TEXT, "
				+ VIDEO_SINGER + " TEXT, " + VIDEO_TYPE + " INTEGER )";
//		SharedPreferences preferences = PreferenceManager
//				.getDefaultSharedPreferences(ctx);
//		String hasdb = preferences.getString("hasdb", null);
//		if (hasdb == null) {

			String CREATE_KAR_TABLE = "CREATE TABLE " + TABLE_SONG + " ( "
					+ K_ID_ + " INTEGER PRIMARY KEY AUTOINCREMENT, " + K_ID
					+ "  INTEGER, " + K_TITLE + " TEXT, " + K_LYRIC + " TEXT, "
					+ K_CODE + " INTEGER, " + K_VOL + " INTEGER, " + K_SINGER
					+ " TEXT, " + K_COMPOSER + " TEXT, " + K_FAV + " INTEGER )";
			db.execSQL(CREATE_KAR_TABLE);
		// Editor edit = preferences.edit();
		// edit.putString("hasdb", "1");
		// edit.commit();
//		}
		// create table

		db.execSQL(CREATE_VIDEOS_TABLE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_Video);

		onCreate(db);

	}
}
