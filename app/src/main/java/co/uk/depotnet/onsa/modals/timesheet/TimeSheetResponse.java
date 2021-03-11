package co.uk.depotnet.onsa.modals.timesheet;

import java.util.ArrayList;

import co.uk.depotnet.onsa.database.DBHandler;

public class TimeSheetResponse {
    private ArrayList<TimeSheet> timesheets;

    public ArrayList<TimeSheet> getTimesheets() {
        return timesheets;
    }

    public void setTimesheets(ArrayList<TimeSheet> timesheets) {
        this.timesheets = timesheets;
    }

    public void toContentValues(){
        if(isEmpty()){
            return;
        }

        for (TimeSheet timeSheet : timesheets){
            DBHandler.getInstance().replaceData(TimeSheet.DBTable.NAME , timeSheet.toContentValues());
        }
    }

    public boolean isEmpty(){
        return timesheets == null || timesheets.isEmpty();
    }
}
