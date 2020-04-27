package co.uk.depotnet.onsa.modals;//TODO: package name

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

public class Document implements Parcelable {
    public static final Creator<Document> CREATOR = new Creator<Document>() {
        @Override
        public Document createFromParcel(Parcel in) {
            return new Document(in);
        }

        @Override
        public Document[] newArray(int size) {
            return new Document[size];
        }
    };

    private String jobId;
    private String dateTime;
    private String jobDocumentId;
    private String type;
    private String documentName;

    protected Document(Parcel in) {
        jobId = in.readString();
        dateTime = in.readString();
        jobDocumentId = in.readString();
        type = in.readString();
        documentName = in.readString();
    }

    public Document(Cursor cursor) {
        jobId = cursor.getString(cursor.getColumnIndex(DBTable.jobId));
        dateTime = cursor.getString(cursor.getColumnIndex(DBTable.dateTime));
        jobDocumentId = cursor.getString(cursor.getColumnIndex(DBTable.jobDocumentId));
        type = cursor.getString(cursor.getColumnIndex(DBTable.type));
        documentName = cursor.getString(cursor.getColumnIndex(DBTable.documentName));
    }

    public void setdateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getdateTime() {
        return this.dateTime;
    }

    public void setjobDocumentId(String id) {
        this.jobDocumentId = id;
    }

    public String getjobDocumentId() {
        return this.jobDocumentId;
    }

    public void settype(String type) {
        this.type = type;
    }

    public String gettype() {
        return this.type;
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

    public String getDocumentName() {
        return documentName;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(jobId);
        parcel.writeString(dateTime);
        parcel.writeString(jobDocumentId);
        parcel.writeString(type);
        parcel.writeString(documentName);
    }

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(DBTable.jobId, this.jobId);
        cv.put(DBTable.dateTime, this.dateTime);
        cv.put(DBTable.jobDocumentId, this.jobDocumentId);
        cv.put(DBTable.type, this.type);
        cv.put(DBTable.documentName, this.documentName);
        return cv;
    }

    public static class DBTable {
        public static final String NAME = "JobPack";
        public static final String jobId = "jobId";
        public static final String dateTime = "dateTime";
        public static final String jobDocumentId = "jobDocumentId";
        public static final String type = "type";
        public static final String documentName = "documentName";
    }
}