//package co.uk.depotnet.onsa.networking;
//
//import android.content.Context;
//
//import androidx.annotation.NonNull;
//
//import retrofit2.Call;
//import retrofit2.Response;
//
//public abstract class Callback<T> implements retrofit2.Callback<T> {
//
//    private final Context context;
//
//    public Callback(Context context) {
//        this.context = context;
//    }
//
//    @Override
//    public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
//        int code = response.code();
//        if(code == 401 || code == 429){
//            return;
//        }
//        if(response.isSuccessful()) {
//            onResponse(response.body());
//            return;
//        }
//
//    }
//
//    @Override
//    public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
//        onFailure(t);
//    }
//
//    abstract void onResponse(T response);
//    abstract void onFailure(Throwable t);
//
//}