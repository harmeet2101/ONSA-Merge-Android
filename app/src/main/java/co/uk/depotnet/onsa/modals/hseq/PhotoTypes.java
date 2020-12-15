package co.uk.depotnet.onsa.modals.hseq;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PhotoTypes implements Parcelable {
    @SerializedName("photoTypeName")
    @Expose
    private String photoTypeName;
    @SerializedName("photoTypeId")
    @Expose
    private Integer photoTypeId;

    public PhotoTypes() {
    }
    public PhotoTypes(Cursor cursor) {
        photoTypeName = cursor.getString(cursor.getColumnIndex(DBTable.photoTypeName));
        photoTypeId = cursor.getInt(cursor.getColumnIndex(DBTable.photoTypeId));
    }
    protected PhotoTypes(Parcel in) {
        photoTypeName = in.readString();
        if (in.readByte() == 0) {
            photoTypeId = null;
        } else {
            photoTypeId = in.readInt();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(photoTypeName);
        if (photoTypeId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(photoTypeId);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PhotoTypes> CREATOR = new Creator<PhotoTypes>() {
        @Override
        public PhotoTypes createFromParcel(Parcel in) {
            return new PhotoTypes(in);
        }

        @Override
        public PhotoTypes[] newArray(int size) {
            return new PhotoTypes[size];
        }
    };

    public String getPhotoTypeName() {
        return photoTypeName;
    }

    public void setPhotoTypeName(String photoTypeName) {
        this.photoTypeName = photoTypeName;
    }

    public Integer getPhotoTypeId() {
        return photoTypeId;
    }

    public void setPhotoTypeId(Integer photoTypeId) {
        this.photoTypeId = photoTypeId;
    }
    public ContentValues toContentValues() {

        ContentValues cv = new ContentValues();
        cv.put(DBTable.photoTypeName, this.photoTypeName);
        cv.put(DBTable.photoTypeId, this.photoTypeId);
        return cv;
    }
    public static class DBTable {
        public static final String NAME = "PhotoTypes";
        public static final String photoTypeName = "photoTypeName";
        public static final String photoTypeId = "photoTypeId";
    }
}
