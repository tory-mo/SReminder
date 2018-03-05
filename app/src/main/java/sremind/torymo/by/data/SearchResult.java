package sremind.torymo.by.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "search_result",
        indices = {@Index(value = {"mdbid"}, unique = true),
                @Index(value = {"imdbid"}, unique = true)})
public class SearchResult {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String mName;

    @ColumnInfo(name = "original_name")
    private String mOriginalName;

    @ColumnInfo(name = "mdbid")
    private String mMdbId;

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
    private long mSeasons = 0;

    @ColumnInfo(name = "episode_time")
    private String mEpisodeTime;

    @ColumnInfo(name = "ongoing")
    private boolean mOngoing;

    @ColumnInfo(name = "genres")
    private String mGenres;

    @ColumnInfo(name = "popularity")
    private float mPopularity = 0;

    @ColumnInfo(name = "status")
    private String mStatus;

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String mStatus) {
        this.mStatus = mStatus;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getMdbId() {
        return mMdbId;
    }

    public void setMdbId(String mSRId) {
        this.mMdbId = mSRId;
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

    public long getSeasons() {
        return mSeasons;
    }

    public void setSeasons(long mSeasons) {
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

    public String getOriginalName() {
        return mOriginalName;
    }

    public void setOriginalName(String mOriginalName) {
        this.mOriginalName = mOriginalName;
    }
}
