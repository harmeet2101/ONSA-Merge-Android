package co.uk.depotnet.onsa.modals;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

public class A75Groups implements Parcelable {

    private String jobId;
    private String dpNo;
    private String poleInfo;

    public A75Groups(){

    }

    public A75Groups(Cursor cursor) {
        this.jobId = cursor.getString(cursor.getColumnIndex(DBTable.jobId));
        this.dpNo = cursor.getString(cursor.getColumnIndex(DBTable.dpNo));
        this.poleInfo = cursor.getString(cursor.getColumnIndex(DBTable.poleInfo));
    }


    protected A75Groups(Parcel in) {
        jobId = in.readString();
        dpNo = in.readString();
        poleInfo = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(jobId);
        dest.writeString(dpNo);
        dest.writeString(poleInfo);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<A75Groups> CREATOR = new Creator<A75Groups>() {
        @Override
        public A75Groups createFromParcel(Parcel in) {
            return new A75Groups(in);
        }

        @Override
        public A75Groups[] newArray(int size) {
            return new A75Groups[size];
        }
    };

    public String getDpNo() {
        return dpNo;
    }

    public void setDpNo(String dpNo) {
        this.dpNo = dpNo;
    }

    public String getPoleInfo() {
        return poleInfo;
    }

    public void setPoleInfo(String poleInfo) {
        this.poleInfo = poleInfo;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public static class DBTable {
        public static final String NAME = "A75Groups";
        public static final String jobId = "jobId";
        public static final String poleInfo = "poleInfo";
        public static final String dpNo = "dpNo";
    }

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(DBTable.jobId , this.jobId);
        cv.put(DBTable.dpNo , this.dpNo);
        cv.put(DBTable.poleInfo , this.poleInfo);
        return cv;
    }


}
