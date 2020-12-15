package co.uk.depotnet.onsa.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.util.List;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.modals.briefings.IssuedDocModal;


public class IssueDocAdapter extends ArrayAdapter<IssuedDocModal> {

    private Context context;
    private List<IssuedDocModal> values;

    public IssueDocAdapter(Context context, int textViewResourceId,
                           List<IssuedDocModal> values) {
        super(context, textViewResourceId, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public int getCount(){
        return values.size();
    }

    @Override
    public IssuedDocModal getItem(int position){
        return values.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public boolean isEnabled(int position) {
        //return super.isEnabled(position);
        return position != 0;
    }

    // This is for the "passive" state of the spinner_item
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        TextView label = (TextView) super.getView(position, convertView, parent);
        label.setTextColor(ContextCompat.getColor(context, R.color.ColorBriefing));
        label.setText(values.get(position).getBriefingName());
        return label;
    }
    @Override
    public View getDropDownView(int position, View convertView,
                                @NonNull ViewGroup parent) {
        TextView label = (TextView) super.getDropDownView(position, convertView, parent);
        if(position == 0){
            // Set the hint text color gray
            label.setTextColor(ContextCompat.getColor(context, R.color.txt_color_light_grey));
        }
        else {
            label.setPaintFlags(label.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
            label.setTextColor(ContextCompat.getColor(context,R.color.ColorBriefing));
        }
        label.setText(values.get(position).getBriefingName());

        return label;
    }
}
