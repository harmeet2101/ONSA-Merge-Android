package co.uk.depotnet.onsa.listeners;

import co.uk.depotnet.onsa.adapters.HomeAdapter;
import co.uk.depotnet.onsa.modals.Job;

public interface HomeJobListListener {

    void openJobDetail(Job job);
    void openWorkLog(Job job);
    void openJobPack(Job job);
    void openSurvey(Job job);
    void openRiskAssessment(Job job);
    void onQualityCheck(Job job);
    void openVisitorAttendance(Job job);
    void openTakePhotoAndVideo(Job job);
    void showHotJobDialog(HomeAdapter.ViewHolder holder);
    void openPhotoGallery(Job job);
    void openAddNotes(Job job);
    void onLogStores(Job job);
    void openRequestTask(Job job);

    void openSiteClear(Job job);
}
