package sremind.torymo.by.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "Series",
        indices = {@Index(value = {"imdbid"},unique = true),
                @Index(value = {"mdbid"}, unique = true)})
public class Series {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String mName;

    @ColumnInfo(name = "original_name")
    private String mOriginalName;

    @ColumnInfo(name = "imdbid")
    private String mImdbId;

    @ColumnInfo(name = "mdbid")
    private String mMdbId;

    @ColumnInfo(name = "poster")
    private String mPoster;

    @ColumnInfo(name = "watchlist")
    private boolean mWatchlist = true;

    public Series(String mName, String mOriginalName, String mImdbId, String mMdbId, String mPoster, boolean mWatchlist) {
        this.mName = mName;
        this.mOriginalName = mOriginalName;
        this.mImdbId = mImdbId;
        this.mWatchlist = mWatchlist;
        this.mMdbId = mMdbId;
        this.mPoster = mPoster;
    }

    @Ignore
    public Series(String mName, String mOriginalName, String mImdbId, String mMdbId, String mPoster) {
        this.mName = mName;
        this.mOriginalName = mOriginalName;
        this.mImdbId = mImdbId;
        this.mMdbId = mMdbId;
        this.mPoster = mPoster;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getImdbId() {
        return mImdbId;
    }

    public void setImdbId(String mImdbId) {
        this.mImdbId = mImdbId;
    }

    public boolean isWatchlist() {
        return mWatchlist;
    }

    public void setWatchlist(boolean mWatchlist) {
        this.mWatchlist = mWatchlist;
    }

    public String getMdbId() {
        return mMdbId;
    }

    public void setMdbId(String mMdbId) {
        this.mMdbId = mMdbId;
    }

    public String getPoster() {
        return mPoster;
    }

    public void setPoster(String mPoster) {
        this.mPoster = mPoster;
    }

    public String getOriginalName() {
        return mOriginalName;
    }

    public void setOriginalName(String mOriginalName) {
        this.mOriginalName = mOriginalName;
    }
}
