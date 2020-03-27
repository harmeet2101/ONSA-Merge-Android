package co.uk.depotnet.onsa.modals.responses;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import co.uk.depotnet.onsa.modals.Job;

public class JobResponse implements Parcelable {

    private ArrayList<Job> jobs;

    protected JobResponse(Parcel in) {
        jobs = in.createTypedArrayList(Job.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(jobs);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<JobResponse> CREATOR = new Creator<JobResponse>() {
        @Override
        public JobResponse createFromParcel(Parcel in) {
            return new JobResponse(in);
        }

        @Override
        public JobResponse[] newArray(int size) {
            return new JobResponse[size];
        }
    };

    public ArrayList<Job> getJobs() {
        return jobs;
    }

    public void setJobs(ArrayList<Job> jobs) {
        this.jobs = jobs;
    }
}
