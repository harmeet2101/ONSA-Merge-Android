package co.uk.depotnet.onsa.modals.httpresponses;

import android.os.Parcel;
import android.os.Parcelable;

public class    BaseTask implements Parcelable {

    private int SiteActivityTaskID;
    private int JobID;
    private int MeasureID;
    private int SurfaceTypeID;
    private String SurfaceTypeName;

    private int MaterialTypeID;
    private String MaterialTypeName;

    private float Width;
    private float Length;
    private float DepthB;
    private float DepthT;

    private String DateCreated;

    private int SiteActivityLogID;

    private Integer NoticeID;
    private String NoticeRef;

    private boolean IsPermanent;

    private boolean isSelectable;
    private boolean isSelected;

    private String Comments;

    protected BaseTask(Parcel in) {
        SiteActivityTaskID = in.readInt();
        JobID = in.readInt();
        MeasureID = in.readInt();
        SurfaceTypeID = in.readInt();
        SurfaceTypeName = in.readString();
        MaterialTypeID = in.readInt();
        MaterialTypeName = in.readString();
        Width = in.readFloat();
        Length = in.readFloat();
        DepthB = in.readFloat();
        DepthT = in.readFloat();
        DateCreated = in.readString();
        SiteActivityLogID = in.readInt();
        if (in.readByte() == 0) {
            NoticeID = null;
        } else {
            NoticeID = in.readInt();
        }
        NoticeRef = in.readString();
        IsPermanent = in.readByte() != 0;
        isSelectable = in.readByte() != 0;
        isSelected = in.readByte() != 0;
        Comments = in.readString();
    }

    public static final Creator<BaseTask> CREATOR = new Creator<BaseTask>() {
        @Override
        public BaseTask createFromParcel(Parcel in) {
            return new BaseTask(in);
        }

        @Override
        public BaseTask[] newArray(int size) {
            return new BaseTask[size];
        }
    };

    public int getJobID() {
        return JobID;
    }

    public void setJobID(int jobID) {
        JobID = jobID;
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

    public float getWidth() {
        return Width;
    }

    public void setWidth(float width) {
        Width = width;
    }

    public float getLength() {
        return Length;
    }

    public void setLength(float length) {
        Length = length;
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

    public String getDateCreated() {
        return DateCreated;
    }

    public void setDateCreated(String dateCreated) {
        DateCreated = dateCreated;
    }

    public int getSiteActivityLogID() {
        return SiteActivityLogID;
    }

    public void setSiteActivityLogID(int siteActivityLogID) {
        SiteActivityLogID = siteActivityLogID;
    }

    public Integer getNoticeID() {
        return NoticeID;
    }

    public void setNoticeID(Integer noticeID) {
        NoticeID = noticeID;
    }

    public String getNoticeRef() {
        return NoticeRef;
    }

    public void setNoticeRef(String noticeRef) {
        NoticeRef = noticeRef;
    }

    public boolean isPermanent() {
        return IsPermanent;
    }

    public void setPermanent(boolean permanent) {
        IsPermanent = permanent;
    }

    public boolean isSelectable() {
        return isSelectable;
    }

    public void setSelectable(boolean selectable) {
        isSelectable = selectable;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getMeasureID() {
        return MeasureID;
    }

    public void setMeasureID(int measureID) {
        MeasureID = measureID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(SiteActivityTaskID);
        parcel.writeInt(JobID);
        parcel.writeInt(MeasureID);
        parcel.writeInt(SurfaceTypeID);
        parcel.writeString(SurfaceTypeName);
        parcel.writeInt(MaterialTypeID);
        parcel.writeString(MaterialTypeName);
        parcel.writeFloat(Width);
        parcel.writeFloat(Length);
        parcel.writeFloat(DepthB);
        parcel.writeFloat(DepthT);
        parcel.writeString(DateCreated);
        parcel.writeInt(SiteActivityLogID);
        if (NoticeID == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(NoticeID);
        }
        parcel.writeString(NoticeRef);
        parcel.writeByte((byte) (IsPermanent ? 1 : 0));
        parcel.writeByte((byte) (isSelectable ? 1 : 0));
        parcel.writeByte((byte) (isSelected ? 1 : 0));
        parcel.writeString(Comments);
    }

    public int getSiteActivityTaskID() {
        return SiteActivityTaskID;
    }

    public void setSiteActivityTaskID(int siteActivityTaskID) {
        SiteActivityTaskID = siteActivityTaskID;
    }

    public String getComments() {
        return Comments;
    }

    public void setComments(String comments) {
        Comments = comments;
    }
}
