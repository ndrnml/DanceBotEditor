package ch.ethz.asl.dancebots.danceboteditor.view;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;

import ch.ethz.asl.dancebots.danceboteditor.R;
import ch.ethz.asl.dancebots.danceboteditor.adapters.BeatElementAdapter;
import ch.ethz.asl.dancebots.danceboteditor.handlers.AutomaticScrollHandler;
import ch.ethz.asl.dancebots.danceboteditor.model.ChoreographyManager;
import ch.ethz.asl.dancebots.danceboteditor.utils.DividerItemDecoration;

/**
 * Created by andrin on 24.10.15.
 */
public class HorizontalRecyclerViews implements ChoreographyManager.ChoreographyViewManager, AutomaticScrollHandler.ScrollViewMethods {

    private static final String LOG_TAG = "RECYCLER_VIEW";

    private LinearLayoutManager mMotorLayoutManager;
    private LinearLayoutManager mLedLayoutManager;
    private RecyclerView mMotorView;
    private RecyclerView mLedView;

    private final RecyclerView.OnScrollListener mTopOnScrollListener = new SelfRemovingOnScrollListener() {
        @Override
        public void onScrolled(final RecyclerView recyclerView, final int dx, final int dy) {
            super.onScrolled(recyclerView, dx, dy);
            mLedView.scrollBy(dx, dy);
        }
    };

    private final RecyclerView.OnScrollListener mBottomOnScrollListener = new SelfRemovingOnScrollListener() {

        @Override
        public void onScrolled(final RecyclerView recyclerView, final int dx, final int dy) {
            super.onScrolled(recyclerView, dx, dy);
            mMotorView.scrollBy(dx, dy);
        }
    };

    public HorizontalRecyclerViews(Activity activity) {

        // Initialize and setup linear layout manager
        mMotorLayoutManager = new LinearLayoutManager(activity.getApplicationContext());
        mLedLayoutManager = new LinearLayoutManager(activity.getApplicationContext());

        mMotorLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mLedLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        // Get divider drawable
        Drawable divider = activity.getResources().getDrawable(R.drawable.divider);

        // Attach motor adapter and linear layout manager to the horizontal recycler view
        mMotorView = (RecyclerView) activity.findViewById(R.id.motor_element_list);
        mMotorView.setHasFixedSize(true);
        mMotorView.setLayoutManager(mMotorLayoutManager);
        mMotorView.addItemDecoration(new DividerItemDecoration(divider));
        mMotorView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

            private int mLastX;

            @Override
            public boolean onInterceptTouchEvent(final RecyclerView rv, final MotionEvent e) {
                Log.v(LOG_TAG, "TOP: onInterceptTouchEvent");

                final Boolean ret = rv.getScrollState() != RecyclerView.SCROLL_STATE_IDLE;
                if (!ret) {
                    onTouchEvent(rv, e);
                }
                return Boolean.FALSE;
            }

            @Override
            public void onTouchEvent(final RecyclerView rv, final MotionEvent e) {
                Log.v(LOG_TAG, "TOP: onTouchEvent");

                final int action;
                if ((action = e.getAction()) == MotionEvent.ACTION_DOWN && mLedView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
                    mLastX = rv.getScrollX();
                    rv.addOnScrollListener(mTopOnScrollListener);

                } else {
                    if (action == MotionEvent.ACTION_UP && rv.getScrollX() == mLastX) {
                        rv.removeOnScrollListener(mTopOnScrollListener);
                    }
                }
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(final boolean disallowIntercept) {
                Log.v(LOG_TAG, "TOP: onRequestDisallowInterceptTouchEvent");
            }
        });

        // Attach led adapter and linear layout manager
        mLedView = (RecyclerView) activity.findViewById(R.id.led_element_list);
        mLedView.setHasFixedSize(true);
        mLedView.setLayoutManager(mLedLayoutManager);
        mLedView.addItemDecoration(new DividerItemDecoration(divider));
        mLedView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

            private int mLastX;

            @Override
            public boolean onInterceptTouchEvent(@NonNull final RecyclerView rv, @NonNull final
            MotionEvent e) {
                Log.v(LOG_TAG, "BOTTOM: onInterceptTouchEvent");

                final Boolean ret = rv.getScrollState() != RecyclerView.SCROLL_STATE_IDLE;
                if (!ret) {
                    onTouchEvent(rv, e);
                }
                return Boolean.FALSE;
            }

            @Override
            public void onTouchEvent(final RecyclerView rv, final MotionEvent e) {
                Log.v(LOG_TAG, "BOTTOM: onTouchEvent");

                final int action;
                if ((action = e.getAction()) == MotionEvent.ACTION_DOWN && mMotorView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
                    mLastX = rv.getScrollX();
                    rv.addOnScrollListener(mBottomOnScrollListener);
                }
                else {
                    if (action == MotionEvent.ACTION_UP && rv.getScrollX() == mLastX) {
                        rv.removeOnScrollListener(mBottomOnScrollListener);
                    }
                }
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(final boolean disallowIntercept) {
                Log.v(LOG_TAG, "BOTTOM: onRequestDisallowInterceptTouchEvent");
            }
        });
    }


    @Override
    public void setLedElementAdapter(BeatElementAdapter ledAdapter) {

        // Attach led element adapter
        mLedView.setAdapter(ledAdapter);
        // Notify adapter that list content changed
        ledAdapter.notifyDataSetChanged();
    }

    @Override
    public void setMotorElementAdapter(BeatElementAdapter motorAdapter) {

        // Attach motor element adapter
        mMotorView.setAdapter(motorAdapter);
        // Notify adapter that list content changed
        motorAdapter.notifyDataSetChanged();
    }

    /******************************
     * ScrollViewMethods Interface
     ******************************/

    @Override
    public void scrollToPosition(int position) {

        // Scroll motor view
        mMotorLayoutManager.scrollToPositionWithOffset(position, 20);

        // Scroll led view
        mLedLayoutManager.scrollToPositionWithOffset(position, 20);

        // Set focus on current element
        setFocus(position);
    }

    @Override
    public int getNumElements() {
        return mMotorView.getAdapter().getItemCount();
    }

    @Override
    public long getSampleAt(int position) {
        return ((BeatElementAdapter) mMotorView.getAdapter()).getItem(position).getSamplePosition();
    }

    @Override
    public int getFirstVisibleItem() {
        return mMotorLayoutManager.findFirstVisibleItemPosition();
    }

    @Override
    public int getLastVisibleItem() {
        return mMotorLayoutManager.findLastVisibleItemPosition();
    }

    @Override
    public void setFocus(int position) {

        // Set focus of current beat element in motor view
        BeatElementAdapter motorAdapter = (BeatElementAdapter) mMotorView.getAdapter();
        motorAdapter.setSelected(position);
        motorAdapter.notifyDataSetChanged();

        // Set focus of current beat element in led view
        BeatElementAdapter ledAdapter = (BeatElementAdapter) mLedView.getAdapter();
        ledAdapter.setSelected(position);
        ledAdapter.notifyDataSetChanged();
    }
}
