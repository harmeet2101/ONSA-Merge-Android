package co.uk.depotnet.onsa.modals.httpresponses;

import android.os.Parcel;
import android.os.Parcelable;

public class DigMeasures implements Parcelable {

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

//    ************************8
    private int MeasureID;
    private int NoticeID;
    private String NoticeRef;



    protected DigMeasures(Parcel in) {
        NoticeID = in.readInt();
        SurfaceTypeName = in.readString();
        NoticeRef = in.readString();
        DateCreated = in.readString();
        SurfaceTypeID = in.readInt();
        MeasureID = in.readInt();
        DepthB = in.readFloat();
        Length = in.readFloat();
        MaterialTypeName = in.readString();
        DepthT = in.readFloat();
        Width = in.readFloat();
        JobID = in.readInt();
        MaterialTypeID = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(NoticeID);
        dest.writeString(SurfaceTypeName);
        dest.writeString(NoticeRef);
        dest.writeString(DateCreated);
        dest.writeInt(SurfaceTypeID);
        dest.writeInt(MeasureID);
        dest.writeFloat(DepthB);
        dest.writeFloat(Length);
        dest.writeString(MaterialTypeName);
        dest.writeFloat(DepthT);
        dest.writeFloat(Width);
        dest.writeInt(JobID);
        dest.writeInt(MaterialTypeID);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DigMeasures> CREATOR = new Creator<DigMeasures>() {
        @Override
        public DigMeasures createFromParcel(Parcel in) {
            return new DigMeasures(in);
        }

        @Override
        public DigMeasures[] newArray(int size) {
            return new DigMeasures[size];
        }
    };

    public int getNoticeID() {
        return NoticeID;
    }

    public void setNoticeID(int noticeID) {
        NoticeID = noticeID;
    }

    public String getSurfaceTypeName() {
        return SurfaceTypeName;
    }

    public void setSurfaceTypeName(String surfaceTypeName) {
        SurfaceTypeName = surfaceTypeName;
    }

    public String getNoticeRef() {
        return NoticeRef;
    }

    public void setNoticeRef(String noticeRef) {
        NoticeRef = noticeRef;
    }

    public String getDateCreated() {
        return DateCreated;
    }

    public void setDateCreated(String dateCreated) {
        DateCreated = dateCreated;
    }

    public int getSurfaceTypeID() {
        return SurfaceTypeID;
    }

    public void setSurfaceTypeID(int surfaceTypeID) {
        SurfaceTypeID = surfaceTypeID;
    }

    public int getMeasureID() {
        return MeasureID;
    }

    public void setMeasureID(int measureID) {
        MeasureID = measureID;
    }

    public float getDepthB() {
        return DepthB;
    }

    public void setDepthB(float depthB) {
        DepthB = depthB;
    }

    public float getLength() {
        return Length;
    }

    public void setLength(float length) {
        Length = length;
    }

    public String getMaterialTypeName() {
        return MaterialTypeName;
    }

    public void setMaterialTypeName(String materialTypeName) {
        MaterialTypeName = materialTypeName;
    }

    public float getDepthT() {
        return DepthT;
    }

    public void setDepthT(float depthT) {
        DepthT = depthT;
    }

    public float getWidth() {
        return Width;
    }

    public void setWidth(float width) {
        Width = width;
    }

    public int getJobID() {
        return JobID;
    }

    public void setJobID(int jobID) {
        JobID = jobID;
    }

    public int getMaterialTypeID() {
        return MaterialTypeID;
    }

    public void setMaterialTypeID(int materialTypeID) {
        MaterialTypeID = materialTypeID;
    }
}