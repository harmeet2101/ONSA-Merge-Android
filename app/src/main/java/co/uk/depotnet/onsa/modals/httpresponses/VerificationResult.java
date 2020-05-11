package co.uk.depotnet.onsa.modals.httpresponses;

import java.util.ArrayList;

public class VerificationResult {

    private String message;
    private ArrayList<String> recoveryCodes;

    public ArrayList<String> getRecoveryCodes() {
        return recoveryCodes;
    }

    public String getMessage() {
        return message;
    }
}
