package co.uk.depotnet.onsa.modals.timesheet;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.forms.Answer;
import co.uk.depotnet.onsa.networking.CommonUtils;

public class LogHourTime implements Parcelable {

    private String timeSheetHoursId;
    private String timeTypeActivityId;
    private String timeTypeActivityName;
    private int normalTimeMinutes;
    private int overtimeMinutes;

    public LogHourTime(String timeSheetHoursId, String timeTypeActivityId, String timeTypeActivityName, int normalTimeMinutes, int overtimeMinutes) {
        this.timeSheetHoursId = timeSheetHoursId;
        this.timeTypeActivityId = timeTypeActivityId;
        this.timeTypeActivityName = timeTypeActivityName;
        this.normalTimeMinutes = normalTimeMinutes;
        this.overtimeMinutes = overtimeMinutes;
    }


    public String getTimeTypeActivityId() {
        return timeTypeActivityId;
    }

    public void setTimeTypeActivityId(String timeTypeActivityId) {
        this.timeTypeActivityId = timeTypeActivityId;
    }

    public String getTimeTypeActivityName() {
        return timeTypeActivityName;
    }

    public void setTimeTypeActivityName(String timeTypeActivityName) {
        this.timeTypeActivityName = timeTypeActivityName;
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

    public String getNormalTimeHours() {
        return CommonUtils.getDisplayTime(normalTimeMinutes);
    }

    public String getTimeSheetHoursId() {
        return timeSheetHoursId;
    }

    public String getOvertimeHours() {
        return CommonUtils.getDisplayTime(overtimeMinutes);
    }


    public String getTotalHoursText(){
        return CommonUtils.getDisplayTime(normalTimeMinutes+overtimeMinutes);
    }

    public int getTotalHours(){
        return normalTimeMinutes+overtimeMinutes;
    }

    protected LogHourTime(Parcel in) {
        timeTypeActivityId = in.readString();
        timeTypeActivityName = in.readString();
        normalTimeMinutes = in.readInt();
        overtimeMinutes = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(timeTypeActivityId);
        dest.writeString(timeTypeActivityName);
        dest.writeInt(normalTimeMinutes);
        dest.writeInt(overtimeMinutes);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LogHourTime> CREATOR = new Creator<LogHourTime>() {
        @Override
        public LogHourTime createFromParcel(Parcel in) {
            return new LogHourTime(in);
        }

        @Override
        public LogHourTime[] newArray(int size) {
            return new LogHourTime[size];
        }
    };

    public void saveAnswer(long submissionID, String repeatID, int repeatCounter) {
            createAnswer(submissionID , "timeSheetHoursId" , repeatID , repeatCounter , timeSheetHoursId , timeSheetHoursId , true , false);
            createAnswer(submissionID, "timeTypeActivityId", repeatID, repeatCounter, timeTypeActivityId, timeTypeActivityName, true, false);
            createAnswer(submissionID, "overtimeMinutes", repeatID, repeatCounter, String.valueOf(overtimeMinutes), getOvertimeHours(), true, false);
            createAnswer(submissionID, "normalTimeMinutes", repeatID, repeatCounter, String.valueOf(normalTimeMinutes), getNormalTimeHours(), true, false);
    }

    public void saveTimeSheetHoursId(long submissionID, String repeatID, int repeatCounter) {
        if (!TextUtils.isEmpty(timeSheetHoursId)) {
            createAnswer(submissionID, "timesheetHoursIds", repeatID, repeatCounter, timeSheetHoursId, timeSheetHoursId, true, true);
        }
    }

    private void createAnswer(long submissionID , String uploadID , String repeatID , int repeatCounter , String value , String displayText , boolean shouldUpload , boolean isMultiAnswer){
        Answer answer = DBHandler.getInstance().getAnswer(submissionID , uploadID , repeatID , repeatCounter);
        if(answer == null){
            answer = new Answer(submissionID , uploadID , repeatID , repeatCounter);
        }
        answer.setAnswer(value);
        answer.setDisplayAnswer(displayText);
        answer.setShouldUpload(shouldUpload);
        answer.setIsMultiList(isMultiAnswer?1:0);
        DBHandler.getInstance().replaceData(Answer.DBTable.NAME , answer.toContentValues());
    }

    public void removeFromDB() {
        if (!TextUtils.isEmpty(timeSheetHoursId)) {
            DBHandler.getInstance().removeTimesheetHour(timeSheetHoursId);
        }
    }
}
