package co.uk.depotnet.onsa.modals;

import android.content.ContentValues;
import android.database.Cursor;

public class Feature {

    private String featureName;
    private boolean isActive;

    public String getFeatureName() {
        return featureName;
    }

    public boolean isActive() {
        return isActive;
    }

    public Feature(Cursor c){
        this.featureName = c.getString(c.getColumnIndex(DBTable.featureName));
        this.isActive = c.getInt(c.getColumnIndex(DBTable.isActive)) == 1;
    }

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(DBTable.featureName , featureName);
        cv.put(DBTable.isActive , isActive ? 1: 0);
        return cv;
    }

    public static class DBTable{
        public static final String NAME = "Feature";
        public static final String featureName = "featureName";
        public static final String isActive = "isActive";
    }



}
