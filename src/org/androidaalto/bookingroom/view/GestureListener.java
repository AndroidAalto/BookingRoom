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

import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

/**
 * @author jush
 *
 */
public class GestureListener extends SimpleOnGestureListener {
    
    private WeekView mView;

    public GestureListener(WeekView view) {
        mView = view;
    }
    
    @Override
    public boolean onSingleTapUp(MotionEvent ev) {
        mView.doSingleTapUp(ev);
        return true;
    }

    @Override
    public void onLongPress(MotionEvent ev) {
        mView.doLongPress(ev);
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        mView.doScroll(e1, e2, distanceX, distanceY);
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        mView.doFling(e1, e2, velocityX, velocityY);
        return true;
    }

    @Override
    public boolean onDown(MotionEvent ev) {
        mView.doDown(ev);
        return true;
    }

}
