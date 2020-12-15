package co.uk.depotnet.onsa.modals.schedule;


import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Schedule implements Parcelable {
    @SerializedName("scheduledInspectionId")
    @Expose
    private String id;
    @SerializedName("dateCompleted")
    @Expose
    private String dateCompleted;
    @SerializedName("dueDate")
    @Expose
    private String dueDate;
    @SerializedName("scheduledInspectionStatus")
    @Expose
    private Integer scheduledInspectionStatus;
    @SerializedName("inspectionTemplateId")
    @Expose
    private String inspectionTemplateId;
    @SerializedName("inspectionTemplateName")
    @Expose
    private String inspectionTemplateName;
    @SerializedName("inspectionTemplateVersionId")
    @Expose
    private String inspectionTemplateVersionId;
    /*@SerializedName("jobRef")
    @Expose
    private String jobRef;*/
    @SerializedName("jobEstimateNumber")
    @Expose
    private String jobEstimateNumber;
    @SerializedName("jobGangId")
    @Expose
    private String jobGangId;
    public Schedule() {
    }

    protected Schedule(Parcel in) {
        id = in.readString();
        dateCompleted = in.readString();
        dueDate = in.readString();
        if (in.readByte() == 0) {
            scheduledInspectionStatus = null;
        } else {
            scheduledInspectionStatus = in.readInt();
        }
        inspectionTemplateId = in.readString();
        inspectionTemplateName = in.readString();
        inspectionTemplateVersionId = in.readString();
        //jobRef = in.readString();
        jobEstimateNumber = in.readString();
        jobGangId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(dateCompleted);
        dest.writeString(dueDate);
        if (scheduledInspectionStatus == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(scheduledInspectionStatus);
        }
        dest.writeString(inspectionTemplateId);
        dest.writeString(inspectionTemplateName);
        dest.writeString(inspectionTemplateVersionId);
        //dest.writeString(jobRef);
        dest.writeString(jobEstimateNumber);
        dest.writeString(jobGangId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Schedule> CREATOR = new Creator<Schedule>() {
        @Override
        public Schedule createFromParcel(Parcel in) {
            return new Schedule(in);
        }

        @Override
        public Schedule[] newArray(int size) {
            return new Schedule[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(String dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public Integer getScheduledInspectionStatus() {
        return scheduledInspectionStatus;
    }

    public void setScheduledInspectionStatus(Integer scheduledInspectionStatus) {
        this.scheduledInspectionStatus = scheduledInspectionStatus;
    }

    public String getInspectionTemplateId() {
        return inspectionTemplateId;
    }

    public void setInspectionTemplateId(String inspectionTemplateId) {
        this.inspectionTemplateId = inspectionTemplateId;
    }

    public String getInspectionTemplateName() {
        return inspectionTemplateName;
    }

    public void setInspectionTemplateName(String inspectionTemplateName) {
        this.inspectionTemplateName = inspectionTemplateName;
    }

    public String getInspectionTemplateVersionId() {
        return inspectionTemplateVersionId;
    }

    public void setInspectionTemplateVersionId(String inspectionTemplateVersionId) {
        this.inspectionTemplateVersionId = inspectionTemplateVersionId;
    }

    public String getJobEstimateNumber() {
        return jobEstimateNumber;
    }

    public void setJobEstimateNumber(String jobEstimateNumber) {
        this.jobEstimateNumber = jobEstimateNumber;
    }

    public String getJobGangId() {
        return jobGangId;
    }

    public void setJobGangId(String jobGangId) {
        this.jobGangId = jobGangId;
    }

    public static Creator<Schedule> getCREATOR() {
        return CREATOR;
    }
    public Schedule(Cursor cursor) {
        if (cursor != null && !cursor.isBeforeFirst() && !cursor.isAfterLast()) {
            id = cursor.getString(cursor.getColumnIndex(Schedule.DBTable.id));
            dateCompleted = cursor.getString(cursor.getColumnIndex(Schedule.DBTable.dateCompleted));
            dueDate = cursor.getString(cursor.getColumnIndex(Schedule.DBTable.dueDate));
            scheduledInspectionStatus = cursor.getInt(cursor.getColumnIndex(Schedule.DBTable.scheduledInspectionStatus));
            inspectionTemplateId = cursor.getString(cursor.getColumnIndex(Schedule.DBTable.inspectionTemplateId));
            inspectionTemplateName = cursor.getString(cursor.getColumnIndex(Schedule.DBTable.inspectionTemplateName));
            inspectionTemplateVersionId = cursor.getString(cursor.getColumnIndex(Schedule.DBTable.inspectionTemplateVersionId));
            //jobRef = cursor.getString(cursor.getColumnIndex(Schedule.DBTable.jobRef));
            jobEstimateNumber = cursor.getString(cursor.getColumnIndex(Schedule.DBTable.jobEstimateNumber));
            jobGangId = cursor.getString(cursor.getColumnIndex(Schedule.DBTable.jobGangId));
        }
    }
    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        if (id!=null) {
            cv.put(Schedule.DBTable.id, id);
        }
        cv.put(DBTable.dateCompleted, dateCompleted);
        cv.put(DBTable.dueDate, dueDate);
        cv.put(DBTable.scheduledInspectionStatus, scheduledInspectionStatus);
        cv.put(DBTable.inspectionTemplateId, inspectionTemplateId);
        cv.put(DBTable.inspectionTemplateName, inspectionTemplateName);
        cv.put(DBTable.inspectionTemplateVersionId, inspectionTemplateVersionId);
        //cv.put(DBTable.jobRef, jobRef);
        cv.put(DBTable.jobEstimateNumber, jobEstimateNumber);
        cv.put(DBTable.jobGangId, jobGangId);

        return cv;
    }
    public static class DBTable {
        public static final String NAME = "Schedule";
        public static final String id = "id";
        public static final String dateCompleted = "dateCompleted";
        public static final String dueDate = "dueDate";
        public static final String scheduledInspectionStatus = "scheduledInspectionStatus";
        public static final String inspectionTemplateId = "inspectionTemplateId";
        public static final String inspectionTemplateName = "inspectionTemplateName";
        public static final String inspectionTemplateVersionId = "inspectionTemplateVersionId";
        //public static final String jobRef = "jobRef";
        public static final String jobEstimateNumber = "jobEstimateNumber";
        public static final String jobGangId = "jobGangId";
    }
}
