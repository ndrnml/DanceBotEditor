package ch.ethz.asl.dancebots.danceboteditor.listener;

/**
 * Created by andrin on 01.02.16.
 */

/**
 * An interface that implements a recycler view scroll change listener. It listens for recycler
 * view scroll changes. This is used to update the recycler view state on media player progress or
 * seek bar progress.
 */
public interface RecyclerViewScrollListener {

    void scrollToPosition(int position);

    void setFocus(int position);

    int getFirstVisibleItem();

    int getLastVisibleItem();
}
