package co.uk.depotnet.onsa.modals;//TODO: package name

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

public class Note implements Parcelable {
    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];

        }

    };
    private String jobId;
    private String dateTime;
    private String userFullName;
    private String text;

    protected Note(Parcel in) {
        jobId = in.readString();
        dateTime = in.readString();
        userFullName = in.readString();
        text = in.readString();

    }

    public Note(Cursor cursor) {
        jobId = cursor.getString(cursor.getColumnIndex(DBTable.jobId));
        dateTime = cursor.getString(cursor.getColumnIndex(DBTable.dateTime));
        userFullName = cursor.getString(cursor.getColumnIndex(DBTable.userFullName));
        text = cursor.getString(cursor.getColumnIndex(DBTable.text));

    }

    public void setdateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getdateTime() {
        return this.dateTime;
    }

    public void setuserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getuserFullName() {
        return this.userFullName;
    }

    public void settext(String text) {
        this.text = text;
    }

    public String gettext() {
        return this.text;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(jobId);
        parcel.writeString(dateTime);
        parcel.writeString(userFullName);
        parcel.writeString(text);

    }

    public ContentValues toContentValues() {

        ContentValues cv = new ContentValues();

        cv.put(DBTable.jobId, this.jobId);
        cv.put(DBTable.dateTime, this.dateTime);

        cv.put(DBTable.userFullName, this.userFullName);

        cv.put(DBTable.text, this.text);

        return cv;
    }

    public static class DBTable {
        public static final String NAME = "Note";
        public static final String jobId = "jobId";
        public static final String dateTime = "dateTime";
        public static final String userFullName = "userFullName";
        public static final String text = "text";

    }
}