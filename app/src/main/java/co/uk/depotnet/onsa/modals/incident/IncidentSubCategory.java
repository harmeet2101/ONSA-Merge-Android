package co.uk.depotnet.onsa.modals.incident;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import co.uk.depotnet.onsa.listeners.DropDownItem;

public class IncidentSubCategory implements Parcelable, DropDownItem {
    private String incidentCategoryId;
    private String incidentSubcategoryId;
    private String incidentSubcategoryName;

    public IncidentSubCategory() {
    }
    public IncidentSubCategory(Cursor cursor) {
        incidentCategoryId = cursor.getString(cursor.getColumnIndex(DBTable.incidentCategoryId));
        incidentSubcategoryId = cursor.getString(cursor.getColumnIndex(DBTable.incidentSubcategoryId));
        incidentSubcategoryName = cursor.getString(cursor.getColumnIndex(DBTable.incidentSubcategoryName));

    }
    protected IncidentSubCategory(Parcel in) {
        incidentCategoryId = in.readString();
        incidentSubcategoryId = in.readString();
        incidentSubcategoryName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(incidentCategoryId);
        dest.writeString(incidentSubcategoryId);
        dest.writeString(incidentSubcategoryName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<IncidentSubCategory> CREATOR = new Creator<IncidentSubCategory>() {
        @Override
        public IncidentSubCategory createFromParcel(Parcel in) {
            return new IncidentSubCategory(in);
        }

        @Override
        public IncidentSubCategory[] newArray(int size) {
            return new IncidentSubCategory[size];
        }
    };

    public String getIncidentSubcategoryId() {
        return incidentSubcategoryId;
    }

    public void setIncidentSubcategoryId(String incidentSubcategoryId) {
        this.incidentSubcategoryId = incidentSubcategoryId;
    }

    public IncidentSubCategory(String incidentSubcategoryName) {
        this.incidentSubcategoryName = incidentSubcategoryName;
    }

    public String getIncidentCategoryId() {
        return incidentCategoryId;
    }

    public void setIncidentCategoryId(String incidentCategoryId) {
        this.incidentCategoryId = incidentCategoryId;
    }

    public ContentValues toContentValues() {

        ContentValues cv = new ContentValues();
        cv.put(DBTable.incidentCategoryId, this.incidentCategoryId);
        cv.put(DBTable.incidentSubcategoryId, this.incidentSubcategoryId);
        cv.put(DBTable.incidentSubcategoryName, this.incidentSubcategoryName);
        return cv;
    }

    @Override
    public String getDisplayItem() {
        return incidentSubcategoryName;
    }

    @Override
    public String getUploadValue() {
        return incidentSubcategoryId;
    }

    public static class DBTable {
        public static final String NAME = "IncidentSubCategory";
        public static final String incidentCategoryId = "incidentCategoryId";
        public static final String incidentSubcategoryId = "incidentSubcategoryId";
        public static final String incidentSubcategoryName = "incidentSubcategoryName";
    }

    @Override
    public String toString() {
        return "InspectionTemplate{" +
                "incidentSubcategoryId='" + incidentSubcategoryId + '\'' +
                ", incidentSubcategoryName='" + incidentSubcategoryName + '\'' +
                '}';
    }
}
