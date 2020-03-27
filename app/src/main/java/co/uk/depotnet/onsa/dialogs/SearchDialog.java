package co.uk.depotnet.onsa.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import co.uk.depotnet.onsa.R;

public class SearchDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private EditText etSearch;

    public SearchDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View dialogLayout = LayoutInflater.from(context).inflate(R.layout.dialog_search, null);
        setContentView(dialogLayout);

        etSearch = dialogLayout.findViewById(R.id.et_search);
        dialogLayout.findViewById(R.id.btn_search).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_search:
                String keyword = etSearch.getText().toString().trim();
                if(!keyword.isEmpty()){
                    search(keyword);
                }
                break;
        }
    }

    public void search(String keyword){
        //TODO: search
    }
}
