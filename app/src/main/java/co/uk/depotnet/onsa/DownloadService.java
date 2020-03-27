package co.uk.depotnet.onsa;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

public class DownloadService extends IntentService {

    private static final String DOWNLOAD_PATH = "com.spartons.androiddownloadmanager_DownloadSongService_Download_path";
    private static final String DESTINATION_PATH = "com.spartons.androiddownloadmanager_DownloadSongService_Destination_path";

    public DownloadService() {
        super("DownloadService");
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

        }
    }
}
