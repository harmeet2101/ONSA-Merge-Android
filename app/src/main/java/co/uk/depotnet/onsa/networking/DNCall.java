package co.uk.depotnet.onsa.networking;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class DNCall<T> implements Callback<T> {

    private static final int TOTAL_RETRIES = 3;
    private static final String TAG = DNCall.class.getSimpleName();
    private final Call<T> call;
    private int retryCount = 0;

    public DNCall(Call<T> call) {
        this.call = call;
    }




    @Override
    public void onFailure(Call<T> call, Throwable t) {
        if (retryCount++ < TOTAL_RETRIES) {
            Log.v(TAG, "Retrying... (" + retryCount + " out of " + TOTAL_RETRIES + ")");
            retry();
        }else {
            this.onFailure(t);
        }
    }

    private void retry() {
        call.clone().enqueue(this);
    }

    abstract void onFailure(Throwable t);
}
