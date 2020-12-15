package co.uk.depotnet.onsa.networking;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.dialogs.JWTErrorDialog;
import co.uk.depotnet.onsa.modals.User;

public class CommonUtils {

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static boolean onTokenExpired(Context context , int code){
        if(code == 401 || code == 429){
            DBHandler.getInstance().clearTable(User.DBTable.NAME);
            JWTErrorDialog dialog = new JWTErrorDialog(context , "Token expired. Please login again.","Error");
            dialog.show();
            return true;
        }
        return false;
    }

    public static boolean validateToken(Context context){
        User user = DBHandler.getInstance().getUser();
        if(user == null || !validDate(user.getexpirationUtc())){
            DBHandler.getInstance().clearTable(User.DBTable.NAME);
            JWTErrorDialog dialog = new JWTErrorDialog(context , "Token expired. Please login again.","Error");
            dialog.show();
            return false;
        }
        return true;
    }

    private static boolean validDate(String dateStr){
        if (TextUtils.isEmpty(dateStr)) {
            return false;
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.ENGLISH);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = df.parse(dateStr);
            df.setTimeZone(TimeZone.getDefault());
            String formattedDate = df.format(date);
            date = df.parse(formattedDate);
            if(date == null){
                return false;
            }

            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
            Date currentDate = calendar.getTime();
            String formattedCurrDate = df.format(currentDate);
            currentDate = df.parse(formattedCurrDate);
            return currentDate.compareTo(date) < 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;

        }

    }

}
