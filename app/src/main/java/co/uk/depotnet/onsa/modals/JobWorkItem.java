package co.uk.depotnet.onsa.modals;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import co.uk.depotnet.onsa.listeners.DropDownItem;

public class JobWorkItem implements Parcelable , DropDownItem {
    private String jobId;
    private String itemId;
    private String unitType;
    private float quantity;
    private float measuredQuantity;
    private float availableToMeasureQuantity;
    private String itemCode;
    private String description;

    public JobWorkItem(Cursor cursor) {
        jobId = cursor.getString(cursor.getColumnIndex(DBTable.jobId));
        itemId = cursor.getString(cursor.getColumnIndex(DBTable.itemId));
        unitType = cursor.getString(cursor.getColumnIndex(DBTable.unitType));
        quantity = cursor.getFloat(cursor.getColumnIndex(DBTable.quantity));
        measuredQuantity = cursor.getFloat(cursor.getColumnIndex(DBTable.measuredQuantity));
        availableToMeasureQuantity = cursor.getFloat(cursor.getColumnIndex(DBTable.availableToMeasureQuantity));
        itemCode = cursor.getString(cursor.getColumnIndex(DBTable.itemCode));
        description = cursor.getString(cursor.getColumnIndex(DBTable.description));

    }

    protected JobWorkItem(Parcel in) {
        jobId = in.readString();
        itemId = in.readString();
        unitType = in.readString();
        quantity = in.readFloat();
        measuredQuantity = in.readFloat();
        availableToMeasureQuantity = in.readFloat();
        itemCode = in.readString();
        description = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(jobId);
        dest.writeString(itemId);
        dest.writeString(unitType);
        dest.writeFloat(quantity);
        dest.writeFloat(measuredQuantity);
        dest.writeFloat(availableToMeasureQuantity);
        dest.writeString(itemCode);
        dest.writeString(description);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<JobWorkItem> CREATOR = new Creator<JobWorkItem>() {
        @Override
        public JobWorkItem createFromParcel(Parcel in) {
            return new JobWorkItem(in);
        }

        @Override
        public JobWorkItem[] newArray(int size) {
            return new JobWorkItem[size];
        }
    };

    public void setunitType(String unitType) {
        this.unitType = unitType;
    }

    public String getunitType() {
        return this.unitType;
    }

    public void setquantity(float quantity) {
        this.quantity = quantity;
    }

    public float getquantity() {
        return this.quantity;
    }

    public void setitemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getitemCode() {
        return this.itemCode;
    }

    public void setdescription(String description) {
        this.description = description;
    }

    public String getdescription() {
        return this.description;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public float getAvailableToMeasureQuantity() {
        return availableToMeasureQuantity;
    }

    public float getMeasuredQuantity() {
        return measuredQuantity;
    }

    public ContentValues toContentValues() {

        ContentValues cv = new ContentValues();

        cv.put(DBTable.jobId, this.jobId);
        cv.put(DBTable.itemId, this.itemId);
        cv.put(DBTable.unitType, this.unitType);
        cv.put(DBTable.quantity, this.quantity);
        cv.put(DBTable.measuredQuantity, this.measuredQuantity);
        cv.put(DBTable.availableToMeasureQuantity, this.availableToMeasureQuantity);
        cv.put(DBTable.itemCode, this.itemCode);
        cv.put(DBTable.description, this.description);
        return cv;
    }

    @Override
    public String getDisplayItem() {
        return itemCode;
    }

    @Override
    public String getUploadValue() {
        return itemId;
    }

    public static class DBTable {
        public static final String NAME = "JobWorkItem";
        public static final String jobId = "jobId";
        public static final String itemId = "itemId";
        public static final String unitType = "unitType";
        public static final String quantity = "quantity";
        public static final String measuredQuantity = "measuredQuantity";
        public static final String availableToMeasureQuantity = "availableToMeasureQuantity";
        public static final String itemCode = "itemCode";
        public static final String description = "description";

    }
}