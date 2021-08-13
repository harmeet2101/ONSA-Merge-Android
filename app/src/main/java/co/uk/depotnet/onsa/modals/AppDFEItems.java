package co.uk.depotnet.onsa.modals;

import android.content.ContentValues;

import java.util.ArrayList;

import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.responses.DatasetResponse;

public class AppDFEItems {

    private ArrayList<WorkItem> appDFEItems;

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();

        DBHandler dbHandler = DBHandler.getInstance();

        if (this.appDFEItems != null && !this.appDFEItems.isEmpty()) {
            dbHandler.replaceDataDFEWorkItems(appDFEItems, DatasetResponse.DBTable.dfeWorkItems);
        }


        return cv;
    }

    public ArrayList<WorkItem> getAppDFEItems() {
        return appDFEItems;
    }
}
