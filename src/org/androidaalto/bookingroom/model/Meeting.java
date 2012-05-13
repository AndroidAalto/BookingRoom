/**
   Copyright: 2011 Android Aalto

   This file is part of BookingRoom.

   BookingRoom is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 3 of the License, or
   (at your option) any later version.

   BookingRoom is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with BookingRoom. If not, see <http://www.gnu.org/licenses/>.
 */

package org.androidaalto.bookingroom.model;

import android.text.format.Time;

/**
 * @author hannu
 */
public class Meeting {
    private final Long id;
    private final Long userId;
    private final Time start;
    private final Time end;
    private final String title;
    private final int pincode;

    public Meeting(Long userId, String title, Time start, Time end, int pincode) {
        this(null, userId, title, start, end, pincode);
    }

    public Meeting(Long id, Long userId, String title, Time start, Time end, int pincode) {
        this.id = id;
        this.userId = userId;
        this.start = start;
        this.end = end;
        this.title = title;
        this.pincode = pincode;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Time getStart() {
        return start;
    }

    public Time getEnd() {
        return end;
    }

    public String getTitle() {
        return title;
    }

    public int getPin() {
        return pincode;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "id = " + id + " userId = " + userId + " start = " + start + " end = " + end
                + " title = " + title + " pincode = " + pincode;
    }
}
