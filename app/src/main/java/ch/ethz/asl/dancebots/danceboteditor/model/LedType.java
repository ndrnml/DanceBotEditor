package ch.ethz.asl.dancebots.danceboteditor.model;

import ch.ethz.asl.dancebots.danceboteditor.R;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotEditorProjectFile;

/**
 * Created by andrin on 21.10.15.
 */
// 'Static' enum types are instantiated with the object
public enum LedType implements MotionType {
    DEFAULT(DanceBotEditorProjectFile.getInstance().getContext().getResources().getColor(R.color.led_list_default_color), ""),
    KNIGHT_RIDER(DanceBotEditorProjectFile.getInstance().getContext().getResources().getColor(R.color.led_elem_color1), "K"),
    RANDOM(DanceBotEditorProjectFile.getInstance().getContext().getResources().getColor(R.color.led_elem_color2), "R"),
    BLINK(DanceBotEditorProjectFile.getInstance().getContext().getResources().getColor(R.color.led_elem_color3), "B"),
    SAME_BLINK(DanceBotEditorProjectFile.getInstance().getContext().getResources().getColor(R.color.led_elem_color4), "S"),
    CONSTANT(DanceBotEditorProjectFile.getInstance().getContext().getResources().getColor(R.color.led_elem_color5), "C");

    private int mColor;
    private String mTag;

    LedType(int color, String tag) {
        mColor = color;
        mTag = tag;
    }

    public int getColor() {
        return mColor;
    }

    public String getTag() {
        return mTag;
    }
}