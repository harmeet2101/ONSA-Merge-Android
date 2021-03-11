package co.uk.depotnet.onsa.modals.incident;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import co.uk.depotnet.onsa.listeners.DropDownItem;

public class UniqueIncident implements Parcelable, DropDownItem {
    private String incidentId;
    private String incidentTitle;

    public UniqueIncident() {
    }
    public UniqueIncident(Cursor cursor) {
        incidentId = cursor.getString(cursor.getColumnIndex(DBTable.incidentId));
        incidentTitle = cursor.getString(cursor.getColumnIndex(DBTable.incidentTitle));

    }
    protected UniqueIncident(Parcel in) {
        incidentId = in.readString();
        incidentTitle = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(incidentId);
        dest.writeString(incidentTitle);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UniqueIncident> CREATOR = new Creator<UniqueIncident>() {
        @Override
        public UniqueIncident createFromParcel(Parcel in) {
            return new UniqueIncident(in);
        }

        @Override
        public UniqueIncident[] newArray(int size) {
            return new UniqueIncident[size];
        }
    };

    public String getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(String incidentId) {
        this.incidentId = incidentId;
    }

    public UniqueIncident(String incidentTitle) {
        this.incidentTitle = incidentTitle;
    }


    public ContentValues toContentValues() {

        ContentValues cv = new ContentValues();
        cv.put(DBTable.incidentId, this.incidentId);
        cv.put(DBTable.incidentTitle, this.incidentTitle);
        return cv;
    }

    @Override
    public String getDisplayItem() {
        return incidentTitle;
    }

    @Override
    public String getUploadValue() {
        return incidentId;
    }

    public static class DBTable {
        public static final String NAME = "UniqueIncident";
        public static final String incidentId = "incidentId";
        public static final String incidentTitle = "incidentTitle";
    }

    @Override
    public String toString() {
        return "InspectionTemplate{" +
                "incidentId='" + incidentId + '\'' +
                ", incidentTitle='" + incidentTitle + '\'' +
                '}';
    }
}
