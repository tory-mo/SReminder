package sremind.torymo.by.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "search_result", indices = {@Index(value = {"sr_id"},
        unique = true)})
public class SearchResult {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String mName;

    @ColumnInfo(name = "sr_id")
    private String mSRId;

    @ColumnInfo(name = "imdbid")
    private String mImdbId;

    @ColumnInfo(name = "poster")
    private String mPoster;

    @ColumnInfo(name = "overview")
    private String mOverview;

    @ColumnInfo(name = "first_date")
    private long mFirstDate;

    @ColumnInfo(name = "homepage")
    private String mHomepage;

    @ColumnInfo(name = "seasons")
    private int mSeasons = 0;

    @ColumnInfo(name = "episode_time")
    private String mEpisodeTime;

    @ColumnInfo(name = "ongoing")
    private boolean mOngoing;

    @ColumnInfo(name = "genres")
    private String mGenres;

    @ColumnInfo(name = "popularity")
    private float mPopularity = 0;


    public int getId() {
        return id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getSRId() {
        return mSRId;
    }

    public void setSRId(String mSRId) {
        this.mSRId = mSRId;
    }

    public String getImdbId() {
        return mImdbId;
    }

    public void setImdbId(String mImdbId) {
        this.mImdbId = mImdbId;
    }

    public String getPoster() {
        return mPoster;
    }

    public void setPoster(String mPoster) {
        this.mPoster = mPoster;
    }

    public String getOverview() {
        return mOverview;
    }

    public void setOverview(String mOverview) {
        this.mOverview = mOverview;
    }

    public long getFirstDate() {
        return mFirstDate;
    }

    public void setFirstDate(long mFirstDate) {
        this.mFirstDate = mFirstDate;
    }

    public String getHomepage() {
        return mHomepage;
    }

    public void setHomepage(String mHomepage) {
        this.mHomepage = mHomepage;
    }

    public int getSeasons() {
        return mSeasons;
    }

    public void setSeasons(int mSeasons) {
        this.mSeasons = mSeasons;
    }

    public String getEpisodeTime() {
        return mEpisodeTime;
    }

    public void setEpisodeTime(String mEpisodeTime) {
        this.mEpisodeTime = mEpisodeTime;
    }

    public boolean isOngoing() {
        return mOngoing;
    }

    public void setOngoing(boolean mOngoing) {
        this.mOngoing = mOngoing;
    }

    public String getGenres() {
        return mGenres;
    }

    public void setGenres(String mGenres) {
        this.mGenres = mGenres;
    }

    public float getPopularity() {
        return mPopularity;
    }

    public void setPopularity(float mPopularity) {
        this.mPopularity = mPopularity;
    }

    public void setId(int id) {
        this.id = id;
    }
}