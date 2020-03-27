package co.uk.depotnet.onsa.modals.forms;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.Gson;

public class StoreItem implements Parcelable {
    public static final Creator<StoreItem> CREATOR = new Creator<StoreItem>() {
        @Override
        public StoreItem createFromParcel(Parcel in) {
            return new StoreItem(in);
        }

        @Override
        public StoreItem[] newArray(int size) {
            return new StoreItem[size];
        }
    };
    private String itemId;
    private String type;
    private String quantity;

    public StoreItem() {

    }

    protected StoreItem(Parcel in) {
        itemId = in.readString();
        type = in.readString();
        quantity = in.readString();
    }

    public static StoreItem fromJson(String json) {

        if(TextUtils.isEmpty(json)){
            return null;
        }

        Gson gson = new Gson();

        try {
            return gson.fromJson(json, StoreItem.class);
        }catch (Exception e){
            return null;
        }
    }

    public String toJson() {
        Gson gson = new Gson();
        try {
            return gson.toJson(this);
        }catch (Exception e){
            return null;
        }
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(itemId);
        dest.writeString(type);
        dest.writeString(quantity);
    }
}
