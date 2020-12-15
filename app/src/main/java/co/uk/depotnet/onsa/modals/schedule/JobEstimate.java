
package co.uk.depotnet.onsa.modals.schedule;

import android.os.Parcel;
import android.os.Parcelable;

public class JobEstimate implements Parcelable {
    private String jobId;
    private String estimateNumber;
    private String jobRef;
    private String exchange;
    private String location;
    private String gangId;

    public JobEstimate() {
    }

    protected JobEstimate(Parcel in) {
        jobId = in.readString();
        estimateNumber = in.readString();
        jobRef = in.readString();
        exchange = in.readString();
        location = in.readString();
        gangId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(jobId);
        dest.writeString(estimateNumber);
        dest.writeString(jobRef);
        dest.writeString(exchange);
        dest.writeString(location);
        dest.writeString(gangId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<JobEstimate> CREATOR = new Creator<JobEstimate>() {
        @Override
        public JobEstimate createFromParcel(Parcel in) {
            return new JobEstimate(in);
        }

        @Override
        public JobEstimate[] newArray(int size) {
            return new JobEstimate[size];
        }
    };

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getEstimateNumber() {
        return estimateNumber;
    }

    public void setEstimateNumber(String estimateNumber) {
        this.estimateNumber = estimateNumber;
    }

    public String getJobRef() {
        return jobRef;
    }

    public void setJobRef(String jobRef) {
        this.jobRef = jobRef;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getGangId() {
        return gangId;
    }

    public void setGangId(String gangId) {
        this.gangId = gangId;
    }

    @Override
    public String toString() {
        return "JobEstimate{" +
                "estimateNumber='" + estimateNumber + '\'' +
                ", exchange='" + exchange + '\'' +
                ", gangId=" + gangId +
                ", jobRef=" + jobRef +
                ", location='" + location + '\'' +
                '}';
    }
}
