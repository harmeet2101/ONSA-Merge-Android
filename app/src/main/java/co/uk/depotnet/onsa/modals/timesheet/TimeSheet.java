package co.uk.depotnet.onsa.modals.timesheet;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class TimeSheet implements Parcelable {
    private String timesheetId;
    private String weekCommencing;
    private String timesheetStatus;
    private String rejectionReason;

    protected TimeSheet(Parcel in) {
        timesheetId = in.readString();
        weekCommencing = in.readString();
        timesheetStatus = in.readString();
        rejectionReason = in.readString();
    }

    public TimeSheet(Cursor c) {
        this.timesheetId = c.getString(c.getColumnIndex(DBTable.timesheetId));
        this.weekCommencing = c.getString(c.getColumnIndex(DBTable.weekCommencing));
        this.timesheetStatus = c.getString(c.getColumnIndex(DBTable.timesheetStatus));
        this.rejectionReason = c.getString(c.getColumnIndex(DBTable.rejectionReason));
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(timesheetId);
        dest.writeString(weekCommencing);
        dest.writeString(timesheetStatus);
        dest.writeString(rejectionReason);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TimeSheet> CREATOR = new Creator<TimeSheet>() {
        @Override
        public TimeSheet createFromParcel(Parcel in) {
            return new TimeSheet(in);
        }

        @Override
        public TimeSheet[] newArray(int size) {
            return new TimeSheet[size];
        }
    };

    public String getTimesheetId() {
        return timesheetId;
    }

    public void setTimesheetId(String timesheetId) {
        this.timesheetId = timesheetId;
    }

    public String getWeekCommencing() {
        return weekCommencing;
    }

    public void setWeekCommencing(String weekCommencing) {
        this.weekCommencing = weekCommencing;
    }

    public String getTimesheetStatus() {
        return timesheetStatus;
    }

    public void setTimesheetStatus(String timesheetStatus) {
        this.timesheetStatus = timesheetStatus;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public static class DBTable{
        public static final String NAME = "TimeSheet";
        public static final String timesheetId = "timesheetId";
        public static final String weekCommencing = "weekCommencing";
        public static final String timesheetStatus = "timesheetStatus";
        public static final String rejectionReason = "rejectionReason";
    }

    public ContentValues toContentValues(){
        ContentValues cv = new ContentValues();
        cv.put(DBTable.timesheetId , timesheetId);
        cv.put(DBTable.weekCommencing , weekCommencing);
        cv.put(DBTable.timesheetStatus , timesheetStatus);
        cv.put(DBTable.rejectionReason , rejectionReason);
        return cv;
    }

    public HashMap<String , String> getAllWeekDates(){
        HashMap<String , String> dates = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(weekCommencing);
            dates.put(sdf.format(date) , timesheetStatus);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            for(int i = 1 ; i < 7 ; i++){
                c.add(Calendar.DATE , 1);
                dates.put(sdf.format(c.getTime()) , timesheetStatus);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dates;

    }
}
