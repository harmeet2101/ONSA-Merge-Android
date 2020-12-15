package co.uk.depotnet.onsa.modals.briefings;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IssuedDocModal implements Parcelable {
    @SerializedName("briefingId")
    @Expose
    private String briefingId;
    @SerializedName("briefingName")
    @Expose
    private String briefingName;

    public IssuedDocModal() {
        this.briefingId = "0";
        this.briefingName = "Document";
    }

    protected IssuedDocModal(Parcel in) {
        briefingId = in.readString();
        briefingName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(briefingId);
        dest.writeString(briefingName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<IssuedDocModal> CREATOR = new Creator<IssuedDocModal>() {
        @Override
        public IssuedDocModal createFromParcel(Parcel in) {
            return new IssuedDocModal(in);
        }

        @Override
        public IssuedDocModal[] newArray(int size) {
            return new IssuedDocModal[size];
        }
    };

    public String getBriefingId() {
        return briefingId;
    }

    public void setBriefingId(String briefingId) {
        this.briefingId = briefingId;
    }

    public String getBriefingName() {
        return briefingName;
    }

    public void setBriefingName(String briefingName) {
        this.briefingName = briefingName;
    }

    public IssuedDocModal(Cursor cursor) {
        if (cursor != null && !cursor.isBeforeFirst() && !cursor.isAfterLast()) {
            briefingId = cursor.getString(cursor.getColumnIndex(DBTable.briefingId));
            briefingName = cursor.getString(cursor.getColumnIndex(DBTable.briefingName));
        }
    }
    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        if (briefingId!=null){
            cv.put(IssuedDocModal.DBTable.briefingId, briefingId);
        }
        cv.put(DBTable.briefingName, briefingName);
        return cv;
    }
    public static class DBTable {
        public static final String briefingId = "briefingId";
        public static final String briefingName = "briefingName";
    }
}
