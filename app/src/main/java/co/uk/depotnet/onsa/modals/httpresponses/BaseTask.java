package co.uk.depotnet.onsa.modals.httpresponses;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import co.uk.depotnet.onsa.modals.forms.Answer;

public class BaseTask implements Parcelable {

    private String siteActivityTaskId;
    private String dateCreated;
    private int siteActivityTypeId;
    private String siteActivityTypeName;
    private String createdByGangId;
    private String createdByGangName;
    private String createdByUserId;
    private String createdByUserName;
    private String completedByGangId;
    private String completedByGangName;
    private String completedByUserId;
    private String completedByUserName;
    private String jobId;
    private String materialTypeId;
    private String materialTypeName;
    private String surfaceTypeId;
    private String surfaceTypeName;
    private float length;
    private float width;
    private float depth;
    private String jobEtonSiteId;
    private String noticePermitRef;
    private String comments;
    private float cones;
    private float barriers;
    private float chpt8;
    private float fwBoards;
    private float bags;
    private float sand;
    private float stone;
    private boolean isSelectable;
    private boolean isSelected;

    public BaseTask(Cursor cursor){
        siteActivityTaskId = cursor.getString(cursor.getColumnIndex(DBTable.siteActivityTaskId));
        dateCreated = cursor.getString(cursor.getColumnIndex(DBTable.dateCreated));
        siteActivityTypeId = cursor.getInt(cursor.getColumnIndex(DBTable.siteActivityTypeId));
        siteActivityTypeName = cursor.getString(cursor.getColumnIndex(DBTable.siteActivityTypeName));
        createdByGangId = cursor.getString(cursor.getColumnIndex(DBTable.createdByGangId));
        createdByGangName = cursor.getString(cursor.getColumnIndex(DBTable.createdByGangName));
        createdByUserId = cursor.getString(cursor.getColumnIndex(DBTable.createdByUserId));
        createdByUserName = cursor.getString(cursor.getColumnIndex(DBTable.createdByUserName));
        completedByGangId = cursor.getString(cursor.getColumnIndex(DBTable.completedByGangId));
        completedByGangName = cursor.getString(cursor.getColumnIndex(DBTable.completedByGangName));
        completedByUserId = cursor.getString(cursor.getColumnIndex(DBTable.completedByUserId));
        completedByUserName = cursor.getString(cursor.getColumnIndex(DBTable.completedByUserName));
        jobId = cursor.getString(cursor.getColumnIndex(DBTable.jobId));
        materialTypeId = cursor.getString(cursor.getColumnIndex(DBTable.materialTypeId));
        materialTypeName = cursor.getString(cursor.getColumnIndex(DBTable.materialTypeName));
        surfaceTypeId = cursor.getString(cursor.getColumnIndex(DBTable.surfaceTypeId));
        surfaceTypeName = cursor.getString(cursor.getColumnIndex(DBTable.surfaceTypeName));
        length = cursor.getFloat(cursor.getColumnIndex(DBTable.length));
        width = cursor.getFloat(cursor.getColumnIndex(DBTable.width));
        depth = cursor.getFloat(cursor.getColumnIndex(DBTable.depth));
        jobEtonSiteId = cursor.getString(cursor.getColumnIndex(DBTable.jobEtonSiteId));
        noticePermitRef = cursor.getString(cursor.getColumnIndex(DBTable.noticePermitRef));
        comments = cursor.getString(cursor.getColumnIndex(DBTable.comments));
        cones = cursor.getFloat(cursor.getColumnIndex(DBTable.cones));
        barriers = cursor.getFloat(cursor.getColumnIndex(DBTable.barriers));
        chpt8 = cursor.getFloat(cursor.getColumnIndex(DBTable.chpt8));
        fwBoards = cursor.getFloat(cursor.getColumnIndex(DBTable.fwBoards));
        bags = cursor.getFloat(cursor.getColumnIndex(DBTable.bags));
        sand = cursor.getFloat(cursor.getColumnIndex(DBTable.sand));
        stone = cursor.getFloat(cursor.getColumnIndex(DBTable.stone));
    }

    public static class DBTable{
        public static final String NAME = "BaseTask";
        public static final String siteActivityTaskId = "siteActivityTaskId";
        public static final String dateCreated = "dateCreated";
        public static final String siteActivityTypeId = "siteActivityTypeId";
        public static final String siteActivityTypeName = "siteActivityTypeName";
        public static final String createdByGangId = "createdByGangId";
        public static final String createdByGangName = "createdByGangName";
        public static final String createdByUserId = "createdByUserId";
        public static final String createdByUserName = "createdByUserName";
        public static final String completedByGangId = "completedByGangId";
        public static final String completedByGangName = "completedByGangName";
        public static final String completedByUserId = "completedByUserId";
        public static final String completedByUserName = "completedByUserName";
        public static final String jobId = "jobId";
        public static final String materialTypeId = "materialTypeId";
        public static final String materialTypeName = "materialTypeName";
        public static final String surfaceTypeId = "surfaceTypeId";
        public static final String surfaceTypeName = "surfaceTypeName";
        public static final String length = "length";
        public static final String width = "width";
        public static final String depth = "depth";
        public static final String jobEtonSiteId = "jobEtonSiteId";
        public static final String noticePermitRef = "noticePermitRef";
        public static final String comments = "comments";
        public static final String cones = "cones";
        public static final String barriers = "barriers";
        public static final String chpt8 = "chpt8";
        public static final String fwBoards = "fwBoards";
        public static final String bags = "bags";
        public static final String sand = "sand";
        public static final String stone = "stone";
    }

    public ContentValues toContentValues(){
        ContentValues cv = new ContentValues();
        cv.put(DBTable.siteActivityTaskId , siteActivityTaskId);
        cv.put(DBTable.dateCreated , dateCreated);
        cv.put(DBTable.siteActivityTypeId , siteActivityTypeId);
        cv.put(DBTable.siteActivityTypeName , siteActivityTypeName);
        cv.put(DBTable.createdByGangId , createdByGangId);
        cv.put(DBTable.createdByGangName , createdByGangName);
        cv.put(DBTable.createdByUserId , createdByUserId);
        cv.put(DBTable.createdByUserName , createdByUserName);
        cv.put(DBTable.completedByGangId , completedByGangId);
        cv.put(DBTable.completedByGangName , completedByGangName);
        cv.put(DBTable.completedByUserId , completedByUserId);
        cv.put(DBTable.completedByUserName , completedByUserName);
        cv.put(DBTable.jobId , jobId);
        cv.put(DBTable.materialTypeId , materialTypeId);
        cv.put(DBTable.materialTypeName , materialTypeName);
        cv.put(DBTable.surfaceTypeId , surfaceTypeId);
        cv.put(DBTable.surfaceTypeName , surfaceTypeName);
        cv.put(DBTable.length , length);
        cv.put(DBTable.width , width);
        cv.put(DBTable.depth , depth);
        cv.put(DBTable.jobEtonSiteId , jobEtonSiteId);
        cv.put(DBTable.noticePermitRef , noticePermitRef);
        cv.put(DBTable.comments , comments);
        cv.put(DBTable.cones , cones);
        cv.put(DBTable.barriers , barriers);
        cv.put(DBTable.chpt8 , chpt8);
        cv.put(DBTable.fwBoards , fwBoards);
        cv.put(DBTable.bags , bags);
        cv.put(DBTable.sand , sand);
        cv.put(DBTable.stone , stone);
        return cv;
    }


    protected BaseTask(Parcel in) {
        siteActivityTaskId = in.readString();
        dateCreated = in.readString();
        siteActivityTypeId = in.readInt();
        siteActivityTypeName = in.readString();
        createdByGangId = in.readString();
        createdByGangName = in.readString();
        createdByUserId = in.readString();
        createdByUserName = in.readString();
        completedByGangId = in.readString();
        completedByGangName = in.readString();
        completedByUserId = in.readString();
        completedByUserName = in.readString();
        jobId = in.readString();
        materialTypeId = in.readString();
        materialTypeName = in.readString();
        surfaceTypeId = in.readString();
        surfaceTypeName = in.readString();
        length = in.readFloat();
        width = in.readFloat();
        depth = in.readFloat();
        jobEtonSiteId = in.readString();
        noticePermitRef = in.readString();
        comments = in.readString();
        cones = in.readFloat();
        barriers = in.readFloat();
        chpt8 = in.readFloat();
        fwBoards = in.readFloat();
        bags = in.readFloat();
        sand = in.readFloat();
        stone = in.readFloat();
        isSelectable = in.readByte() != 0;
        isSelected = in.readByte() != 0;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(siteActivityTaskId);
        parcel.writeString(dateCreated);
        parcel.writeInt(siteActivityTypeId);
        parcel.writeString(siteActivityTypeName);
        parcel.writeString(createdByGangId);
        parcel.writeString(createdByGangName);
        parcel.writeString(createdByUserId);
        parcel.writeString(createdByUserName);
        parcel.writeString(completedByGangId);
        parcel.writeString(completedByGangName);
        parcel.writeString(completedByUserId);
        parcel.writeString(completedByUserName);
        parcel.writeString(jobId);
        parcel.writeString(materialTypeId);
        parcel.writeString(materialTypeName);
        parcel.writeString(surfaceTypeId);
        parcel.writeString(surfaceTypeName);
        parcel.writeFloat(length);
        parcel.writeFloat(width);
        parcel.writeFloat(depth);
        parcel.writeString(jobEtonSiteId);
        parcel.writeString(noticePermitRef);
        parcel.writeString(comments);
        parcel.writeFloat(cones);
        parcel.writeFloat(barriers);
        parcel.writeFloat(chpt8);
        parcel.writeFloat(fwBoards);
        parcel.writeFloat(bags);
        parcel.writeFloat(sand);
        parcel.writeFloat(stone);
        parcel.writeByte((byte) (isSelectable ? 1 : 0));
        parcel.writeByte((byte) (isSelected ? 1 : 0));
    }

    public String getSiteActivityTaskId() {
        return siteActivityTaskId;
    }

    public void setSiteActivityTaskId(String siteActivityTaskId) {
        this.siteActivityTaskId = siteActivityTaskId;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Integer getSiteActivityTypeId() {
        return siteActivityTypeId;
    }

    public void setSiteActivityTypeId(Integer siteActivityTypeId) {
        this.siteActivityTypeId = siteActivityTypeId;
    }

    public String getSiteActivityTypeName() {
        return siteActivityTypeName;
    }

    public void setSiteActivityTypeName(String siteActivityTypeName) {
        this.siteActivityTypeName = siteActivityTypeName;
    }

    public String getCreatedByGangId() {
        return createdByGangId;
    }

    public void setCreatedByGangId(String createdByGangId) {
        this.createdByGangId = createdByGangId;
    }

    public String getCreatedByGangName() {
        return createdByGangName;
    }

    public void setCreatedByGangName(String createdByGangName) {
        this.createdByGangName = createdByGangName;
    }

    public String getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(String createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public String getCreatedByUserName() {
        return createdByUserName;
    }

    public void setCreatedByUserName(String createdByUserName) {
        this.createdByUserName = createdByUserName;
    }

    public String getCompletedByGangId() {
        return completedByGangId;
    }

    public void setCompletedByGangId(String completedByGangId) {
        this.completedByGangId = completedByGangId;
    }

    public String getCompletedByGangName() {
        return completedByGangName;
    }

    public void setCompletedByGangName(String completedByGangName) {
        this.completedByGangName = completedByGangName;
    }

    public String getCompletedByUserId() {
        return completedByUserId;
    }

    public void setCompletedByUserId(String completedByUserId) {
        this.completedByUserId = completedByUserId;
    }

    public String getCompletedByUserName() {
        return completedByUserName;
    }

    public void setCompletedByUserName(String completedByUserName) {
        this.completedByUserName = completedByUserName;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getMaterialTypeId() {
        return materialTypeId;
    }

    public void setMaterialTypeId(String materialTypeId) {
        this.materialTypeId = materialTypeId;
    }

    public String getMaterialTypeName() {
        return materialTypeName;
    }

    public void setMaterialTypeName(String materialTypeName) {
        this.materialTypeName = materialTypeName;
    }

    public String getSurfaceTypeId() {
        return surfaceTypeId;
    }

    public void setSurfaceTypeId(String surfaceTypeId) {
        this.surfaceTypeId = surfaceTypeId;
    }

    public String getSurfaceTypeName() {
        return surfaceTypeName;
    }

    public void setSurfaceTypeName(String surfaceTypeName) {
        this.surfaceTypeName = surfaceTypeName;
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getDepth() {
        return depth;
    }

    public void setDepth(float depth) {
        this.depth = depth;
    }

    public String getJobEtonSiteId() {
        return jobEtonSiteId;
    }

    public void setJobEtonSiteId(String jobEtonSiteId) {
        this.jobEtonSiteId = jobEtonSiteId;
    }

    public String getNoticePermitRef() {
        return noticePermitRef;
    }

    public void setNoticePermitRef(String noticePermitRef) {
        this.noticePermitRef = noticePermitRef;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public float getCones() {
        return cones;
    }

    public void setCones(float cones) {
        this.cones = cones;
    }

    public float getBarriers() {
        return barriers;
    }

    public void setBarriers(float barriers) {
        this.barriers = barriers;
    }

    public float getChpt8() {
        return chpt8;
    }

    public void setChpt8(float chpt8) {
        this.chpt8 = chpt8;
    }

    public float getFwBoards() {
        return fwBoards;
    }

    public void setFwBoards(float fwBoards) {
        this.fwBoards = fwBoards;
    }

    public float getBags() {
        return bags;
    }

    public void setBags(float bags) {
        this.bags = bags;
    }

    public float getSand() {
        return sand;
    }

    public void setSand(float sand) {
        this.sand = sand;
    }

    public float getStone() {
        return stone;
    }

    public void setStone(float stone) {
        this.stone = stone;
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

    public Answer setAnswer(Answer answer){
        String uploadId = answer.getUploadID();
        if (uploadId.equalsIgnoreCase("SurfaceTypeId")) {
            answer.setAnswer(String.valueOf(getSurfaceTypeId()));
            answer.setDisplayAnswer(getSurfaceTypeName());
        } else if (uploadId.equalsIgnoreCase("MaterialTypeId")) {
            answer.setAnswer(String.valueOf(getMaterialTypeId()));
            answer.setDisplayAnswer(getMaterialTypeName());
        } else if (uploadId.equalsIgnoreCase("Length")) {
            answer.setAnswer(String.valueOf(getLength()));
            answer.setDisplayAnswer(String.valueOf(getLength()));
        } else if (uploadId.equalsIgnoreCase("Width")) {
            answer.setAnswer(String.valueOf(getWidth()));
            answer.setDisplayAnswer(String.valueOf(getWidth()));
        } else if (uploadId.equalsIgnoreCase("Depth")) {
            answer.setAnswer(String.valueOf(getDepth()));
            answer.setDisplayAnswer(String.valueOf(getDepth()));
        }else if (uploadId.equalsIgnoreCase("cones")) {
            answer.setAnswer(String.valueOf(getCones()));
            answer.setDisplayAnswer(String.valueOf(getCones()));
        }else if (uploadId.equalsIgnoreCase("barriers")) {
            answer.setAnswer(String.valueOf(getBarriers()));
            answer.setDisplayAnswer(String.valueOf(getBarriers()));
        }else if (uploadId.equalsIgnoreCase("chpt8")) {
            answer.setAnswer(String.valueOf(getChpt8()));
            answer.setDisplayAnswer(String.valueOf(getChpt8()));
        }else if (uploadId.equalsIgnoreCase("fwBoards")) {
            answer.setAnswer(String.valueOf(getFwBoards()));
            answer.setDisplayAnswer(String.valueOf(getFwBoards()));
        }else if (uploadId.equalsIgnoreCase("bags")) {
            answer.setAnswer(String.valueOf(getBags()));
            answer.setDisplayAnswer(String.valueOf(getBags()));
        }else if (uploadId.equalsIgnoreCase("sand")) {
            answer.setAnswer(String.valueOf(getSand()));
            answer.setDisplayAnswer(String.valueOf(getSand()));
        }else if (uploadId.equalsIgnoreCase("stone")) {
            answer.setAnswer(String.valueOf(getStone()));
            answer.setDisplayAnswer(String.valueOf(getStone()));
        }
        return answer;
    }
}

