package co.uk.depotnet.onsa.modals.timesheet;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.forms.Answer;
import co.uk.depotnet.onsa.networking.CommonUtils;
import co.uk.depotnet.onsa.utils.Utils;

public class LogHourItem implements Parcelable {

    private String jobId;
    private String estimate;
    private String day;
    private String timeTypeName;
    private String dateWorked;
    private String weekCommencing;
    private boolean isApproved;
    private boolean isWaitingApproval;
    private ArrayList<LogHourTime> logHourTimes;


    public LogHourItem(TimeSheetHour timeSheetHour, ArrayList<LogHourTime> logHourTimes) {
        this.jobId = timeSheetHour.getJobId();
        this.estimate = timeSheetHour.getEstimate();
        this.day = timeSheetHour.getDay();
        this.timeTypeName = timeSheetHour.getTimeTypeName();
        this.dateWorked = timeSheetHour.getDateWorked();
        this.weekCommencing = timeSheetHour.getWeekCommencing();
        this.isApproved = timeSheetHour.isApproved();
        this.isWaitingApproval = timeSheetHour.isWaitingApproval();
        this.logHourTimes = logHourTimes;
    }

    protected LogHourItem(Parcel in) {
        jobId = in.readString();
        estimate = in.readString();
        day = in.readString();
        timeTypeName = in.readString();
        dateWorked = in.readString();
        weekCommencing = in.readString();
        isApproved = in.readByte() != 0;
        isWaitingApproval = in.readByte() != 0;
        logHourTimes = in.createTypedArrayList(LogHourTime.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(jobId);
        dest.writeString(estimate);
        dest.writeString(day);
        dest.writeString(timeTypeName);
        dest.writeString(dateWorked);
        dest.writeString(weekCommencing);
        dest.writeByte((byte) (isApproved ? 1 : 0));
        dest.writeByte((byte) (isWaitingApproval ? 1 : 0));
        dest.writeTypedList(logHourTimes);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LogHourItem> CREATOR = new Creator<LogHourItem>() {
        @Override
        public LogHourItem createFromParcel(Parcel in) {
            return new LogHourItem(in);
        }

        @Override
        public LogHourItem[] newArray(int size) {
            return new LogHourItem[size];
        }
    };

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

    public ArrayList<LogHourTime> getLogHourTimes() {
        return logHourTimes;
    }

    public String getStatus() {
        if(isApproved){
            return "Approved";
        }
        if(isWaitingApproval){
            return "Awaiting Approval";
        }

        if(logHourTimes!= null && !logHourTimes.isEmpty() && !TextUtils.isEmpty(logHourTimes.get(0).getTimeSheetHoursId())){
            return "In Progress";
        }

        return null;
    }

    public void setLogHourTimes(ArrayList<LogHourTime> logHourTimes) {
        this.logHourTimes = logHourTimes;
    }

    public boolean isWaitingApproval() {
        return isWaitingApproval;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public String getDisplayedWorkDate(){
        String displayDate = dateWorked;
        if(!TextUtils.isEmpty(dateWorked)){
            Date date = Utils.parseDate(dateWorked , "yyyy-MM-dd'T'HH:mm:ss");
            if(date != null) {
                displayDate = Utils.formatDate(date , "dd/MM/yyyy");
            }
        }

        return displayDate;
    }

    public int getTotalTime(){
        if(logHourTimes == null || logHourTimes.isEmpty()){
            return 0;
        }
        int timeInMinuts = 0;

        for (int i = 0 ; i < logHourTimes.size() ; i++){
            timeInMinuts += logHourTimes.get(i).getTotalHours();
        }
        return timeInMinuts;
    }

    public String getTotalHoursText(){
        return CommonUtils.getDisplayTime(getTotalTime());
    }

    public void saveAnswers(long submissionID , String repeatID){
        createAnswer(submissionID , "selected_date" , null , 0 , dateWorked , getDisplayedWorkDate() , false);

        if(logHourTimes == null || logHourTimes.isEmpty()){
            return;
        }
        for (int i = 0 ; i < logHourTimes.size() ;i++) {
            LogHourTime logHourTime = logHourTimes.get(i);
            if(!TextUtils.isEmpty(logHourTime.getTimeSheetHoursId())) {
                createAnswer(submissionID, "dateWorked", repeatID, i, dateWorked, getDisplayedWorkDate(), true);
                createAnswer(submissionID, "jobId", repeatID, i, jobId, estimate, true);
                createAnswer(submissionID, "timeTypeName", repeatID, i, timeTypeName, timeTypeName, false);

                logHourTime.saveAnswer(submissionID, repeatID, i);
            }
        }
    }

    public void deleteLogHourItem(){
        if(logHourTimes == null || logHourTimes.isEmpty()){
            return;
        }
        for (int i = 0 ; i < logHourTimes.size() ;i++) {
            logHourTimes.get(i).removeFromDB();
        }
    }

    private void createAnswer(long submissionID , String uploadID , String repeatID , int repeatCounter , String value , String displayText , boolean shouldUpload){
        Answer answer = DBHandler.getInstance().getAnswer(submissionID , uploadID , repeatID , repeatCounter);
        if(answer == null){
            answer = new Answer(submissionID , uploadID , repeatID , repeatCounter);
        }
        answer.setAnswer(value);
        answer.setDisplayAnswer(displayText);
        answer.setShouldUpload(shouldUpload);
        DBHandler.getInstance().replaceData(Answer.DBTable.NAME , answer.toContentValues());
    }

    public int saveTimeHoursID(long submissionID, String repeatID , int repeatCount) {
        if(logHourTimes == null || logHourTimes.isEmpty()){
            return repeatCount;
        }

        for (int i = 0 ; i < logHourTimes.size() ;i++) {
            LogHourTime logHourTime = logHourTimes.get(i);
            logHourTime.saveTimeSheetHoursId(submissionID , repeatID , repeatCount);
            repeatCount++;
        }

        return repeatCount;

    }
}
