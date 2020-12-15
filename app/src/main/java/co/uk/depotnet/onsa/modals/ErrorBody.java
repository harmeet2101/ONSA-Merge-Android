package co.uk.depotnet.onsa.modals;

public class ErrorBody {

    private boolean success;
    private String status;
    private String message;

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }

    public boolean isSuccess() {
        return success;
    }
}
