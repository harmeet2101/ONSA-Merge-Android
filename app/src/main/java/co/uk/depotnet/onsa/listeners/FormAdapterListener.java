package co.uk.depotnet.onsa.listeners;

import android.content.Intent;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
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

    void startActivityForResultFromAdapter(Intent intent, int requestCode);


}
