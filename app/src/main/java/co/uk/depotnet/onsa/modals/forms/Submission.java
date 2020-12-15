package co.uk.depotnet.onsa.modals.forms;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Submission implements Parcelable {
    public static final Creator<Submission> CREATOR = new Creator<Submission>() {
        @Override
        public Submission createFromParcel(Parcel in) {
            return new Submission(in);
        }

        @Override
        public Submission[] newArray(int size) {
            return new Submission[size];
        }
    };
    private long id;
    private int queued;
    private String jsonFile;
    private String title;
    private String jobID;
    private String date;
    private double latitude;
    private double longitude;

    protected Submission(Parcel in) {
        id = in.readLong();
        queued = in.readInt();
        jsonFile = in.readString();
        title = in.readString();
        jobID = in.readString();
        date = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public Submission(Cursor cursor) {
        if (cursor != null && !cursor.isBeforeFirst() && !cursor.isAfterLast()) {
            id = cursor.getInt(cursor.getColumnIndex(DBTable.id));
            jsonFile = cursor.getString(cursor.getColumnIndex(DBTable.jsonFile));
            title = cursor.getString(cursor.getColumnIndex(DBTable.title));
            jobID = cursor.getString(cursor.getColumnIndex(DBTable.jobID));
            queued = cursor.getInt(cursor.getColumnIndex(DBTable.queued));
            date = cursor.getString(cursor.getColumnIndex(DBTable.date));
            latitude = cursor.getDouble(cursor.getColumnIndex(DBTable.latitude));
            longitude = cursor.getDouble(cursor.getColumnIndex(DBTable.longitude));
        }
    }

    public Submission(String jsonFile, String title, String jobID) {

        this.jsonFile = jsonFile;
        this.title = title;
        this.jobID = jobID;
        this.queued = 0;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        date = sdf.format(new Date());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeInt(queued);
        parcel.writeString(jsonFile);
        parcel.writeString(title);
        parcel.writeString(jobID);
        parcel.writeString(date);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
    }

    public long getID() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getJsonFileName() {
        return jsonFile;
    }

    public void setJsonFile(String jsonFile) {
        this.jsonFile = jsonFile;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setQueued(int queued) {
        this.queued = queued;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getJobID() {
        return jobID;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public ContentValues toContentValues() {
        ContentValues out = new ContentValues();

        if (id > 0) {
            out.put(DBTable.id, id);
        }
        if (date != null) {
            out.put(DBTable.date, date);
        }
        out.put(DBTable.jsonFile, jsonFile);
        out.put(DBTable.title, title);
        out.put(DBTable.jobID, jobID);
        out.put(DBTable.queued, queued);
        out.put(DBTable.latitude, latitude);
        out.put(DBTable.longitude, longitude);

        return out;
    }

    public void setJobID(String jobID) {
        this.jobID = jobID;
    }


    public void setId(long id) {
        this.id = id;
    }

    public static class DBTable {
        public static final String NAME = "Submissions";
        public static final String id = "id";
        public static final String queued = "queued";
        public static final String jsonFile = "jsonFile";
        public static final String title = "title";
        public static final String jobID = "jobID";
        public static final String date = "date";
        public static final String latitude = "latitude";
        public static final String longitude = "longitude";
    }
}
