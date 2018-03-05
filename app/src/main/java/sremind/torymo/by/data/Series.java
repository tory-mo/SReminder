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

    @ColumnInfo(name = "genres")
    private String mGenres;

    @ColumnInfo(name = "ongoing")
    private boolean mOngoing = false;

    @ColumnInfo(name = "seasons")
    private long mSeasons = 0;

    @ColumnInfo(name = "overview")
    private String mOverview;

    @ColumnInfo(name = "popularity")
    private float mPopularity = 0;

    @ColumnInfo(name = "status")
    private String mStatus;

    public Series(String mName, String mOriginalName, String mImdbId, String mMdbId, String mPoster, long mSeasons, boolean mOngoing, String mStatus, String mGenres, String mOverview, float mPopularity, boolean mWatchlist) {
        this.mName = mName;
        this.mOriginalName = mOriginalName;
        this.mImdbId = mImdbId;
        this.mWatchlist = mWatchlist;
        this.mMdbId = mMdbId;
        this.mPoster = mPoster;
        this.mSeasons = mSeasons;
        this.mOngoing = mOngoing;
        this.mStatus = mStatus;
        this.mGenres = mGenres;
        this.mOverview = mOverview;
        this.mPopularity = mPopularity;
    }

    @Ignore
    public Series(String mName, String mOriginalName, String mImdbId, String mMdbId, String mPoster, long mSeasons, boolean mOngoing, String mStatus, String mGenres, String mOverview, float mPopularity) {
        this.mName = mName;
        this.mOriginalName = mOriginalName;
        this.mImdbId = mImdbId;
        this.mMdbId = mMdbId;
        this.mPoster = mPoster;
        this.mSeasons = mSeasons;
        this.mOngoing = mOngoing;
        this.mStatus = mStatus;
        this.mGenres = mGenres;
        this.mOverview = mOverview;
        this.mPopularity = mPopularity;
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

    public String getGenres() {
        return mGenres;
    }

    public void setGenres(String mGenres) {
        this.mGenres = mGenres;
    }

    public boolean isOngoing() {
        return mOngoing;
    }

    public void setOngoing(boolean mOngoing) {
        this.mOngoing = mOngoing;
    }

    public long getSeasons() {
        return mSeasons;
    }

    public void setSeasons(long mSeasons) {
        this.mSeasons = mSeasons;
    }

    public String getOverview() {
        return mOverview;
    }

    public void setOverview(String mOverview) {
        this.mOverview = mOverview;
    }

    public float getPopularity() {
        return mPopularity;
    }

    public void setPopularity(float mPopularity) {
        this.mPopularity = mPopularity;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String mStatus) {
        this.mStatus = mStatus;
    }
}
