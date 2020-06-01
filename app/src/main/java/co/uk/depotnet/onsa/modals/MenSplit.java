package co.uk.depotnet.onsa.modals;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import co.uk.depotnet.onsa.listeners.DropDownItem;

public class MenSplit implements Parcelable , DropDownItem {
    public static final Creator<MenSplit> CREATOR = new Creator<MenSplit>() {
        @Override
        public MenSplit createFromParcel(Parcel in) {
            return new MenSplit(in);
        }

        @Override
        public MenSplit[] newArray(int size) {
            return new MenSplit[size];

        }

    };
    private int menSplitId;
    private String menSplitName;
    private float menSplitPercent;

    protected MenSplit(Parcel in) {
        menSplitId = in.readInt();
        menSplitName = in.readString();
        menSplitPercent = in.readFloat();
    }

    public MenSplit(Cursor cursor) {
        menSplitId = cursor.getInt(cursor.getColumnIndex(DBTable.menSplitId));
        menSplitName = cursor.getString(cursor.getColumnIndex(DBTable.menSplitName));
        menSplitPercent = cursor.getFloat(cursor.getColumnIndex(DBTable.menSplitPercent));

    }

    public int getMenSplitId() {
        return menSplitId;
    }

    public float getMenSplitPercent() {
        return menSplitPercent;
    }

    public String getMenSplitName() {
        return menSplitName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(menSplitId);
        parcel.writeString(menSplitName);
        parcel.writeFloat(menSplitPercent);
    }

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(DBTable.menSplitId, this.menSplitId);
        cv.put(DBTable.menSplitName, this.menSplitName);
        cv.put(DBTable.menSplitPercent, this.menSplitPercent);
        return cv;
    }

    public static class DBTable {
        public static final String NAME = "MenSplits";
        public static final String menSplitId = "menSplitId";
        public static final String menSplitName = "menSplitName";
        public static final String menSplitPercent = "menSplitPercent";
    }

    @Override
    public String getDisplayItem() {
        return menSplitName;
    }

    @Override
    public String getUploadValue() {
        return String.valueOf(menSplitId);
    }
}