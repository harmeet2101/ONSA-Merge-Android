package co.uk.depotnet.onsa.modals.responses;

import java.util.ArrayList;

import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.MeasureItems;

public class MeasureItemResponse {

    ArrayList<MeasureItems> measureItems;

    public ArrayList<MeasureItems> getMeasureItems() {
        return measureItems;
    }

    public void toContentValues(){
        if(measureItems == null || measureItems.isEmpty()){
            return;
        }

        for (MeasureItems mi :
                measureItems) {
            DBHandler.getInstance().replaceData(MeasureItems.DBTable.NAME , mi.toContentValues());
        }
    }

}
