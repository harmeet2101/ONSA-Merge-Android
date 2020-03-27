package co.uk.depotnet.onsa.utils;


import android.content.Context;

import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2.Request;
import com.tonyodev.fetch2core.Func;

public class DownloadUtils {
    private static Fetch fetch;

    private static Fetch getInstance(Context context){
        if(fetch == null){
            FetchConfiguration fetchConfiguration = new FetchConfiguration.Builder(context)
                    .setDownloadConcurrentLimit(3)
                    .build();

            fetch = Fetch.Impl.getInstance(fetchConfiguration);
        }

        return fetch;
    }

    public static int startDownload(Request request , Func<Request> requestFunc2 , Func<Error> errorFunc2){
        fetch.enqueue(request , requestFunc2 , errorFunc2);
        return 0;
    }

    public static boolean cancle(int downloadId){
        return true;
//        return getManager().cancel(downloadId)==1;
    }

    public static int getStatus(int downloadId){
//        return getManager().query(downloadId);
        return 1;
    }
}
