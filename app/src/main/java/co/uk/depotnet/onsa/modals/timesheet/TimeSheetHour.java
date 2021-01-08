package co.uk.depotnet.onsa.modals.timesheet;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.forms.Answer;

public class TimeSheetHour implements Parcelable{

    private String timesheetHoursId; //pk
    private String hoursId; //pk
    private String dateWorked; //pk
    private String timeFrom;
    private String timeTo;
    private String timeTypeActivityId;
    private String day;
    private String weekCommencing; //pk
    private boolean isEdited;

    protected TimeSheetHour(Parcel in) {
        timeTypeActivityId = in.readString();
        hoursId = in.readString();
        dateWorked = in.readString();
        timeFrom = in.readString();
        timeTo = in.readString();
        timeTypeActivityId = in.readString();
        day = in.readString();
        weekCommencing = in.readString();
        isEdited = in.readByte() == 1;
    }

    public TimeSheetHour(Cursor c) {
        timeTypeActivityId = c.getString(c.getColumnIndex(DBTable.timeTypeActivityId));
        dateWorked = c.getString(c.getColumnIndex(DBTable.dateWorked));
        timeFrom = c.getString(c.getColumnIndex(DBTable.timeFrom));
        timeTo = c.getString(c.getColumnIndex(DBTable.timeTo));
        timesheetHoursId = c.getString(c.getColumnIndex(DBTable.timesheetHoursId));
        hoursId = c.getString(c.getColumnIndex(DBTable.hoursId));
        day = c.getString(c.getColumnIndex(DBTable.day));
        weekCommencing = c.getString(c.getColumnIndex(DBTable.weekCommencing));
        isEdited = c.getInt(c.getColumnIndex(DBTable.isEdited)) == 1;
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



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(timesheetHoursId);
        dest.writeString(hoursId);
        dest.writeString(dateWorked);
        dest.writeString(timeFrom);
        dest.writeString(timeTo);
        dest.writeString(timeTypeActivityId);
        dest.writeString(day);
        dest.writeString(weekCommencing);
        dest.writeByte((byte) (isEdited?1:0));
    }

    public ContentValues toContentValues(){
        ContentValues cv = new ContentValues();

        cv.put(DBTable.timesheetHoursId , timesheetHoursId);
        cv.put(DBTable.hoursId , hoursId);
        cv.put(DBTable.dateWorked , dateWorked);
        cv.put(DBTable.timeFrom , timeFrom);
        cv.put(DBTable.timeTo , timeTo);
        cv.put(DBTable.timeTypeActivityId , timeTypeActivityId);
        cv.put(DBTable.day , day);
        cv.put(DBTable.weekCommencing , weekCommencing);
        cv.put(DBTable.isEdited , isEdited);

        return cv;
    }

    public void formatTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

        if(!TextUtils.isEmpty(dateWorked)){

            try {
//                dateWorked = Utils.getSimpleDateFormat(dateWorked);
                Date date = dateFormat1.parse(dateWorked);
                dateWorked = dateFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if(!TextUtils.isEmpty(timeFrom)){

                try {
                    Date date = sdf.parse(timeFrom);
                    timeFrom = sdf.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
        }
        if(!TextUtils.isEmpty(timeTo)){

            try {
                Date date = sdf.parse(timeTo);
                timeTo = sdf.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }


    public static class DBTable{
        public static final String NAME = "TimeSheetHour";
        public static final String timesheetHoursId = "timesheetHoursId";
        public static final String hoursId = "hoursId";
        public static final String dateWorked = "dateWorked";
        public static final String timeFrom = "timeFrom";
        public static final String timeTo = "timeTo";
        public static final String timeTypeActivityId = "timeTypeActivityId";
        public static final String day = "day";
        public static final String weekCommencing = "weekCommencing";
        public static final String isEdited = "isEdited";
    }

    public boolean isEdited() {
        return isEdited;
    }

    public void setEdited(boolean edited) {
        isEdited = edited;
    }

    public String getTimesheetHoursId() {
        return timesheetHoursId;
    }

    public void setTimesheetHoursId(String timesheetHoursId) {
        this.timesheetHoursId = timesheetHoursId;
    }

    public String getDateWorked() {
        return dateWorked;
    }

    public void setDateWorked(String dateWorked) {
        this.dateWorked = dateWorked;
    }

    public String getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(String timeFrom) {
        this.timeFrom = timeFrom;
    }

    public String getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(String timeTo) {
        this.timeTo = timeTo;
    }

    public String getTimeTypeActivityId() {
        return timeTypeActivityId;
    }

    public void setTimeTypeActivityId(String timeTypeActivityId) {
        this.timeTypeActivityId = timeTypeActivityId;
    }



    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getWeekCommencing() {
        return weekCommencing;
    }

    public void setWeekCommencing(String weekCommencing) {
        this.weekCommencing = weekCommencing;
    }

    public void saveAnswers(long submissionID , String repeatID , int repeatCounter){
        boolean shouldUpload = isEdited;
        createAnswer(submissionID , DBTable.day , repeatID , repeatCounter , day, day , false);
        createAnswer(submissionID , DBTable.timesheetHoursId , repeatID , repeatCounter , timesheetHoursId, timesheetHoursId , isEdited);
        createAnswer(submissionID , DBTable.dateWorked , repeatID , repeatCounter , dateWorked, dateWorked , shouldUpload);
        TimeTypeActivity timeTypeActivity = DBHandler.getInstance().getTimeTypeActivity(timeTypeActivityId);
        if(timeTypeActivity != null) {
            createAnswer(submissionID, DBTable.timeTypeActivityId, repeatID, repeatCounter, timeTypeActivityId, timeTypeActivity.getDisplayItem(), shouldUpload);
        }
        createAnswer(submissionID , DBTable.timeTo , repeatID , repeatCounter , timeTo, timeTo , shouldUpload);
        createAnswer(submissionID , DBTable.timeFrom , repeatID , repeatCounter , timeFrom, timeFrom , shouldUpload);
    }

    public void deleteAnswers(long submissionID , String repeatID , int repeatCounter){
        deleteAnswer(submissionID , DBTable.day , repeatID , repeatCounter);
        deleteAnswer(submissionID , DBTable.timesheetHoursId , repeatID , repeatCounter);
        deleteAnswer(submissionID , DBTable.dateWorked , repeatID , repeatCounter);
        deleteAnswer(submissionID , DBTable.timeTypeActivityId , repeatID , repeatCounter);
        deleteAnswer(submissionID , DBTable.timeTo , repeatID , repeatCounter);
        deleteAnswer(submissionID , DBTable.timeFrom , repeatID , repeatCounter);
    }

    public void resetAnswers(long submissionID , String repeatID , int repeatCounter){
        isEdited = true;
        timeFrom = "00:00";
        timeTo = "00:00";
        boolean shouldUpload = isEdited;
        createAnswer(submissionID , DBTable.timeTo , repeatID , repeatCounter , "00:00", "00:00" , shouldUpload);
        createAnswer(submissionID , DBTable.timeFrom , repeatID , repeatCounter , "00:00", "00:00" , shouldUpload);
        DBHandler.getInstance().replaceData(DBTable.NAME , toContentValues());
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

    private void deleteAnswer(long submissionID , String uploadID , String repeatID , int repeatCounter){
        Answer answer = DBHandler.getInstance().getAnswer(submissionID , uploadID , repeatID , repeatCounter);
        if(answer != null){
            DBHandler.getInstance().removeAnswer(answer);
        }
    }

    public void setHoursId(String hoursId) {
        this.hoursId = hoursId;
    }

    public long getTimeSheetHours(long submissionID , String repeatID , int repeatCounter){
        String timeFrom = getTimeFrom();
        String timeTo = getTimeTo();
        Answer answertimeFrom = DBHandler.getInstance().getAnswer(submissionID , DBTable.timeFrom , repeatID , repeatCounter);
        Answer answertimeTo = DBHandler.getInstance().getAnswer(submissionID , DBTable.timeTo , repeatID , repeatCounter);
        if(answertimeFrom == null || TextUtils.isEmpty(answertimeFrom.getAnswer())){
            return 0;
        }

        if(answertimeTo == null || TextUtils.isEmpty(answertimeTo.getAnswer())){
            return 0;
        }

        timeFrom = answertimeFrom.getAnswer()+":00";
        timeTo = answertimeTo.getAnswer()+":00";

        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = df.parse(timeFrom);
            date2 = df.parse(timeTo);
            long diff = date2.getTime() - date1.getTime();
            diff = diff/60000;

            return diff;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
