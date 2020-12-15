package co.uk.depotnet.onsa.activities.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.activities.FormActivity;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.briefings.BriefingsDocument;
import co.uk.depotnet.onsa.modals.forms.Answer;
import co.uk.depotnet.onsa.modals.forms.Submission;
import co.uk.depotnet.onsa.utils.AppPreferences;

public class BriefingReadActivity extends AppCompatActivity {
    public static final String ARG_DOCS = "docsread";
    private TextView signature_read;
    private RecyclerView recyclerView;
    private Briefing_read_adaptor readAdaptor;
    ArrayList<BriefingsDocument> briefingsDocuments = new ArrayList<>();
    private ArrayList<String> recipients;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_briefing_read);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.ColorBriefing));
        }

        signature_read=findViewById(R.id.signature_read);
        recyclerView=findViewById(R.id.recycler_view_read);
        readAdaptor=new Briefing_read_adaptor();
        recyclerView.setAdapter(readAdaptor);
        recipients = getIntent().getStringArrayListExtra("Doc_Recipient");

            briefingsDocuments.clear();
            briefingsDocuments = this.getIntent().getExtras().getParcelableArrayList("readDocs");
            if (briefingsDocuments != null && briefingsDocuments.size() > 0){
                //briefingsDocuments.add(briefingsDocument);
                readAdaptor.SetList(briefingsDocuments);
                readAdaptor.notifyDataSetChanged();
                }
            else
            {
                Toast.makeText(this, "Read Documents not found...", Toast.LENGTH_SHORT).show();
            }

        signature_read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppPreferences.setTheme(2);
                String jsonFileName = "briefing_sign.json";// case sensitive for submission model too
                Submission submission = new Submission(jsonFileName, "Briefing Sign", "0");// if jobid not than 0
                // unique submission id for every form
                long submissionID = DBHandler.getInstance().insertData(Submission.DBTable.NAME, submission.toContentValues());
                submission.setId(submissionID);
                Intent intent = new Intent(BriefingReadActivity.this, FormActivity.class);
                intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
                intent.putParcelableArrayListExtra(FormActivity.ARG_DOCS, briefingsDocuments);
                intent.putStringArrayListExtra(FormActivity.Doc_Recipient, recipients);
                startActivity(intent);

            }
        });
    }


}

class Briefing_read_adaptor extends RecyclerView.Adapter<Briefing_read_adaptor.ReadHoler>
{
    List<BriefingsDocument> BriefingsDocuments=new ArrayList<>();
    @NonNull
    @Override
    public Briefing_read_adaptor.ReadHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReadHoler(LayoutInflater.from(parent.getContext()).inflate(R.layout.briefings_read_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull Briefing_read_adaptor.ReadHoler holder, int position) {
        BriefingsDocument read=BriefingsDocuments.get(position);
        holder.textView.setText(read.getBriefingName());
    }

    @Override
    public int getItemCount() {
        return BriefingsDocuments.size();
    }
    void SetList(List<BriefingsDocument> readList)
    {
        BriefingsDocuments.clear();
        BriefingsDocuments.addAll(readList);
        notifyDataSetChanged();

    }

    public class ReadHoler extends RecyclerView.ViewHolder {
        private TextView textView;
        public ReadHoler(@NonNull View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.read_title);
        }
    }
}