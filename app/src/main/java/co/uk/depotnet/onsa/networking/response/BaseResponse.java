package co.uk.depotnet.onsa.networking.response;

import android.os.Parcel;
import android.os.Parcelable;

public class BaseResponse implements Parcelable {

    private boolean success;
    private String status;

    public BaseResponse(){

    }

    protected BaseResponse(Parcel in) {
        success = in.readInt()==1;
        status = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(success?1:0);
        dest.writeString(status);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BaseResponse> CREATOR = new Creator<BaseResponse>() {
        @Override
        public BaseResponse createFromParcel(Parcel in) {
            return new BaseResponse(in);
        }

        @Override
        public BaseResponse[] newArray(int size) {
            return new BaseResponse[size];
        }
    };

    public boolean isSuccess() {
        return success;
    }

    public void setErrorCode(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return "";
    }
}
