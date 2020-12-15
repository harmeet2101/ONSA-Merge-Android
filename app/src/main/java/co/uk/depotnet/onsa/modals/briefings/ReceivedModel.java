package co.uk.depotnet.onsa.modals.briefings;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ReceivedModel implements Parcelable {
    @SerializedName("dateRead")
    @Expose
    private String dateRead;
    @SerializedName("briefedByUserFullName")
    @Expose
    private String briefedByUserFullName;
    @SerializedName("briefings")
    @Expose
    private List<IssuedDocModal> briefings = null;

    public ReceivedModel() {
    }

    protected ReceivedModel(Parcel in) {
        dateRead = in.readString();
        briefedByUserFullName = in.readString();
        briefings = in.createTypedArrayList(IssuedDocModal.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dateRead);
        dest.writeString(briefedByUserFullName);
        dest.writeTypedList(briefings);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ReceivedModel> CREATOR = new Creator<ReceivedModel>() {
        @Override
        public ReceivedModel createFromParcel(Parcel in) {
            return new ReceivedModel(in);
        }

        @Override
        public ReceivedModel[] newArray(int size) {
            return new ReceivedModel[size];
        }
    };

    public String getDateRead() {
        return dateRead;
    }

    public void setDateRead(String dateRead) {
        this.dateRead = dateRead;
    }

    public String getBriefedByUserFullName() {
        return briefedByUserFullName;
    }

    public void setBriefedByUserFullName(String briefedByUserFullName) {
        this.briefedByUserFullName = briefedByUserFullName;
    }

    public List<IssuedDocModal> getBriefings() {
        return briefings;
    }

    public void setBriefings(List<IssuedDocModal> briefings) {
        this.briefings = briefings;
    }

    public ReceivedModel(Cursor cursor) {
        if (cursor != null && !cursor.isBeforeFirst() && !cursor.isAfterLast()) {
            briefedByUserFullName = cursor.getString(cursor.getColumnIndex(DBTable.names));
            briefings = new ArrayList<>();
            briefings.add(new IssuedDocModal(cursor));
            dateRead = cursor.getString(cursor.getColumnIndex(ReceivedModel.DBTable.dateRead));
        }
    }

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        if (dateRead!=null){
            cv.put(ReceivedModel.DBTable.dateRead, dateRead);
        }
        for (IssuedDocModal issuedDocModal : briefings)
        {
            cv.putAll(issuedDocModal.toContentValues());
        }
        cv.put(DBTable.names, briefedByUserFullName);
        return cv;
    }
    public static class DBTable {
        public static final String NAME = "BriefingsRead";
        public static final String dateRead = "dateRead";
        public static final String names = "briefedByUserFullName";

    }
}
