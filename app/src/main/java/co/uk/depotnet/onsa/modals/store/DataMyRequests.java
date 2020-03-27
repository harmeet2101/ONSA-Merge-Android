package co.uk.depotnet.onsa.modals.store;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import co.uk.depotnet.onsa.database.DBHandler;

public class DataMyRequests implements Parcelable {

    ArrayList<MyRequest> data;

    protected DataMyRequests(Parcel in) {
        data = in.createTypedArrayList(MyRequest.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(data);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DataMyRequests> CREATOR = new Creator<DataMyRequests>() {
        @Override
        public DataMyRequests createFromParcel(Parcel in) {
            return new DataMyRequests(in);
        }

        @Override
        public DataMyRequests[] newArray(int size) {
            return new DataMyRequests[size];
        }
    };

    public void toContentValues(){
        DBHandler.getInstance().resetMyRequest();
        if(data != null && !data.isEmpty()){
            for (MyRequest r:data) {
                DBHandler.getInstance().replaceData(MyRequest.DBTable.NAME , r.toContentValues());
            }
        }
    }

    public int getCount(){
        if(data == null || data.isEmpty()){
            return 0;
        }

        return data.size();
    }
}
