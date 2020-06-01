package co.uk.depotnet.onsa.networking;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import co.uk.depotnet.onsa.activities.VerificationActivity;
import co.uk.depotnet.onsa.dialogs.JWTErrorDialog;
import retrofit2.Response;

public class CommonUtils {

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static boolean onTokenExpired(Context context , int code){
        if(code == 401 || code == 429){
            JWTErrorDialog dialog = new JWTErrorDialog(context , "Token expired. Please login again.","Error");
            dialog.show();
            return true;
        }
        return false;
    }
}
