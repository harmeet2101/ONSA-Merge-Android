package co.uk.depotnet.onsa.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.activities.FormActivity;
import co.uk.depotnet.onsa.activities.PollingSurveyActivity;
import co.uk.depotnet.onsa.adapters.OfflineQueueAdapter;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.fragments.store.ReceiptItemsFragment;
import co.uk.depotnet.onsa.listeners.FragmentActionListener;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.modals.forms.Answer;
import co.uk.depotnet.onsa.modals.forms.Submission;
import co.uk.depotnet.onsa.modals.store.MyStore;
import co.uk.depotnet.onsa.modals.store.Receipts;
import co.uk.depotnet.onsa.networking.CommonUtils;
import co.uk.depotnet.onsa.utils.VerticalSpaceItemDecoration;
import co.uk.depotnet.onsa.views.MaterialAlertDialog;


public class FragmentQueue extends Fragment implements OfflineQueueAdapter.QueueListener {

    ArrayList<Submission> submissions;
    private Context context;
    private OfflineQueueAdapter adapter;
    private FragmentActionListener listener;
    private User user;

    public static FragmentQueue newInstance() {
        FragmentQueue fragment = new FragmentQueue();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof FragmentActionListener) {
            listener = (FragmentActionListener) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        submissions = new ArrayList<>();
        user = DBHandler.getInstance().getUser();
        adapter = new OfflineQueueAdapter(context, submissions, this);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_queue, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        VerticalSpaceItemDecoration itemDecoration = new VerticalSpaceItemDecoration(20);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        submissions.clear();
        submissions.addAll(DBHandler.getInstance().getQueuedSubmissions());
        adapter.notifyDataSetChanged();
    }

    public void showErrorDialog(String title, String message) {
        MaterialAlertDialog dialog = new MaterialAlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositive(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                })
                .build();

        dialog.setCancelable(false);
        dialog.show(getChildFragmentManager(), "_ERROR_DIALOG");
    }

    public void showQueue(final int position , final String title, String message, final Submission submission) {
        MaterialAlertDialog dialog = new MaterialAlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositive(getString(R.string.submit), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        String json = submission.getJsonFileName();

                        dialog.dismiss();
                        if (!TextUtils.isEmpty(json) && (json.startsWith("store_log_"))) {
                            ArrayList<Answer> answers = DBHandler.getInstance().getRepeatedAnswers(submission.getID() , "StaStockItemId" ,
                                    json.equalsIgnoreCase("store_log_issue.json") ? null: "Items");
                            if(answers.isEmpty()){
                                return;
                            }

                            HashMap<String , Object> map = new HashMap<>();

                            for (Answer a : answers){
                                if(a != null && !TextUtils.isEmpty(a.getAnswer())) {
                                    Answer answer = DBHandler.getInstance().getAnswer(submission.getID(),
                                            "Quantity",
                                            a.getRepeatID(), a.getRepeatCount());
                                    if(answer != null) {
                                        MyStore store = DBHandler.getInstance().getMyStores(a.getAnswer());
                                        if(store != null) {
                                            map.put(a.getAnswer(), store);
                                            map.put(a.getAnswer() + "_qty", Integer.parseInt(answer.getAnswer()));
                                        }
                                    }
                                }
                            }

                            Intent intent = new Intent(context, FormActivity.class);
                            intent.putExtra(FormActivity.ARG_USER, DBHandler.getInstance().getUser());
                            intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
                            intent.putExtra(FormActivity.ARG_MY_STORE_ITEMS, map);
                            startActivityForResult(intent, 1000);

                        }else if (!TextUtils.isEmpty(title) && (title.equalsIgnoreCase("receipt_accept") || title.equalsIgnoreCase("receipt_reject"))) {
                            Answer answer = DBHandler.getInstance().getAnswer(submission.getID(), "MyReceiptID",
                                    null, 0);

                            if (answer != null) {
                                Receipts receipts = DBHandler.getInstance().getReceipt(answer.getAnswer());
                                listener.addFragment(ReceiptItemsFragment.newInstance(receipts, user, submission.getID()),
                                        false);
                            }

                        } else if (TextUtils.isEmpty(title) || !title.equalsIgnoreCase("poling_survey")) {
                            Intent intent = new Intent(context, FormActivity.class);
                            intent.putExtra(FormActivity.ARG_USER, user);
                            intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(context, PollingSurveyActivity.class);
                            intent.putExtra(FormActivity.ARG_USER, user);
                            intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
                            intent.putExtra(FormActivity.ARG_REPEAT_COUNT, 0);
                            startActivityForResult(intent, 1234);
                        }
                    }
                }).setNegative(getString(R.string.delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                        submissions.remove(position);
                        DBHandler.getInstance().removeAnswers(submission);
                        adapter.notifyDataSetChanged();
                    }
                }).build();

        dialog.setCancelable(false);
        dialog.show(getChildFragmentManager(), "_ERROR_DIALOG");
    }

    @Override
    public void onItemClick(final Submission submission, final int position) {
        if (!CommonUtils.isNetworkAvailable(context)) {
            String title = "Submission Error";
            String message = "Internet connection is not available. Please check your internet connection.";
            showErrorDialog(title, message);
            listener.hideProgressBar();
            return;
        }

        showQueue(position , submission.getTitle(),
                "Do you want to submit this information to server?", submission);

    }
}
