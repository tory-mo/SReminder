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

    public Episode(String mName, long mDate, String mSeries, long mNumber, long mSeasonNumber, boolean mSeen) {
        this.mName = mName;
        this.mDate = mDate;
        this.mSeries = mSeries;
        this.mNumber = mNumber;
        this.mSeasonNumber = mSeasonNumber;
        this.mSeen = mSeen;
    }

    @Ignore
    public Episode(String mName, long mDate, String mSeries, long mNumber, long mSeasonNumber) {
        this.mName = mName;
        this.mDate = mDate;
        this.mSeries = mSeries;
        this.mNumber = mNumber;
        this.mSeasonNumber = mSeasonNumber;
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
