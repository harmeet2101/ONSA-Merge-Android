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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

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

}
