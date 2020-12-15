package co.uk.depotnet.onsa;

import android.app.Application;

import co.uk.depotnet.onsa.utils.AppPreferences;

import com.gu.toolargetool.TooLargeTool;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2.HttpUrlConnectionDownloader;
import com.tonyodev.fetch2core.Downloader;


public class OnsaApp extends Application {



    @Override
    public void onCreate() {
        super.onCreate();

        AppPreferences.initAppPreferences(getApplicationContext());
        TooLargeTool.startLogging(this);
        final FetchConfiguration fetchConfiguration = new FetchConfiguration.Builder(this)
                .enableRetryOnNetworkGain(true)
                .setDownloadConcurrentLimit(3)
                .setHttpDownloader(new HttpUrlConnectionDownloader(Downloader.FileDownloaderType.PARALLEL))
                // OR
                //.setHttpDownloader(getOkHttpDownloader())
                .build();
        Fetch.Impl.setDefaultInstanceConfiguration(fetchConfiguration);
    }
}
