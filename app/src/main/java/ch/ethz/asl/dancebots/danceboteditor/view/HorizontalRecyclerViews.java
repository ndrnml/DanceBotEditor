package ch.ethz.asl.dancebots.danceboteditor.view;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import ch.ethz.asl.dancebots.danceboteditor.R;
import ch.ethz.asl.dancebots.danceboteditor.adapters.BeatElementAdapter;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotEditorProjectFile;
import ch.ethz.asl.dancebots.danceboteditor.utils.DividerItemDecoration;

/**
 * Created by andrin on 24.10.15.
 */
public class HorizontalRecyclerViews {

    private Activity mActivity;

    private DanceBotEditorProjectFile mProjectFile;

    private LinearLayoutManager mMotorLayoutManager;
    private LinearLayoutManager mLedLayoutManager;
    private RecyclerView mMotorView;
    private RecyclerView mLedView;

    public HorizontalRecyclerViews(Activity activity) {

        // Keep host activity
        mActivity = activity;

        mProjectFile = DanceBotEditorProjectFile.getInstance();

        // Initialize and setup linear layout manager
        mMotorLayoutManager = new LinearLayoutManager(mActivity.getApplicationContext());
        mLedLayoutManager = new LinearLayoutManager(mActivity.getApplicationContext());

        mMotorLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mLedLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        // Get divider drawable
        Drawable divider = mActivity.getResources().getDrawable(R.drawable.divider);

        // Attach motor adapter and linear layout manager to the horizontal recycler view
        mMotorView = (RecyclerView) mActivity.findViewById(R.id.motor_element_list);
        mMotorView.setHasFixedSize(true);
        mMotorView.setLayoutManager(mMotorLayoutManager);
        mMotorView.addItemDecoration(new DividerItemDecoration(divider));

        // Attach led adapter and linear layout manager
        mLedView = (RecyclerView) mActivity.findViewById(R.id.led_element_list);
        mLedView.setHasFixedSize(true);
        mLedView.setLayoutManager(mLedLayoutManager);
        mLedView.addItemDecoration(new DividerItemDecoration(divider));
    }

    public void setAdapters() {

        // Create the beat adapters
        BeatElementAdapter motorAdapter = new BeatElementAdapter(mProjectFile.getChoreoManager().mMotorChoreography.mBeatElements);
        BeatElementAdapter ledAdapter = new BeatElementAdapter(mProjectFile.getChoreoManager().mLedChoregraphy.mBeatElements);

        // Attach apapters
        mMotorView.setAdapter(motorAdapter);
        mLedView.setAdapter(ledAdapter);
    }
}
