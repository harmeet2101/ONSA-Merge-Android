package co.uk.depotnet.onsa.modals.store;

import android.content.ContentValues;
import android.database.Cursor;

public class MyStoreFav {

    public String stockItemId;
    public boolean isFavorite;


    public MyStoreFav(String stockItemId, boolean isFavorite) {
        this.stockItemId = stockItemId;
        this.isFavorite = isFavorite;
    }

    public MyStoreFav(Cursor cursor) {
        this.isFavorite = cursor.getInt(cursor.getColumnIndex(DBTable.isFavorite))!=0;
        this.stockItemId = cursor.getString(cursor.getColumnIndex(DBTable.stockItemId));
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public static class DBTable{
        public static String NAME = "MyStoreFav";
        public static String stockItemId = "stockItemId";
        public static String isFavorite = "isFavorite";

    }

    public ContentValues toContentValues(){
        ContentValues cv = new ContentValues();
        cv.put(DBTable.stockItemId , stockItemId);
        cv.put(DBTable.isFavorite , isFavorite ? 1 : 0);
        return cv;
    }
}
