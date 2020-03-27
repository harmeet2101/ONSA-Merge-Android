package co.uk.depotnet.onsa.modals.httprequests;

import android.os.Parcel;
import android.os.Parcelable;

public class UserRequest implements Parcelable {

    private String Username;
    private String Password;

    public UserRequest(String UserName , String Password){
        this.Username = UserName;
        this.Password = Password;
    }

    protected UserRequest(Parcel in) {
        Username = in.readString();
        Password = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Username);
        dest.writeString(Password);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserRequest> CREATOR = new Creator<UserRequest>() {
        @Override
        public UserRequest createFromParcel(Parcel in) {
            return new UserRequest(in);
        }

        @Override
        public UserRequest[] newArray(int size) {
            return new UserRequest[size];
        }
    };
}


