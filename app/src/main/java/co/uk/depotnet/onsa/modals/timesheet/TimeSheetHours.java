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
                timeSheetHour.setApproved(isApproved);
                timeSheetHour.setWaitingApproval(isWaitingApproval);
                timeSheetHour.setHoursId(UUID.randomUUID().toString());
                DBHandler.getInstance().replaceData(TimeSheetHour.DBTable.NAME , timeSheetHour.toContentValues());
            }
        }
    }

    public boolean isEmpty(){
        return timesheetHours == null || timesheetHours.isEmpty();
    }

    public void setWeekCommencing() {
        if(!TextUtils.isEmpty(weekCommencing)){
            weekCommencing = weekCommencing.split("T")[0];
        }
    }

    public String getWeekCommencing() {
        return weekCommencing;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public boolean isWaitingApproval() {
        return isWaitingApproval;
    }
}
