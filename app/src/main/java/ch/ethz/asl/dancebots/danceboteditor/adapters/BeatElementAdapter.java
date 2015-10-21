package ch.ethz.asl.dancebots.danceboteditor.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ch.ethz.asl.dancebots.danceboteditor.dialogs.BeatElementMenuDialog;
import ch.ethz.asl.dancebots.danceboteditor.model.BeatElement;
import ch.ethz.asl.dancebots.danceboteditor.R;

/**
 * Created by andrin on 28.08.15.
 */
public class BeatElementAdapter<T extends BeatElement> extends RecyclerView.Adapter<BeatElementAdapter.SimpleViewHolder> {

    private ArrayList<T> mBeatElements;
    private Toast mToast;

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

    public BeatElementAdapter(ArrayList<T> elems) {
        mBeatElements = elems;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public BeatElementAdapter.SimpleViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        // Create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.beat_grid_element, parent, false);

        // Set the view's size, margins, paddings and layout parameters
        final SimpleViewHolder vh = new SimpleViewHolder((TextView) v);

        mToast = Toast.makeText(parent.getContext(), "", Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER, 0, 0);

        // TODO Ensure long clicks are also registered
        vh.mTextView.setLongClickable(true);

        // Create and attach on click listener
        vh.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context c = parent.getContext();
                BeatElementMenuDialog dialog = new BeatElementMenuDialog();
                dialog.initializeMenu(BeatElementAdapter.this, mBeatElements.get(vh.getPosition()));
                dialog.show(((Activity) c).getFragmentManager(), "element_menu");
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
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        // Populate the data into the template view using the data object
        holder.mTextView.setText(mBeatElements.get(position).getMotionType().getTag());

        // Stylize list item according to type
        holder.mTextView.setBackgroundColor(mBeatElements.get(position).getMotionType().getColor());
    }

    @Override
    public int getItemCount() {
        return mBeatElements.size();
    }

}
