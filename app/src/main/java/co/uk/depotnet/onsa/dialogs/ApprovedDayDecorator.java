package co.uk.depotnet.onsa.dialogs;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import co.uk.depotnet.onsa.R;

public class ApprovedDayDecorator implements DayViewDecorator {


    private Drawable drawable;
    private boolean isApproved;
    private boolean isWaitingApproval;

    public ApprovedDayDecorator(Context context , boolean isApproved , boolean isWaitingApproval) {
        if(isApproved) {
            this.drawable = ContextCompat.getDrawable(context, R.drawable.approved_day_circle);
        }else if(isWaitingApproval){
            this.drawable = ContextCompat.getDrawable(context, R.drawable.pending_day_circle);
        }
        this.isApproved = isApproved;
        this.isWaitingApproval = isWaitingApproval;
    }
    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return isWaitingApproval || isApproved;
    }

    @Override
    public void decorate(DayViewFacade view) {
        if(isApproved || isWaitingApproval) {
            view.setSelectionDrawable(drawable);
        }
    }
}
