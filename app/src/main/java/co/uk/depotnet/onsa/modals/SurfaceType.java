package co.uk.depotnet.onsa.modals;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import co.uk.depotnet.onsa.listeners.DropDownItem;

public class SurfaceType implements Parcelable , DropDownItem {

    private int SurfaceTypeID;
    private String SurfaceTypeName;


    protected SurfaceType(Parcel in) {
        SurfaceTypeID = in.readInt();
        SurfaceTypeName = in.readString();
    }

    public SurfaceType(Cursor cursor){
        SurfaceTypeID = cursor.getInt(cursor.getColumnIndex(DBTable.SurfaceTypeID));
        SurfaceTypeName = cursor.getString(cursor.getColumnIndex(DBTable.SurfaceTypeName));
    }

    public static final Creator<SurfaceType> CREATOR = new Creator<SurfaceType>() {
        @Override
        public SurfaceType createFromParcel(Parcel in) {
            return new SurfaceType(in);
        }

        @Override
        public SurfaceType[] newArray(int size) {
            return new SurfaceType[size];
        }
    };



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(SurfaceTypeID);
        parcel.writeString(SurfaceTypeName);
    }

    @Override
    public String getDisplayItem() {
        return this.SurfaceTypeName;
    }

    @Override
    public String getUploadValue() {
        return String.valueOf(SurfaceTypeID);
    }

    public static class DBTable{
        public static final String NAME = "SurfaceType";
        public static final String SurfaceTypeID = "SurfaceTypeID";
        public static final String SurfaceTypeName = "SurfaceTypeName";
    }

    public ContentValues toContentValues(){
        ContentValues cv = new ContentValues();
        cv.put(DBTable.SurfaceTypeID , this.SurfaceTypeID);
        cv.put(DBTable.SurfaceTypeName , this.SurfaceTypeName);
        return cv;
    }
}
