package com.fstrise.ilovekara.classinfo;

public class Song {
	private int id;
	private String title;
	private String lyric;
	private int code;
	private int vol;
	private String singer;
	private String composer;
	private int fav;

	public Song() {

	}

	public Song(int id, String title, String lyric, int code, int vol, String singer, String composer, int fav) {
		this.id = id;
		this.title = title;
		this.lyric = lyric;
		this.code = code;
		this.vol = vol;
		this.singer = singer;
		this.composer = composer;
		this.fav = fav;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLyric() {
		return lyric;
	}

	public void setLyric(String lyric) {
		this.lyric = lyric;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public int getVol() {
		return vol;
	}

	public void setVol(int vol) {
		this.vol = vol;
	}

	public String getSinger() {
		return singer;
	}

	public void setSinger(String singer) {
		this.singer = singer;
	}

	public String getComposer() {
		return composer;
	}

	public void setComposer(String composer) {
		this.composer = composer;
	}

	public int getFav() {
		return fav;
	}

	public void setFav(int fav) {
		this.fav = fav;
	}

	
}
