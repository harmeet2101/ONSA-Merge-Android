package co.uk.depotnet.onsa.modals;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import co.uk.depotnet.onsa.listeners.DropDownItem;

public class MeasureItems implements Parcelable , DropDownItem {
    public static final Creator<MeasureItems> CREATOR = new Creator<MeasureItems>() {
        @Override
        public MeasureItems createFromParcel(Parcel in) {
            return new MeasureItems(in);
        }

        @Override
        public MeasureItems[] newArray(int size) {
            return new MeasureItems[size];

        }

    };
    private String subcontractorRateId;
    private String itemCode;

    protected MeasureItems(Parcel in) {
        subcontractorRateId = in.readString();
        itemCode = in.readString();
    }

    public MeasureItems(Cursor cursor) {
        subcontractorRateId = cursor.getString(cursor.getColumnIndex(DBTable.subcontractorRateId));
        itemCode = cursor.getString(cursor.getColumnIndex(DBTable.itemCode));

    }

    public String getSubcontractorRateId() {
        return subcontractorRateId;
    }

    public String getItemCode() {
        return itemCode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(subcontractorRateId);
        parcel.writeString(itemCode);
    }

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(DBTable.subcontractorRateId, this.subcontractorRateId);
        cv.put(DBTable.itemCode, this.itemCode);
        return cv;
    }

    public static class DBTable {
        public static final String NAME = "MeasureItems";
        public static final String subcontractorRateId = "subcontractorRateId";
        public static final String itemCode = "itemCode";
    }

    @Override
    public String getDisplayItem() {
        return itemCode;
    }

    @Override
    public String getUploadValue() {
        return String.valueOf(subcontractorRateId);
    }
}