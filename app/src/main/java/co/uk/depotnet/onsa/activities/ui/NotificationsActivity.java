package co.uk.depotnet.onsa.activities.ui;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.uk.depotnet.onsa.networking.CallUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.fcm.NotifyUtils;
import co.uk.depotnet.onsa.listeners.NotifyClickListner;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.modals.notify.NotifyModel;
import co.uk.depotnet.onsa.modals.notify.NotifyReadPush;
import co.uk.depotnet.onsa.networking.APICalls;
import co.uk.depotnet.onsa.networking.CommonUtils;

public class NotificationsActivity extends AppCompatActivity implements View.OnClickListener, NotifyClickListner {
    private User user;
    private HorizontalScrollView scrollTags;
    private LinearLayout ll_ui_blocker;
    private RecyclerView recyclerView;
    private TextView alerts_error;
    private Alerts_Adaptor adaptor;
    private ArrayList<NotifyModel> modalList=new ArrayList<>();
    private MaterialButton notify_all;
    private ImageView notify_Actions,notify_briefings,notify_schedule;
    private Map<String, Class> activityMap = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.ColorAlerts));
        }
        user = DBHandler.getInstance().getUser();
        scrollTags=findViewById(R.id.scrollTags);
        notify_all=findViewById(R.id.notify_all);
        notify_Actions=findViewById(R.id.notify_Actions);
        notify_briefings=findViewById(R.id.notify_briefings);
        notify_schedule=findViewById(R.id.notify_schedule);
        ll_ui_blocker=findViewById(R.id.ll_ui_blocker);
        recyclerView=findViewById(R.id.alerts_recycler);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        alerts_error=findViewById(R.id.alerts_error);

        notify_all.setOnClickListener(this);
        notify_Actions.setOnClickListener(this);
        notify_briefings.setOnClickListener(this);
        notify_schedule.setOnClickListener(this);
        activityMap.put("1", ActionsActivity.class);
        activityMap.put("2", BriefingsActivity.class);
        activityMap.put("3", ScheduleInspectionActivity.class);
        activityMap.put("4", ScheduleInspectionActivity.class);
        try {
            HideShow_Progress(true);
            alerts_error.setVisibility(View.GONE);
            if (CommonUtils.isNetworkAvailable(NotificationsActivity.this))
            {
                GetUserNotificatins();
            }
            else
            {
                GetNotifyFromDB();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void GetUserNotificatins()
    {
        CallUtils.enqueueWithRetry(APICalls.getNotifications(user.gettoken()),new Callback<ArrayList<NotifyModel>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<NotifyModel>> call, @NonNull Response<ArrayList<NotifyModel>> response) {
                if(CommonUtils.onTokenExpired(NotificationsActivity.this, response.code())){
                    return;
                }
                if (response.isSuccessful())
                {
                    DBHandler.getInstance().resetNotification();
                    ArrayList<NotifyModel> list = response.body();
                    if (list!=null && list.size()>0) {
                        for (NotifyModel modal : list)
                        {
                            DBHandler.getInstance().replaceData(NotifyModel.DBTable.NAME, modal.toContentValues());
                        }
                    }
                    else {
                        alerts_error.setVisibility(View.VISIBLE);
                        HideShow_Progress(false);
                    }
                    GetNotifyFromDB();
                }
                else {
                    alerts_error.setVisibility(View.VISIBLE);
                    HideShow_Progress(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<NotifyModel>> call, @NonNull Throwable t) {
                HideShow_Progress(false);
                alerts_error.setVisibility(View.VISIBLE);
            }
        });
    }
    private void GetNotifyFromDB()
    {
        ArrayList<NotifyModel> jobs = DBHandler.getInstance().getNotification();
        modalList.clear();
        if (jobs!=null && !jobs.isEmpty())
        {
            modalList.addAll(jobs);
            adaptor=new Alerts_Adaptor(modalList,NotificationsActivity.this);
            recyclerView.setAdapter(adaptor);
            adaptor.notifyDataSetChanged();
            alerts_error.setVisibility(View.GONE);
        }
        else
        {
            alerts_error.setVisibility(View.VISIBLE);
        }
        HideShow_Progress(false);
        NotifyUtils.scheduleJob(getApplicationContext(),true); // schedule the job for notify
    }
    private void ShowNotifyFilter(String Tags)
    {
        if (Tags.isEmpty())
        {
            return;
        }
        try {
            adaptor.getFilter().filter(Tags);
            //abhiAdaptor.filter(FilterType.NAME, s.toString(), false);
            //abhiAdaptor.filter(FilterType.NAME, s.toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void HideShow_Progress(boolean flag) {
        if (flag) {
            ll_ui_blocker.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE); }
        else
        {
            ll_ui_blocker.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
           case R.id.notify_all:
               ShowNotifyFilter("All");
            break;
            case R.id.notify_Actions:
                ShowNotifyFilter("1");
                break;
            case R.id.notify_briefings:
                ShowNotifyFilter("2");
                break;
            case R.id.notify_schedule:
                ShowNotifyFilter("3");
                break;
        }
    }

    @Override
    public void NotifyCallActivy(NotifyModel notifyModel) {
        if (!notifyModel.getHasBeenRead() && CommonUtils.isNetworkAvailable(getApplicationContext()))
        {
            HideShow_Progress(true);
            CallUtils.enqueueWithRetry(APICalls.callNotificationMarkRead(new NotifyReadPush(notifyModel.getNotificationId()),user.gettoken()),new Callback<NotifyReadPush>() {
                @Override
                public void onResponse(@NonNull Call<NotifyReadPush> call, @NonNull Response<NotifyReadPush> response) {
                    if(CommonUtils.onTokenExpired(NotificationsActivity.this, response.code())){
                        return;
                    }
                    if (response.isSuccessful())
                    {
                        if (activityMap.containsKey(String.valueOf(notifyModel.getNotificationType())))
                        {
                            Intent intent=new Intent(NotificationsActivity.this,activityMap.get(String.valueOf(notifyModel.getNotificationType())));
                            startActivity(intent);

                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Notification type enum found null/empty...", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            Toast.makeText(getApplicationContext(), jObjError.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                    HideShow_Progress(false);
                }

                @Override
                public void onFailure(@NonNull Call<NotifyReadPush> call, @NonNull Throwable t) {
                    HideShow_Progress(false);
                }
            });
        }
        else
        {
            if (activityMap.containsKey(String.valueOf(notifyModel.getNotificationType())))
            {
                Intent intent=new Intent(NotificationsActivity.this,activityMap.get(String.valueOf(notifyModel.getNotificationType())));
                startActivity(intent);
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Notification type enum found null/empty...", Toast.LENGTH_SHORT).show();
            }
        }
    }

}

class Alerts_Adaptor extends RecyclerView.Adapter<Alerts_Adaptor.AlertHolder> implements Filterable
{
    private ArrayList<NotifyModel> modals;
    private ArrayList<NotifyModel> modalsfull;
    private NotifyClickListner listner;

    public Alerts_Adaptor(ArrayList<NotifyModel> modalsfull, NotifyClickListner listner) {
        this.modalsfull = modalsfull;
        this.modals = modalsfull;//duplicate list
        this.listner = listner;
    }

    @NonNull
    @Override
    public Alerts_Adaptor.AlertHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.alerts_item_list, parent, false);
        return new AlertHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlertHolder holder, int position) {
    NotifyModel notifyModel=modalsfull.get(position);
    holder.title.setText(notifyModel.getNotificationText());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (notifyModel.getNotificationType() == 1 )
            {
                holder.iconcolor.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(holder.itemView.getContext(),R.color.ColorActions)));
            }
            else if (notifyModel.getNotificationType() == 2)
            {
                holder.iconcolor.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(holder.itemView.getContext(),R.color.ColorBriefing)));
            }
            else if (notifyModel.getNotificationType() == 3 || notifyModel.getNotificationType() == 4)
            {
                holder.iconcolor.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(holder.itemView.getContext(),R.color.ColorHseq)));
            }
            else
            {
                holder.iconcolor.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(holder.itemView.getContext(),R.color.ColorAlerts)));
            }
        }
        if (notifyModel.getHasBeenRead())
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.alerts_cards.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.windowBackground));
            }
        }
        else
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.alerts_cards.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.item_bg_light_gray));
            }
        }
        holder.alerts_cards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listner.NotifyCallActivy(notifyModel);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.alerts_cards.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.windowBackground));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return modalsfull==null ? 0 : modalsfull.size();
    }


    public class AlertHolder extends RecyclerView.ViewHolder {
        private CardView alerts_cards;
        private ImageView iconcolor;
        private TextView title;
        public AlertHolder(@NonNull View itemView) {
            super(itemView);
            alerts_cards=itemView.findViewById(R.id.alerts_cards);
            iconcolor=itemView.findViewById(R.id.alerts_icon);
            title=itemView.findViewById(R.id.alerts_title);
        }
    }
    @Override
    public Filter getFilter() {
        return new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                modalsfull = (ArrayList<NotifyModel>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<NotifyModel> filteredResults;
                if (constraint.length() == 0) {
                    filteredResults = modals;
                }
                else if (constraint.toString().equalsIgnoreCase("All"))
                {
                    filteredResults = modals;
                }
                else {
                    filteredResults = getFilteredResults(constraint.toString().toLowerCase());
                }

                FilterResults results = new FilterResults();
                results.values = filteredResults;

                return results;
            }
        };
    }

    private List<NotifyModel> getFilteredResults(String constraint) {
        List<NotifyModel> results = new ArrayList<>();

        for (NotifyModel item : modals) {
            if (String.valueOf(item.getNotificationType()).contains(constraint))
            {
                results.add(item);
            }
        }
        return results;
    }
}