package co.uk.depotnet.onsa.listeners;

import android.content.Intent;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.datepicker.MaterialDatePicker;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import co.uk.depotnet.onsa.modals.forms.FormItem;
import co.uk.depotnet.onsa.modals.forms.Photo;

import java.util.ArrayList;

public interface FormAdapterListener extends LocationPermissionListener {

    void openCamera(long submissionId, FormItem formItem , int repeatCount);
    void openSignature(FormItem formItem , long submissionId , int repeatCount);
    void openForkFragment(FormItem formItem,long submissionId , int repeatCount);
    void openFragment(Fragment fragment);
    void showBottomSheet(BottomSheetDialogFragment dialogFragment);
    void showDatePicker(DialogFragment dialogFragment);
    void startActivityForResultFromAdapter(Intent intent, int requestCode);
    void showValidationDialog(String title, String message);
    void showErrorDialog(String title, String message , boolean shouldActivityFinished);
    void showProgressBar();
    void hideProgressBar();

    void openTaskAmendment(FormItem formItem, long submissionId , int repeatCount);
    void getEstimateOperative(String estno, int position , boolean isSubmit);
}
