package ch.ethz.asl.dancebots.danceboteditor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by andrin on 28.08.15.
 */
public class BeatElementAdapter extends ArrayAdapter<BeatElement> {

    // View lookup cache
    private static class ViewHolder {
        TextView name;
    }

    public BeatElementAdapter(Context context, ArrayList<BeatElement> elems) {
        super(context, 0, elems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get beat grid element for this position
        BeatElement elem = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());

            convertView = inflater.inflate(R.layout.beat_grid_element, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.txt_beat_grid_elem_type);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object
        viewHolder.name.setText(elem.getName());

        // Return the completed view to render on screen
        return convertView;

    }
}
