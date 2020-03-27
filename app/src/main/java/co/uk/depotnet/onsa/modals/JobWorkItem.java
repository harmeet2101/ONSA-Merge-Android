package co.uk.depotnet.onsa.modals;//TODO: package name

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

public class JobWorkItem implements Parcelable {
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
    private String jobId;
    private String unitType;
    private int quantity;
    private String itemCode;
    private String description;

    protected JobWorkItem(Parcel in) {
        jobId = in.readString();
        unitType = in.readString();
        quantity = in.readInt();
        itemCode = in.readString();
        description = in.readString();

    }

    public JobWorkItem(Cursor cursor) {
        jobId = cursor.getString(cursor.getColumnIndex(DBTable.jobId));
        unitType = cursor.getString(cursor.getColumnIndex(DBTable.unitType));
        quantity = cursor.getInt(cursor.getColumnIndex(DBTable.quantity));
        itemCode = cursor.getString(cursor.getColumnIndex(DBTable.itemCode));
        description = cursor.getString(cursor.getColumnIndex(DBTable.description));

    }

    public void setunitType(String unitType) {
        this.unitType = unitType;
    }

    public String getunitType() {
        return this.unitType;
    }

    public void setquantity(int quantity) {
        this.quantity = quantity;
    }

    public int getquantity() {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(jobId);
        parcel.writeString(unitType);
        parcel.writeInt(quantity);
        parcel.writeString(itemCode);
        parcel.writeString(description);

    }

    public ContentValues toContentValues() {

        ContentValues cv = new ContentValues();

        cv.put(DBTable.jobId, this.jobId);
        cv.put(DBTable.unitType, this.unitType);

        cv.put(DBTable.quantity, this.quantity);

        cv.put(DBTable.itemCode, this.itemCode);

        cv.put(DBTable.description, this.description);

        return cv;
    }

    public static class DBTable {
        public static final String NAME = "JobWorkItem";
        public static final String jobId = "jobId";
        public static final String unitType = "unitType";
        public static final String quantity = "quantity";
        public static final String itemCode = "itemCode";
        public static final String description = "description";

    }
}