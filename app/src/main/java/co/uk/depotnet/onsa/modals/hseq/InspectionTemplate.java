package co.uk.depotnet.onsa.modals.hseq;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import co.uk.depotnet.onsa.listeners.DropDownItem;

public class InspectionTemplate implements Parcelable , DropDownItem {
    private String inspectionTemplateId;
    private String latestTemplateVersionId;
    private String inspectionTemplateTitle;

    public InspectionTemplate() {
    }
    public InspectionTemplate(Cursor cursor) {
        inspectionTemplateId = cursor.getString(cursor.getColumnIndex(DBTable.inspectionTemplateId));
        latestTemplateVersionId = cursor.getString(cursor.getColumnIndex(DBTable.latestTemplateVersionId));
        inspectionTemplateTitle = cursor.getString(cursor.getColumnIndex(DBTable.inspectionTemplateTitle));

    }
    protected InspectionTemplate(Parcel in) {
        inspectionTemplateId = in.readString();
        latestTemplateVersionId = in.readString();
        inspectionTemplateTitle = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(inspectionTemplateId);
        dest.writeString(latestTemplateVersionId);
        dest.writeString(inspectionTemplateTitle);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<InspectionTemplate> CREATOR = new Creator<InspectionTemplate>() {
        @Override
        public InspectionTemplate createFromParcel(Parcel in) {
            return new InspectionTemplate(in);
        }

        @Override
        public InspectionTemplate[] newArray(int size) {
            return new InspectionTemplate[size];
        }
    };

    public String getInspectionTemplateId() {
        return inspectionTemplateId;
    }

    public void setInspectionTemplateId(String inspectionTemplateId) {
        this.inspectionTemplateId = inspectionTemplateId;
    }

    public InspectionTemplate(String latestTemplateVersionId) {
        this.latestTemplateVersionId = latestTemplateVersionId;
    }

    public String getInspectionTemplateTitle() {
        return inspectionTemplateTitle;
    }

    public void setInspectionTemplateTitle(String inspectionTemplateTitle) {
        this.inspectionTemplateTitle = inspectionTemplateTitle;
    }

    public ContentValues toContentValues() {

        ContentValues cv = new ContentValues();
        cv.put(DBTable.inspectionTemplateId, this.inspectionTemplateId);
        cv.put(DBTable.latestTemplateVersionId, this.latestTemplateVersionId);
        cv.put(DBTable.inspectionTemplateTitle, this.inspectionTemplateTitle);
        return cv;
    }

    @Override
    public String getDisplayItem() {
        return inspectionTemplateTitle;
    }

    @Override
    public String getUploadValue() {
        return inspectionTemplateId;
    }

    public static class DBTable {
        public static final String NAME = "InspectionHseq";
        public static final String inspectionTemplateId = "inspectionTemplateId";
        public static final String latestTemplateVersionId = "latestTemplateVersionId";
        public static final String inspectionTemplateTitle = "inspectionTemplateTitle";
    }

    @Override
    public String toString() {
        return "InspectionTemplate{" +
                "inspectionTemplateId='" + inspectionTemplateId + '\'' +
                ", latestTemplateVersionId='" + latestTemplateVersionId + '\'' +
                ", inspectionTemplateTitle='" + inspectionTemplateTitle + '\'' +
                '}';
    }
}
