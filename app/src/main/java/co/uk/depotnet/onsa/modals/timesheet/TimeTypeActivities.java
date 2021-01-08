package co.uk.depotnet.onsa.modals.timesheet;

import java.util.ArrayList;

import co.uk.depotnet.onsa.database.DBHandler;

public class TimeTypeActivities {
    private ArrayList<TimeTypeActivity> timeTypeActivities;

    public void toContentValues(){
        if(timeTypeActivities != null && !timeTypeActivities.isEmpty()){
            for(TimeTypeActivity timeTypeActivity: timeTypeActivities){
                DBHandler.getInstance().replaceData(TimeTypeActivity.DBTable.NAME , timeTypeActivity.toContentValues());
            }
        }
    }
}
