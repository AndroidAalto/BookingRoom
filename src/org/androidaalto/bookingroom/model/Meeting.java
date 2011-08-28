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

import android.database.Cursor;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @author hannu
 */
public class Meeting {
    private final Integer id;
    private final int userId;
    private final Timestamp start;
    private final Timestamp end;
    private final String title;

    public Meeting(int userId, Timestamp start, Timestamp end, String title) {
        this(null, userId, start, end, title);
    }

    public Meeting(Integer id, int userId, Timestamp start, Timestamp end, String title) {
        this.id = id;
        this.userId = userId;
        this.start = start;
        this.end = end;
        this.title = title;
    }

    /**
     * @param cursor
     */
    public Meeting(Cursor cursor) {
        this.id = cursor.getInt(0);
        this.userId = cursor.getInt(1);
        this.start = new Timestamp(cursor.getLong(2));
        this.end = new Timestamp(cursor.getLong(3));
        this.title = cursor.getString(4);
    }

    public Integer getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public Timestamp getStart() {
        return start;
    }

    public Timestamp getEnd() {
        return end;
    }

    public String getTitle() {
        return title;
    }
}
