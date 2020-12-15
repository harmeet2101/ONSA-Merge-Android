package co.uk.depotnet.onsa.modals.httprequests;

public class VerificationRequest {

    private String code;
    private boolean rememberMe;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    public boolean isRememberMe() {
        return rememberMe;
    }
}
