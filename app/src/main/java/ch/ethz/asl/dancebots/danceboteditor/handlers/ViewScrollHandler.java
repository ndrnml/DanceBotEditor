package ch.ethz.asl.dancebots.danceboteditor.handlers;

import android.os.Handler;

import ch.ethz.asl.dancebots.danceboteditor.listener.RecyclerViewScrollListener;
import ch.ethz.asl.dancebots.danceboteditor.view.HorizontalRecyclerViews;

/**
 * Created by andrin on 01.02.16.
 */
public class ViewScrollHandler implements Runnable {

    private static final String LOG_TAG = ViewScrollHandler.class.getSimpleName();

    private final int TIME_TO_LIVE = 10000;

    private long mLastInteraction;

    private Handler mScrollHandler;

    private RecyclerViewScrollListener mScrollViewListener;

    private boolean mIsRunning;

    private int mSeekBarProgress;

    public ViewScrollHandler(HorizontalRecyclerViews view) {

        mScrollHandler = new Handler();

        // Keep track of user interaction to register user inactivity
        mLastInteraction = System.currentTimeMillis();

        // Initialize seek bar progress
        mSeekBarProgress = 0;
    }

    public void setScrollListener(RecyclerViewScrollListener listener) {
        mScrollViewListener = listener;
    }

    @Override
    public void run() {
    }
}
