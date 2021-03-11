package co.uk.depotnet.onsa.dialogs;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import androidx.core.content.ContextCompat;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import co.uk.depotnet.onsa.R;

public class StatusDayDecorator implements DayViewDecorator {


    private final Drawable approvedDrawable;
    private final Drawable pendingDrawable;
    private final Drawable inProgressDrawable;
    private String date;
    private String value;

    public StatusDayDecorator(Context context, String date , String status) {
        this.approvedDrawable = ContextCompat.getDrawable(context, R.drawable.approved_day_circle);
        this.pendingDrawable = ContextCompat.getDrawable(context, R.drawable.pending_day_circle);
        this.inProgressDrawable = ContextCompat.getDrawable(context, R.drawable.in_progress_day_circle);
        this.date = date;
        this.value = status;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return getDate(day).equalsIgnoreCase(date);
    }

    private String getDate(CalendarDay day){
        Date date = day.getCalendar().getTime();
        return new SimpleDateFormat("yyyy-MM-dd", Locale.UK).format(date);
    }

    @Override
    public void decorate(DayViewFacade view) {
        if(TextUtils.isEmpty(value)){
            return;
        }
        if (value.equalsIgnoreCase("Awaiting Approval")) {
            view.setSelectionDrawable(pendingDrawable);
        }else if (value.equalsIgnoreCase("Approved")) {
            view.setSelectionDrawable(approvedDrawable);
        }else if (value.equalsIgnoreCase("In Progress")) {
            view.setSelectionDrawable(inProgressDrawable);
        }
    }
}
