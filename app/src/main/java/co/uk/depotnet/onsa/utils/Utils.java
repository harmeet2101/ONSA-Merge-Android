package co.uk.depotnet.onsa.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import androidx.annotation.NonNull;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class Utils {

    public static Boolean store_call = false;
    @NonNull
    public static String getMimeType(@NonNull final Context context, @NonNull final Uri uri) {
        final ContentResolver cR = context.getContentResolver();
        final MimeTypeMap mime = MimeTypeMap.getSingleton();
        String type = mime.getExtensionFromMimeType(cR.getType(uri));
        if (type == null) {
            type = "*/*";
        }
        return type;
    }

    public static String getSaveDir(Context context) {
        String path = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/onsa_store/";
        return path;
    }

    public static String getNameFromUrl(final String url) {
        return Uri.parse(url).getLastPathSegment();
    }

    public static File createImageFile(Context context) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.UK).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        String storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath() +
                "/onsa_store/Photos";
        File dir = new File(storageDir);
//        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File image = File.createTempFile(imageFileName, ".jpg", dir);

        return image;
    }


    public static File saveSignature(Context context ,Bitmap bmp) {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.UK).format(new Date());
        String imageFileName = "SIGN_" + timeStamp;

        String file_path = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath() +
                "/onsa_store/Signature";
        File dir = new File(file_path);
        if (!dir.exists())
            dir.mkdirs();
        try {
            File file = File.createTempFile(imageFileName, ".jpg", dir);
            FileOutputStream fOut = null;

            fOut = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.flush();
            fOut.close();
            return file;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getSimpleDateFormat(String datetime) throws ParseException {
        if (!datetime.isEmpty())
        {
            SimpleDateFormat inputFormat = null;
            if (datetime.length()>19)
            {
                inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",Locale.US);
            }
            else
            {
                inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss",Locale.ENGLISH);
            }
            // use UTC as timezone
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
            // assuming a timezone in India
            //outputFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
            //2020-08-10T00:00:00
            Date date = inputFormat.parse(datetime);
            //System.out.println(formattedDate); // prints 10-04-2018
            return outputFormat.format(Objects.requireNonNull(date));
        }

        return datetime;
    }

    public static String getSaveBriefingsDir(Context context) {
        String docPath= Objects.requireNonNull(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)).getAbsolutePath() + "/briefings/";
        File root = new File(docPath);
        if (!root.exists()) {
            root.mkdirs();
        }
        return docPath;
    }

}
