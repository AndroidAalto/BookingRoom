/**
   Copyright: 2011 Android Aalto

   This file is part of BookingRoom_jush.

   BookingRoom_jush is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 3 of the License, or
   (at your option) any later version.

   BookingRoom_jush is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with BookingRoom_jush. If not, see <http://www.gnu.org/licenses/>.
 */
package org.androidaalto.bookingroom.view;

/**
 * @author jush
 *
 */
public class FlingEffect implements Runnable {

    int mSignDeltaY;
    int mAbsDeltaY;
    float mFloatDeltaY;
    long mFreeSpinTime;
    private WeekView mView;
    private static final float FRICTION_COEF = 0.7F;
    private static final long FREE_SPIN_MILLIS = 180;
    private static final int MAX_DELTA = 60;
    private static final int SCROLL_REPEAT_INTERVAL = 30;

    public void init(WeekView view, int deltaY) {
        mView = view;
        mSignDeltaY = 0;
        if (deltaY > 0) {
            mSignDeltaY = 1;
        } else if (deltaY < 0) {
            mSignDeltaY = -1;
        }
        mAbsDeltaY = Math.abs(deltaY);

        // Limit the maximum speed
        if (mAbsDeltaY > MAX_DELTA) {
            mAbsDeltaY = MAX_DELTA;
        }
        mFloatDeltaY = mAbsDeltaY;
        mFreeSpinTime = System.currentTimeMillis() + FREE_SPIN_MILLIS;
//        Log.i("Cal", "init scroll: mAbsDeltaY: " + mAbsDeltaY
//                + " mViewStartY: " + mViewStartY);
    }

    public void run() {
        long time = System.currentTimeMillis();

        // Start out with a frictionless "free spin"
        if (time > mFreeSpinTime) {
            // If the delta is small, then apply a fixed deceleration.
            // Otherwise
            if (mAbsDeltaY <= 10) {
                mAbsDeltaY -= 2;
            } else {
                mFloatDeltaY *= FRICTION_COEF;
                mAbsDeltaY = (int) mFloatDeltaY;
            }

            if (mAbsDeltaY < 0) {
                mAbsDeltaY = 0;
            }
        }

        if (mSignDeltaY == 1) {
            mView.increaseViewStartY(-mAbsDeltaY);
        } else {
            mView.increaseViewStartY(mAbsDeltaY);
        }
//        Log.i("Cal", "  scroll: mAbsDeltaY: " + mAbsDeltaY
//                + " mViewStartY: " + mViewStartY);

        int mViewStartY = mView.getViewStartY();
        int mMaxViewStartY = mView.getMaxViewStartY();
        if (mViewStartY  < 0 || mViewStartY > mMaxViewStartY) {
            mAbsDeltaY = 0;
        }

        mView.computeFirstHour();

        if (mAbsDeltaY > 0) {
            mView.postDelayed(this, SCROLL_REPEAT_INTERVAL);
        } else {
            // Done scrolling.
            mView.finishScrolling();
            
        }

        mView.invalidate();
    }

}
