package ch.ethz.asl.dancebots.danceboteditor.view;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by andrin on 15.10.15.
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    protected VisibilityProvider mVisibilityProvider;
    private Drawable mDivider;

    public DividerItemDecoration(Drawable divider, VisibilityProvider visibilityProvider) {
        mDivider = divider;
        mVisibilityProvider = visibilityProvider;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();
        final int childCount = parent.getChildCount();

        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            int childPosition = parent.getChildAdapterPosition(child);

            // Hide divider if specified
            if (mVisibilityProvider.shouldHideDivider(childPosition, parent)) {
                continue;
            }

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getRight() + params.rightMargin +
                    Math.round(ViewCompat.getTranslationX(child));
            final int right = left + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        /**
         * I think this is not needed
         */
        /*
        if (mDivider == null) {
            return;
        }
        outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
        */
    }

    /**
     * Interface for controlling divider visibility
     */
    public interface VisibilityProvider {

        /**
         * Returns true if divider should be hidden.
         *
         * @param position Divider position
         * @param parent   RecyclerView
         * @return True if the divider at position should be hidden
         */
        boolean shouldHideDivider(int position, RecyclerView parent);
    }
}
