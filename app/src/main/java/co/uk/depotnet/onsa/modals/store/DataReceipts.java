package co.uk.depotnet.onsa.modals.store;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import co.uk.depotnet.onsa.database.DBHandler;

public class DataReceipts implements Parcelable {

    ArrayList<Receipts> data;

    protected DataReceipts(Parcel in) {
        data = in.createTypedArrayList(Receipts.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(data);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DataReceipts> CREATOR = new Creator<DataReceipts>() {
        @Override
        public DataReceipts createFromParcel(Parcel in) {
            return new DataReceipts(in);
        }

        @Override
        public DataReceipts[] newArray(int size) {
            return new DataReceipts[size];
        }
    };

    public void toContentValues(){
        DBHandler.getInstance().resetReceipts();
        if(data != null && !data.isEmpty()){
            for (Receipts r:data) {
                DBHandler.getInstance().replaceData(Receipts.DBTable.NAME , r.toContentValues());
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
