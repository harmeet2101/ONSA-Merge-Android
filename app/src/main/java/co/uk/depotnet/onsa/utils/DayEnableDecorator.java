package co.uk.depotnet.onsa.utils;

import android.content.Context;
import android.text.style.AbsoluteSizeSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.ArrayList;
import java.util.Calendar;

import co.uk.depotnet.onsa.R;

public class DayEnableDecorator implements DayViewDecorator {
    private ArrayList<CalendarDay> dates;

    private Calendar checkDate = Calendar.getInstance();
    private Context context;

    public DayEnableDecorator(Context context , ArrayList<CalendarDay> dates) {
        this.dates = dates;
        this.context = context;

    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        checkDate.set(day.getYear(), day.getMonth(), day.getDay());
//        return checkDate.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY;

        return false;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setDaysDisabled(true);
        view.addSpan(new AbsoluteSizeSpan(context.getResources().getDimensionPixelOffset(R.dimen._110sdp)));
    }

}