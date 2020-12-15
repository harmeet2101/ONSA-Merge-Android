package co.uk.depotnet.onsa.listeners;

public interface ScheduleFragmentListener {

    void showProgressBar();
    void hideProgressBar();
    void refreshData(OnScheduleListUpdate listUpdate);
}
