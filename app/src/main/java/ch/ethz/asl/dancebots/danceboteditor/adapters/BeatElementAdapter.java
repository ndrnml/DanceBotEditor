package ch.ethz.asl.dancebots.danceboteditor.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.asl.dancebots.danceboteditor.dialogs.BeatElementMenuDialog;
import ch.ethz.asl.dancebots.danceboteditor.model.BeatElement;
import ch.ethz.asl.dancebots.danceboteditor.R;
import ch.ethz.asl.dancebots.danceboteditor.model.Choreography;
import ch.ethz.asl.dancebots.danceboteditor.model.DanceSequence;
import ch.ethz.asl.dancebots.danceboteditor.utils.DividerItemDecoration;

/**
 * Created by andrin on 28.08.15.
 */
public class BeatElementAdapter<T extends BeatElement> extends RecyclerView.Adapter<BeatElementAdapter.ListItemViewHolder> implements DividerItemDecoration.VisibilityProvider {

    private final Context mContext;
    private ArrayList<T> mBeatElements;
    private int mPlayingItem;
    private Choreography<T> mChoregoraphy;

    private SparseBooleanArray selectedItems = new SparseBooleanArray();

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ListItemViewHolder extends RecyclerView.ViewHolder {
        // Each data item is just a string in this case
        public TextView mTextView;

        public ListItemViewHolder(View textView) {
            super(textView);
            mTextView = (TextView) textView.findViewById(R.id.tv_list_item);
        }
    }

    public BeatElementAdapter(Context context, ArrayList<T> elems, Choreography<T> choreography) {

        mContext = context;
        mBeatElements = elems;
        mChoregoraphy = choreography;

        mPlayingItem = 0;
    }

    public void setPlayingItem(int position) {
        mPlayingItem = position;
    }

    private void selectDanceSequence(List<Integer> selectedElems) {

        for (int elem : selectedElems) {
            if (selectedItems.get(elem, false)) {
                selectedItems.delete(elem);
            } else {
                selectedItems.put(elem, true);
            }

            notifyItemChanged(elem);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListItemViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        // Create a new view
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.beat_element, parent, false);

        // Set the view's size, margins, paddings and layout parameters
        final ListItemViewHolder listItemViewHolder = new ListItemViewHolder(itemView);

        // Ensure long clicks are enabled
        listItemViewHolder.mTextView.setLongClickable(true);

        // Create and attach on click listener
        listItemViewHolder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BeatElementMenuDialog dialog = new BeatElementMenuDialog();

                dialog.initializeMenuFromElement(BeatElementAdapter.this, mBeatElements.get(listItemViewHolder.getAdapterPosition()));
                dialog.show(((Activity) mContext).getFragmentManager(), "element_menu");
            }
        });

        // Create and attach on long click listener
        /*listItemViewHolder.mTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Toast mToast = Toast.makeText(mContext, "", Toast.LENGTH_LONG);
                mToast.setText("Item long clicked: " + listItemViewHolder.getAdapterPosition());
                mToast.show();

                BeatElement selBeatElem = mBeatElements.get(listItemViewHolder.getAdapterPosition());
                DanceSequence<T> danceSequence = mChoregoraphy.getDanceSequence(selBeatElem.getDanceSequenceId());

                if (danceSequence != null) {
                    selectDanceSequence(danceSequence.getElementIndices());
                }

                return true;
            }
        });*/

        return listItemViewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ListItemViewHolder viewHolder, int position) {

        /*
         * Get element from dataset at this position
         * Replace the contents of the view with that element
         * Populate the data into the template view using the data object
         */
        viewHolder.mTextView.setText(mBeatElements.get(position).getMotionType().getTag());

        /**
         * It would be nice to perform all selected and active elements with 'selector' in a xml file.
         *
         * Each motion has its own background color which is set here. This background color
         * overwrites all active or selected states set by the view holder.
         *
         * viewHolder.itemView.setSelected(true);
         * viewHolder.itemView.setActivated(selectedItems.get(position, false));
         */
        // Stylize list item according to type
        viewHolder.itemView.setBackgroundColor(mBeatElements.get(position).getMotionType().getColor());

        // Set background color of current playing/(selected) item
        if (mPlayingItem == position) {
            viewHolder.itemView.setBackground(mContext.getDrawable(R.drawable.playing_item));
        }

        // Set background color of activate items, belonging to this dance sequence
        if (selectedItems.get(position, false)) {
            viewHolder.itemView.setBackground(mContext.getDrawable(R.drawable.activated_item));
        }
    }

    @Override
    public int getItemCount() {
        return mBeatElements.size();
    }

    public BeatElement getItem(int position) {
        return mBeatElements.get(position);
    }

    @Override
    public boolean shouldHideDivider(int position, RecyclerView parent) {

        BeatElement selBeatElem = mBeatElements.get(position);
        DanceSequence<T> danceSequence = mChoregoraphy.getDanceSequence(selBeatElem.getDanceSequenceId());
        boolean isMiddleElement = false;

        // Check if element at position, belongs to a middle part of a dance sequence
        if (danceSequence != null) {
            isMiddleElement = danceSequence.isMiddleElement(position);
        }

        return isMiddleElement;
    }
}
