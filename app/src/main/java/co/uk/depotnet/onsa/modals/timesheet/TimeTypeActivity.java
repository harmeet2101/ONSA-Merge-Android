package co.uk.depotnet.onsa.modals.timesheet;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import co.uk.depotnet.onsa.listeners.DropDownItem;

public class TimeTypeActivity implements Parcelable , DropDownItem {

    private String timeTypeActivityId;
    private String timeTypeActivityName;

    protected TimeTypeActivity(Parcel in) {
        timeTypeActivityId = in.readString();
        timeTypeActivityName = in.readString();
    }

    public TimeTypeActivity(Cursor c) {
        timeTypeActivityId = c.getString(c.getColumnIndex(DBTable.timeTypeActivityId));
        timeTypeActivityName = c.getString(c.getColumnIndex(DBTable.timeTypeActivityName));
    }



    public static final Creator<TimeTypeActivity> CREATOR = new Creator<TimeTypeActivity>() {
        @Override
        public TimeTypeActivity createFromParcel(Parcel in) {
            return new TimeTypeActivity(in);
        }

        @Override
        public TimeTypeActivity[] newArray(int size) {
            return new TimeTypeActivity[size];
        }
    };

    public String getTimeTypeActivityId() {
        return timeTypeActivityId;
    }

    public void setTimeTypeActivityId(String timeTypeActivityId) {
        this.timeTypeActivityId = timeTypeActivityId;
    }

    public String getTimeTypeActivityName() {
        return timeTypeActivityName;
    }

    public void setTimeTypeActivityName(String timeTypeActivityName) {
        this.timeTypeActivityName = timeTypeActivityName;
    }

    @Override
    public String getDisplayItem() {
        return timeTypeActivityName;
    }

    @Override
    public String getUploadValue() {
        return timeTypeActivityId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(timeTypeActivityId);
        dest.writeString(timeTypeActivityName);
    }

    public ContentValues toContentValues(){
        ContentValues cv = new ContentValues();

        cv.put(DBTable.timeTypeActivityId , timeTypeActivityId);
        cv.put(DBTable.timeTypeActivityName , timeTypeActivityName);

        return cv;
    }


    public static class DBTable{
        public static final String NAME = "TimeTypeActivity";
        public static final String timeTypeActivityId = "timeTypeActivityId";
        public static final String timeTypeActivityName = "timeTypeActivityName";
    }
}
