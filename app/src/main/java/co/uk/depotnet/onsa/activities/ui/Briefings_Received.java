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

import co.uk.depotnet.onsa.networking.CallUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.adapters.IssueDocAdapter;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.modals.briefings.IssuedDocModal;
import co.uk.depotnet.onsa.modals.briefings.ReceivedModel;
import co.uk.depotnet.onsa.networking.APICalls;
import co.uk.depotnet.onsa.networking.CommonUtils;
import co.uk.depotnet.onsa.utils.Utils;

public class Briefings_Received extends AppCompatActivity {

    private User user;
    private ProgressBar progressBar;
    private Briefings_Received_Adaptor mAdaptor;
    private List<ReceivedModel> modelList=new ArrayList<>();
    private TextView errormsg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_briefings_received);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.ColorBriefing));
        }
        user=DBHandler.getInstance().getUser();
        progressBar=findViewById(R.id.progress_bar_received);
        RecyclerView recyclerView_received = findViewById(R.id.recyclerView_received);
        mAdaptor=new Briefings_Received_Adaptor(Briefings_Received.this);
        recyclerView_received.setAdapter(mAdaptor);
        errormsg=findViewById(R.id.received_error);
       /* ReceivedModel issuedModel=new ReceivedModel();
        issuedModel.setDateRead("3rd august");
        issuedModel.setBriefedByUserFullName("Darren Bridge");
        List<IssuedDocModal> docModalList=new ArrayList<>();
        IssuedDocModal issuedDocModal=new IssuedDocModal();
        issuedDocModal.setBriefingId("1");
        issuedDocModal.setBriefingName("Covid-19 test");
        docModalList.add(issuedDocModal);
        IssuedDocModal issuedDocModal1=new IssuedDocModal();
        issuedDocModal1.setBriefingId("2");
        issuedDocModal1.setBriefingName("dev test");
        docModalList.add(issuedDocModal1);
        issuedModel.setBriefings(docModalList);
        modelList.add(issuedModel);
        mAdaptor.UpdateDoc(modelList);*/
        try {
            progressBar.setVisibility(View.VISIBLE);
            errormsg.setVisibility(View.GONE);
            if(!CommonUtils.isNetworkAvailable(getApplicationContext())) {
                getIssuedFromDb();
            }
            else
            {
                GetReceivedDocs();;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        findViewById(R.id.received_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    protected void GetReceivedDocs()
    {
        CallUtils.enqueueWithRetry(APICalls.getBriefingsReceivedList(user.gettoken()),new Callback<List<ReceivedModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<ReceivedModel>> call, @NonNull Response<List<ReceivedModel>> response) {
                if(CommonUtils.onTokenExpired(Briefings_Received.this , response.code())){
                    return;
                }
                if (response.isSuccessful()) {
                    DBHandler.getInstance().resetBriefingsRead();
                    List<ReceivedModel> issuedModels=response.body();
                    if (issuedModels!=null && !issuedModels.isEmpty())
                    {
                       for (ReceivedModel receivedModel : issuedModels)
                       {
                           DBHandler.getInstance().replaceData(ReceivedModel.DBTable.NAME,receivedModel.toContentValues());
                       }
                    }
                    else
                    {
                        errormsg.setVisibility(View.VISIBLE);
                    }
                    getIssuedFromDb();
                }
                else
                {
                    errormsg.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<List<ReceivedModel>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                errormsg.setVisibility(View.VISIBLE);
            }
        });
    }
    private void getIssuedFromDb()
    {
        List<ReceivedModel> jobs = DBHandler.getInstance().getBriefingsRead();
        modelList.clear();
        if (jobs!=null && !jobs.isEmpty())
        {
            modelList.addAll(jobs);
            mAdaptor.UpdateDoc(jobs);
            mAdaptor.notifyDataSetChanged();
            errormsg.setVisibility(View.GONE);
        }
        else
        {
            errormsg.setVisibility(View.VISIBLE);
        }
        progressBar.setVisibility(View.GONE);
    }
}

class Briefings_Received_Adaptor extends RecyclerView.Adapter<Briefings_Received_Adaptor.IssuedHolder>
{
    private Context mContext;
    private List<ReceivedModel> modelList=new ArrayList<>();
    private IssueDocAdapter dataAdapter;
    private List<IssuedDocModal> docsList;
    public Briefings_Received_Adaptor(Context mContext) {
        this.mContext = mContext;
        this.docsList=new ArrayList<>();
    }

    @NonNull
    @Override
    public Briefings_Received_Adaptor.IssuedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new IssuedHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.briefings_received_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull Briefings_Received_Adaptor.IssuedHolder holder, int position) {
        ReceivedModel model=modelList.get(position);
        try {
            holder.date.setText(Utils.getSimpleDateFormat(model.getDateRead()));
        } catch (ParseException e) {
            e.printStackTrace();
            holder.date.setText(model.getDateRead());
        }
        holder.users.setText(model.getBriefedByUserFullName());
        String docscount= String.valueOf(model.getBriefings().size());
        String spinnnerheader=docscount.concat(" document");
        docsList=model.getBriefings();
        IssuedDocModal issuedDocModal=new IssuedDocModal();
        issuedDocModal.setBriefingId("1");
        issuedDocModal.setBriefingName(spinnnerheader);
        docsList.add(0,issuedDocModal);
        dataAdapter  = new IssueDocAdapter(mContext,
               R.layout.spinner_item,model.getBriefings()
                );
        //dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.docs.setAdapter(dataAdapter);
        //holder.docs.setSelection(0,false); // test check
        holder.docs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                IssuedDocModal issuedocs = dataAdapter.getItem(position);
                if (position>0)
                    Log.d("abhikr","abhikr: "+"ID: " + issuedocs.getBriefingId() + "\nName: " + issuedocs.getBriefingName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return modelList==null ? 0 : modelList.size();
    }
    void UpdateDoc(List<ReceivedModel> issuedModelList)
    {
        modelList.clear();
        modelList.addAll(issuedModelList);
        notifyDataSetChanged();
    }
    public class IssuedHolder extends RecyclerView.ViewHolder {
        private TextView date,users;
        private AppCompatSpinner docs;
        public IssuedHolder(@NonNull View itemView) {
            super(itemView);
            date=itemView.findViewById(R.id.briefings_received_date);
            users=itemView.findViewById(R.id.briefings_received_user);
            docs=itemView.findViewById(R.id.briefings_received_spinner);
        }
    }
}