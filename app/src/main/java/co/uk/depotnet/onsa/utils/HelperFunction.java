package co.uk.depotnet.onsa.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import org.apache.commons.lang3.ArrayUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
public class HelperFunction {

    Context context;

    public HelperFunction(Context context) {
        this.context = context;
    }

    public HelperFunction() {

    }



    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static final int getColor(Context context, int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            return ContextCompat.getColor(context, id);
        } else {
            return context.getResources().getColor(id);
        }
    }

    public static boolean checkStringEmpty(String str) {
        if (str != null && !TextUtils.isEmpty(str)) {
            return true;
        }
        return false;
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }









    public static boolean isValidEmail(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public static void showKeyboard(Context ctx , EditText editText) {
        InputMethodManager inputManager = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);


        if(inputManager != null){
            inputManager.toggleSoftInputFromWindow(editText.getWindowToken() , InputMethodManager.SHOW_IMPLICIT , InputMethodManager.HIDE_IMPLICIT_ONLY);
        }

    }


    public static boolean isImage(File file) {
        if (file == null || !file.exists()) {
            return false;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getPath(), options);
        return options.outWidth != -1 && options.outHeight != -1;
    }

    public static List<Point> getRightCircle(List<Point> points) {
        List<Point> operatingList = new ArrayList<>(points);
        Point[] pointsArray = operatingList.toArray(new Point[operatingList.size()]);
        Collections.sort(operatingList, new Comparator<Point>() {
            @Override
            public int compare(Point point, Point t1) {
                return Integer.valueOf(point.y).compareTo(Integer.valueOf(t1.y));
            }
        });
        int index = ArrayUtils.indexOf(pointsArray, operatingList.get(0));
        pointsArray = ArrayUtils.subarray(pointsArray, index, pointsArray.length);
        return Arrays.asList(pointsArray);
    }

    public static List<Point> getBottomCircle(List<Point> points) {
        List<Point> operatingList = getRightCircle(points);
        Point[] pointsArray = operatingList.toArray(new Point[operatingList.size()]);
        Collections.sort(operatingList, new Comparator<Point>() {
            @Override
            public int compare(Point point, Point t1) {
                return Integer.valueOf(point.y).compareTo(Integer.valueOf(t1.y));
            }
        });
        int index = ArrayUtils.indexOf(pointsArray, operatingList.get(operatingList.size() - 1));
        pointsArray = ArrayUtils.subarray(pointsArray, 0, index + 1);
        return Arrays.asList(pointsArray);
    }



}