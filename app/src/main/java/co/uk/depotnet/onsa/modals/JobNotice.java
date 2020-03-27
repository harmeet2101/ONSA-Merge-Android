package co.uk.depotnet.onsa.modals;

//TODO: package name

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import co.uk.depotnet.onsa.listeners.DropDownItem;

public class JobNotice implements Parcelable , DropDownItem {
    public static final Creator<JobNotice> CREATOR = new Creator<JobNotice>() {
        @Override
        public JobNotice createFromParcel(Parcel in) {
            return new JobNotice(in);
        }

        @Override
        public JobNotice[] newArray(int size) {
            return new JobNotice[size];

        }

    };
    private int jobId;
    private String StreetName;
    private String NoticeStartDate;
    private int NoticeID;
    private String NoticeEndDate;
    private String NoticeType;
    private String NoticeReference;
    private String NoticeSubType;
    private String Postcode;

    protected JobNotice(Parcel in) {
        jobId = in.readInt();
        StreetName = in.readString();
        NoticeStartDate = in.readString();
        NoticeID = in.readInt();
        NoticeEndDate = in.readString();
        NoticeType = in.readString();
        NoticeReference = in.readString();
        NoticeSubType = in.readString();
        Postcode = in.readString();

    }

    public JobNotice(Cursor cursor) {
        jobId = cursor.getInt(cursor.getColumnIndex(DBTable.jobId));
        StreetName = cursor.getString(cursor.getColumnIndex(DBTable.StreetName));
        NoticeStartDate = cursor.getString(cursor.getColumnIndex(DBTable.NoticeStartDate));
        NoticeID = cursor.getInt(cursor.getColumnIndex(DBTable.NoticeID));
        NoticeEndDate = cursor.getString(cursor.getColumnIndex(DBTable.NoticeEndDate));
        NoticeType = cursor.getString(cursor.getColumnIndex(DBTable.NoticeType));
        NoticeReference = cursor.getString(cursor.getColumnIndex(DBTable.NoticeReference));
        NoticeSubType = cursor.getString(cursor.getColumnIndex(DBTable.NoticeSubType));
        Postcode = cursor.getString(cursor.getColumnIndex(DBTable.Postcode));

    }

    public String getStreetName() {
        return this.StreetName;
    }

    public void setStreetName(String StreetName) {
        this.StreetName = StreetName;
    }

    public String getNoticeStartDate() {
        return this.NoticeStartDate;
    }

    public void setNoticeStartDate(String NoticeStartDate) {
        this.NoticeStartDate = NoticeStartDate;
    }

    public int getNoticeID() {
        return this.NoticeID;
    }

    public void setNoticeID(int NoticeID) {
        this.NoticeID = NoticeID;
    }

    public String getNoticeEndDate() {
        return this.NoticeEndDate;
    }

    public void setNoticeEndDate(String NoticeEndDate) {
        this.NoticeEndDate = NoticeEndDate;
    }

    public String getNoticeType() {
        return this.NoticeType;
    }

    public void setNoticeType(String NoticeType) {
        this.NoticeType = NoticeType;
    }

    public String getNoticeReference() {
        return this.NoticeReference;
    }

    public void setNoticeReference(String NoticeReference) {
        this.NoticeReference = NoticeReference;
    }

    public String getNoticeSubType() {
        return this.NoticeSubType;
    }

    public void setNoticeSubType(String NoticeSubType) {
        this.NoticeSubType = NoticeSubType;
    }

    public String getPostcode() {
        return this.Postcode;
    }

    public void setPostcode(String Postcode) {
        this.Postcode = Postcode;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(jobId);
        parcel.writeString(StreetName);
        parcel.writeString(NoticeStartDate);
        parcel.writeInt(NoticeID);
        parcel.writeString(NoticeEndDate);
        parcel.writeString(NoticeType);
        parcel.writeString(NoticeReference);
        parcel.writeString(NoticeSubType);
        parcel.writeString(Postcode);

    }

    public ContentValues toContentValues() {

        ContentValues cv = new ContentValues();

        cv.put(DBTable.jobId, this.jobId);
        cv.put(DBTable.StreetName, this.StreetName);

        cv.put(DBTable.NoticeStartDate, this.NoticeStartDate);

        cv.put(DBTable.NoticeID, this.NoticeID);

        cv.put(DBTable.NoticeEndDate, this.NoticeEndDate);

        cv.put(DBTable.NoticeType, this.NoticeType);

        cv.put(DBTable.NoticeReference, this.NoticeReference);

        cv.put(DBTable.NoticeSubType, this.NoticeSubType);

        cv.put(DBTable.Postcode, this.Postcode);

        return cv;
    }

    @Override
    public String getDisplayItem() {
        return this.NoticeReference;
    }

    @Override
    public String getUploadValue() {
        return String.valueOf(this.getNoticeID());
    }

    public static class DBTable {
        public static final String NAME = "JobNotices";
        public static final String jobId = "jobId";
        public static final String StreetName = "StreetName";
        public static final String NoticeStartDate = "NoticeStartDate";
        public static final String NoticeID = "NoticeID";
        public static final String NoticeEndDate = "NoticeEndDate";
        public static final String NoticeType = "NoticeType";
        public static final String NoticeReference = "NoticeReference";
        public static final String NoticeSubType = "NoticeSubType";
        public static final String Postcode = "Postcode";

    }
}