package co.uk.depotnet.onsa.adapters.timesheet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.activities.FormActivity;
import co.uk.depotnet.onsa.activities.ThemeBaseActivity;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.formholders.LogDayHoursViewHolder;
import co.uk.depotnet.onsa.modals.forms.Answer;
import co.uk.depotnet.onsa.modals.forms.Submission;
import co.uk.depotnet.onsa.modals.timesheet.LogHourItem;
import co.uk.depotnet.onsa.modals.timesheet.LogHourTime;
import co.uk.depotnet.onsa.networking.CommonUtils;
import co.uk.depotnet.onsa.utils.AppPreferences;
import co.uk.depotnet.onsa.utils.Utils;

public class LohHoursAdapter extends
        RecyclerView.Adapter<LogDayHoursViewHolder> {

    private final List<LogHourItem> logHourItems;
    private final Context context;
    private final DBHandler dbHandler;

    public LohHoursAdapter(Context context) {
        this.context = context;
        this.logHourItems = new ArrayList<>();
        dbHandler = DBHandler.getInstance();
    }

    @NonNull
    @Override
    public LogDayHoursViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        return new LogDayHoursViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_day_log_hours, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LogDayHoursViewHolder holder,
                                 int position) {
        LogHourItem timeSheetHour = logHourItems.get(position);
        String status = dbHandler.getTimeSheetsStatus(timeSheetHour.getWeekCommencing());
        holder.setColor(status);
//        holder.imgBtnDelete.setVisibility(View.GONE);
        if (TextUtils.isEmpty(timeSheetHour.getTimeTypeName()) && TextUtils.isEmpty(timeSheetHour.getJobId())) {
            holder.llHoursContainer.setVisibility(View.GONE);
            holder.txtNoLoggedHours.setVisibility(View.VISIBLE);
        } else {
            holder.llHoursContainer.setVisibility(View.VISIBLE);
            holder.txtNoLoggedHours.setVisibility(View.GONE);
            holder.txtTaskType.setText(timeSheetHour.getTimeTypeName());
            holder.txtJobReference.setText(timeSheetHour.getEstimate());
            holder.txtOperatives.setVisibility(View.GONE);
            try {
                holder.txtDate.setText(Utils.getSimpleDateFormat(timeSheetHour.getDateWorked()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            addLayout(holder , timeSheetHour.getLogHourTimes());

            holder.llTotalHours5.setText(timeSheetHour.getTotalHoursText());
        }

        holder.itemView.setOnClickListener(v -> {
            if(timeSheetHour.isApproved() || timeSheetHour.isWaitingApproval()){
                return;
            }

            Intent intent = new Intent(context , FormActivity.class);
            AppPreferences.setTheme(ThemeBaseActivity.THEME_TIME_SHEET);
            String jsonFileName = "timesheet_log_hours.json";
            Submission submission = new Submission(jsonFileName, "Log Hours", "");
            long submissionID = dbHandler.insertData(Submission.DBTable.NAME, submission.toContentValues());
            submission.setId(submissionID);

            timeSheetHour.saveAnswers(submission.getID() , "timesheetHours");
            intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
            ((Activity)context).startActivityForResult(intent , 10001);
        });
    }

    public void update(List<LogHourItem> logHourItems) {
        this.logHourItems.clear();
        if (logHourItems == null || logHourItems.isEmpty()) {
            notifyDataSetChanged();
            return;
        }

        this.logHourItems.addAll(logHourItems);

        notifyDataSetChanged();
    }


    private void addLayout(LogDayHoursViewHolder viewHolder, ArrayList<LogHourTime> times){
        if(times.isEmpty()){
            return;
        }

        viewHolder.llRow1.setVisibility(View.GONE);
        viewHolder.llRow2.setVisibility(View.GONE);
        viewHolder.llRow3.setVisibility(View.GONE);
        viewHolder.llRow4.setVisibility(View.GONE);

        for(int i = 0 ; i < times.size() ; i++){
            if(i == 0){
                viewHolder.llRow1.setVisibility(View.VISIBLE);
                viewHolder.txtTimeTypeName1.setText(times.get(i).getTimeTypeActivityName());
                viewHolder.txtNormalTime1.setText(times.get(i).getNormalTimeHours());
                viewHolder.llOverTime1.setText(times.get(i).getOvertimeHours());
                viewHolder.llTotalHours1.setText(times.get(i).getTotalHoursText());
            }
            if(i == 1){
                viewHolder.llRow2.setVisibility(View.VISIBLE);
                viewHolder.txtTimeTypeName2.setText(times.get(i).getTimeTypeActivityName());
                viewHolder.txtNormalTime2.setText(times.get(i).getNormalTimeHours());
                viewHolder.llOverTime2.setText(times.get(i).getOvertimeHours());
                viewHolder.llTotalHours2.setText(times.get(i).getTotalHoursText());
            }

            if(i == 2){
                viewHolder.llRow3.setVisibility(View.VISIBLE);
                viewHolder.txtTimeTypeName3.setText(times.get(i).getTimeTypeActivityName());
                viewHolder.txtNormalTime3.setText(times.get(i).getNormalTimeHours());
                viewHolder.llOverTime3.setText(times.get(i).getOvertimeHours());
                viewHolder.llTotalHours3.setText(times.get(i).getTotalHoursText());
            }

            if(i == 3){
                viewHolder.llRow4.setVisibility(View.VISIBLE);
                viewHolder.txtTimeTypeName4.setText(times.get(i).getTimeTypeActivityName());
                viewHolder.txtNormalTime4.setText(times.get(i).getNormalTimeHours());
                viewHolder.llOverTime4.setText(times.get(i).getOvertimeHours());
                viewHolder.llTotalHours4.setText(times.get(i).getTotalHoursText());
            }
        }
    }

    @Override
    public int getItemCount() {
        return logHourItems.size();
    }

    public int getTotalTime(){
        int totalTime = 0;
        for (int  i = 0 ; i < logHourItems.size() ; i++){
            totalTime+= logHourItems.get(i).getTotalTime();
        }
        return totalTime;
    }

    public String getTotalHoursText(){
        int totalTimeInMinutes = getTotalTime();
        return CommonUtils.getDisplayTime(totalTimeInMinutes);
    }
}
