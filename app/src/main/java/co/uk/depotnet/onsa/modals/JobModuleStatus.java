package co.uk.depotnet.onsa.modals;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

public class JobModuleStatus implements Parcelable {

    private long submissionId;
    private String JobId;
    private String ModuleName;
    private boolean Status;
    private String selectedDate;


    protected JobModuleStatus(Parcel in) {
        submissionId = in.readLong();
        JobId = in.readString();
        ModuleName = in.readString();
        Status = in.readByte() == 1;
        selectedDate = in.readString();
    }

    public static final Creator<JobModuleStatus> CREATOR = new Creator<JobModuleStatus>() {
        @Override
        public JobModuleStatus createFromParcel(Parcel in) {
            return new JobModuleStatus(in);
        }

        @Override
        public JobModuleStatus[] newArray(int size) {
            return new JobModuleStatus[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(submissionId);
        parcel.writeString(JobId);
        parcel.writeString(ModuleName);
        parcel.writeByte((byte) (Status? 1:0));
        parcel.writeString(selectedDate);
    }

    public static class DBTable{
        public static final String NAME = "JobModuleStatus";
        public static final String submissionId = "submissionId";
        public static final String JobId = "JobId";
        public static final String ModuleName = "ModuleName";
        public static final String Status = "Status";
        public static final String selectedDate = "selectedDate";
    }

    public String getJobId() {
        return JobId;
    }

    public void setJobId(String jobId) {
        JobId = jobId;
    }

    public String getModuleName() {
        return ModuleName;
    }

    public void setModuleName(String moduleName) {
        ModuleName = moduleName;
    }

    public boolean isStatus() {
        return Status;
    }

    public void setStatus(boolean status) {
        Status = status;
    }

    public String getSelectedDate() {
        return selectedDate;
    }


    public void setSubmissionId(long submissionId) {
        this.submissionId = submissionId;
    }

    public long getSubmissionId() {
        return submissionId;
    }

    public ContentValues toContentValues(){
        ContentValues cv = new ContentValues();
        cv.put(DBTable.submissionId, this.submissionId);
        cv.put(DBTable.JobId, this.JobId);
        cv.put(DBTable.ModuleName, this.ModuleName);
        cv.put(DBTable.Status, this.Status);
        cv.put(DBTable.selectedDate, this.selectedDate);
        return cv;
    }

    public JobModuleStatus(Cursor cursor) {
        submissionId = cursor.getLong(cursor.getColumnIndex(DBTable.submissionId));
        JobId = cursor.getString(cursor.getColumnIndex(DBTable.JobId));
        ModuleName = cursor.getString(cursor.getColumnIndex(DBTable.ModuleName));
        Status = cursor.getInt(cursor.getColumnIndex(DBTable.Status)) ==1;
        selectedDate = cursor.getString(cursor.getColumnIndex(DBTable.selectedDate));
    }

    public JobModuleStatus(){

    }


    @Override
    public String toString() {
        return JobId+" m = "+ModuleName+" "+Status;
    }
}
