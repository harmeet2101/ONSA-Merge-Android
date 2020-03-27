package co.uk.depotnet.onsa.modals;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import co.uk.depotnet.onsa.listeners.DropDownItem;

public class Notice implements Parcelable , DropDownItem {
    public static final Creator<Notice> CREATOR = new Creator<Notice>() {
        @Override
        public Notice createFromParcel(Parcel in) {
            return new Notice(in);
        }

        @Override
        public Notice[] newArray(int size) {
            return new Notice[size];

        }

    };
    private String noticeId;
    private String jobId;
    private String permitConditions;
    private String worksAddress;
    private String endDate;
    private String noticeType;
    private String comment;
    private String startDate;
    private String worksReference;

    protected Notice(Parcel in) {
        noticeId = in.readString();
        jobId = in.readString();
        permitConditions = in.readString();
        worksAddress = in.readString();
        endDate = in.readString();
        noticeType = in.readString();
        comment = in.readString();
        startDate = in.readString();
        worksReference = in.readString();

    }

    public Notice(Cursor cursor) {
        noticeId = cursor.getString(cursor.getColumnIndex(DBTable.noticeId));
        jobId = cursor.getString(cursor.getColumnIndex(DBTable.jobId));
        permitConditions = cursor.getString(cursor.getColumnIndex(DBTable.permitConditions));
        worksAddress = cursor.getString(cursor.getColumnIndex(DBTable.worksAddress));
        endDate = cursor.getString(cursor.getColumnIndex(DBTable.endDate));
        noticeType = cursor.getString(cursor.getColumnIndex(DBTable.noticeType));
        comment = cursor.getString(cursor.getColumnIndex(DBTable.comment));
        startDate = cursor.getString(cursor.getColumnIndex(DBTable.startDate));
        worksReference = cursor.getString(cursor.getColumnIndex(DBTable.worksReference));

    }

    public String getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(String noticeId) {
        this.noticeId = noticeId;
    }



    public void setpermitConditions(String permitConditions) {
        this.permitConditions = permitConditions;
    }

    public String getpermitConditions() {
        return this.permitConditions;
    }

    public void setworksAddress(String worksAddress) {
        this.worksAddress = worksAddress;
    }

    public String getworksAddress() {
        return this.worksAddress;
    }

    public void setendDate(String endDate) {
        this.endDate = endDate;
    }

    public String getendDate() {
        return this.endDate;
    }

    public void setnoticeType(String noticeType) {
        this.noticeType = noticeType;
    }

    public String getnoticeType() {
        return this.noticeType;
    }

    public void setcomment(String comment) {
        this.comment = comment;
    }

    public String getcomment() {
        return this.comment;
    }


    public void setstartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getstartDate() {
        return this.startDate;
    }

    public void setworksReference(String worksReference) {
        this.worksReference = worksReference;
    }

    public String getworksReference() {
        return this.worksReference;
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
        parcel.writeString(noticeId);
        parcel.writeString(jobId);
        parcel.writeString(permitConditions);
        parcel.writeString(worksAddress);
        parcel.writeString(endDate);
        parcel.writeString(noticeType);
        parcel.writeString(comment);
        parcel.writeString(startDate);
        parcel.writeString(worksReference);

    }

    public ContentValues toContentValues() {

        ContentValues cv = new ContentValues();

        cv.put(DBTable.noticeId, this.noticeId);
        cv.put(DBTable.jobId, this.jobId);
        cv.put(DBTable.permitConditions, this.permitConditions);

        cv.put(DBTable.worksAddress, this.worksAddress);

        cv.put(DBTable.endDate, this.endDate);

        cv.put(DBTable.noticeType, this.noticeType);

        cv.put(DBTable.comment, this.comment);

        cv.put(DBTable.startDate, this.startDate);

        cv.put(DBTable.worksReference, this.worksReference);

        return cv;
    }

    public static class DBTable {
        public static final String NAME = "Notice";
        public static final String noticeId = "noticeId";
        public static final String jobId = "jobId";
        public static final String permitConditions = "permitConditions";
        public static final String worksAddress = "worksAddress";
        public static final String endDate = "endDate";
        public static final String noticeType = "noticeType";
        public static final String comment = "comment";
        public static final String id = "id";
        public static final String startDate = "startDate";
        public static final String worksReference = "worksReference";

    }

    @Override
    public String getDisplayItem() {
        StringBuilder builder = new StringBuilder();
        if(!TextUtils.isEmpty(worksReference)) {
            builder.append("Work Reference : ");
            builder.append(worksReference+"\n");
        }

        if(!TextUtils.isEmpty(worksAddress)) {
            builder.append("Work Address : ");
            builder.append(worksAddress+"\n");
        }

        if(!TextUtils.isEmpty(noticeType)) {
            builder.append("Notice Type : ");
            builder.append(noticeType+"\n");
        }

        if(!TextUtils.isEmpty(comment)) {
            builder.append("Comment : ");
            builder.append(comment+"\n");
        }
        if(!TextUtils.isEmpty(startDate)) {
            builder.append("Start Date : ");
            builder.append(startDate+"\n");
        }
        if(!TextUtils.isEmpty(endDate)) {
            builder.append("End Date : ");
            builder.append(endDate+"\n");
        }
        return builder.toString();
    }

    @Override
    public String getUploadValue() {
        return noticeId;
    }
}