package co.uk.depotnet.onsa.modals.httpresponses;

import android.os.Parcel;
import android.os.Parcelable;

public class BackfillMeasures implements Parcelable {
    private int JobID;
    private int SurfaceTypeID;
    private String SurfaceTypeName;

    private int MaterialTypeID;
    private String MaterialTypeName;

    private float Width;
    private float Length;
    private float DepthB;
    private float DepthT;

    private String DateCreated;

//    ***************************

    private int SiteActivityLogID;




    protected BackfillMeasures(Parcel in) {
        JobID = in.readInt();
        SiteActivityLogID = in.readInt();
        Length = in.readFloat();
        Width = in.readFloat();
        DepthB = in.readFloat();
        DepthT = in.readFloat();
        MaterialTypeID = in.readInt();
        MaterialTypeName = in.readString();
        SurfaceTypeID = in.readInt();
        SurfaceTypeName = in.readString();
        DateCreated = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(JobID);
        dest.writeInt(SiteActivityLogID);
        dest.writeFloat(Length);
        dest.writeFloat(Width);
        dest.writeFloat(DepthB);
        dest.writeFloat(DepthT);
        dest.writeInt(MaterialTypeID);
        dest.writeString(MaterialTypeName);
        dest.writeInt(SurfaceTypeID);
        dest.writeString(SurfaceTypeName);
        dest.writeString(DateCreated);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BackfillMeasures> CREATOR = new Creator<BackfillMeasures>() {
        @Override
        public BackfillMeasures createFromParcel(Parcel in) {
            return new BackfillMeasures(in);
        }

        @Override
        public BackfillMeasures[] newArray(int size) {
            return new BackfillMeasures[size];
        }
    };

    public int getJobID() {
        return JobID;
    }

    public void setJobID(int jobID) {
        JobID = jobID;
    }

    public int getSiteActivityLogID() {
        return SiteActivityLogID;
    }

    public void setSiteActivityLogID(int siteActivityLogID) {
        SiteActivityLogID = siteActivityLogID;
    }

    public float getLength() {
        return Length;
    }

    public void setLength(float length) {
        Length = length;
    }

    public float getWidth() {
        return Width;
    }

    public void setWidth(float width) {
        Width = width;
    }

    public float getDepthB() {
        return DepthB;
    }

    public void setDepthB(float depthB) {
        DepthB = depthB;
    }

    public float getDepthT() {
        return DepthT;
    }

    public void setDepthT(float depthT) {
        DepthT = depthT;
    }

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

    public int getSurfaceTypeID() {
        return SurfaceTypeID;
    }

    public void setSurfaceTypeID(int surfaceTypeID) {
        SurfaceTypeID = surfaceTypeID;
    }

    public String getSurfaceTypeName() {
        return SurfaceTypeName;
    }

    public void setSurfaceTypeName(String surfaceTypeName) {
        SurfaceTypeName = surfaceTypeName;
    }

    public String getDateCreated() {
        return DateCreated;
    }

    public void setDateCreated(String dateCreated) {
        DateCreated = dateCreated;
    }
}