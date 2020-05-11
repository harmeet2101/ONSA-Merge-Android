package co.uk.depotnet.onsa.modals.httprequests;

public class ActiveMfa {

    private String sharedKey;
    private String authenticatorUri;

    public String getAuthenticatorUri() {
        return authenticatorUri;
    }

    public String getSharedKey() {
        return sharedKey;
    }
}
