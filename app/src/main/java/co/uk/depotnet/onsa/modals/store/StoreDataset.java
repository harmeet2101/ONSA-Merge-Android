package co.uk.depotnet.onsa.modals.store;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.ItemType;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.networking.APICalls;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoreDataset implements Parcelable {
    public static final Creator<StoreDataset> CREATOR = new Creator<StoreDataset>() {
        @Override
        public StoreDataset createFromParcel(Parcel in) {
            return new StoreDataset(in);
        }

        @Override
        public StoreDataset[] newArray(int size) {
            return new StoreDataset[size];
        }
    };
    private ArrayList<ItemType> issueTypes;
    private ArrayList<ItemType> stas;
    private ArrayList<ItemType> operatives;
    private ArrayList<StockItems> stockItems;



    protected StoreDataset(Parcel in) {
        issueTypes = in.createTypedArrayList(ItemType.CREATOR);
        stas = in.createTypedArrayList(ItemType.CREATOR);
        operatives = in.createTypedArrayList(ItemType.CREATOR);
        stockItems = in.createTypedArrayList(StockItems.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(issueTypes);
        dest.writeTypedList(stas);
        dest.writeTypedList(operatives);
        dest.writeTypedList(stockItems);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public ContentValues toContentValues() {

        ContentValues cv = new ContentValues();

        DBHandler dbHandler = DBHandler.getInstance();

        if (this.issueTypes != null && !this.issueTypes.isEmpty()) {
            for (ItemType item : this.issueTypes) {
                item.settype(DBTable.issueTypes);
                dbHandler.insertData(ItemType.DBTable.NAME, item.toContentValues());
            }
        }


        if (this.stas != null && !this.stas.isEmpty()) {
            for (ItemType item : this.stas) {
                item.settype(DBTable.stas);
                dbHandler.insertData(ItemType.DBTable.NAME, item.toContentValues());
            }
        }

        if (this.operatives != null && !this.operatives.isEmpty()) {
            for (ItemType item : this.operatives) {
                item.settype(DBTable.operatives);
                dbHandler.insertData(ItemType.DBTable.NAME, item.toContentValues());
            }
        }



        if (this.stockItems != null && !this.stockItems.isEmpty()) {
            for (StockItems item : this.stockItems) {
                item.setType(DBTable.stockItems);

                dbHandler.replaceData(StockItems.DBTable.NAME, item.toContentValues());
            }
        }

        return cv;
    }

    public static class DBTable {
        public static final String NAME = "DatasetResponse";
        public static final String issueTypes = "issueTypes";
        public static final String stas = "stas";
        public static final String operatives = "operatives";
        public static final String stockItems = "stockItems";
    }
}