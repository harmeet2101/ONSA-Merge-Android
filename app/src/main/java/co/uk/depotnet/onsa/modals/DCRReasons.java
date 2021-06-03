package co.uk.depotnet.onsa.modals;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import co.uk.depotnet.onsa.listeners.DropDownItem;

public class DCRReasons implements Parcelable , DropDownItem {

    private String jobId;
    private String value;
    private String text;
    private String onScreenText;
    private String reasonId;

    public DCRReasons(Cursor c) {
        jobId = c.getString(c.getColumnIndex(DBTable.jobId));
        value = c.getString(c.getColumnIndex(DBTable.value));
        text = c.getString(c.getColumnIndex(DBTable.text));
        onScreenText = c.getString(c.getColumnIndex(DBTable.onScreenText));
        reasonId = c.getString(c.getColumnIndex(DBTable.reasonId));
    }

    protected DCRReasons(Parcel in) {
        jobId = in.readString();
        value = in.readString();
        text = in.readString();
        onScreenText = in.readString();
        reasonId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(jobId);
        dest.writeString(value);
        dest.writeString(text);
        dest.writeString(onScreenText);
        dest.writeString(reasonId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DCRReasons> CREATOR = new Creator<DCRReasons>() {
        @Override
        public DCRReasons createFromParcel(Parcel in) {
            return new DCRReasons(in);
        }

        @Override
        public DCRReasons[] newArray(int size) {
            return new DCRReasons[size];
        }
    };

    @Override
    public String getDisplayItem() {
        return value+" : "+onScreenText;
    }

    @Override
    public String getUploadValue() {
        return reasonId;
    }

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(DBTable.jobId , jobId);
        cv.put(DBTable.value , value);
        cv.put(DBTable.text , text);
        cv.put(DBTable.onScreenText , onScreenText);
        cv.put(DBTable.reasonId , reasonId);

        return cv;
    }

    public static class DBTable{
        public static final String NAME = "DCRReasons";
        public static final String jobId = "jobId";
        public static final String value = "value";
        public static final String text = "text";
        public static final String onScreenText = "onScreenText";
        public static final String reasonId = "reasonId";
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getReasonId() {
        return reasonId;
    }

    public void setReasonId(String reasonId) {
        this.reasonId = reasonId;
    }
}
