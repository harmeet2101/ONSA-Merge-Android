package co.uk.depotnet.onsa.fragments.briefings;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.activities.ui.BriefingsActivity;
import co.uk.depotnet.onsa.activities.ui.Briefings_Issued;
import co.uk.depotnet.onsa.activities.ui.Briefings_Received;

public class BriefingFragment extends Fragment implements View.OnClickListener {

    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public static BriefingFragment newInstance() {
        BriefingFragment fragment = new BriefingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_briefing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.btn_read_issue).setOnClickListener(this);
        view.findViewById(R.id.btn_briefing_given).setOnClickListener(this);
        view.findViewById(R.id.btn_briefing_received).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_read_issue:
               if (((BriefingsActivity)context).checkAndRequestPermissions())
               {
                   LoadReadIssueFrag();
               }
                break;
            case R.id.btn_briefing_given:
                Intent intent=new Intent(getContext(), Briefings_Issued.class);
                startActivity(intent);
                break;
            case R.id.btn_briefing_received:
                Intent intentI=new Intent(getContext(), Briefings_Received.class);
                startActivity(intentI);
                break;
            default:
                break;
        }
    }
    private void LoadReadIssueFrag()
    {
        ReadIssueFragment fragment = ReadIssueFragment.newInstance();
        ((BriefingsActivity)context).ABHIKRCall(fragment,true);
    }
}