package com.fstrise.ilovekara.classinfo;

public class Song {
	private int id;
	private String title;
	private String title_simple;
	private String lang;
	private String lyric;
	private String source;
	private int fav;

	public Song() {

	}

	public Song(int id, String title, String titles, String lang, String lyric,
			String source,int fav) {
		this.id = id;
		this.title = title;
		this.title_simple = titles;
		this.lang = lang;
		this.lyric = lyric;
		this.source = source;
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

	public String getTitle_simple() {
		return title_simple;
	}

	public void setTitle_simple(String title_simple) {
		this.title_simple = title_simple;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getLyric() {
		return lyric;
	}

	public void setLyric(String lyric) {
		this.lyric = lyric;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * @return the fav
	 */
	public int getFav() {
		return fav;
	}

	/**
	 * @param fav the fav to set
	 */
	public void setFav(int fav) {
		this.fav = fav;
	}
}
