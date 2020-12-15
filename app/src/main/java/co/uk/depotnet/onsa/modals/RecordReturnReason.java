package co.uk.depotnet.onsa.modals;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import co.uk.depotnet.onsa.listeners.DropDownItem;

public class RecordReturnReason implements DropDownItem, Parcelable {

    private String onScreenText;
    private String text;
    private String value;
    private String jobId;


    protected RecordReturnReason(Parcel in) {
        onScreenText = in.readString();
        text = in.readString();
        value = in.readString();
        jobId = in.readString();
    }

    public RecordReturnReason(Cursor cursor) {
        onScreenText = cursor.getString(cursor.getColumnIndex(RecordReturnReason.DBTable.onScreenText));
        text = cursor.getString(cursor.getColumnIndex(RecordReturnReason.DBTable.text));
        value = cursor.getString(cursor.getColumnIndex(RecordReturnReason.DBTable.value));
        jobId = cursor.getString(cursor.getColumnIndex(RecordReturnReason.DBTable.jobId));
    }

    public static final Creator<RecordReturnReason> CREATOR = new Creator<RecordReturnReason>() {
        @Override
        public RecordReturnReason createFromParcel(Parcel in) {
            return new RecordReturnReason(in);
        }

        @Override
        public RecordReturnReason[] newArray(int size) {
            return new RecordReturnReason[size];
        }
    };

    @Override
    public String getDisplayItem() {
        return value+": "+onScreenText;
    }

    @Override
    public String getUploadValue() {
        return value;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(onScreenText);
        dest.writeString(text);
        dest.writeString(value);
    }

    public static class DBTable {
        public static final String NAME = "RecordReturnReason";
        public static final String onScreenText = "onScreenText";
        public static final String text = "text";
        public static final String value = "value";
        public static final String jobId = "jobId";
    }

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(RecordReturnReason.DBTable.onScreenText, this.onScreenText);
        cv.put(RecordReturnReason.DBTable.text, this.text);
        cv.put(RecordReturnReason.DBTable.value, this.value);
        cv.put(RecordReturnReason.DBTable.jobId, this.jobId);
        return cv;
    }

    public String getOnScreenText() {
        return onScreenText;
    }

    public void setOnScreenText(String onScreenText) {
        this.onScreenText = onScreenText;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
}
