package co.uk.depotnet.onsa.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.modals.Notice;
import co.uk.depotnet.onsa.modals.httpresponses.BaseTask;

public class AdapterTasks extends RecyclerView.Adapter<AdapterTasks.ViewHolder> {

    private Context context;
    private List<BaseTask> baseTasks;

    public AdapterTasks(Context context , List<BaseTask> baseTasks){
        this.context = context;
        this.baseTasks = baseTasks;
    }

    @NonNull
    @Override
    public AdapterTasks.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_base_task , viewGroup , false) );
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterTasks.ViewHolder viewHolder, int position) {
        if(position%2 == 0){
            viewHolder.view.setBackgroundColor(ContextCompat.getColor(context , R.color.white));
        }else{
            viewHolder.view.setBackgroundColor(ContextCompat.getColor(context , R.color.item_bg_light_gray));
        }
        BaseTask baseTask = baseTasks.get(position);

        viewHolder.txtTaskType.setText("Task Type: "+baseTask.getSiteActivityTypeName());
        if(!TextUtils.isEmpty(baseTask.getMaterialTypeName())){
            viewHolder.txtMaterialType.setVisibility(View.VISIBLE);
            viewHolder.txtMaterialType.setText("Material Type: "+baseTask.getMaterialTypeName());
        }else{
            viewHolder.txtMaterialType.setVisibility(View.GONE);
        }
        if(!TextUtils.isEmpty(baseTask.getSurfaceTypeName())){
            viewHolder.txtSurfaceType.setVisibility(View.VISIBLE);
            viewHolder.txtSurfaceType.setText("Surface Type"+baseTask.getSurfaceTypeName());
        }else{
            viewHolder.txtSurfaceType.setVisibility(View.GONE);
        }
        if(baseTask.getLength() != 0){
            viewHolder.txtLength.setVisibility(View.VISIBLE);
            viewHolder.txtLength.setText("Length: "+baseTask.getLength());
        }else{
            viewHolder.txtLength.setVisibility(View.GONE);
        }
        if(baseTask.getWidth() != 0){
            viewHolder.txtWidth.setVisibility(View.VISIBLE);
            viewHolder.txtWidth.setText("Width: "+baseTask.getWidth());
        }else{
            viewHolder.txtWidth.setVisibility(View.GONE);
        }
        if(baseTask.getDepth() != 0){
            viewHolder.txtDepth.setVisibility(View.VISIBLE);
            viewHolder.txtDepth.setText("Depth: "+baseTask.getDepth());
        }else{
            viewHolder.txtDepth.setVisibility(View.GONE);
        }
        if(baseTask.getCones() != 0){
            viewHolder.txtCones.setVisibility(View.VISIBLE);
            viewHolder.txtCones.setText("Cones: "+baseTask.getCones());
        }else{
            viewHolder.txtCones.setVisibility(View.GONE);
        }
        if(baseTask.getBarriers() != 0){
            viewHolder.txtBarriers.setVisibility(View.VISIBLE);
            viewHolder.txtBarriers.setText("Barriers: "+baseTask.getBarriers());
        }else{
            viewHolder.txtBarriers.setVisibility(View.GONE);
        }
        if(baseTask.getChpt8() != 0){
            viewHolder.txtChpt8.setVisibility(View.VISIBLE);
            viewHolder.txtChpt8.setText("Chpt8: "+baseTask.getChpt8());
        }else{
            viewHolder.txtChpt8.setVisibility(View.GONE);
        }
        if(baseTask.getFwBoards() != 0){
            viewHolder.txtFwBoards.setVisibility(View.VISIBLE);
            viewHolder.txtFwBoards.setText("FwBoards: "+baseTask.getFwBoards());
        }else{
            viewHolder.txtFwBoards.setVisibility(View.GONE);
        }
        if(baseTask.getBags() != 0){
            viewHolder.txtBags.setVisibility(View.VISIBLE);
            viewHolder.txtBags.setText("Bags: "+baseTask.getBags());
        }else{
            viewHolder.txtBags.setVisibility(View.GONE);
        }
        if(baseTask.getSand() != 0){
            viewHolder.txtSand.setVisibility(View.VISIBLE);
            viewHolder.txtSand.setText("Sand: "+baseTask.getSand());
        }else{
            viewHolder.txtSand.setVisibility(View.GONE);
        }
        if(baseTask.getStone() != 0){
            viewHolder.txtStone.setVisibility(View.VISIBLE);
            viewHolder.txtStone.setText("Stone: "+baseTask.getStone());
        }else{
            viewHolder.txtStone.setVisibility(View.GONE);
        }

        if(!TextUtils.isEmpty(baseTask.getComments())){
            viewHolder.txtComments.setVisibility(View.VISIBLE);
            viewHolder.txtComments.setText("Comments: "+baseTask.getComments());
        }else{
            viewHolder.txtComments.setVisibility(View.GONE);
        }

        viewHolder.rlTaskType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewHolder.llCollapse.getVisibility() == View.GONE){
                    viewHolder.llCollapse.setVisibility(View.VISIBLE);
                    viewHolder.imgViewJob.setImageResource(R.drawable.ic_remove_circle);
                }else{
                    viewHolder.llCollapse.setVisibility(View.GONE);
                    viewHolder.imgViewJob.setImageResource(R.drawable.ic_add_circle);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return baseTasks.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        final View view;
        private RelativeLayout rlTaskType;
        private LinearLayout llCollapse;
        private ImageView imgViewJob;
        final TextView txtTaskType;
        final TextView txtMaterialType;
        final TextView txtSurfaceType;
        final TextView txtLength;
        final TextView txtWidth;
        final TextView txtDepth;
        final TextView txtCones;
        final TextView txtBarriers;
        final TextView txtChpt8;
        final TextView txtFwBoards;
        final TextView txtBags;
        final TextView txtSand;
        final TextView txtStone;
        final TextView txtComments;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            this.rlTaskType = itemView.findViewById(R.id.rl_task_type);
            this.llCollapse = itemView.findViewById(R.id.ll_collapse);
            this.imgViewJob = itemView.findViewById(R.id.img_view_job);
            this.txtTaskType = itemView.findViewById(R.id.txt_task_type);
            this.txtMaterialType = itemView.findViewById(R.id.txt_material_type);
            this.txtSurfaceType = itemView.findViewById(R.id.txt_surface_type);
            this.txtLength = itemView.findViewById(R.id.txt_length);
            this.txtWidth = itemView.findViewById(R.id.txt_width);
            this.txtDepth = itemView.findViewById(R.id.txt_depth);
            this.txtCones = itemView.findViewById(R.id.txt_cones);
            this.txtBarriers = itemView.findViewById(R.id.txt_barriers);
            this.txtChpt8 = itemView.findViewById(R.id.txt_chpt8);
            this.txtFwBoards = itemView.findViewById(R.id.txt_fwBoards);
            this.txtBags = itemView.findViewById(R.id.txt_bags);
            this.txtSand = itemView.findViewById(R.id.txt_sand);
            this.txtStone = itemView.findViewById(R.id.txt_stone);
            this.txtComments = itemView.findViewById(R.id.txt_comments);
        }
    }
}
