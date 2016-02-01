package ch.ethz.asl.dancebots.danceboteditor.listener;

/**
 * Created by andrin on 01.02.16.
 */

/**
 * An interface that defines methods that the scroll views implement. An instance of
 * HorizontalRecyclerViews passes itself to the AutomaticScrollHandler. This is needed
 * to handle scroll events correctly and communicate between scroll views and seek bars.
 */
public interface RecyclerViewScrollListener {

    void scrollToPosition(int position);

    int getNumElements();

    long getSampleAt(int position);

    int getFirstVisibleItem();

    int getLastVisibleItem();

    void setFocus(int position);
}
