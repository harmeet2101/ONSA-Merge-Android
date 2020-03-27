package co.uk.depotnet.onsa.modals.store;

import android.os.Parcel;
import android.os.Parcelable;

public class StockLevel implements Parcelable {
    private double stockLevel;

    protected StockLevel(Parcel in) {
        stockLevel = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(stockLevel);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<StockLevel> CREATOR = new Creator<StockLevel>() {
        @Override
        public StockLevel createFromParcel(Parcel in) {
            return new StockLevel(in);
        }

        @Override
        public StockLevel[] newArray(int size) {
            return new StockLevel[size];
        }
    };

    public double getStockLevel() {
        return stockLevel;
    }

    public void setStockLevel(double stockLevel) {
        this.stockLevel = stockLevel;
    }


}
