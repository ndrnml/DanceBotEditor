package ch.ethz.asl.dancebots.danceboteditor.model;

import android.util.Pair;

import java.util.ArrayList;
import java.util.EnumSet;

/**
 * Created by andrin on 03.12.15.
 */
public class BeatElementContents {

    private final int VELOCITY_MIN = 0;
    private final int VELOCITY_MAX = 110;
    private final int VELOCITY_STEP = 10;
    private final int CHOREO_LENGTH_MIN = 1;
    private final int CHOREO_LENGTH_MAX = 50;
    private final int CHOREO_LENGTH_STEP = 1;

    private static int DEFAULT_LENGTH_INDEX = 0;
    private static int DEFAULT_VELOCITY_INDEX = 0;

    private static BeatElementContents sInstance = null;

    private static final ArrayList<Pair<Integer, Integer>> mMotorFrequencies = new ArrayList<>();
    private static final ArrayList<Pair<Integer, Integer>> mLedFrequencies = new ArrayList<>();
    private static final ArrayList<Integer> mVelocityValues = new ArrayList<>();
    private static final ArrayList<Integer> mLengthValues = new ArrayList<>();
    private static final ArrayList<MotorType> mMotorTypes = new ArrayList<>();
    private static final ArrayList<LedType> mLedTypes = new ArrayList<>();

    // A static block that sets class fields
    static {
        // Creates a single static instance of BeatElementContents
        sInstance = new BeatElementContents();
    }

    /**
     * Initialize all final BeatElement properties collections
     */
    private BeatElementContents() {

        generateMotorFrequencies(mMotorFrequencies);
        generateLedFrequencies(mLedFrequencies);

        generateIntegersInRange(mVelocityValues, VELOCITY_MIN, VELOCITY_MAX, VELOCITY_STEP);
        generateIntegersInRange(mLengthValues, CHOREO_LENGTH_MIN, CHOREO_LENGTH_MAX, CHOREO_LENGTH_STEP);

        DEFAULT_VELOCITY_INDEX = Math.round(mVelocityValues.size() / 2); // This should be 50
        DEFAULT_LENGTH_INDEX = 7; // This is set to 8 (if common time beat)

        generateMotorTypes();
        generateLedTypes();
    }

    /**
     * The DEFAULT type is deliberately omitted
     * @return final valid motor element types
     */
    private void generateMotorTypes() {

        mMotorTypes.addAll(EnumSet.of(
                MotorType.BACK_AND_FORTH,
                MotorType.CONSTANT,
                MotorType.SPIN,
                MotorType.STRAIGHT,
                MotorType.TWIST,
                MotorType.WAIT));
    }

    /**
     * The DEFAULT type is deliberately omitted
     * @return final valid led element types
     */
    private void generateLedTypes() {

        mLedTypes.addAll(EnumSet.of(
                LedType.BLINK,
                LedType.CONSTANT,
                LedType.KNIGHT_RIDER,
                LedType.RANDOM,
                LedType.SAME_BLINK));
    }

    /**
     * Generate led frequencies
     */
    private void generateLedFrequencies(ArrayList<Pair<Integer, Integer>> frequencyList) {
        // "1/4", "1/3", "1/2", "2/3", "3/2", "2", "3", "4"
        frequencyList.add(new Pair<>(1, 4));
        frequencyList.add(new Pair<>(1, 3));
        frequencyList.add(new Pair<>(1, 2));
        frequencyList.add(new Pair<>(2, 3));
        frequencyList.add(new Pair<>(1, 1));
        frequencyList.add(new Pair<>(3, 2));
        frequencyList.add(new Pair<>(2, 1));
        frequencyList.add(new Pair<>(3, 1));
        frequencyList.add(new Pair<>(4, 1));
    }

    /**
     *
     * Generate motor frequencies
     */
    private void generateMotorFrequencies(ArrayList<Pair<Integer, Integer>> frequencyList) {
        // "1/4", "1/3", "1/2", "2/3", "1"
        frequencyList.add(new Pair<>(1, 4));
        frequencyList.add(new Pair<>(1, 3));
        frequencyList.add(new Pair<>(2, 3));
        frequencyList.add(new Pair<>(1, 1));
    }

    /**
     * Generates arbitrary integer arrays in the range of min and max, with step size: step
     * @param min start of the integer array list
     * @param max end of the integer array list
     * @param step step size
     * @return newly created integer array list
     */
    private void generateIntegersInRange(ArrayList<Integer> collection, int min, int max, int step) {

        int number_of_elements = (max / step);
        for (int i = 0; i < number_of_elements; ++i) {
            collection.add(min + (i * step));
        }
    }

    /**
     * @return static BeatElementContents instance
     */
    public BeatElementContents getInstance() {
        return sInstance;
    }

    public static ArrayList<Pair<Integer,Integer>> getMotorFrequencies() {
        return mMotorFrequencies;
    }

    public static ArrayList<Pair<Integer,Integer>> getLedFrequencies() {
        return mLedFrequencies;
    }

    public static ArrayList<LedType> getLedTypes() {
        return mLedTypes;
    }

    public static ArrayList<MotorType> getMotorTypes() {
        return mMotorTypes;
    }

    public static ArrayList<Integer> getVelocityValues() {
        return mVelocityValues;
    }

    public static ArrayList<Integer> getLengthValues() {
        return mLengthValues;
    }

    public static int getDefaultVelocityIdx() {
        return DEFAULT_VELOCITY_INDEX;
    }

    public static int getDefaultLengthIdx() {
        return DEFAULT_LENGTH_INDEX;
    }

}
