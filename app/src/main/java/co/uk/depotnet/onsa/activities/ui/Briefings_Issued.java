package co.uk.depotnet.onsa.activities.ui;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.adapters.IssueDocAdapter;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.modals.briefings.IssuedDocModal;
import co.uk.depotnet.onsa.modals.briefings.IssuedModel;
import co.uk.depotnet.onsa.networking.APICalls;
import co.uk.depotnet.onsa.networking.CommonUtils;
import co.uk.depotnet.onsa.utils.Utils;

public class Briefings_Issued extends AppCompatActivity {

    private User user;
    private ProgressBar progressBar;
    private Briefings_Issued_Adaptor mAdaptor;
    private List<IssuedModel> modelList = new ArrayList<>();
    private TextView errormsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_briefings_issued);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.ColorBriefing));
        }
        user = DBHandler.getInstance().getUser();
        progressBar = findViewById(R.id.progress_bar_issued);
        RecyclerView recyclerView_received = findViewById(R.id.recyclerView_issued);
        mAdaptor = new Briefings_Issued_Adaptor(Briefings_Issued.this);
        recyclerView_received.setAdapter(mAdaptor);
        errormsg = findViewById(R.id.issued_error);
        try {
            progressBar.setVisibility(View.VISIBLE);
            if (!CommonUtils.isNetworkAvailable(getApplicationContext())) {
                getIssuedFromDb();
            } else {
                GetIssuedDoc();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        findViewById(R.id.issued_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    protected void GetIssuedDoc() {
        errormsg.setVisibility(View.GONE);
        APICalls.getBriefingsIssuedList(user.gettoken()).enqueue(new Callback<List<IssuedModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<IssuedModel>> call, @NonNull Response<List<IssuedModel>> response) {
                if (CommonUtils.onTokenExpired(Briefings_Issued.this, response.code())) {
                    return;
                }
                if (response.isSuccessful()) {
                    DBHandler.getInstance().resetBriefingsShared();
                    List<IssuedModel> issuedModels = response.body();
                    if (issuedModels != null) {
                        for (IssuedModel schedule : issuedModels) {
                            DBHandler.getInstance().replaceData(IssuedModel.DBTable.NAME, schedule.toContentValues());
                        }
                    }
                    getIssuedFromDb();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<List<IssuedModel>> call, @NonNull Throwable t) {
                Log.d("abhikr", "briefings issues response error: " + t.getLocalizedMessage());
                progressBar.setVisibility(View.GONE);
                errormsg.setVisibility(View.VISIBLE);
                errormsg.setText(t.getLocalizedMessage());
            }
        });
    }

    private void getIssuedFromDb() {
        List<IssuedModel> jobs = DBHandler.getInstance().getBriefingsShared();
        modelList.clear();
        if (jobs != null && !jobs.isEmpty()) {
            modelList.addAll(jobs);
            mAdaptor.UpdateDoc(jobs);
            mAdaptor.notifyDataSetChanged();
            errormsg.setVisibility(View.GONE);
        } else {
            errormsg.setVisibility(View.VISIBLE);
        }
        progressBar.setVisibility(View.GONE);
    }
}

class Briefings_Issued_Adaptor extends RecyclerView.Adapter<Briefings_Issued_Adaptor.IssuedHolder> {
    private Context mContext;
    private List<IssuedModel> modelList = new ArrayList<>();
    private IssueDocAdapter dataAdapter;
    private List<IssuedDocModal> docsList;
    private Briefing_issued_subadaptor issuedSubadaptor;

    public Briefings_Issued_Adaptor(Context mContext) {
        this.mContext = mContext;
        this.docsList = new ArrayList<>();
        issuedSubadaptor = new Briefing_issued_subadaptor();
    }

    @NonNull
    @Override
    public Briefings_Issued_Adaptor.IssuedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new IssuedHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.briefings_issued_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Briefings_Issued_Adaptor.IssuedHolder holder, int position) {
        IssuedModel model = modelList.get(position);
        try {
            holder.date.setText(Utils.getSimpleDateFormat(model.getDateRead()));
        } catch (ParseException e) {
            e.printStackTrace();
            holder.date.setText(model.getDateRead());
        }
        holder.users.setAdapter(issuedSubadaptor);
        issuedSubadaptor.SetList(model.getOperativeNames());
        String docscount = String.valueOf(model.getBriefings().size());
        String spinnnerheader = docscount.concat(" document");
        docsList = model.getBriefings();
        IssuedDocModal issuedDocModal = new IssuedDocModal();
        issuedDocModal.setBriefingId("1");
        issuedDocModal.setBriefingName(spinnnerheader);
        docsList.add(0, issuedDocModal);
        dataAdapter = new IssueDocAdapter(mContext,
                R.layout.spinner_item, model.getBriefings()
        );
        //dataAdapter  = new SpinnAdaptor(mContext, android.R.layout.simple_spinner_item,model.getBriefings());
        //dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.docs.setAdapter(dataAdapter);
        holder.docs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                IssuedDocModal issuedocs = dataAdapter.getItem(position);
                if (position > 0)
                    Log.d("abhikr", "abhikr: " + "ID: " + issuedocs.getBriefingId() + "\nName: " + issuedocs.getBriefingName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return modelList == null ? 0 : modelList.size();
    }

    void UpdateDoc(List<IssuedModel> issuedModelList) {
        modelList.clear();
        modelList.addAll(issuedModelList);
        notifyDataSetChanged();
    }

    public class IssuedHolder extends RecyclerView.ViewHolder {
        private TextView date;
        private AppCompatSpinner docs;
        private RecyclerView users;

        public IssuedHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.briefings_issued_date);
            users = itemView.findViewById(R.id.briefings_issued_user);
            docs = itemView.findViewById(R.id.briefings_issued_spinner);
        }
    }
}

class Briefing_issued_subadaptor extends RecyclerView.Adapter<Briefing_issued_subadaptor.SubHolder> {
    List<String> strings = new ArrayList<>();

    @NonNull
    @Override
    public Briefing_issued_subadaptor.SubHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SubHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.briefings_issued_subitem, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Briefing_issued_subadaptor.SubHolder holder, int position) {
        String data = strings.get(position);
        holder.bioperative.setText(data);
    }

    @Override
    public int getItemCount() {
        return strings.size();
    }

    public void SetList(List<String> list) {
        strings.clear();
        strings.addAll(list);
        notifyDataSetChanged();
    }

    public class SubHolder extends RecyclerView.ViewHolder {
        private TextView bioperative;

        public SubHolder(@NonNull View itemView) {
            super(itemView);
            bioperative = itemView.findViewById(R.id.BIsubitem_operative);
        }
    }
}