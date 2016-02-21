package ch.ethz.asl.dancebots.danceboteditor.view;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

/**
 * Created by andrin on 22.10.15.
 */

/**
 * The SelfRemovingOnScrollListener is needed for the synchronized scrolling of the
 * HorizontalRecyclerViews (motor and led view)
 */
public class SelfRemovingOnScrollListener extends RecyclerView.OnScrollListener {

    private static final String LOG_TAG = SelfRemovingOnScrollListener.class.getSimpleName();

    @Override
    public final void onScrollStateChanged(final RecyclerView recyclerView, final int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            recyclerView.removeOnScrollListener(this);
        }

        Log.v(LOG_TAG, "Scroll state: " + recyclerView.getScrollState());
    }
}