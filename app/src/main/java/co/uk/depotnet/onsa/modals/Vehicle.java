package co.uk.depotnet.onsa.modals;//TODO: package name

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import co.uk.depotnet.onsa.listeners.DropDownItem;

public class Vehicle implements Parcelable , DropDownItem {
    public static final Creator<Vehicle> CREATOR = new Creator<Vehicle>() {
        @Override
        public Vehicle createFromParcel(Parcel in) {
            return new Vehicle(in);
        }

        @Override
        public Vehicle[] newArray(int size) {
            return new Vehicle[size];

        }

    };
    private boolean IsTm;
    private int VehicleTypeID;
    private boolean Limit70;
    private String VehicleSupplierName;
    private int VehicleDriverID;
    private int VehicleID;
    private String TaxExpiryDate;
    private int CurrentMileage;
    private String OnHireDate;
    private int ServiceMileage;
    private boolean Active;
    private String TwokServiceReminder;
    private String VehicleTypeName;
    private String LastServiceDate;
    private int VehicleSupplierID;
    private String VehicleDriverName;
    private String Registration;
    private String Notes;

    protected Vehicle(Parcel in) {
        IsTm = in.readByte() != 0;
        VehicleTypeID = in.readInt();
        Limit70 = in.readByte() != 0;
        VehicleSupplierName = in.readString();
        VehicleDriverID = in.readInt();
        VehicleID = in.readInt();
        TaxExpiryDate = in.readString();
        CurrentMileage = in.readInt();
        OnHireDate = in.readString();
        ServiceMileage = in.readInt();
        Active = in.readByte() != 0;
        TwokServiceReminder = in.readString();
        VehicleTypeName = in.readString();
        LastServiceDate = in.readString();
        VehicleSupplierID = in.readInt();
        VehicleDriverName = in.readString();
        Registration = in.readString();
        Notes = in.readString();

    }

    public Vehicle(Cursor cursor) {
        IsTm = cursor.getInt(cursor.getColumnIndex(DBTable.IsTm)) != 0;
        VehicleTypeID = cursor.getInt(cursor.getColumnIndex(DBTable.VehicleTypeID));
        Limit70 = cursor.getInt(cursor.getColumnIndex(DBTable.Limit70)) != 0;
        VehicleSupplierName = cursor.getString(cursor.getColumnIndex(DBTable.VehicleSupplierName));
        VehicleDriverID = cursor.getInt(cursor.getColumnIndex(DBTable.VehicleDriverID));
        VehicleID = cursor.getInt(cursor.getColumnIndex(DBTable.VehicleID));
        TaxExpiryDate = cursor.getString(cursor.getColumnIndex(DBTable.TaxExpiryDate));
        CurrentMileage = cursor.getInt(cursor.getColumnIndex(DBTable.CurrentMileage));
        OnHireDate = cursor.getString(cursor.getColumnIndex(DBTable.OnHireDate));
        ServiceMileage = cursor.getInt(cursor.getColumnIndex(DBTable.ServiceMileage));
        Active = cursor.getInt(cursor.getColumnIndex(DBTable.Active)) != 0;
        TwokServiceReminder = cursor.getString(cursor.getColumnIndex(DBTable.TwokServiceReminder));
        VehicleTypeName = cursor.getString(cursor.getColumnIndex(DBTable.VehicleTypeName));
        LastServiceDate = cursor.getString(cursor.getColumnIndex(DBTable.LastServiceDate));
        VehicleSupplierID = cursor.getInt(cursor.getColumnIndex(DBTable.VehicleSupplierID));
        VehicleDriverName = cursor.getString(cursor.getColumnIndex(DBTable.VehicleDriverName));
        Registration = cursor.getString(cursor.getColumnIndex(DBTable.Registration));
        Notes = cursor.getString(cursor.getColumnIndex(DBTable.Notes));

    }

    public boolean isIsTm() {
        return this.IsTm;
    }

    public void setIsTm(boolean IsTm) {
        this.IsTm = IsTm;
    }

    public int getVehicleTypeID() {
        return this.VehicleTypeID;
    }

    public void setVehicleTypeID(int VehicleTypeID) {
        this.VehicleTypeID = VehicleTypeID;
    }

    public boolean isLimit70() {
        return this.Limit70;
    }

    public void setLimit70(boolean Limit70) {
        this.Limit70 = Limit70;
    }

    public String getVehicleSupplierName() {
        return this.VehicleSupplierName;
    }

    public void setVehicleSupplierName(String VehicleSupplierName) {
        this.VehicleSupplierName = VehicleSupplierName;
    }

    public int getVehicleDriverID() {
        return this.VehicleDriverID;
    }

    public void setVehicleDriverID(int VehicleDriverID) {
        this.VehicleDriverID = VehicleDriverID;
    }

    public int getVehicleID() {
        return this.VehicleID;
    }

    public void setVehicleID(int VehicleID) {
        this.VehicleID = VehicleID;
    }

    public String getTaxExpiryDate() {
        return this.TaxExpiryDate;
    }

    public void setTaxExpiryDate(String TaxExpiryDate) {
        this.TaxExpiryDate = TaxExpiryDate;
    }

    public int getCurrentMileage() {
        return this.CurrentMileage;
    }

    public void setCurrentMileage(int CurrentMileage) {
        this.CurrentMileage = CurrentMileage;
    }

    public String getOnHireDate() {
        return this.OnHireDate;
    }

    public void setOnHireDate(String OnHireDate) {
        this.OnHireDate = OnHireDate;
    }

    public int getServiceMileage() {
        return this.ServiceMileage;
    }

    public void setServiceMileage(int ServiceMileage) {
        this.ServiceMileage = ServiceMileage;
    }

    public boolean isActive() {
        return this.Active;
    }

    public void setActive(boolean Active) {
        this.Active = Active;
    }

    public String getTwokServiceReminder() {
        return this.TwokServiceReminder;
    }

    public void setTwokServiceReminder(String TwokServiceReminder) {
        this.TwokServiceReminder = TwokServiceReminder;
    }

    public String getVehicleTypeName() {
        return this.VehicleTypeName;
    }

    public void setVehicleTypeName(String VehicleTypeName) {
        this.VehicleTypeName = VehicleTypeName;
    }

    public String getLastServiceDate() {
        return this.LastServiceDate;
    }

    public void setLastServiceDate(String LastServiceDate) {
        this.LastServiceDate = LastServiceDate;
    }

    public int getVehicleSupplierID() {
        return this.VehicleSupplierID;
    }

    public void setVehicleSupplierID(int VehicleSupplierID) {
        this.VehicleSupplierID = VehicleSupplierID;
    }

    public String getVehicleDriverName() {
        return this.VehicleDriverName;
    }

    public void setVehicleDriverName(String VehicleDriverName) {
        this.VehicleDriverName = VehicleDriverName;
    }

    public String getRegistration() {
        return this.Registration;
    }

    public void setRegistration(String Registration) {
        this.Registration = Registration;
    }

    public String getNotes() {
        return this.Notes;
    }

    public void setNotes(String Notes) {
        this.Notes = Notes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte((byte) (IsTm ? 1 : 0));
        parcel.writeInt(VehicleTypeID);
        parcel.writeByte((byte) (Limit70 ? 1 : 0));
        parcel.writeString(VehicleSupplierName);
        parcel.writeInt(VehicleDriverID);
        parcel.writeInt(VehicleID);
        parcel.writeString(TaxExpiryDate);
        parcel.writeInt(CurrentMileage);
        parcel.writeString(OnHireDate);
        parcel.writeInt(ServiceMileage);
        parcel.writeByte((byte) (Active ? 1 : 0));
        parcel.writeString(TwokServiceReminder);
        parcel.writeString(VehicleTypeName);
        parcel.writeString(LastServiceDate);
        parcel.writeInt(VehicleSupplierID);
        parcel.writeString(VehicleDriverName);
        parcel.writeString(Registration);
        parcel.writeString(Notes);

    }

    public ContentValues toContentValues() {

        ContentValues cv = new ContentValues();

        cv.put(DBTable.IsTm, this.IsTm);

        cv.put(DBTable.VehicleTypeID, this.VehicleTypeID);

        cv.put(DBTable.Limit70, this.Limit70);

        cv.put(DBTable.VehicleSupplierName, this.VehicleSupplierName);

        cv.put(DBTable.VehicleDriverID, this.VehicleDriverID);

        cv.put(DBTable.VehicleID, this.VehicleID);

        cv.put(DBTable.TaxExpiryDate, this.TaxExpiryDate);

        cv.put(DBTable.CurrentMileage, this.CurrentMileage);

        cv.put(DBTable.OnHireDate, this.OnHireDate);

        cv.put(DBTable.ServiceMileage, this.ServiceMileage);

        cv.put(DBTable.Active, this.Active);

        cv.put(DBTable.TwokServiceReminder, this.TwokServiceReminder);

        cv.put(DBTable.VehicleTypeName, this.VehicleTypeName);

        cv.put(DBTable.LastServiceDate, this.LastServiceDate);

        cv.put(DBTable.VehicleSupplierID, this.VehicleSupplierID);

        cv.put(DBTable.VehicleDriverName, this.VehicleDriverName);

        cv.put(DBTable.Registration, this.Registration);

        cv.put(DBTable.Notes, this.Notes);

        return cv;
    }

    @Override
    public String getDisplayItem() {
        return this.getRegistration();
    }

    @Override
    public String getUploadValue() {
        return String.valueOf(getVehicleID());
    }

    public static class DBTable {
        public static final String NAME = "Vehicles";
        public static final String IsTm = "IsTm";
        public static final String VehicleTypeID = "VehicleTypeID";
        public static final String Limit70 = "Limit70";
        public static final String VehicleSupplierName = "VehicleSupplierName";
        public static final String VehicleDriverID = "VehicleDriverID";
        public static final String VehicleID = "VehicleID";
        public static final String TaxExpiryDate = "TaxExpiryDate";
        public static final String CurrentMileage = "CurrentMileage";
        public static final String OnHireDate = "OnHireDate";
        public static final String ServiceMileage = "ServiceMileage";
        public static final String Active = "Active";
        public static final String TwokServiceReminder = "TwokServiceReminder";
        public static final String VehicleTypeName = "VehicleTypeName";
        public static final String LastServiceDate = "LastServiceDate";
        public static final String VehicleSupplierID = "VehicleSupplierID";
        public static final String VehicleDriverName = "VehicleDriverName";
        public static final String Registration = "Registration";
        public static final String Notes = "Notes";

    }
}