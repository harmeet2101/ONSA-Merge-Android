package co.uk.depotnet.onsa.modals;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.httpresponses.BaseTask;
import co.uk.depotnet.onsa.networking.Constants;

public class JobTasks implements Parcelable {

    private String jobId;
    private ArrayList<BaseTask> serviceMaterialDropTasks;
    private ArrayList<BaseTask> muckawayTasks;
    private ArrayList<BaseTask> backfillTasks;
    private ArrayList<BaseTask> reinstatementTasks;
    private ArrayList<BaseTask> siteClearanceTasks;

    public JobTasks(String jobId){
        this.jobId = jobId;
        serviceMaterialDropTasks = DBHandler.getInstance().getTaskItems(jobId ,  Constants.TYPE_ID_SERVICE_MATERAL , 0);
        muckawayTasks = DBHandler.getInstance().getTaskItems(jobId ,  Constants.TYPE_ID_MUCKAWAY , 0);
        backfillTasks = DBHandler.getInstance().getTaskItems(jobId ,  Constants.TYPE_ID_BACKFILL , 0);
        reinstatementTasks = DBHandler.getInstance().getTaskItems(jobId ,  Constants.TYPE_ID_REINSTATEMENT , 0);
        siteClearanceTasks = DBHandler.getInstance().getTaskItems(jobId ,  Constants.TYPE_ID_SITE_CLEAR, 0);
    }

    protected JobTasks(Parcel in) {
        jobId = in.readString();
        serviceMaterialDropTasks = in.createTypedArrayList(BaseTask.CREATOR);
        muckawayTasks = in.createTypedArrayList(BaseTask.CREATOR);
        backfillTasks = in.createTypedArrayList(BaseTask.CREATOR);
        reinstatementTasks = in.createTypedArrayList(BaseTask.CREATOR);
        siteClearanceTasks = in.createTypedArrayList(BaseTask.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(jobId);
        dest.writeTypedList(serviceMaterialDropTasks);
        dest.writeTypedList(muckawayTasks);
        dest.writeTypedList(backfillTasks);
        dest.writeTypedList(reinstatementTasks);
        dest.writeTypedList(siteClearanceTasks);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<JobTasks> CREATOR = new Creator<JobTasks>() {
        @Override
        public JobTasks createFromParcel(Parcel in) {
            return new JobTasks(in);
        }

        @Override
        public JobTasks[] newArray(int size) {
            return new JobTasks[size];
        }
    };

    public void toContentValues(){
        DBHandler dbHandler = DBHandler.getInstance();
        
        dbHandler.deleteBaseTasks(jobId , Constants.TYPE_ID_SERVICE_MATERAL , 0);
        dbHandler.deleteBaseTasks(jobId , Constants.TYPE_ID_MUCKAWAY , 0);
        dbHandler.deleteBaseTasks(jobId , Constants.TYPE_ID_BACKFILL , 0);
        dbHandler.deleteBaseTasks(jobId , Constants.TYPE_ID_REINSTATEMENT, 0);
        dbHandler.deleteBaseTasks(jobId , Constants.TYPE_ID_SITE_CLEAR ,0);
        if(serviceMaterialDropTasks != null && !serviceMaterialDropTasks.isEmpty()){
            for (BaseTask baseTask : serviceMaterialDropTasks){
                dbHandler.replaceData(BaseTask.DBTable.NAME , baseTask.toContentValues());
            }
        }

        if(muckawayTasks != null && !muckawayTasks.isEmpty()){
            for (BaseTask baseTask : muckawayTasks){
                dbHandler.replaceData(BaseTask.DBTable.NAME , baseTask.toContentValues());
            }
        }

        if(backfillTasks != null && !backfillTasks.isEmpty()){
            for (BaseTask baseTask : backfillTasks){
                dbHandler.replaceData(BaseTask.DBTable.NAME , baseTask.toContentValues());
            }
        }

        if(reinstatementTasks != null && !reinstatementTasks.isEmpty()){
            for (BaseTask baseTask : reinstatementTasks){
                dbHandler.replaceData(BaseTask.DBTable.NAME , baseTask.toContentValues());
            }
        }

        if(siteClearanceTasks != null && !siteClearanceTasks.isEmpty()){
            for (BaseTask baseTask : siteClearanceTasks){
                dbHandler.replaceData(BaseTask.DBTable.NAME , baseTask.toContentValues());
            }
        }
    }

    public ArrayList<BaseTask> getBackfillTasks() {
        return backfillTasks;
    }

    public ArrayList<BaseTask> getMuckawayTasks() {
        return muckawayTasks;
    }

    public ArrayList<BaseTask> getReinstatementTasks() {
        return reinstatementTasks;
    }

    public ArrayList<BaseTask> getServiceMaterialDropTasks() {
        return serviceMaterialDropTasks;
    }

    public ArrayList<BaseTask> getSiteClearanceTasks() {
        return siteClearanceTasks;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
}
