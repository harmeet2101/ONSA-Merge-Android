package co.uk.depotnet.onsa.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.modals.Color;
import co.uk.depotnet.onsa.utils.HelperFunction;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ViewHolder> {

    private List<Color> colors;
    private Context context;



    public ColorAdapter(Context context , List<Color> colors) {
        this.context = context;
        this.colors = colors;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_color , parent , false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.imgBody.setSelected(colors.get(position).isSelected());

        StateListDrawable drawable = (StateListDrawable) holder.imgBody.getBackground();
        DrawableContainer.DrawableContainerState dcs = (DrawableContainer.DrawableContainerState) drawable.getConstantState();
        Drawable[] drawableItems = dcs.getChildren();
        LayerDrawable layerDrawableChecked = (LayerDrawable)drawableItems[0]; // item 1
        LayerDrawable layerDrawableUnChecked = (LayerDrawable)drawableItems[1]; // item 2

        String colorCode = colors.get(position).getRgb_code();
        if(HelperFunction.checkStringEmpty(colorCode)) {
            if(colors.get(position).isSelected()) {
                GradientDrawable gradientDrawable1 = (GradientDrawable) layerDrawableChecked.getDrawable(0);
                GradientDrawable gradientDrawable2 = (GradientDrawable) layerDrawableChecked.getDrawable(1);
                gradientDrawable1.setColor(getColor(colorCode));
                gradientDrawable2.setStroke(2, android.graphics.Color.GRAY);
            }else{
                GradientDrawable gradientDrawable1 = (GradientDrawable) layerDrawableUnChecked.getDrawable(0);
                GradientDrawable gradientDrawable2 = (GradientDrawable) layerDrawableUnChecked.getDrawable(1);

                gradientDrawable1.setStroke(2, android.graphics.Color.GRAY);
                gradientDrawable1.setColor(getColor(colorCode));

                gradientDrawable2.setStroke(2, android.graphics.Color.GRAY);
            }
        }


        String name = colors.get(position).getName();
        if(HelperFunction.checkStringEmpty(name)) {
            holder.txtColorName.setText(name.trim());
        }

        holder.view.setOnClickListener(new ClickListener(holder));
    }

    private int getColor(String color){
        try {
            return android.graphics.Color.parseColor(color);
        }catch (Exception e){

        }
        return android.graphics.Color.WHITE;
    }

    private class ClickListener implements View.OnClickListener{
        private ViewHolder holder;

        ClickListener(ViewHolder holder){
            this.holder = holder;
        }

        @Override
        public void onClick(View view) {
            int position = holder.getAdapterPosition();
            colors.get(position).setSelected(!colors.get(position).isSelected());
            notifyItemChanged(position);
        }
    }

    @Override
    public int getItemCount() {
        return colors.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private View view;
        private ImageView imgBody;
        private TextView txtColorName;


        public ViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            imgBody = itemView.findViewById(R.id.body);
            txtColorName = itemView.findViewById(R.id.txt_color_name);
        }
    }
}
