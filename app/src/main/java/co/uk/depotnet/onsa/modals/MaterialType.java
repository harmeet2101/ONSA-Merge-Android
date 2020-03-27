package co.uk.depotnet.onsa.modals;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import co.uk.depotnet.onsa.listeners.DropDownItem;

public class MaterialType implements Parcelable , DropDownItem {

    private int MaterialTypeID;
    private String MaterialTypeName;


    protected MaterialType(Parcel in) {
        MaterialTypeID = in.readInt();
        MaterialTypeName = in.readString();
    }

    public MaterialType(Cursor cursor){
        MaterialTypeID = cursor.getInt(cursor.getColumnIndex(DBTable.MaterialTypeName));
        MaterialTypeName = cursor.getString(cursor.getColumnIndex(DBTable.MaterialTypeName));
    }

    public static final Creator<MaterialType> CREATOR = new Creator<MaterialType>() {
        @Override
        public MaterialType createFromParcel(Parcel in) {
            return new MaterialType(in);
        }

        @Override
        public MaterialType[] newArray(int size) {
            return new MaterialType[size];
        }
    };

    public int getMaterialTypeID() {
        return MaterialTypeID;
    }

    public void setMaterialTypeID(int materialTypeID) {
        MaterialTypeID = materialTypeID;
    }

    public String getMaterialTypeName() {
        return MaterialTypeName;
    }

    public void setMaterialTypeName(String materialTypeName) {
        MaterialTypeName = materialTypeName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(MaterialTypeID);
        parcel.writeString(MaterialTypeName);
    }

    @Override
    public String getDisplayItem() {
        return this.MaterialTypeName;
    }

    @Override
    public String getUploadValue() {
        return String.valueOf(MaterialTypeID);
    }

    public static class DBTable{
        public static final String NAME = "MaterialType";
        public static final String MaterialTypeID = "MaterialTypeID";
        public static final String MaterialTypeName = "MaterialTypeName";
    }

    public ContentValues toContentValues(){
        ContentValues cv = new ContentValues();
        cv.put(DBTable.MaterialTypeID , this.MaterialTypeID);
        cv.put(DBTable.MaterialTypeName , this.MaterialTypeName);
        return cv;
    }
}
