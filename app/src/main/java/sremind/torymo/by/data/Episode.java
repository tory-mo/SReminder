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
        onDelete = ForeignKey.CASCADE), indices = {@Index(value = {"ep_number"},
        unique = true), @Index(value = {"series"})})
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
    private String mNumber;

    @ColumnInfo(name = "seen")
    private boolean mSeen = false;

    public Episode(String mName, long mDate, String mSeries, String mNumber, boolean mSeen) {
        this.mName = mName;
        this.mDate = mDate;
        this.mSeries = mSeries;
        this.mNumber = mNumber;
        this.mSeen = mSeen;
    }

    @Ignore
    public Episode(String mName, long mDate, String mSeries, String mNumber) {
        this.mName = mName;
        this.mDate = mDate;
        this.mSeries = mSeries;
        this.mNumber = mNumber;
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

    public String getNumber() {
        return mNumber;
    }

    public void setNumber(String mNumber) {
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
}
