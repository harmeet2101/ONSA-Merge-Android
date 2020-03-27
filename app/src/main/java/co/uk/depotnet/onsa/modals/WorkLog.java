package co.uk.depotnet.onsa.modals;

import android.os.Parcel;
import android.os.Parcelable;

public class WorkLog implements Parcelable {

    private String title;
    private String json;
    private boolean status;

    protected WorkLog(Parcel in) {
        title = in.readString();
        json = in.readString();
        status = in.readByte() != 0;
    }

    public static final Creator<WorkLog> CREATOR = new Creator<WorkLog>() {
        @Override
        public WorkLog createFromParcel(Parcel in) {
            return new WorkLog(in);
        }

        @Override
        public WorkLog[] newArray(int size) {
            return new WorkLog[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(json);
        parcel.writeByte((byte) (status ? 1 : 0));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
