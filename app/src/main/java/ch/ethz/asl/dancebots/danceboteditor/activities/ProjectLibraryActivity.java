package ch.ethz.asl.dancebots.danceboteditor.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

import ch.ethz.asl.dancebots.danceboteditor.R;
import ch.ethz.asl.dancebots.danceboteditor.adapters.ProjectListAdapter;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotHelper;

/**
 * Created by andrin on 07.05.16. Hello
 */
public class ProjectLibraryActivity extends ListActivity {

    private static final String LOG_TAG = ProjectLibraryActivity.class.getSimpleName();

    public static final String INTENT_PROJECT_NAME = "NAME";
    public static final String INTENT_PROJECT_EDIT_DATE = "EDIT_DATE";
    public static final String INTENT_PROJECT_PATH = "PATH";
    private ArrayList<File> projectList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set list view
        setContentView(R.layout.activity_project_library);

        loadProjectFileList();

        // Create song list view adapter, initialized with empty arrays
        ProjectListAdapter projectListAdapter = new ProjectListAdapter(this, projectList);
        setListAdapter(projectListAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        // Create return results for intent
        Intent returnIntent = new Intent();
        File file = projectList.get(position);
        returnIntent.putExtra(INTENT_PROJECT_NAME, file.getName());
        returnIntent.putExtra(INTENT_PROJECT_EDIT_DATE, file.lastModified());
        returnIntent.putExtra(INTENT_PROJECT_PATH, file.getAbsolutePath());

        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private void loadProjectFileList() {
        File projectPath = DanceBotHelper.getProjectDirectory(this);
        Log.v(LOG_TAG, "Project path: " + projectPath);

        File fileList[] = projectPath.listFiles();

        for (File f : fileList) {
            if (DanceBotHelper.isProjectFile(f)) {
                projectList.add(f);
            }
        }
    }
}
