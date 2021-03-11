package co.uk.depotnet.onsa.modals.incident;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.listeners.DropDownItem;

public class IncidentCategory implements Parcelable, DropDownItem {
    private String incidentCategoryId;
    private String incidentCategoryName;
    private ArrayList<IncidentSubCategory> subcategories;

    public IncidentCategory() {
    }
    public IncidentCategory(Cursor cursor) {
        incidentCategoryId = cursor.getString(cursor.getColumnIndex(DBTable.incidentCategoryId));
        incidentCategoryName = cursor.getString(cursor.getColumnIndex(DBTable.incidentCategoryName));
        subcategories = DBHandler.getInstance().getIncidentSubCategories(incidentCategoryId);

    }

    protected IncidentCategory(Parcel in) {
        incidentCategoryId = in.readString();
        incidentCategoryName = in.readString();
        subcategories = in.createTypedArrayList(IncidentSubCategory.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(incidentCategoryId);
        dest.writeString(incidentCategoryName);
        dest.writeTypedList(subcategories);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<IncidentCategory> CREATOR = new Creator<IncidentCategory>() {
        @Override
        public IncidentCategory createFromParcel(Parcel in) {
            return new IncidentCategory(in);
        }

        @Override
        public IncidentCategory[] newArray(int size) {
            return new IncidentCategory[size];
        }
    };

    public String getIncidentCategoryId() {
        return incidentCategoryId;
    }

    public void setIncidentCategoryId(String incidentCategoryId) {
        this.incidentCategoryId = incidentCategoryId;
    }

    public IncidentCategory(String incidentCategoryName) {
        this.incidentCategoryName = incidentCategoryName;
    }



    public ContentValues toContentValues() {

        ContentValues cv = new ContentValues();
        cv.put(DBTable.incidentCategoryId, this.incidentCategoryId);
        cv.put(DBTable.incidentCategoryName, this.incidentCategoryName);

        if(subcategories != null && !subcategories.isEmpty()){
            for (IncidentSubCategory subcategory :
                    subcategories) {
                subcategory.setIncidentCategoryId(incidentCategoryId);
                DBHandler.getInstance().replaceData(IncidentSubCategory.DBTable.NAME , subcategory.toContentValues());
            }
        }
        return cv;
    }

    @Override
    public String getDisplayItem() {
        return incidentCategoryName;
    }

    @Override
    public String getUploadValue() {
        return incidentCategoryId;
    }

    public ArrayList<IncidentSubCategory> getSubcategories() {
        return subcategories;
    }

    public void setSubcategories(ArrayList<IncidentSubCategory> subcategories) {
        this.subcategories = subcategories;
    }

    public static class DBTable {
        public static final String NAME = "IncidentCategory";
        public static final String incidentCategoryId = "incidentCategoryId";
        public static final String incidentCategoryName = "incidentCategoryName";
    }

    @Override
    public String toString() {
        return "InspectionTemplate{" +
                "incidentCategoryId='" + incidentCategoryId + '\'' +
                ", incidentCategoryName='" + incidentCategoryName + '\'' +
                '}';
    }
}
