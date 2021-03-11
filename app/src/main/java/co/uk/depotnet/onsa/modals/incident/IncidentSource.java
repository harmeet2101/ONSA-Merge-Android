package co.uk.depotnet.onsa.modals.incident;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import co.uk.depotnet.onsa.listeners.DropDownItem;

public class IncidentSource implements Parcelable, DropDownItem {
    private String incidentSourceId;
    private String incidentSourceName;

    public IncidentSource() {
    }

    public IncidentSource(Cursor cursor) {
        incidentSourceId = cursor.getString(cursor.getColumnIndex(DBTable.incidentSourceId));
        incidentSourceName = cursor.getString(cursor.getColumnIndex(DBTable.incidentSourceName));

    }
    protected IncidentSource(Parcel in) {
        incidentSourceId = in.readString();
        incidentSourceName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(incidentSourceId);
        dest.writeString(incidentSourceName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<IncidentSource> CREATOR = new Creator<IncidentSource>() {
        @Override
        public IncidentSource createFromParcel(Parcel in) {
            return new IncidentSource(in);
        }

        @Override
        public IncidentSource[] newArray(int size) {
            return new IncidentSource[size];
        }
    };

    public String getIncidentSourceId() {
        return incidentSourceId;
    }

    public void setIncidentSourceId(String incidentSourceId) {
        this.incidentSourceId = incidentSourceId;
    }

    public IncidentSource(String incidentSourceName) {
        this.incidentSourceName = incidentSourceName;
    }



    public ContentValues toContentValues() {

        ContentValues cv = new ContentValues();
        cv.put(DBTable.incidentSourceId, this.incidentSourceId);
        cv.put(DBTable.incidentSourceName, this.incidentSourceName);
        return cv;
    }

    @Override
    public String getDisplayItem() {
        return incidentSourceName;
    }

    @Override
    public String getUploadValue() {
        return incidentSourceId;
    }

    public static class DBTable {
        public static final String NAME = "IncidentSource";
        public static final String incidentSourceId = "incidentSourceId";
        public static final String incidentSourceName = "incidentSourceName";
    }

    @Override
    public String toString() {
        return "InspectionTemplate{" +
                "incidentSourceId='" + incidentSourceId + '\'' +
                ", incidentSourceName='" + incidentSourceName + '\'' +
                '}';
    }
}
