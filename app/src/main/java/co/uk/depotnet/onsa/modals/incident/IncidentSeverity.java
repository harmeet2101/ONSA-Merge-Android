package co.uk.depotnet.onsa.modals.incident;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import co.uk.depotnet.onsa.listeners.DropDownItem;

public class IncidentSeverity implements Parcelable, DropDownItem {
    private String incidentSeverityId;
    private String incidentSeverityName;

    public IncidentSeverity() {
    }
    public IncidentSeverity(Cursor cursor) {
        incidentSeverityId = cursor.getString(cursor.getColumnIndex(DBTable.incidentSeverityId));
        incidentSeverityName = cursor.getString(cursor.getColumnIndex(DBTable.incidentSeverityName));

    }
    protected IncidentSeverity(Parcel in) {
        incidentSeverityId = in.readString();
        incidentSeverityName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(incidentSeverityId);
        dest.writeString(incidentSeverityName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<IncidentSeverity> CREATOR = new Creator<IncidentSeverity>() {
        @Override
        public IncidentSeverity createFromParcel(Parcel in) {
            return new IncidentSeverity(in);
        }

        @Override
        public IncidentSeverity[] newArray(int size) {
            return new IncidentSeverity[size];
        }
    };

    public String getIncidentSeverityId() {
        return incidentSeverityId;
    }

    public void setIncidentSeverityId(String incidentSeverityId) {
        this.incidentSeverityId = incidentSeverityId;
    }

    public IncidentSeverity(String incidentSeverityName) {
        this.incidentSeverityName = incidentSeverityName;
    }


    public ContentValues toContentValues() {

        ContentValues cv = new ContentValues();
        cv.put(DBTable.incidentSeverityId, this.incidentSeverityId);
        cv.put(DBTable.incidentSeverityName, this.incidentSeverityName);
        return cv;
    }

    @Override
    public String getDisplayItem() {
        return incidentSeverityName;
    }

    @Override
    public String getUploadValue() {
        return incidentSeverityId;
    }

    public static class DBTable {
        public static final String NAME = "IncidentSeverity";
        public static final String incidentSeverityId = "incidentSeverityId";
        public static final String incidentSeverityName = "incidentSeverityName";
    }

    @Override
    public String toString() {
        return "InspectionTemplate{" +
                "incidentSeverityId='" + incidentSeverityId + '\'' +
                ", incidentSeverityName='" + incidentSeverityName + '\'' +
                '}';
    }
}
