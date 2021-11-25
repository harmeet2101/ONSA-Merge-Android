package co.uk.depotnet.onsa.modals;

import android.os.Parcel;
import android.os.Parcelable;

import co.uk.depotnet.onsa.listeners.DropDownItem;

public class TransferTypes implements Parcelable, DropDownItem {


    private String transferTypeId;
    private String transferTypeTitle;

    public TransferTypes(String transferTypeId, String transferTypeTitle) {
        this.transferTypeId = transferTypeId;
        this.transferTypeTitle = transferTypeTitle;
    }

    protected TransferTypes(Parcel in) {
        transferTypeId = in.readString();
        transferTypeTitle = in.readString();
    }

    public static final Creator<TransferTypes> CREATOR = new Creator<TransferTypes>() {
        @Override
        public TransferTypes createFromParcel(Parcel in) {
            return new TransferTypes(in);
        }

        @Override
        public TransferTypes[] newArray(int size) {
            return new TransferTypes[size];
        }
    };

    public String getTransferTypeId() {
        return transferTypeId;
    }

    public void setTransferTypeId(String transferTypeId) {
        this.transferTypeId = transferTypeId;
    }

    public String getTransferTypeTitle() {
        return transferTypeTitle;
    }

    public void setTransferTypeTitle(String transferTypeTitle) {
        this.transferTypeTitle = transferTypeTitle;
    }

    @Override
    public String getDisplayItem() {
        return transferTypeTitle;
    }

    @Override
    public String getUploadValue() {
        return transferTypeId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(transferTypeId);
        parcel.writeString(transferTypeTitle);
    }

    @Override
    public String toString() {
        return "TransferTypes{" +
                "transferTypeId='" + transferTypeId + '\'' +
                ", transferTypeTitle='" + transferTypeTitle + '\'' +
                '}';
    }
}
