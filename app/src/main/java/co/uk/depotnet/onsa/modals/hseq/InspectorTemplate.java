package co.uk.depotnet.onsa.modals.hseq;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import co.uk.depotnet.onsa.listeners.DropDownItem;

public class InspectorTemplate implements Parcelable , DropDownItem {
    @SerializedName("inspectorId")
    @Expose
    private String inspectorId;
    @SerializedName("inspectorFullName")
    @Expose
    private String inspectorFullName;

    public InspectorTemplate() {
    }
    public InspectorTemplate(Cursor cursor) {
        inspectorId = cursor.getString(cursor.getColumnIndex(DBTable.inspectorId));
        inspectorFullName = cursor.getString(cursor.getColumnIndex(DBTable.inspectorFullName));

    }
    protected InspectorTemplate(Parcel in) {
        inspectorId = in.readString();
        inspectorFullName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(inspectorId);
        dest.writeString(inspectorFullName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<InspectorTemplate> CREATOR = new Creator<InspectorTemplate>() {
        @Override
        public InspectorTemplate createFromParcel(Parcel in) {
            return new InspectorTemplate(in);
        }

        @Override
        public InspectorTemplate[] newArray(int size) {
            return new InspectorTemplate[size];
        }
    };

    public String getInspectorId() {
        return inspectorId;
    }

    public void setInspectorId(String inspectorId) {
        this.inspectorId = inspectorId;
    }

    public String getInspectorFullName() {
        return inspectorFullName;
    }

    public void setInspectorFullName(String inspectorFullName) {
        this.inspectorFullName = inspectorFullName;
    }

    public ContentValues toContentValues() {

        ContentValues cv = new ContentValues();
        cv.put(DBTable.inspectorId, this.inspectorId);
        cv.put(DBTable.inspectorFullName, this.inspectorFullName);
        return cv;
    }

    @Override
    public String getDisplayItem() {
        return inspectorFullName;
    }

    @Override
    public String getUploadValue() {
        return inspectorId;
    }

    public static class DBTable {
        public static final String NAME = "InspectorsHseq";
        public static final String inspectorId = "inspectorId";
        public static final String inspectorFullName = "inspectorFullName";
    }
    @Override
    public String toString() {
        return "InspectorTemplate{" +
                "inspectorId='" + inspectorId + '\'' +
                ", inspectorFullName='" + inspectorFullName + '\'' +
                '}';
    }
}
