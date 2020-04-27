package co.uk.depotnet.onsa.modals.store;

import android.content.ContentValues;

import java.util.ArrayList;

import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.Feature;

public class FeatureResult {

    private ArrayList<Feature> featureResult;

    public ArrayList<Feature> getFeatureResult() {
        return featureResult;
    }

    public void setFeatureResult(ArrayList<Feature> featureResult) {
        this.featureResult = featureResult;
    }

    public void toContentValues(){
        if(featureResult != null && !featureResult.isEmpty()){
            for (Feature feature : featureResult){
                DBHandler.getInstance().replaceData(Feature.DBTable.NAME , feature.toContentValues());
            }
        }
    }
}
