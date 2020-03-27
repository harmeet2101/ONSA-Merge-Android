package co.uk.depotnet.onsa.networking.response;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class VehicleCheck extends BaseResponse implements Parcelable {

    private int VehicleCheckID;


    protected VehicleCheck(Parcel in) {
        super(in);
    }

    public int getVehicleCheckID() {
        return VehicleCheckID;
    }

    public void setVehicleCheckID(int vehicleCheckID) {
        VehicleCheckID = vehicleCheckID;
    }
}