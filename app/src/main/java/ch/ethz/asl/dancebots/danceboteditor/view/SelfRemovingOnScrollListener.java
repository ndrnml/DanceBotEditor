package ch.ethz.asl.dancebots.danceboteditor.view;

import android.support.v7.widget.RecyclerView;

/**
 * Created by andrin on 22.10.15.
 */
public class SelfRemovingOnScrollListener extends RecyclerView.OnScrollListener {

    @Override
    public final void onScrollStateChanged(final RecyclerView recyclerView, final int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            recyclerView.removeOnScrollListener(this);
        }
    }
}