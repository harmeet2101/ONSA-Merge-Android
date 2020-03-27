package co.uk.depotnet.onsa.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.modals.LogMeasureForkItem;
import co.uk.depotnet.onsa.modals.forms.FormItem;

import java.util.ArrayList;
import java.util.List;

public class AdapterLogMeasure extends RecyclerView.Adapter<AdapterLogMeasure.ViewHolder> {

    private Context context;
    private List<LogMeasureForkItem> items;
    private int repeatCounter;
    private AdapterListener listener;
    private FormItem formItem;

    public interface AdapterListener{
        void onEdit(FormItem item , int position);
        void onRemove(FormItem item , int position);
    }

    public AdapterLogMeasure(Context context,
                             FormItem formItem , ArrayList<LogMeasureForkItem> items,
                             int repeatCounter , AdapterListener listener) {
        this.context = context;
        this.items = items;
        this.formItem = formItem;
        this.repeatCounter = repeatCounter;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        View view = LayoutInflater.from(context).
                inflate(R.layout.item_log_measure_repeated,
                null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        LogMeasureForkItem item = items.get(position);

        holder.txtSurfaceValue.setText(item.getSurfaceTypeID());
        holder.txtMaterialValue.setText(item.getMaterialTypeID());
        holder.txtLength.setText(item.getLength());
        holder.txtWidth.setText(item.getWidth());
        holder.txtBase.setText(item.getDepthB());
        holder.txtTopping.setText(item.getDepthT());


        holder.llBtnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onEdit(formItem , holder.getAdapterPosition());
            }
        });

        holder.llBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onRemove(formItem , holder.getAdapterPosition());
                items.remove(holder.getAdapterPosition());
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtSurfaceValue;
        TextView txtMaterialValue;
        TextView txtLength;
        TextView txtWidth;
        TextView txtBase;
        TextView txtTopping;

        LinearLayout llBtnEdit;
        LinearLayout llBtnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtSurfaceValue = itemView.findViewById(R.id.txt_surface_value);
            txtMaterialValue = itemView.findViewById(R.id.txt_material_value);
            txtLength = itemView.findViewById(R.id.txt_length);
            txtWidth = itemView.findViewById(R.id.txt_width);
            txtBase = itemView.findViewById(R.id.txt_base);
            txtTopping = itemView.findViewById(R.id.txt_topping);
            llBtnEdit = itemView.findViewById(R.id.ll_btn_edit);
            llBtnDelete = itemView.findViewById(R.id.ll_btn_delete);
        }
    }
}
