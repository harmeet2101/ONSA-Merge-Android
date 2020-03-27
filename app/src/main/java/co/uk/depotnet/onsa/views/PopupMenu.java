package co.uk.depotnet.onsa.views;

import android.content.Context;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;

import co.uk.depotnet.onsa.R;

public class PopupMenu extends PopupWindow implements View.OnClickListener {

    private final EditText etSearch;
    private final OnSearchListener listener;

    public interface OnSearchListener{
        void onSearch(String keyword);
    }




    public PopupMenu(Context context , ViewGroup viewGroup , OnSearchListener listener) {
        super(context);
        this.listener = listener;
        View view = LayoutInflater.from(context).
                inflate(R.layout.dialog_search, viewGroup , false);
        setContentView(view);
        setOutsideTouchable(true);
        setFocusable(true);

        etSearch = view.findViewById(R.id.et_search);
        view.findViewById(R.id.btn_search).setOnClickListener(this);

        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        int width = (int)context.getResources().getDimension(R.dimen._240sdp);
        setWidth(width);
    }

    public void show(View anchor) {
        showAsDropDown(anchor);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_search:
                String keyword = etSearch.getText().toString().trim();
                if(!keyword.isEmpty()){
                    listener.onSearch(keyword);
                }
                dismiss();
                break;
        }
    }
}