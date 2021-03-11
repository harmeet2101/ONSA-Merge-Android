package co.uk.depotnet.onsa.listeners;

import androidx.fragment.app.Fragment;

import com.tonyodev.fetch2.Fetch;

public interface FragmentActionListener {
    void addFragment(Fragment fragment, boolean isHorizontalAnim);
    void setTitle(String title);
    void showProgressBar();
    void hideProgressBar();
    void setReceiptsBadge(String number);
}
