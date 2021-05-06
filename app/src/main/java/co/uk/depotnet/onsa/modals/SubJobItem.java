package co.uk.depotnet.onsa.modals;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import co.uk.depotnet.onsa.listeners.DropDownItem;

public class SubJobItem implements Parcelable , DropDownItem {

    private String text;
    private String value;

    protected SubJobItem(Parcel in) {
        text = in.readString();
        value = in.readString();
    }

    public SubJobItem(Cursor c) {
        text = c.getString(c.getColumnIndex(DBTable.text));
        value = c.getString(c.getColumnIndex(DBTable.value));
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeString(value);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SubJobItem> CREATOR = new Creator<SubJobItem>() {
        @Override
        public SubJobItem createFromParcel(Parcel in) {
            return new SubJobItem(in);
        }

        @Override
        public SubJobItem[] newArray(int size) {
            return new SubJobItem[size];
        }
    };

    @Override
    public String getDisplayItem() {
        return text;
    }

    @Override
    public String getUploadValue() {
        return value;
    }

    public static class DBTable{
        public static final String NAME = "SubJobItem";
        public static final String text = "text";
        public static final String value = "value";
    }

    public ContentValues toContentValues(){
        ContentValues cv = new ContentValues();
        cv.put(DBTable.text , this.text);
        cv.put(DBTable.value , this.value);
        return cv;
    }
}
