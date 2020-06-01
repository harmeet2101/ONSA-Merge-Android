package co.uk.depotnet.onsa.modals.responses;

import java.util.ArrayList;

import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.MenSplit;

public class MenSplitResponse {

    ArrayList<MenSplit> menSplits;

    public ArrayList<MenSplit> getMenSplits() {
        return menSplits;
    }

    public void setMenSplits(ArrayList<MenSplit> menSplits) {
        this.menSplits = menSplits;
    }

    public void toContentValues(){
        if(menSplits == null || menSplits.isEmpty()){
            return;
        }

        for (MenSplit ms :
                menSplits) {
            DBHandler.getInstance().replaceData(MenSplit.DBTable.NAME , ms.toContentValues());
        }
    }
}
