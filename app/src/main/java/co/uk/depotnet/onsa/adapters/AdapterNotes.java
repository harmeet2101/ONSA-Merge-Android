package co.uk.depotnet.onsa.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.modals.Note;
import co.uk.depotnet.onsa.networking.CommonUtils;

public class AdapterNotes extends RecyclerView.Adapter<AdapterNotes.ViewHolder> {

    private final SimpleDateFormat inDateFormat;
    private final SimpleDateFormat outDateFormat;
    private Context context;
    private List<Note> notes;

    public AdapterNotes(Context context , List<Note> notes){
        this.context = context;
        this.notes = notes;
        inDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        outDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    }

    @NonNull
    @Override
    public AdapterNotes.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_notes , viewGroup , false) );
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterNotes.ViewHolder viewHolder, int position) {
        if(position%2 == 0){
            viewHolder.view.setBackgroundColor(ContextCompat.getColor(context , R.color.white));
        }else{
            viewHolder.view.setBackgroundColor(ContextCompat.getColor(context , R.color.item_bg_light_gray));
        }
        Note item = notes.get(position);
        viewHolder.txtUserFullName.setText(item.getuserFullName());

        viewHolder.txtDate.setText(getDate(item.getdateTime()));
        viewHolder.txtType.setText(item.getType());
        viewHolder.txtDesc.setText(item.gettext());
    }

    private String getDate(String dateStr){
        if(TextUtils.isEmpty(dateStr)){
            return "";
        }
        try {
            Date date = inDateFormat.parse(dateStr);
            return outDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateStr;
    }


    @Override
    public int getItemCount() {
        return notes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        final View view;
        final TextView txtUserFullName;
        final TextView txtDesc;
        final TextView txtDate;
        final TextView txtType;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            this.txtUserFullName = itemView.findViewById(R.id.txt_user_name);
            this.txtDate = itemView.findViewById(R.id.txt_date);
            this.txtType = itemView.findViewById(R.id.txt_type);
            this.txtDesc = itemView.findViewById(R.id.txt_desc);
        }
    }
}
