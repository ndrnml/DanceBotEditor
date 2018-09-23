package ch.ethz.asl.dancebots.danceboteditor.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.dragselectrecyclerview.DragSelectRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.asl.dancebots.danceboteditor.R;
import ch.ethz.asl.dancebots.danceboteditor.dialogs.BeatElementMenuDialog;
import ch.ethz.asl.dancebots.danceboteditor.model.BeatElement;
import ch.ethz.asl.dancebots.danceboteditor.model.Choreography;
import ch.ethz.asl.dancebots.danceboteditor.model.DanceSequence;
import ch.ethz.asl.dancebots.danceboteditor.view.DividerItemDecoration;

/**
 * Created by andrin on 28.08.15.
 */
public class BeatElementAdapter<T extends BeatElement> extends DragSelectRecyclerViewAdapter<BeatElementAdapter.ListItemViewHolder>
        implements DividerItemDecoration.VisibilityProvider {

    public interface TouchClickListener {
        void onClick(int index);

        void onLongClick(int index);
    }

    private final Context mContext;
    private ArrayList<T> mBeatElements;
    private int mPlayingItem;
    private Choreography<T> mChoreography;

    private SparseBooleanArray selectedItems = new SparseBooleanArray();

    private TouchClickListener mCallback;

    public static class ListItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private final TouchClickListener mCallback;
        public final TextView mTextView;

        public ListItemViewHolder(View textView, TouchClickListener callback) {
            super(textView);
            mTextView = (TextView) textView.findViewById(R.id.tv_list_item);
            mCallback = callback;

            this.mTextView.setOnClickListener(this);
            this.mTextView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mCallback != null) {
                mCallback.onClick(getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (mCallback != null) {
                mCallback.onLongClick(getAdapterPosition());
            }
            return true;
        }
    }

    public BeatElementAdapter(Context context, ArrayList<T> elems, Choreography<T> choreography) {

        mContext = context;
        mBeatElements = elems;
        mChoreography = choreography;

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

    public void setTouchClickListener(TouchClickListener clickListener) {
        this.mCallback = clickListener;
    }

    @Override
    public ListItemViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        // Create a new view
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_beat_element, parent, false);

        // Set the view's size, margins, paddings and layout parameters
        final ListItemViewHolder listItemViewHolder = new ListItemViewHolder(itemView, mCallback);

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

        return listItemViewHolder;
    }

    @Override
    public void onBindViewHolder(ListItemViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);

        viewHolder.mTextView.setText(mBeatElements.get(position).getMotionType().getTag());

        viewHolder.itemView.setBackgroundColor(mBeatElements.get(position).getMotionType().getColor());

        // Set background color of current playing/(selected) item
        if (mPlayingItem == position) {
            viewHolder.itemView.setBackground(mContext.getDrawable(R.drawable.playing_item));
        }
        // Set background color of activate items, belonging to this dance sequence
        if (selectedItems.get(position, false)) {
            viewHolder.itemView.setBackground(mContext.getDrawable(R.drawable.activated_item));
        }

        if (isIndexSelected(position)) {
            // Change item somehow
        } else {
            // Reset item
        }
    }

    @Override
    public int getItemCount() {
        return mBeatElements.size();
    }

    @Override
    public boolean shouldHideDivider(int position, RecyclerView parent) {

        BeatElement selBeatElem = mBeatElements.get(position);
        DanceSequence<T> danceSequence = mChoreography.getDanceSequence(selBeatElem.getDanceSequenceId());
        boolean isMiddleElement = false;

        // Check if element at position, belongs to a middle part of a dance sequence
        if (danceSequence != null) {
            isMiddleElement = danceSequence.isMiddleElement(position);
        }

        return isMiddleElement;
    }
}
