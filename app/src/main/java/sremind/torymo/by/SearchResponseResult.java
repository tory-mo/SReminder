package sremind.torymo.by;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class SearchResponseResult {
    @SerializedName("name")
    private String mName;

    @SerializedName("original_name")
    private String mOriginalName;

    @SerializedName("id")
    private String mMdbId;

    @SerializedName("poster_path")
    private String mPoster;

    @SerializedName("overview")
    private String mOverview;

    @SerializedName("first_air_date")
    private Date mFirstDate;

    @SerializedName("popularity")
    private float mPopularity = 0;

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

    public Date getFirstDate() {
        return mFirstDate;
    }

    public void setFirstDate(Date mFirstDate) {
        this.mFirstDate = mFirstDate;
    }

    public float getPopularity() {
        return mPopularity;
    }

    public void setPopularity(float mPopularity) {
        this.mPopularity = mPopularity;
    }

    public String getOriginalName() {
        return mOriginalName;
    }

    public void setOriginalName(String mOriginalName) {
        this.mOriginalName = mOriginalName;
    }
}
