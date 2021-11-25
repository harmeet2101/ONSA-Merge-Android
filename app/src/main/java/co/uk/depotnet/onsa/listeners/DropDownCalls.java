package co.uk.depotnet.onsa.listeners;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import java.util.ArrayList;

import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.DCRReasons;
import co.uk.depotnet.onsa.modals.ItemType;
import co.uk.depotnet.onsa.modals.Job;
import co.uk.depotnet.onsa.modals.JobWorkItem;
import co.uk.depotnet.onsa.modals.MeasureItems;
import co.uk.depotnet.onsa.modals.MenSplit;
import co.uk.depotnet.onsa.modals.RecordReturnReason;
import co.uk.depotnet.onsa.modals.TransferTypes;
import co.uk.depotnet.onsa.modals.WorkItem;
import co.uk.depotnet.onsa.modals.forms.Answer;
import co.uk.depotnet.onsa.modals.forms.FormItem;
import co.uk.depotnet.onsa.modals.forms.Submission;
import co.uk.depotnet.onsa.modals.hseq.HseqDataset;
import co.uk.depotnet.onsa.modals.incident.IncidentCategory;
import co.uk.depotnet.onsa.modals.incident.IncidentSeverity;
import co.uk.depotnet.onsa.modals.incident.IncidentSource;
import co.uk.depotnet.onsa.modals.incident.IncidentSubCategory;
import co.uk.depotnet.onsa.modals.incident.UniqueIncident;
import co.uk.depotnet.onsa.modals.responses.DatasetResponse;
import co.uk.depotnet.onsa.modals.timesheet.TimesheetOperative;
import co.uk.depotnet.onsa.utils.AppPreferences;

public class DropDownCalls {

    private final FormItem formItem;
    private final Submission submission;
    private final Handler handler;
    private final String estimateGangId;
    private int count = 0;
    private final DropDownDataListner listener;
    private final DBHandler dbHandler;
    private boolean isDatasetEndpointFinished;


    public DropDownCalls(FormItem formItem, Submission submission, String estimateGangId, DropDownDataListner listener) {
        this.formItem = formItem;
        this.submission = submission;
        this.estimateGangId = estimateGangId;
        this.listener = listener;
        dbHandler = DBHandler.getInstance();
        handler = new Handler(Looper.myLooper());
        this.isDatasetEndpointFinished = AppPreferences.getBoolean("DatasetEndpoint" , false);
    }

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            final ArrayList<DropDownItem> items = new ArrayList<>();
            String uploadId = formItem.getUploadId();
            boolean isDependOnDatasetEndpoint = true;
            if (uploadId.equalsIgnoreCase("noticeId")) {
                isDependOnDatasetEndpoint = false;
                items.addAll(dbHandler.getNotices(submission.getJobID()));
            }else if (uploadId.equalsIgnoreCase("jobEtonSiteId")) {
                isDependOnDatasetEndpoint = false;
                items.addAll(dbHandler.getNotices(submission.getJobID()));
            }else if (formItem.getKey().equalsIgnoreCase(Job.DBTable.NAME)) {
                isDependOnDatasetEndpoint = false;
                items.addAll(dbHandler.getJobs());
            }else if (formItem.getKey().equalsIgnoreCase("NormalJobs")) {
                isDependOnDatasetEndpoint = false;
                items.addAll(dbHandler.getNormalJobsOnly());
            } else if (formItem.getKey().equalsIgnoreCase(DatasetResponse.DBTable.dfeWorkItems)) {
                items.addAll(dbHandler.getWorkItem(DatasetResponse.DBTable.dfeWorkItems,
                        WorkItem.DBTable.itemCode));
            } else if (formItem.getKey().equalsIgnoreCase(JobWorkItem.DBTable.NAME)) {
                items.addAll(dbHandler.getJobWorkItem(submission.getJobID()));
            } else if (formItem.getKey().equalsIgnoreCase(MeasureItems.DBTable.NAME)) {
                items.addAll(dbHandler.getMeasures());
            } else if (formItem.getKey().equalsIgnoreCase(MenSplit.DBTable.NAME)) {
                items.addAll(dbHandler.getMenSplit());
            } else if (formItem.getKey().equalsIgnoreCase(HseqDataset.DBTable.OperativesHseq)) {
                if (estimateGangId != null && !estimateGangId.isEmpty()) {
                    items.addAll(dbHandler.getOperativeTemplateByGangId(estimateGangId));
                } else {
                    items.addAll(dbHandler.getOperativeHseq());
                }
            } else if (formItem.getKey().equalsIgnoreCase(HseqDataset.DBTable.InspectionHseq)) {
                items.addAll(dbHandler.getInspectionHseq());
            } else if (formItem.getKey().equalsIgnoreCase(HseqDataset.DBTable.InspectorsHseq)) {
                items.addAll(dbHandler.getInspectorHseq());
            } else if (formItem.getKey().equalsIgnoreCase(DatasetResponse.DBTable.engCompOutcomes)) {
                ArrayList<ItemType> outComes = dbHandler.getItemTypes(DatasetResponse.DBTable.engCompOutcomes);


                Job job = dbHandler.getJob(submission.getJobID());
                if(job!= null && job.isSubJob()){
                    items.addAll(outComes);
                }else {
                    boolean hasRFNA = job != null && job.hasRFNA();
                    boolean rfnaNotRequired = job != null && job.rfnaNotRequired();
                    for (int i = 0; i < outComes.size(); i++) {
                        String displayItems = outComes.get(i).getDisplayItem();
                        boolean isSuccessMessage = displayItems.startsWith("01") || displayItems.startsWith("02");
                        String prefKey = AppPreferences.getString("RadioButton_" + submission.getJobID(), null);
                        if (isSuccessMessage) {
                            if (!rfnaNotRequired && (hasRFNA || (!TextUtils.isEmpty(prefKey) && prefKey.equalsIgnoreCase("true")))) {
                                items.add(outComes.get(i));
                            }
                        } else if (rfnaNotRequired || (!hasRFNA && (!TextUtils.isEmpty(prefKey) && prefKey.equalsIgnoreCase("false")))) {
                            items.add(outComes.get(i));
                        }
                    }
                }
            } else if (formItem.getKey().equalsIgnoreCase(RecordReturnReason.DBTable.NAME)) {
                ArrayList<RecordReturnReason> recordReturnReasons = dbHandler.getRecordReturnReasons(submission.getJobID());
                items.addAll(recordReturnReasons);
            }else if (formItem.getKey().equalsIgnoreCase(DCRReasons.DBTable.NAME)) {
                isDependOnDatasetEndpoint = false;
                ArrayList<DCRReasons> dcrReasons = dbHandler.getDCRReason(submission.getJobID());
                items.addAll(dcrReasons);
            }else if (formItem.getKey().equalsIgnoreCase(TimesheetOperative.DBTable.NAME)) {
                ArrayList<TimesheetOperative> timeSheetOperatives = dbHandler.getTimeSheetOperatives();
                items.addAll(timeSheetOperatives);
            }else if (formItem.getKey().equalsIgnoreCase(IncidentSource.DBTable.NAME)) {
                ArrayList<IncidentSource> incidentSources = DBHandler.getInstance().getIncidentSources();
                items.addAll(incidentSources);
            } else if (formItem.getKey().equalsIgnoreCase(IncidentCategory.DBTable.NAME)) {
                ArrayList<IncidentCategory> incidentCategories = DBHandler.getInstance().getIncidentCategories();
                items.addAll(incidentCategories);
            } else if (formItem.getKey().equalsIgnoreCase(IncidentSubCategory.DBTable.NAME)) {
                Answer answer1 = DBHandler.getInstance().getAnswer(submission.getID(),
                        "incidentCategoryId",
                        formItem.getRepeatId(), formItem.getRepeatCount());
                if (answer1 == null || TextUtils.isEmpty(answer1.getAnswer())) {
                    listener.showValidationDialog("Error", "Please select incident category first.");
                    return;
                }

                ArrayList<IncidentSubCategory> incidentSubCategories = DBHandler.getInstance().getIncidentSubCategories(answer1.getAnswer());
                items.addAll(incidentSubCategories);
            } else if (formItem.getKey().equalsIgnoreCase(UniqueIncident.DBTable.NAME)) {
                ArrayList<UniqueIncident> uniqueIncidents = DBHandler.getInstance().getUniqueIncident();
                items.addAll(uniqueIncidents);
            } else if (formItem.getKey().equalsIgnoreCase(IncidentSeverity.DBTable.NAME)) {

                ArrayList<IncidentSeverity> incidentSeverities = DBHandler.getInstance().getIncidentSeverities();
                items.addAll(incidentSeverities);
            } else if (formItem.getKey().equalsIgnoreCase("TransferType")){

                ArrayList<TransferTypes> transferTypes = new ArrayList<>();
                transferTypes.add(new TransferTypes("STA","STA"));
                transferTypes.add(new TransferTypes("MTA","MTA"));
                items.addAll(transferTypes);
            }else {
                items.addAll(dbHandler.getItemTypes(formItem.getKey()));
            }

            if(!isDatasetEndpointFinished) {
                if (isDependOnDatasetEndpoint && items.isEmpty() && count < 120) {
                    count++;
                    handler.postDelayed(runnable, 2000);
                    return;
                }
            }else{
                isDependOnDatasetEndpoint = false;
            }
            handler.removeCallbacks(runnable);
            listener.success(items , isDependOnDatasetEndpoint);
        }
    };

    public void getDropDownItems(){
        handler.post(runnable);
    }

}
