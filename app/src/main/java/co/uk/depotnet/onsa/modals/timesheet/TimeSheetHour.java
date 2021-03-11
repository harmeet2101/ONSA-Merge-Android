package co.uk.depotnet.onsa.modals.timesheet;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.forms.Answer;

public class TimeSheetHour implements Parcelable{

    private String hoursId;
    private String timesheetHoursId;
    private String timeTypeActivityId;
    private String jobId;
    private String estimate;
    private int normalTimeMinutes;
    private int overtimeMinutes;
    private String day;
    private String timeTypeName;
    private String dateWorked;
    private String weekCommencing;
    private boolean isApproved;
    private boolean isWaitingApproval;
    private boolean isEdited;

    public static class DBTable{
        public static final String NAME = "TimeSheetHour";
        public static final String hoursId = "hoursId";
        public static final String timesheetHoursId = "timesheetHoursId";
        public static final String timeTypeActivityId = "timeTypeActivityId";
        public static final String jobId = "jobId";
        public static final String estimate = "estimate";
        public static final String normalTimeMinutes = "normalTimeMinutes";
        public static final String overtimeMinutes = "overtimeMinutes";
        public static final String day = "day";
        public static final String timeTypeName = "timeTypeName";
        public static final String dateWorked = "dateWorked";
        public static final String weekCommencing = "weekCommencing";
        public static final String isApproved = "isApproved";
        public static final String isWaitingApproval = "isWaitingApproval";
        public static final String isEdited = "isEdited";
    }

    public TimeSheetHour(Cursor c) {
        hoursId = c.getString(c.getColumnIndex(DBTable.hoursId));
        timesheetHoursId = c.getString(c.getColumnIndex(DBTable.timesheetHoursId));
        timeTypeActivityId = c.getString(c.getColumnIndex(DBTable.timeTypeActivityId));
        jobId = c.getString(c.getColumnIndex(DBTable.jobId));
        estimate = c.getString(c.getColumnIndex(DBTable.estimate));
        normalTimeMinutes = c.getInt(c.getColumnIndex(DBTable.normalTimeMinutes));
        overtimeMinutes = c.getInt(c.getColumnIndex(DBTable.overtimeMinutes));
        day = c.getString(c.getColumnIndex(DBTable.day));
        timeTypeName = c.getString(c.getColumnIndex(DBTable.timeTypeName));
        dateWorked = c.getString(c.getColumnIndex(DBTable.dateWorked));
        weekCommencing = c.getString(c.getColumnIndex(DBTable.weekCommencing));
        isApproved = c.getInt(c.getColumnIndex(DBTable.isApproved))==1;
        isWaitingApproval = c.getInt(c.getColumnIndex(DBTable.isWaitingApproval)) == 1;
        isEdited = c.getInt(c.getColumnIndex(DBTable.isEdited)) == 1;
    }


    protected TimeSheetHour(Parcel in) {
        hoursId = in.readString();
        timesheetHoursId = in.readString();
        timeTypeActivityId = in.readString();
        jobId = in.readString();
        estimate = in.readString();
        normalTimeMinutes = in.readInt();
        overtimeMinutes = in.readInt();
        day = in.readString();
        timeTypeName = in.readString();
        dateWorked = in.readString();
        weekCommencing = in.readString();
        isApproved = in.readByte() != 0;
        isWaitingApproval = in.readByte() != 0;
        isEdited = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(hoursId);
        dest.writeString(timesheetHoursId);
        dest.writeString(timeTypeActivityId);
        dest.writeString(jobId);
        dest.writeString(estimate);
        dest.writeInt(normalTimeMinutes);
        dest.writeInt(overtimeMinutes);
        dest.writeString(day);
        dest.writeString(timeTypeName);
        dest.writeString(dateWorked);
        dest.writeString(weekCommencing);
        dest.writeByte((byte) (isApproved ? 1 : 0));
        dest.writeByte((byte) (isWaitingApproval ? 1 : 0));
        dest.writeByte((byte) (isEdited ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TimeSheetHour> CREATOR = new Creator<TimeSheetHour>() {
        @Override
        public TimeSheetHour createFromParcel(Parcel in) {
            return new TimeSheetHour(in);
        }

        @Override
        public TimeSheetHour[] newArray(int size) {
            return new TimeSheetHour[size];
        }
    };

    public ContentValues toContentValues(){
        ContentValues cv = new ContentValues();
        cv.put(DBTable.hoursId , hoursId);
        cv.put(DBTable.timesheetHoursId , timesheetHoursId);
        cv.put(DBTable.timeTypeActivityId , timeTypeActivityId);
        cv.put(DBTable.jobId , jobId);
        cv.put(DBTable.estimate , estimate);
        cv.put(DBTable.normalTimeMinutes , normalTimeMinutes);
        cv.put(DBTable.overtimeMinutes , overtimeMinutes);
        cv.put(DBTable.day , day);
        cv.put(DBTable.timeTypeName , timeTypeName);
        cv.put(DBTable.dateWorked , dateWorked);
        cv.put(DBTable.weekCommencing , weekCommencing);
        cv.put(DBTable.isApproved , isApproved);
        cv.put(DBTable.isWaitingApproval , isWaitingApproval);
        cv.put(DBTable.isEdited , isEdited);
        return cv;
    }

    public String getHoursId() {
        return hoursId;
    }

    public String getTimesheetHoursId() {
        return timesheetHoursId;
    }

    public void setTimesheetHoursId(String timesheetHoursId) {
        this.timesheetHoursId = timesheetHoursId;
    }

    public String getTimeTypeActivityId() {
        return timeTypeActivityId;
    }

    public String getTimeTypeActivityName() {
        TimeTypeActivity timeTypeActivity = DBHandler.getInstance().getTimeTypeActivity(timeTypeActivityId);
        if(timeTypeActivity != null) {
            return  timeTypeActivity.getTimeTypeActivityName();
        }
        return null;
    }

    public void setTimeTypeActivityId(String timeTypeActivityId) {
        this.timeTypeActivityId = timeTypeActivityId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getEstimate() {
        return estimate;
    }

    public void setEstimate(String estimate) {
        this.estimate = estimate;
    }

    public int getNormalTimeMinutes() {
        return normalTimeMinutes;
    }

    public void setNormalTimeMinutes(int normalTimeMinutes) {
        this.normalTimeMinutes = normalTimeMinutes;
    }

    public int getOvertimeMinutes() {
        return overtimeMinutes;
    }

    public void setOvertimeMinutes(int overtimeMinutes) {
        this.overtimeMinutes = overtimeMinutes;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTimeTypeName() {
        return timeTypeName;
    }

    public void setTimeTypeName(String timeTypeName) {
        this.timeTypeName = timeTypeName;
    }

    public String getDateWorked() {
        return dateWorked;
    }

    public void setDateWorked(String dateWorked) {
        this.dateWorked = dateWorked;
    }

    public String getWeekCommencing() {
        return weekCommencing;
    }

    public void setWeekCommencing(String weekCommencing) {
        this.weekCommencing = weekCommencing;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public void setEdited(boolean edited) {
        isEdited = edited;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public boolean isWaitingApproval() {
        return isWaitingApproval;
    }

    public void setWaitingApproval(boolean waitingApproval) {
        isWaitingApproval = waitingApproval;
    }

    public void deleteAnswers(long submissionID , String repeatID , int repeatCounter){
        deleteAnswer(submissionID , DBTable.day , repeatID , repeatCounter);
        deleteAnswer(submissionID , DBTable.timesheetHoursId , repeatID , repeatCounter);
        deleteAnswer(submissionID , DBTable.dateWorked , repeatID , repeatCounter);
        deleteAnswer(submissionID , DBTable.timeTypeActivityId , repeatID , repeatCounter);

    }

    public String getUniqueKey(){
        return timeTypeName+"_"+jobId+"_"+dateWorked;
    }

    public String getHourKey(){
        return getUniqueKey()+"_"+getTimeTypeActivityId()+"_"+getTimesheetHoursId();
    }

    private void deleteAnswer(long submissionID , String uploadID , String repeatID , int repeatCounter){
        Answer answer = DBHandler.getInstance().getAnswer(submissionID , uploadID , repeatID , repeatCounter);
        if(answer != null){
            DBHandler.getInstance().removeAnswer(answer);
        }
    }

    public void setHoursId(String hoursId) {
        this.hoursId = hoursId;
    }
}
