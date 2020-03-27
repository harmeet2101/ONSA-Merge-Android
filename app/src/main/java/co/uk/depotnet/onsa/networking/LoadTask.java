package co.uk.depotnet.onsa.networking;

import co.uk.depotnet.onsa.modals.forms.FormItem;
import co.uk.depotnet.onsa.modals.httpresponses.BaseTask;
import co.uk.depotnet.onsa.modals.httpresponses.SiteActivityTask;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoadTask {
    private OnSuccessListener listener;

    public LoadTask(OnSuccessListener listener){
        this.listener = listener;
    }

    public Callback<List<BaseTask>> callback = new Callback<List<BaseTask>>() {
        @Override
        public void onResponse(Call<List<BaseTask>> call, Response<List<BaseTask>> response) {

            if (response.isSuccessful()) {
                List<BaseTask> tasks = response.body();
                if(tasks != null && !tasks.isEmpty()){
                    listener.onSuccess(tasks);
                    return;
                }
            }

            listener.onFailed();
        }

        @Override
        public void onFailure(Call<List<BaseTask>> call, Throwable t) {
            listener.onFailed();
        }
    };

    public interface OnSuccessListener{
        void onSuccess(List<BaseTask> tasks);

        void onFailed();
    }

}
