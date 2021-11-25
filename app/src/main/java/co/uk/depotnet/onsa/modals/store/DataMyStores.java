package co.uk.depotnet.onsa.modals.store;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;

import co.uk.depotnet.onsa.database.DBHandler;

public class DataMyStores implements Parcelable {

    public ArrayList<MyStore> getData() {
        return data;
    }

    ArrayList<MyStore> data;

    protected DataMyStores(Parcel in) {
        data = in.createTypedArrayList(MyStore.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(data);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DataMyStores> CREATOR = new Creator<DataMyStores>() {
        @Override
        public DataMyStores createFromParcel(Parcel in) {
            return new DataMyStores(in);
        }

        @Override
        public DataMyStores[] newArray(int size) {
            return new DataMyStores[size];
        }
    };

    public void toContentValues(){
        if(data != null && !data.isEmpty()){
            for (MyStore r:data) {
                long id = DBHandler.getInstance().replaceData(MyStore.DBTable.NAME , r.toContentValues());
                Log.e(DataMyStores.class.getSimpleName(),""+id);
            }
        }
    }

}
