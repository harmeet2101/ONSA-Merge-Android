package co.uk.depotnet.onsa.modals;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import co.uk.depotnet.onsa.listeners.DropDownItem;

public class Driver implements Parcelable , DropDownItem {
    public static final Creator<Driver> CREATOR = new Creator<Driver>() {
        @Override
        public Driver createFromParcel(Parcel in) {
            return new Driver(in);
        }

        @Override
        public Driver[] newArray(int size) {
            return new Driver[size];
        }

    };
    private boolean IsTm;
    private boolean Active;
    private int VehicleDriverID;
    private String VehicleDriverName;

    protected Driver(Parcel in) {
        IsTm = in.readByte() != 0;
        Active = in.readByte() != 0;
        VehicleDriverID = in.readInt();
        VehicleDriverName = in.readString();

    }

    public Driver(Cursor cursor) {
        IsTm = cursor.getInt(cursor.getColumnIndex(DBTable.IsTm)) != 0;
        Active = cursor.getInt(cursor.getColumnIndex(DBTable.Active)) != 0;
        VehicleDriverID = cursor.getInt(cursor.getColumnIndex(DBTable.VehicleDriverID));
        VehicleDriverName = cursor.getString(cursor.getColumnIndex(DBTable.VehicleDriverName));
    }

    public boolean isIsTm() {
        return this.IsTm;
    }

    public void setIsTm(boolean IsTm) {
        this.IsTm = IsTm;
    }

    public boolean isActive() {
        return this.Active;
    }

    public void setActive(boolean Active) {
        this.Active = Active;
    }

    public int getVehicleDriverID() {
        return this.VehicleDriverID;
    }

    public void setVehicleDriverID(int VehicleDriverID) {
        this.VehicleDriverID = VehicleDriverID;
    }

    public String getVehicleDriverName() {
        return this.VehicleDriverName;
    }

    public void setVehicleDriverName(String VehicleDriverName) {
        this.VehicleDriverName = VehicleDriverName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte((byte) (IsTm ? 1 : 0));
        parcel.writeByte((byte) (Active ? 1 : 0));
        parcel.writeInt(VehicleDriverID);
        parcel.writeString(VehicleDriverName);

    }

    public ContentValues toContentValues() {

        ContentValues cv = new ContentValues();

        cv.put(DBTable.IsTm, this.IsTm);

        cv.put(DBTable.Active, this.Active);

        cv.put(DBTable.VehicleDriverID, this.VehicleDriverID);

        cv.put(DBTable.VehicleDriverName, this.VehicleDriverName);

        return cv;
    }

    @Override
    public String getDisplayItem() {
        return getVehicleDriverName();
    }

    @Override
    public String getUploadValue() {
        return String.valueOf(getVehicleDriverID());
    }

    public static class DBTable {
        public static final String NAME = "Drivers";
        public static final String IsTm = "IsTm";
        public static final String Active = "Active";
        public static final String VehicleDriverID = "VehicleDriverID";
        public static final String VehicleDriverName = "VehicleDriverName";

    }
}