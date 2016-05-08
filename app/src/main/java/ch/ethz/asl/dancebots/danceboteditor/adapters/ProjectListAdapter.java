package ch.ethz.asl.dancebots.danceboteditor.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ch.ethz.asl.dancebots.danceboteditor.R;

/**
 * Created by andrin on 07.05.16. Hello
 */
public class ProjectListAdapter extends BaseAdapter {

    private ArrayList<File> mFileList;
    private LayoutInflater mProjectElementLayout;
    private Context mContext;

    public ProjectListAdapter(Context context, ArrayList<File> fileList) {
        mFileList = fileList;
        mContext = context;
        mProjectElementLayout = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mFileList.size();
    }

    @Override
    public Object getItem(int position) {
        return mFileList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Create new view from layout
        LinearLayout songLayout = (LinearLayout) mProjectElementLayout.inflate(R.layout.list_project_element, null);

        TextView projectName = (TextView)songLayout.findViewById(R.id.project_name);
        TextView projectEditDate = (TextView)songLayout.findViewById(R.id.project_edit_date);

        File file = mFileList.get(position);
        String name = file.getName();
        Date editDate = new Date(file.lastModified());

        // Display all relevant properties of this song
        projectName.setText(name);
        projectEditDate.setText(String.format(mContext.getString(R.string.last_edited), getDateAsString(editDate)));

        return songLayout;
    }

    private String getDateAsString(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
        return df.format(date);
    }
}
