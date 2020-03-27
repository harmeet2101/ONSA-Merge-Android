package co.uk.depotnet.onsa.modals.httprequests;

import android.os.Parcel;
import android.os.Parcelable;

public class ResetPassword implements Parcelable {

    private String emailAddress;

    public ResetPassword(String emailAddress){
        this.emailAddress = emailAddress;
    }

    protected ResetPassword(Parcel in) {
        emailAddress = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(emailAddress);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ResetPassword> CREATOR = new Creator<ResetPassword>() {
        @Override
        public ResetPassword createFromParcel(Parcel in) {
            return new ResetPassword(in);
        }

        @Override
        public ResetPassword[] newArray(int size) {
            return new ResetPassword[size];
        }
    };

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
