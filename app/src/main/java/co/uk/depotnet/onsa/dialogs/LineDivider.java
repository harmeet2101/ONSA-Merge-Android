package co.uk.depotnet.onsa.dialogs;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.recyclerview.widget.DividerItemDecoration;

public class LineDivider extends DividerItemDecoration {
    /**
     * Creates a divider {@link androidx.recyclerview.widget.RecyclerView.ItemDecoration} that can be used with a
     * {@link androidx.recyclerview.widget.LinearLayoutManager}.
     *
     * @param context     Current context, it will be used to access resources.
     * @param orientation Divider orientation. Should be {@link #HORIZONTAL} or {@link #VERTICAL}.
     */
    public LineDivider(Context context, int orientation , Drawable drawable) {
        super(context, orientation);
        setDrawable(drawable);
    }
}
