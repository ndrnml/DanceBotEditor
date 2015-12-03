package ch.ethz.asl.dancebots.danceboteditor.model;

import android.content.Context;

import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotEditorManager;

/**
 * Created by andrin on 21.10.15.
 */
public interface MotionType {

    Context mContext = DanceBotEditorManager.getInstance().getContext();

    int getColor();
    String getTag();
    String getReadableName();
}
