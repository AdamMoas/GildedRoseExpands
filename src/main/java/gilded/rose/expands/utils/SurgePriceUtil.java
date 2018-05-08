package gilded.rose.expands.utils;

import java.util.Date;

public class SurgePriceUtil {

    private static final int HUNDRED_PERCENT = 100;
    private static final long PAST_VIEW = -1;

    private long initialSurgeWindowInMilliseconds;

    private int numberOfPreviousViews;
    private long previousViewTimes[];
    private int oldestView;

    private int surgePriceIncrementPercent;
    private int surgePriceMultiplierPercent;

    private static final int RESET_WINDOW = 0;


    public SurgePriceUtil(long initialSurgeWindowInMilliseconds, int viewsPerWindow, int surgePriceIncrementPercent) {
        this.initialSurgeWindowInMilliseconds = initialSurgeWindowInMilliseconds;
        this.numberOfPreviousViews = viewsPerWindow - 1;
        this.previousViewTimes = new long[viewsPerWindow];
        this.surgePriceIncrementPercent = surgePriceIncrementPercent;
        this.surgePriceMultiplierPercent = HUNDRED_PERCENT;

        initializePreviousViews();
    }

    private long getTimeOfViewInMilliseconds() {
        return (new Date()).getTime();
    }

    private boolean withinCurrentWindow(long viewTime) {
        return viewTime <= (previousViewTimes[oldestView] + initialSurgeWindowInMilliseconds);
    }

    private void incrementSurgePriceMultiplier() {
        surgePriceMultiplierPercent = surgePriceMultiplierPercent + surgePriceIncrementPercent;
    }

    private void initializePreviousViews() {
        for (int i = 0; i < previousViewTimes.length; i++) {
            previousViewTimes[i] = PAST_VIEW;
        }
        oldestView = RESET_WINDOW;
    }

    private void updateOldestView() {
        oldestView = ((oldestView + 1) % numberOfPreviousViews);
    }

    private void updateLatestWindow(long viewTime) {
        previousViewTimes[oldestView] = viewTime;
        updateOldestView();
    }

    public int calculateSurgeMultiplierPercent() {
        if (withinCurrentWindow(getTimeOfViewInMilliseconds())) {
            incrementSurgePriceMultiplier();
            initializePreviousViews();
        } else {
            updateLatestWindow(getTimeOfViewInMilliseconds());
        }
        return surgePriceMultiplierPercent;
    }

    public static int applySurgeMultiplier(int price, int surgeMultiplier) {
        return (Math.round(((price * surgeMultiplier) / HUNDRED_PERCENT)));
    }

}
