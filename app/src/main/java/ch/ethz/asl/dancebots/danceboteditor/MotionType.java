package ch.ethz.asl.dancebots.danceboteditor;

/**
 * Created by andrin on 04.09.15.
 */
interface MotionType {
}

enum MoveType implements MotionType {
    STRAIGHT,
    SPIN,
    TWIST,
    BACK_AND_FORTH,
    CONSTANT,
    WAIT
}

enum LedType implements MotionType {
    KNIGHT_RIDER,
    RANDOM,
    BLINK,
    SAME_BLINK,
    CONSTANT
}