package co.uk.depotnet.onsa.modals.timesheet;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import co.uk.depotnet.onsa.listeners.DropDownItem;

public class TimesheetOperative implements Parcelable , DropDownItem {

    private String operativeId;
    private String operativeName;

    protected TimesheetOperative(Parcel in) {
        operativeId = in.readString();
        operativeName = in.readString();
    }

    public TimesheetOperative(Cursor c) {
        operativeId = c.getString(c.getColumnIndex(DBTable.operativeId));
        operativeName = c.getString(c.getColumnIndex(DBTable.operativeName));
    }



    public static final Creator<TimesheetOperative> CREATOR = new Creator<TimesheetOperative>() {
        @Override
        public TimesheetOperative createFromParcel(Parcel in) {
            return new TimesheetOperative(in);
        }

        @Override
        public TimesheetOperative[] newArray(int size) {
            return new TimesheetOperative[size];
        }
    };

    public String getOperativeId() {
        return operativeId;
    }

    public void setOperativeId(String operativeId) {
        this.operativeId = operativeId;
    }

    public String getOperativeName() {
        return operativeName;
    }

    public void setOperativeName(String operativeName) {
        this.operativeName = operativeName;
    }

    @Override
    public String getDisplayItem() {
        return operativeName;
    }

    @Override
    public String getUploadValue() {
        return operativeId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(operativeId);
        dest.writeString(operativeName);
    }

    public ContentValues toContentValues(){
        ContentValues cv = new ContentValues();

        cv.put(DBTable.operativeId, operativeId);
        cv.put(DBTable.operativeName, operativeName);

        return cv;
    }


    public static class DBTable{
        public static final String NAME = "TimesheetOperative";
        public static final String operativeId = "operativeId";
        public static final String operativeName = "operativeName";
    }
}
