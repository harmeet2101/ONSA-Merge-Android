package co.uk.depotnet.onsa.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import co.uk.depotnet.onsa.BuildConfig;

public class DBHelper extends SQLiteOpenHelper {

    private final static String SP_KEY_DB_VER = BuildConfig.APPLICATION_ID+"_databse";
    private static DBHelper dbHelper;
    private final static String DATABASE_PATH = "/data/data/"+BuildConfig.APPLICATION_ID+"/databases/";
    private static final int DATABASE_VERSION = 10;
    private static final String DATABASE_NAME = "onsadb.sqlite";
    private SQLiteDatabase database;


    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        createDatabase(context);
    }

    static DBHelper init(Context context){
        if (dbHelper == null) {
            synchronized (DBHelper.class) {
                if (dbHelper == null) {
                    dbHelper = new DBHelper(context);
                }
            }
        }
        return dbHelper;
    }



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }

    private boolean checkDatabase() {
        String myPath = DATABASE_PATH + DATABASE_NAME;
        File dbfile = new File(myPath);
        return dbfile.exists();
    }

    private void createDatabase(Context context){
        boolean dbExists = checkDatabase();

        if(dbExists){
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(context);
            int dbVersion = prefs.getInt(SP_KEY_DB_VER, 1);

            if (DATABASE_VERSION != dbVersion) {
                String myPath = DATABASE_PATH + DATABASE_NAME;
                File dbfile = new File(myPath);
                if (!dbfile.delete()) {

                }
            }
        }
        dbExists = checkDatabase();
        if(!dbExists){
            getReadableDatabase();
            close();
            copyDatabase(context);
        }


    }

    private void copyDatabase(Context context){
        try {
            InputStream is = context.getAssets().open("database/" + DATABASE_NAME);
            String path = DATABASE_PATH+DATABASE_NAME;
            OutputStream outputStream = new FileOutputStream(path);
            byte[] buffer = new byte[2048];
            int length;
            while ((length = is.read(buffer)) > 0){
                outputStream.write(buffer ,0 , length);
            }
            outputStream.flush();
            outputStream.close();
            is.close();

            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(SP_KEY_DB_VER, DATABASE_VERSION);
            editor.apply();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    void openDatabase(){
        String myPath = DATABASE_PATH + DATABASE_NAME;
        database = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.CREATE_IF_NECESSARY);
    }

    @Override
    public synchronized void close() {
        if (database != null && database.isOpen())
            database.close();
        database = null;
        super.close();
    }
}
