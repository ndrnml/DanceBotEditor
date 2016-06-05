package ch.ethz.asl.dancebots.danceboteditor.model;

import java.io.Serializable;

import ch.ethz.asl.dancebots.danceboteditor.R;

/**
 * Author: Andrin Jenal
 * Copyright: ETH Zürich
 */
// 'Static' enum types are instantiated with the object
public enum LedType implements MotionType, Serializable {

    DEFAULT(mContext.getResources().getColor(R.color.led_list_default_color),
            "",
            "DEFAULT"),

    KNIGHT_RIDER(mContext.getResources().getColor(R.color.led_elem_color1),
            "K",
            mContext.getString(R.string.led_type_knight_rider)),

    RANDOM(mContext.getResources().getColor(R.color.led_elem_color2),
            "R",
            mContext.getString(R.string.led_type_random)),

    BLINK(mContext.getResources().getColor(R.color.led_elem_color3),
            "B",
            mContext.getString(R.string.led_type_blink)),

    SAME_BLINK(mContext.getResources().getColor(R.color.led_elem_color4),
            "S",
            mContext.getString(R.string.led_type_same_blink)),

    CONSTANT(mContext.getResources().getColor(R.color.led_elem_color5),
            "C",
            mContext.getString(R.string.led_type_constant));

    private int mColor;
    private String mTag;
    private String mReadableName;

    LedType(int color, String tag, String readableName) {
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