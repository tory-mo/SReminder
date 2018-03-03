package sremind.torymo.by.response;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

import sremind.torymo.by.Utility;
import sremind.torymo.by.data.SearchResult;

public class SeriesResponseResult {

    @SerializedName("name")
    private String mName;

    @SerializedName("original_name")
    private String mOriginalName;

    @SerializedName("id")
    private String mMdbId;

    @SerializedName("poster_path")
    private String mPoster;

    private List<Genres> genres;

    @SerializedName("external_ids")
    private ExternalIds externalIds;

    @SerializedName("in_production")
    private boolean mOngoing;

    @SerializedName("number_of_seasons")
    private int mSeasons;

    @SerializedName("overview")
    private String mOverview;

    @SerializedName("status")
    private String mStatus;

    public float getPopularity() {
        return mPopularity;
    }

    public void setPopularity(float mPopularity) {
        this.mPopularity = mPopularity;
    }

    @SerializedName("popularity")
    private float mPopularity;

    public String getHomepage() {
        return mHomepage;
    }


    public void setHomepage(String mHomepage) {
        this.mHomepage = mHomepage;
    }

    @SerializedName("homepage")
    private String mHomepage;

    public Date getFirstEpisodeDate() {
        return firstEpisodeDate;
    }

    public void setFirstEpisodeDate(Date firstEpisodeDate) {
        this.firstEpisodeDate = firstEpisodeDate;
    }

    @SerializedName("first_air_date")
    private Date firstEpisodeDate;

    public int[] getEpisodeTime() {
        return episodeTime;
    }

    public void setEpisodeTime(int[] episodeTime) {
        this.episodeTime = episodeTime;
    }

    @SerializedName("episode_run_time")
    private int[] episodeTime;

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getOriginalName() {
        return mOriginalName;
    }

    public void setOriginalName(String mOriginalName) {
        this.mOriginalName = mOriginalName;
    }

    public String getMdbId() {
        return mMdbId;
    }

    public void setMdbId(String mMdbId) {
        this.mMdbId = mMdbId;
    }

    public String getPoster() {
        return Utility.POSTER_PATH + mPoster;
    }

    public void setPoster(String mPoster) {
        this.mPoster = mPoster;
    }

    public List<Genres> getGenres() {
        return genres;
    }

    public void setGenres(List<Genres> genres) {
        this.genres = genres;
    }

    public ExternalIds getExternalIds() {
        return externalIds;
    }

    public void setExternalIds(ExternalIds externalIds) {
        this.externalIds = externalIds;
    }

    public boolean isOngoing() {
        return mOngoing;
    }

    public void setOngoing(boolean mOngoing) {
        this.mOngoing = mOngoing;
    }

    public int getSeasons() {
        return mSeasons;
    }

    public void setSeasons(int mSeasons) {
        this.mSeasons = mSeasons;
    }

    public String getOverview() {
        if(mOverview == null) return "";
        return mOverview;
    }

    public void setOverview(String mOverview) {
            this.mOverview = mOverview;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String mStatus) {
        this.mStatus = mStatus;
    }

    public class Genres{
        public int id;
        public String name;
    }

    public class ExternalIds{
        public String imdb_id;
    }

    public static SearchResult seriesToSearchResult(SeriesResponseResult series){
        if(series == null) return null;

        String genresStr = "";
        if(series.getGenres() != null) {
            genresStr = series.getGenres().get(0).name;
            for (int i = 1; i < series.getGenres().size(); i++) {
                genresStr = genresStr.concat(", " + series.getGenres().get(i).name);
            }
        }

        String episodeTime = "";
        if(series.getEpisodeTime() != null && series.getEpisodeTime().length > 0) {
            episodeTime = String.valueOf(series.getEpisodeTime()[0]);
            for (int i = 1; i < series.getEpisodeTime().length; i++) {
                episodeTime = episodeTime.concat("," + series.getEpisodeTime()[i]);
            }
        }

        SearchResult sr = new SearchResult();
        sr.setImdbId(series.getExternalIds().imdb_id);
        sr.setHomepage(series.getHomepage());
        sr.setOngoing(series.isOngoing());
        sr.setSeasons(series.getSeasons());
        sr.setMdbId(series.getMdbId());
        sr.setPoster(series.getPoster());
        sr.setName(series.getName());
        sr.setOriginalName(series.getOriginalName());
        sr.setPopularity(series.getPopularity());
        sr.setGenres(genresStr);
        sr.setOverview(series.getOverview());
        sr.setEpisodeTime(episodeTime);
        sr.setFirstDate(series.getFirstEpisodeDate().getTime());

        return sr;
    }
}
