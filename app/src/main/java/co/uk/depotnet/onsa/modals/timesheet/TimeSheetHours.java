package co.uk.depotnet.onsa.modals.timesheet;

import android.text.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.UUID;

import co.uk.depotnet.onsa.database.DBHandler;

public class TimeSheetHours {
    private String weekCommencing;
    private boolean isApproved;
    private boolean isWaitingApproval;
    private ArrayList<TimeSheetHour> timesheetHours;

    public void toContentValues(){
        DBHandler.getInstance().removeTimeHours(weekCommencing);
        if(!isEmpty()){
            for(TimeSheetHour timeSheetHour: timesheetHours){
                timeSheetHour.setWeekCommencing(weekCommencing);
                timeSheetHour.setHoursId(UUID.randomUUID().toString());
                timeSheetHour.formatTime();

                DBHandler.getInstance().replaceData(TimeSheetHour.DBTable.NAME , timeSheetHour.toContentValues());
            }
        }
    }

    private long getTotalTimeSheetHours(){
        long totalTime = 0;
        if(timesheetHours == null || timesheetHours.isEmpty()){
            return totalTime;
        }
        for (int i = 0; i < timesheetHours.size(); i++) {
            TimeSheetHour timeSheetHour = timesheetHours.get(i);
            String timeFrom = timeSheetHour.getTimeFrom();
            String timeTo = timeSheetHour.getTimeTo();

            DateFormat df = new java.text.SimpleDateFormat("HH:mm:ss");
            java.util.Date date1 = null;
            java.util.Date date2 = null;
            try {
                date1 = df.parse(timeFrom);
                date2 = df.parse(timeTo);
                long diff = date2.getTime() - date1.getTime();
                diff = diff/60000;
                totalTime += diff;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return totalTime;

    }

    public String getFormatedTotalTimeSheetHours(){
        long totalTime = getTotalTimeSheetHours();
        String time = (totalTime/60)+"h"+(totalTime%60)+"m";
        return time;
    }

    public boolean isEmpty(){
        return timesheetHours == null || timesheetHours.isEmpty();
    }

    public void setWeekCommencing() {
        if(!TextUtils.isEmpty(weekCommencing)){
            weekCommencing = weekCommencing.split("T")[0];
        }
    }

    public ArrayList<TimeSheetHour> getTimesheetHours() {
        return timesheetHours;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public boolean isWaitingApproval() {
        return isWaitingApproval;
    }
}
