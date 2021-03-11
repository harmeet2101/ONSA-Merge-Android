package co.uk.depotnet.onsa.fragments.briefings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import co.uk.depotnet.onsa.modals.briefings.BriefingsRecipient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import co.uk.depotnet.onsa.OnsaApp;
import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.activities.ui.PdfWorkActivity;
import co.uk.depotnet.onsa.adapters.briefing.BriefingsDocAdaptor;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.listeners.BriefingsListner;
import co.uk.depotnet.onsa.modals.briefings.BriefingsData;
import co.uk.depotnet.onsa.modals.briefings.BriefingsDocModal;
import co.uk.depotnet.onsa.networking.APICalls;
import co.uk.depotnet.onsa.networking.CommonUtils;
import co.uk.depotnet.onsa.utils.AppPreferences;
import co.uk.depotnet.onsa.utils.Utils;

public class ReadIssueFragment extends Fragment implements BriefingsListner {
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private TextView errormsg;
    private Context context;
    private List<BriefingsDocModal> docModals = new ArrayList<>();
    private BriefingsDocAdaptor docAdaptor;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public static ReadIssueFragment newInstance() {
        return new ReadIssueFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_read_issue, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = view.findViewById(R.id.progress_bar_readissue);
        recyclerView = view.findViewById(R.id.recycler_view);
        errormsg = view.findViewById(R.id.readissue_error);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        docAdaptor = new BriefingsDocAdaptor(context, this);
        recyclerView.setAdapter(docAdaptor);
        try {
            progressBar.setVisibility(View.VISIBLE);
            errormsg.setVisibility(View.GONE);
            if (CommonUtils.isNetworkAvailable(context)) {
                GetBriefingsCall();
            } else {
                getBriefingsFromDb();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void GetBriefingsCall() {
        APICalls.getBriefingsList(DBHandler.getInstance().getUser().gettoken()).enqueue(new Callback<List<BriefingsDocModal>>() {
            @Override
            public void onResponse(@NonNull Call<List<BriefingsDocModal>> call, @NonNull Response<List<BriefingsDocModal>> response) {
                if (CommonUtils.onTokenExpired(context, response.code())) {
                    return;
                }
                if (response.isSuccessful()) {
                    DBHandler.getInstance().clearTable(BriefingsDocModal.DBTable.NAME);
                    List<BriefingsDocModal> briefingsDocModals = response.body();
                    if (briefingsDocModals != null && briefingsDocModals.size() > 0) {
                        for (BriefingsDocModal modal : briefingsDocModals) {
                            modal.toContentValues();
//                            DBHandler.getInstance().replaceData(BriefingsDocModal.DBTable.NAME, modal.toContentValues());
                        }
                    } else {
                        errormsg.setVisibility(View.VISIBLE);
                    }
                    getBriefingsFromDb();
                } else {
                    errormsg.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<List<BriefingsDocModal>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                errormsg.setVisibility(View.VISIBLE);
                errormsg.setText(t.getLocalizedMessage());
            }
        });
    }

    private void getBriefingsFromDb() {
        List<BriefingsDocModal> briefings = DBHandler.getInstance().getBriefings();
        docModals.clear();
        if (briefings != null && !briefings.isEmpty()) {
            docModals.addAll(briefings);
            docAdaptor.UpdateBriefList(docModals);
            docAdaptor.notifyDataSetChanged();
            errormsg.setVisibility(View.GONE);
        } else {
            errormsg.setVisibility(View.VISIBLE);
        }
        progressBar.setVisibility(View.GONE);
    }


    @Override
    public void StartBriefingsRead(BriefingsDocModal briefingsDocModal) {
        ArrayList<String> recipientList = new ArrayList<>();
        ArrayList<String> selectedBriefings = new ArrayList<>();
        boolean firstSelectedFound = false;
        for (BriefingsDocModal data : docModals) {
            List<BriefingsData> briefingsData = data.getBriefings();
            for (int i = 0; i < briefingsData.size(); i++) {
                BriefingsData data1 = briefingsData.get(i);
                if (data1.isSelected()) {

                    List<BriefingsRecipient> recipients = data1.getRecipients();
                    if (recipients != null && !recipients.isEmpty()) {
                        if (!firstSelectedFound) {
                            firstSelectedFound = true;
                            for (BriefingsRecipient br : recipients) {
                                recipientList.add(br.getUserId());
                            }
                        } else {
                            ArrayList<String> temp = new ArrayList<>();
                            for (String key : recipientList) {
                                for (BriefingsRecipient br : recipients) {
                                    if (key.equalsIgnoreCase(br.getUserId())) {
                                        temp.add(key);
                                        break;
                                    }
                                }
                            }
                            recipientList.clear();
                            recipientList.addAll(temp);
                        }
                    }
                    selectedBriefings.add(data1.getBriefingId());
                }
            }
        }

        if (!CommonUtils.isNetworkAvailable(context)) {
            Toast.makeText(context, "You don't have active Internet connection...please connect to internet", Toast.LENGTH_LONG).show();
            return;
        }

        if (selectedBriefings.isEmpty()) {
            Toast.makeText(context, "Please select briefing document...", Toast.LENGTH_LONG).show();
            return;
        }

        if (recipientList.isEmpty()) {
            Toast.makeText(context, "No recipients found.", Toast.LENGTH_LONG).show();
            return;
        }

        AppPreferences.setTheme(2);//setting briefing theme..
        Intent pdfshow = new Intent(context, PdfWorkActivity.class);
        pdfshow.putExtra(PdfWorkActivity.Doc_Bag, briefingsDocModal);
        pdfshow.putStringArrayListExtra(PdfWorkActivity.Doc_selectedBriefings, selectedBriefings);
        pdfshow.putStringArrayListExtra(PdfWorkActivity.Doc_Recipient, recipientList);
        context.startActivity(pdfshow);
    }
}