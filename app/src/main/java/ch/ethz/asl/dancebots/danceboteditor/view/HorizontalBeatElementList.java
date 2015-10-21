package ch.ethz.asl.dancebots.danceboteditor.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by andrin on 22.10.15.
 */
public class HorizontalBeatElementList extends RecyclerView {

    public HorizontalBeatElementList(Context context) {
        this(context, null);
    }

    public HorizontalBeatElementList(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalBeatElementList(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void smoothScrollToPosition(int position) {
        super.smoothScrollToPosition(position);
    }
}