package sremind.torymo.by.response;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import sremind.torymo.by.Utility;

public class EpisodesResponseResult {

    @SerializedName("name")
    private String mName;

    @SerializedName("air_date")
    private Date mDate;

    @SerializedName("episode_number")
    private long mNumber;

    @SerializedName("overview")
    private String mOverview;

    @SerializedName("season_number")
    private long mSeasonNumber;

    @SerializedName("still_path")
    private String mPoster;

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date mDate) {
        this.mDate = mDate;
    }

    public long getNumber() {
        return mNumber;
    }

    public void setNumber(long mNumber) {
        this.mNumber = mNumber;
    }

    public String getOverview() {
        return mOverview;
    }

    public void setOverview(String mOverview) {
        this.mOverview = mOverview;
    }

    public long getSeasonNumber() {
        return mSeasonNumber;
    }

    public void setSeasonNumber(long mSeasonNumber) {
        this.mSeasonNumber = mSeasonNumber;
    }

    public String getPoster() {
        return Utility.POSTER_PATH + mPoster;
    }

    public void setPoster(String mPoster) {
        this.mPoster = mPoster;
    }
}
