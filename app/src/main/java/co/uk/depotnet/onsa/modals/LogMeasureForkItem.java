package co.uk.depotnet.onsa.modals;

import android.os.Parcel;
import android.os.Parcelable;

public class LogMeasureForkItem implements Parcelable {

    private String SurfaceTypeID;
    private String MaterialTypeID;
    private String Length;
    private String Width;
    private String DepthB;
    private String DepthT;

    protected LogMeasureForkItem(Parcel in) {
        SurfaceTypeID = in.readString();
        MaterialTypeID = in.readString();
        Length = in.readString();
        Width = in.readString();
        DepthB = in.readString();
        DepthT = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(SurfaceTypeID);
        dest.writeString(MaterialTypeID);
        dest.writeString(Length);
        dest.writeString(Width);
        dest.writeString(DepthB);
        dest.writeString(DepthT);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LogMeasureForkItem> CREATOR = new Creator<LogMeasureForkItem>() {
        @Override
        public LogMeasureForkItem createFromParcel(Parcel in) {
            return new LogMeasureForkItem(in);
        }

        @Override
        public LogMeasureForkItem[] newArray(int size) {
            return new LogMeasureForkItem[size];
        }
    };

    public String getMaterialTypeID() {
        return MaterialTypeID;
    }

    public void setMaterialTypeID(String materialTypeID) {
        MaterialTypeID = materialTypeID;
    }

    public String getLength() {
        return Length;
    }

    public void setLength(String length) {
        Length = length;
    }

    public String getWidth() {
        return Width;
    }

    public void setWidth(String width) {
        Width = width;
    }

    public String getDepthB() {
        return DepthB;
    }

    public void setDepthB(String depthB) {
        DepthB = depthB;
    }

    public String getDepthT() {
        return DepthT;
    }

    public void setDepthT(String depthT) {
        DepthT = depthT;
    }

    public String getSurfaceTypeID() {
        return SurfaceTypeID;
    }

    public void setSurfaceTypeID(String surfaceTypeID) {
        SurfaceTypeID = surfaceTypeID;
    }
}
