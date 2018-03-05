package sremind.torymo.by.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "Episodes", foreignKeys = @ForeignKey(entity = Series.class,
        parentColumns = "imdbid",
        childColumns = "series",
        onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = {"ep_number"}),
                @Index(value = {"series"})})
public class Episode {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String mName;

    @ColumnInfo(name = "date")
    private long mDate;

    @ColumnInfo(name = "series")
    private String mSeries;

    @ColumnInfo(name = "ep_number")
    private long mNumber;

    @ColumnInfo(name = "s_number")
    private long mSeasonNumber;

    @ColumnInfo(name = "seen")
    private boolean mSeen = false;

    @ColumnInfo(name = "overview")
    private String mOverview;

    @ColumnInfo(name = "poster")
    private String mPoster;

    public String getOverview() {
        return mOverview;
    }

    public void setOverview(String mOverview) {
        this.mOverview = mOverview;
    }

    public String getPoster() {
        return mPoster;
    }

    public void setPoster(String mPoster) {
        this.mPoster = mPoster;
    }


    public Episode(String mName, long mDate, String mSeries, long mNumber, long mSeasonNumber, String mPoster, String mOverview, boolean mSeen) {
        this.mName = mName;
        this.mDate = mDate;
        this.mSeries = mSeries;
        this.mNumber = mNumber;
        this.mSeasonNumber = mSeasonNumber;
        this.mSeen = mSeen;
        this.mPoster = mPoster;
        this.mOverview = mOverview;
    }

    @Ignore
    public Episode(String mName, long mDate, String mSeries, long mNumber, long mSeasonNumber, String mPoster, String mOverview) {
        this.mName = mName;
        this.mDate = mDate;
        this.mSeries = mSeries;
        this.mNumber = mNumber;
        this.mSeasonNumber = mSeasonNumber;
        this.mPoster = mPoster;
        this.mOverview = mOverview;
    }

    public int getId(){return id;};

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public long getDate() {
        return mDate;
    }

    public void setDate(long mDate) {
        this.mDate = mDate;
    }

    public String getSeries() {
        return mSeries;
    }

    public void setSeries(String mSeries) {
        this.mSeries = mSeries;
    }

    public long getNumber() {
        return mNumber;
    }

    public void setNumber(int mNumber) {
        this.mNumber = mNumber;
    }

    public boolean isSeen() {
        return mSeen;
    }

    public void setSeen(boolean mSeen) {
        this.mSeen = mSeen;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getSeasonNumber() {
        return mSeasonNumber;
    }

    public void setSeasonNumber(int mSeasonNumber) {
        this.mSeasonNumber = mSeasonNumber;
    }
}
