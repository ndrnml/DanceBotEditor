package ch.ethz.asl.dancebots.danceboteditor.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;

import ch.ethz.asl.dancebots.danceboteditor.R;
import ch.ethz.asl.dancebots.danceboteditor.adapters.BeatElementAdapter;
import ch.ethz.asl.dancebots.danceboteditor.listener.RecyclerViewScrollListener;
import ch.ethz.asl.dancebots.danceboteditor.model.ChoreographyManager;

/**
 * Created by andrin on 24.10.15.
 */

/**
 * The HorizontalRecyclerViews implements the motor and the led beat view. Scrolling of either
 * view is synced with the other and blocked while the view itself is scrolling.
 */
public class HorizontalRecyclerViews implements ChoreographyManager.ChoreographyViewManager, RecyclerViewScrollListener {

    private static final String LOG_TAG = "RECYCLER_VIEW";

    // Offset to the 'border' when scrolling to the selected element
    private static final int SCROLL_OFFSET = 20;

    private final Drawable mDivider;

    private LinearLayoutManager mMotorLayoutManager;
    private LinearLayoutManager mLedLayoutManager;
    private RecyclerView mMotorView;
    private RecyclerView mLedView;

    private final RecyclerView.OnScrollListener mMotorViewOnScrollListener = new SelfRemovingOnScrollListener() {

        @Override
        public void onScrolled(final RecyclerView recyclerView, final int dx, final int dy) {
            super.onScrolled(recyclerView, dx, dy);
            mLedView.scrollBy(dx, dy);
            //Log.d(LOG_TAG, "TOP: onScrolled: " + recyclerView.getScrollState());
        }
    };

    private final RecyclerView.OnScrollListener mLedViewOnScrollListener = new SelfRemovingOnScrollListener() {

        @Override
        public void onScrolled(final RecyclerView recyclerView, final int dx, final int dy) {
            super.onScrolled(recyclerView, dx, dy);
            mMotorView.scrollBy(dx, dy);
            //Log.d(LOG_TAG, "BOTTOM: onScrolled: " + recyclerView.getScrollState());
        }
    };

    public HorizontalRecyclerViews(Context context) {

        // Initialize and setup linear layout manager
        mMotorLayoutManager = new LinearLayoutManager(context.getApplicationContext());
        mLedLayoutManager = new LinearLayoutManager(context.getApplicationContext());

        mMotorLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mLedLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        // Get divider drawable
        mDivider = context.getDrawable(R.drawable.divider);

        // Attach motor adapter and linear layout manager to the horizontal recycler view
        mMotorView = (RecyclerView) ((Activity) context).findViewById(R.id.motor_element_list);
        mMotorView.setHasFixedSize(true);
        mMotorView.setLayoutManager(mMotorLayoutManager);
        mMotorView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

            private int mLastX;

            @Override
            public boolean onInterceptTouchEvent(final RecyclerView rv, final MotionEvent e) {
                //Log.d(LOG_TAG, "TOP: onInterceptTouchEvent");

                // This is necessary to prevent the recycler views being desync
                if (mLedView.getScrollState() != RecyclerView.SCROLL_STATE_IDLE) {
                    return Boolean.TRUE;
                }

                if (rv.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
                    //Log.v(LOG_TAG, "TOP: state idle");
                    onTouchEvent(rv, e);
                }
                return Boolean.FALSE;
            }

            @Override
            public void onTouchEvent(final RecyclerView rv, final MotionEvent e) {
                //Log.d(LOG_TAG, "TOP: onTouchEvent");

                final int action;

                if ((action = e.getAction()) == MotionEvent.ACTION_DOWN && mLedView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {

                    mLastX = rv.getScrollX();
                    rv.addOnScrollListener(mMotorViewOnScrollListener);

                } else {

                    // If no scrolling detected remove scroll listener
                    if (action == MotionEvent.ACTION_UP && rv.getScrollX() == mLastX) {
                        rv.removeOnScrollListener(mMotorViewOnScrollListener);
                    }
                }
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(final boolean disallowIntercept) {
                Log.d(LOG_TAG, "TOP: onRequestDisallowInterceptTouchEvent");
            }
        });

        // Attach led adapter and linear layout manager
        mLedView = (RecyclerView) ((Activity) context).findViewById(R.id.led_element_list);
        mLedView.setHasFixedSize(true);
        mLedView.setLayoutManager(mLedLayoutManager);
        mLedView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

            private int mLastX;

            @Override
            public boolean onInterceptTouchEvent(final RecyclerView rv, final
            MotionEvent e) {
                //Log.d(LOG_TAG, "BOTTOM: onInterceptTouchEvent");

                // This is necessary to prevent the recycler views being desync
                if (mMotorView.getScrollState() != RecyclerView.SCROLL_STATE_IDLE) {
                    return Boolean.TRUE;
                }

                if (rv.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
                    onTouchEvent(rv, e);
                }
                return Boolean.FALSE;
            }

            @Override
            public void onTouchEvent(final RecyclerView rv, final MotionEvent e) {
                //Log.d(LOG_TAG, "BOTTOM: onTouchEvent");

                final int action;

                if ((action = e.getAction()) == MotionEvent.ACTION_DOWN && mMotorView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {

                    mLastX = rv.getScrollX();
                    rv.addOnScrollListener(mLedViewOnScrollListener);

                } else {

                    if (action == MotionEvent.ACTION_UP && rv.getScrollX() == mLastX) {
                        rv.removeOnScrollListener(mLedViewOnScrollListener);
                    }
                }
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(final boolean disallowIntercept) {
                Log.d(LOG_TAG, "BOTTOM: onRequestDisallowInterceptTouchEvent");
            }
        });
    }


    @Override
    public void setLedElementAdapter(BeatElementAdapter ledAdapter) {

        // Attach led element adapter
        mLedView.setAdapter(ledAdapter);
        mLedView.addItemDecoration(new DividerItemDecoration(mDivider, ledAdapter));

        // Notify adapter that list content changed
        ledAdapter.notifyDataSetChanged();
    }

    @Override
    public void setMotorElementAdapter(BeatElementAdapter motorAdapter) {

        // Attach motor element adapter
        mMotorView.setAdapter(motorAdapter);
        mMotorView.addItemDecoration(new DividerItemDecoration(mDivider, motorAdapter));

        // Notify adapter that list content changed
        motorAdapter.notifyDataSetChanged();
    }

    public void scrollToPosition(int position) {

        // Scroll motor view
        mMotorLayoutManager.scrollToPositionWithOffset(position, SCROLL_OFFSET);

        // Scroll led view
        mLedLayoutManager.scrollToPositionWithOffset(position, SCROLL_OFFSET);

        // Set focus on current element
        setFocus(position);
    }

    public void setFocus(int position) {

        // Set focus of current beat element in motor view
        BeatElementAdapter motorAdapter = (BeatElementAdapter) mMotorView.getAdapter();
        motorAdapter.setPlayingItem(position);
        motorAdapter.notifyDataSetChanged();

        // Set focus of current beat element in led view
        BeatElementAdapter ledAdapter = (BeatElementAdapter) mLedView.getAdapter();
        ledAdapter.setPlayingItem(position);
        ledAdapter.notifyDataSetChanged();
    }

    public int getNumElements() {
        return mMotorView.getAdapter().getItemCount();
    }

    public long getSampleAt(int position) {
        return ((BeatElementAdapter) mMotorView.getAdapter()).getItem(position).getSamplePosition();
    }

    public int getFirstVisibleItem() {
        return mMotorLayoutManager.findFirstVisibleItemPosition();
    }

    public int getLastVisibleItem() {
        return mMotorLayoutManager.findLastVisibleItemPosition();
    }



}
