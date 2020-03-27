package co.uk.depotnet.onsa.utils;//TODO: package name
import android.content.ContentValues;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable{
    private String stringValue;
    private double DoubleValue;
    private int IntegerValue;
    private boolean BooleanValue;

    public void setstringValue(String stringValue){
        this.stringValue = stringValue;
    }
    public String getstringValue(){
        return this.stringValue;
    }
    public void setDoubleValue(double DoubleValue){
        this.DoubleValue = DoubleValue;
    }
    public double getDoubleValue(){
        return this.DoubleValue;
    }
    public void setIntegerValue(int IntegerValue){
        this.IntegerValue = IntegerValue;
    }
    public int getIntegerValue(){
        return this.IntegerValue;
    }
    public void setBooleanValue(boolean BooleanValue){
        this.BooleanValue = BooleanValue;
    }
    public boolean isBooleanValue(){
        return this.BooleanValue;
    }

    protected User(Parcel in) {
        stringValue = in.readString();
        DoubleValue = in.readDouble();
        IntegerValue = in.readInt();
        BooleanValue = in.readByte() != 0;

    }	public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];

        }

    };

    @Override
    public int describeContents() {
        return 0;
    }

    public static class DBTable{
        public static final String NAME = "User";
        public static final String stringValue = "stringValue";
        public static final String DoubleValue = "DoubleValue";
        public static final String IntegerValue = "IntegerValue";
        public static final String BooleanValue = "BooleanValue";

    }	@Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(stringValue);
        parcel.writeDouble(DoubleValue);
        parcel.writeInt(IntegerValue);
        parcel.writeByte((byte) (BooleanValue ? 1 : 0));

    }
    public User(Cursor cursor) {
        stringValue = cursor.getString(cursor.getColumnIndex(DBTable.stringValue));
        DoubleValue = cursor.getDouble(cursor.getColumnIndex(DBTable.DoubleValue));
        IntegerValue = cursor.getInt(cursor.getColumnIndex(DBTable.IntegerValue));
        BooleanValue = cursor.getInt(cursor.getColumnIndex(DBTable.BooleanValue))!=0;

    }
    public ContentValues toContentValues(){

        ContentValues cv = new ContentValues();

        cv.put(DBTable.stringValue , this.stringValue);

        cv.put(DBTable.DoubleValue , this.DoubleValue);

        cv.put(DBTable.IntegerValue , this.IntegerValue);

        cv.put(DBTable.BooleanValue , this.BooleanValue);

        return cv;

    }
}