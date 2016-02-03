package sremind.torymo.by;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SReminderDatabase extends SQLiteOpenHelper {

	// ��������� ��� ������������
	private static final String DATABASE_NAME = "sreminder_database.db";
	private static final int DATABASE_VERSION = 5;
	
	private static final String tabSeries = "Series";
	private static final String sName = "name";
	private static final String sImdbId = "imdbid";
	private static final String sWatchList = "watchlist";
	
	private static final String tabEpisodes = "Episodes";
	private static final String eName = "name";
	private static final String eSeries = "series";
	private static final String eDate = "date";
	private static final String eEpNumber = "ep_number";
	private static final String eSeen = "seen";
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	
	public SReminderDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("Create table "+tabSeries+"("
				+ "id integer primary key autoincrement,"
				+sImdbId+" text,"
				+sWatchList+" integer,"
				+ sName+" text);");
		
		db.execSQL("Create table "+tabEpisodes+"("
				+"id integer primary key autoincrement,"
				+eName+" text,"
				+eSeries+" integer,"
				+eEpNumber+" text,"
				+eDate+" integer,"
				+eSeen+" inreger);");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVer, int currVer) {
		db.execSQL("DROP TABLE IF EXISTS "+tabSeries);
		db.execSQL("DROP TABLE IF EXISTS "+tabEpisodes);
		onCreate(db);
	}
	
	public boolean addSeries(String name, String imdbId){
		if (isSeriesInBase(imdbId)) return false;//������ �� ���� ���������
		SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(sName, name);
        cv.put(sImdbId, imdbId);
        cv.put(sWatchList, 0);
        db.insert(tabSeries,null,cv);
        db.close();
        return true;//������ ���� ���������
	}
	
	public void changeWatchlistStatus(String imdbId, boolean watchlist){
		String selectQuery = "SELECT id FROM " + tabSeries+" WHERE "+sImdbId+" like '"+imdbId+"'";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		int series = -1;
		if(cursor.moveToFirst()){
			series = cursor.getInt(0);
		}
		byte value = 0;
		if(watchlist) value = 1;
		cursor.close();
		db.close();
		ContentValues cv = new ContentValues();
		cv.put(sWatchList, value);
		db = this.getWritableDatabase();
		db.update(tabSeries, cv, "id="+series, null);
		db.close();
		
	}
	
	public void changeSeenStatus(int series, String name, boolean seen){
		/*int pos = 0;
		while (pos!=-1){
			pos = name.indexOf("'", 0);
			if(pos!=-1)
				name = name.substring(0, pos) + "\\" + name.substring(pos, name.length());
		}*/
		
		String selectQuery = "SELECT id FROM " + tabEpisodes+" WHERE "+eSeries+" ="+series+" AND "+eName+" like \""+name+"\"";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		int episode = -1;
		if(cursor.moveToFirst()){
			episode = cursor.getInt(0);
		}
		byte value = 0;
		if(seen) value = 1;
		cursor.close();
		db.close();
		ContentValues cv = new ContentValues();
		cv.put(eSeen, value);
		db = this.getWritableDatabase();
		db.update(tabEpisodes, cv, "id="+episode, null);
		db.close();		
	}
	
	
	public void deleteSeries(String imdbId){
		String selectQuery = "SELECT id FROM " + tabSeries+" WHERE "+sImdbId+" like '"+imdbId+"'";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		int series = -1;
		if(cursor.moveToFirst()){
			series = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		if(series!=-1){
			db = this.getWritableDatabase();
			//delete episodes
			db.delete(tabEpisodes, eSeries+" = "+series, null);
			
			//delete series			
			db.delete(tabSeries, sImdbId+" like '"+imdbId+"'", null);
			db.close();
		}
	}
	
	public void deleteEpisode(String name, Date date){
		SQLiteDatabase db = this.getWritableDatabase();
		Long dateLong = getDateTime(date);
		db.delete(tabEpisodes, eDate+" = "+dateLong+" AND "+eName+" like '"+name+"'", null);
		db.close();
	}
	
	public void deleteEpisodesForSeries(String imdbId){
		String selectQuery = "SELECT id FROM " + tabSeries+" WHERE "+sImdbId+" like '"+imdbId+"'";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		int series = -1;
		if(cursor.moveToFirst()){
			series = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		if(series!=-1){
			db = this.getWritableDatabase();
			db.delete(tabEpisodes, eSeries+" = "+series, null);
			db.close();
		}
	}
	
	public boolean isSeriesInBase(String imdbId){
		String selectQuery = "SELECT  * FROM " + tabSeries+" WHERE "+sImdbId+" like '"+imdbId+"'";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		if(cursor.moveToFirst()){
			db.close();
			return true;
		}
		cursor.close();
		db.close();
		return false;
	}
	
	public boolean addEpisode(String imdbId, String episodeName, Date date, String episodeNumber){
		
		String selectQuery = "SELECT id FROM " + tabSeries+" WHERE "+sImdbId+" like '"+imdbId+"'";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		int series = -1;
		if(cursor.moveToFirst()){
			series = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		if(series!=-1){
			if (isEpisodeInBase(series, episodeNumber)){
				selectQuery = "SELECT id FROM " + tabEpisodes+" WHERE "+eSeries+" ="+series+" AND "+eEpNumber+" like \""+episodeNumber+"\"";
				db = this.getReadableDatabase();
				cursor = db.rawQuery(selectQuery, null);
				int episode = -1;
				if(cursor.moveToFirst()){
					episode = cursor.getInt(0);
				}
				if(episode!=-1){
					ContentValues cv = new ContentValues();
					cv.put(eName, episodeName);
					if(date!=null){
			        	long dateToBase = getDateTime(date);
			        	cv.put(eDate, dateToBase);
			        }
					db = this.getWritableDatabase();
					db.update(tabEpisodes, cv, "id="+episode, null);
					cursor.close();
					db.close();
					return true;	
				}
				db.close();
				return false;//������ �� ���� ���������
			}else{
				db = this.getWritableDatabase();
		        ContentValues cv = new ContentValues();
		        cv.put(eName, episodeName);
		        cv.put(eSeries, series);
		        if(date!=null){
		        	long dateToBase = getDateTime(date);
		        	cv.put(eDate, dateToBase);
		        }
		        
		        cv.put(eEpNumber, episodeNumber);
		        cv.put(eSeen, 0);
		        db.insert(tabEpisodes,null,cv);
		        db.close();
		        return true;//������ ���� ���������
			}
		}
		return false;
	}
	
	public boolean isEpisodeInBase(int series, String episodeNumber){
		String selectQuery = "SELECT  * FROM " + tabEpisodes+" WHERE "+eSeries+" = "+series+" AND "+eEpNumber+ " like '"+episodeNumber+"'";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		if(cursor.moveToFirst()){
			cursor.close();
			db.close();
			return true;
		}
		cursor.close();
		db.close();
		return false;
	}
	
	public ArrayList<Episode> getEpisodesBetweenDates(Date from, Date to){
		ArrayList<Episode> arr = new ArrayList<Episode>();
		String selectQuery = "Select "+tabSeries+"."+eName+", "+tabEpisodes+"."+sName+", "+tabEpisodes+"."+eDate+" FROM "+tabSeries+" INNER JOIN "+tabEpisodes+" ON "+tabSeries+".id = "+tabEpisodes
								+".id Where "+tabEpisodes+"."+eDate+" BETWEEN "+getDateTime(from)+" AND "+getDateTime(to);
		
		/*
		 * SELECT series.[_name], episodes.[_name], episodes.[_date] FROM series INNER JOIN episodes ON series.id = episodes.[_sname];
		 * */
		
		return arr;
	}
	
	public ArrayList<Episode> getEpisodesForDate(Date date, boolean onlySeen){
		Long dateLong = getDateTime(date);
		
		ArrayList<Episode> arr = new ArrayList<Episode>();
		String selectQuery = "Select "+tabSeries+"."+sName+", "+tabEpisodes+"."+eName+", "+tabEpisodes+"."+eEpNumber+", "+tabEpisodes+"."+eSeen+", "+tabEpisodes+"."+eDate+", "+tabEpisodes+"."+eSeries+" FROM "+tabEpisodes+" INNER JOIN "+tabSeries+" ON "+tabEpisodes+"."+eSeries+" = "+tabSeries
								+".id Where "+tabEpisodes+"."+eDate+" = "+dateLong;
		if(onlySeen){
			selectQuery += " AND "+eSeen+"=0";
		}
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		Episode ep;
		if(cursor.moveToFirst()){
			do{
				ep = new Episode();
				ep.episodeName = cursor.getString(1);
				ep.seriesId = cursor.getInt(5);
				ep.episodeNumber = cursor.getString(2);
				ep.seen = cursor.getInt(3)==1?true:false;
				ep.date = getCalendarFromFormattedLong(cursor.getLong(4));
				ep.seriesName = cursor.getString(0);
				arr.add(ep);
			}while(cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return arr;
	}
	
	public ArrayList<Date> getDatesInPeriod(Date from, Date to, boolean onlySeen){
		ArrayList<Date> arrDate = new ArrayList<Date>();
		Date tmpDate;
		Date date;
		Long fromLong = getDateTime(from);
		Long toLong = getDateTime(to);
		String selectQuery = "Select "+eDate+" FROM "+tabEpisodes+" Where ("+eDate+" BETWEEN "+fromLong+" AND "+toLong+")";
		if(onlySeen){
			selectQuery += " AND "+eSeen+"=0";
		}
		selectQuery += " GROUP BY "+eDate;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		if(cursor.moveToFirst()){
			do{
				tmpDate = getCalendarFromFormattedLong(cursor.getLong(0));
				date = new Date(tmpDate.getYear(),tmpDate.getMonth(),tmpDate.getDate());
				arrDate.add(date);
			}while(cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return arrDate;
	}
	
	public ArrayList<Episode> getEpisodesForSeries(String seriesName){
		ArrayList<Episode> arr = new ArrayList<Episode>();
		String selectQuery = "SELECT  id FROM " + tabSeries+" WHERE "+sName+" like '"+seriesName+"'";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		int series = -1;
		if(cursor.moveToFirst()){
			series = cursor.getInt(0);
		}
		Episode ep;
		
		if(series!=-1){
			selectQuery = "SELECT "+eName+", "+eDate+", "+eEpNumber+", "+eSeen+", "+eSeries+" FROM " + tabEpisodes+" WHERE "+eSeries+"  ="+series;
			cursor = db.rawQuery(selectQuery, null);
			if(cursor.moveToFirst()){
				do{
					ep = new Episode();
					ep.date = getCalendarFromFormattedLong(cursor.getLong(1));
					ep.episodeName = cursor.getString(0);
					ep.episodeNumber = cursor.getString(2);
					ep.seen = cursor.getInt(3)==1?true:false;
					ep.seriesId = cursor.getInt(4);
					ep.seriesName = seriesName;
					arr.add(ep);
				}while(cursor.moveToNext());
			}
		}
		cursor.close();
		db.close();
		return arr;
	}
	
	public ArrayList<String> getAllSeriesList(){
		ArrayList<String> arr = new ArrayList<String>();
		String selectQuery = "Select "+sName+" FROM "+tabSeries;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if(cursor.moveToFirst()){
			do{
				arr.add(cursor.getString(0));
			}while(cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return arr;
	}
	
	public ArrayList<String> getAllEpisodesList(){
		ArrayList<String> arr = new ArrayList<String>();
		String selectQuery = "Select * FROM "+tabEpisodes;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if(cursor.moveToFirst()){
			do{
				arr.add(cursor.getString(1));
			}while(cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return arr;
	}
	
	public ArrayList<SRSeries> getAllSeriesInfo(){
		ArrayList<SRSeries> arr = new ArrayList<SRSeries>();
		String selectQuery = "Select * FROM "+tabSeries+" ORDER BY "+ sName;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		boolean watchlist;
		if(cursor.moveToFirst()){
			do{
				watchlist = false;
				if(cursor.getInt(2)==1)watchlist = true;
				arr.add(new SRSeries(cursor.getString(1), cursor.getString(3), watchlist));
			}while(cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return arr;
	}
	
	public ArrayList<String> getWatchList(){
		ArrayList<String> arr = new ArrayList<String>();
		String selectQuery = "Select "+sName+" FROM "+tabSeries+" WHERE "+sWatchList+"=1 ORDER BY "+sName;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if(cursor.moveToFirst()){
			do{
				arr.add(cursor.getString(0));
			}while(cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return arr;
	}
	
	public ArrayList<SRSeries> getWatchListSeries(){
		ArrayList<SRSeries> arr = new ArrayList<SRSeries>();
		String selectQuery = "Select * FROM "+tabSeries+" WHERE "+sWatchList+"=1 ORDER BY "+sName;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		boolean watchlist;
		if(cursor.moveToFirst()){
			do{
				watchlist = false;
				if(cursor.getInt(2)==1)watchlist = true;
				arr.add(new SRSeries(cursor.getString(1), cursor.getString(3), watchlist));
			}while(cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return arr;
	}
	
	class Episode{
		int seriesId;
		String seriesName;
		String episodeName;
		String episodeNumber;
		Date date;
		boolean seen;
	}
	private Long getDateTime(Date date) {
        return Long.parseLong(dateFormat.format(date));
	}
	
	public static Date getCalendarFromFormattedLong(long l){		
		try {        
			String str = String.valueOf(l);
			Date date = dateFormat.parse(str);
		    return date;              
		} catch (ParseException e) {
			return null;
		}
	}

	public String seriesNameByImdbid(String name) {
		String res = "";
		String selectQuery = "Select "+sImdbId+" FROM "+tabSeries+" WHERE "+sName+" like '"+name+"'";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if(cursor.moveToFirst()){
			res = cursor.getString(0);
		}
		cursor.close();
		db.close();
		
		return res;
	}

}
