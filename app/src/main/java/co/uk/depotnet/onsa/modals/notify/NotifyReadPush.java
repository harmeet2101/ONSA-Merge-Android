package co.uk.depotnet.onsa.modals.notify;

public class NotifyReadPush {

    private String notificationId;

    public NotifyReadPush(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }
}
