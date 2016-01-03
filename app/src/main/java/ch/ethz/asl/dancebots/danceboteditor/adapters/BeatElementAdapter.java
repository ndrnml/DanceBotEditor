package ch.ethz.asl.dancebots.danceboteditor.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ch.ethz.asl.dancebots.danceboteditor.dialogs.BeatElementMenuDialog;
import ch.ethz.asl.dancebots.danceboteditor.model.BeatElement;
import ch.ethz.asl.dancebots.danceboteditor.R;

/**
 * Created by andrin on 28.08.15.
 */
public class BeatElementAdapter<T extends BeatElement> extends RecyclerView.Adapter<BeatElementAdapter.SimpleViewHolder> {

    private final Context mContext;
    private ArrayList<T> mBeatElements;
    private int mSelectedItem;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;

        public SimpleViewHolder(TextView v) {
            super(v);
            mTextView = v;
        }
    }

    public BeatElementAdapter(Context context, ArrayList<T> elems) {

        mContext = context;
        mBeatElements = elems;
        mSelectedItem = 0;
    }

    public void setSelected(int position) {
        mSelectedItem = position;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public BeatElementAdapter.SimpleViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        // Create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.beat_element, parent, false);

        // Set the view's size, margins, paddings and layout parameters
        final SimpleViewHolder vh = new SimpleViewHolder((TextView) v);

        // TODO Ensure long clicks are also registered
        vh.mTextView.setLongClickable(true);

        // Create and attach on click listener
        vh.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BeatElementMenuDialog dialog = new BeatElementMenuDialog();

                dialog.initializeMenuFromElement(BeatElementAdapter.this, mBeatElements.get(vh.getAdapterPosition()));
                dialog.show(((Activity) mContext).getFragmentManager(), "element_menu");
            }
        });

        // Create and attach on long click listener
        /*vh.mTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                mToast.setText(": Item long clicked: " + vh.getPosition());
                mToast.show();

                return true;
            }
        });*/

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(SimpleViewHolder holder, int position) {
        /*
         * Get element from dataset at this position
         * Replace the contents of the view with that element
         * Populate the data into the template view using the data object
         */
        holder.mTextView.setText(Integer.toString(position)/*mBeatElements.get(position).getMotionType().getTag()*/);

        // Stylize list item according to type
        holder.mTextView.setBackgroundColor(mBeatElements.get(position).getMotionType().getColor());

        // Set selected item
        if (mSelectedItem == position) {
            holder.mTextView.setBackground(mContext.getDrawable(R.drawable.selected_item));
        }
    }

    @Override
    public int getItemCount() {
        return mBeatElements.size();
    }

    public BeatElement getItem(int position) {
        return mBeatElements.get(position);
    }
}
