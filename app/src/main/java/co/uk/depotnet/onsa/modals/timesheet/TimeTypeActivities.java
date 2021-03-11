package co.uk.depotnet.onsa.modals.timesheet;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Calendar;

import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.ItemType;
import co.uk.depotnet.onsa.modals.Operative;
import co.uk.depotnet.onsa.networking.response.Operatives;

public class TimeTypeActivities {
    private ArrayList<TimeTypeActivity> timeTypeActivities;
    private ArrayList<TimesheetOperative> operatives;
    private String weekCommencingDay;

    public void toContentValues(){
        DBHandler dbHandler = DBHandler.getInstance();
        if(timeTypeActivities != null && !timeTypeActivities.isEmpty()){
            for(TimeTypeActivity timeTypeActivity: timeTypeActivities){
                dbHandler.replaceData(TimeTypeActivity.DBTable.NAME , timeTypeActivity.toContentValues());
            }
        }

        if(operatives != null && !operatives.isEmpty()){
            for(TimesheetOperative operative: operatives){
                dbHandler.replaceData(TimesheetOperative.DBTable.NAME , operative.toContentValues());
            }
        }

        if(!TextUtils.isEmpty(weekCommencingDay)){



            dbHandler.deleteItemTypes("WeekCommencingDay");
            ItemType itemType = new ItemType(weekCommencingDay , "WeekCommencingDay" , weekCommencingDay);
            dbHandler.replaceData(ItemType.DBTable.NAME , itemType.toContentValues());
        }

    }
}
