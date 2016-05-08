package ch.ethz.asl.dancebots.danceboteditor.model;

import java.io.Serializable;

import ch.ethz.asl.dancebots.danceboteditor.R;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotEditorManager;

/**
 * Created by andrin on 21.10.15.
 */

// 'Static' enum types are instantiated with the object
public enum MotorType implements MotionType, Serializable {

    DEFAULT(
            mContext.getResources().getColor(R.color.motor_list_default_color),
            "",
            "DEFAULT"),

    STRAIGHT(mContext.getResources().getColor(R.color.motor_elem_color1),
            "S",
            mContext.getResources().getString(R.string.motor_type_straight)),

    SPIN(mContext.getResources().getColor(R.color.motor_elem_color2),
            "P",
            mContext.getString(R.string.motor_type_spin)),

    TWIST(mContext.getResources().getColor(R.color.motor_elem_color3),
            "T",
            mContext.getString(R.string.motor_type_twist)),

    BACK_AND_FORTH(mContext.getResources().getColor(R.color.motor_elem_color4),
            "B",
            mContext.getString(R.string.motor_type_back_and_forth)),

    CONSTANT(mContext.getResources().getColor(R.color.motor_elem_color5),
            "C",
            mContext.getString(R.string.motor_type_constant)),

    WAIT(mContext.getResources().getColor(R.color.motor_elem_color6),
            "W",
            mContext.getString(R.string.motor_type_wait));

    private int mColor;
    private String mTag;
    private String mReadableName;

    MotorType(int color, String tag, String readableName) {
        mColor = color;
        mTag = tag;
        mReadableName = readableName;
    }

    @Override
    public int getColor() {
        return mColor;
    }

    @Override
    public String getTag() {
        return mTag;
    }

    @Override
    public String getReadableName() {
        return mReadableName;
    }
}