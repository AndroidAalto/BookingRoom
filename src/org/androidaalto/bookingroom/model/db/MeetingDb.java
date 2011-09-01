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

package org.androidaalto.bookingroom.model.db;

import org.androidaalto.bookingroom.model.Meeting;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.Time;

import java.util.ArrayList;
import java.util.List;

public class MeetingDb {
    public static List<Meeting> getMeetings(Time from, Time to) {
        SQLiteDatabase db = DataBaseHelper.getInstance().getReadableDatabase();
        ArrayList<Meeting> records = new ArrayList<Meeting>();

        try {
            Cursor cursor = db
                    .rawQuery(
                            "SELECT id, user_id, title, (strftime('%s', start) * 1000) AS start_time, (strftime('%s', end) * 1000) AS end_time FROM meeting WHERE start > ? AND end < ?",
                            new String[] {
                                    "" + from.toMillis(false) / 1000,
                                    "" + to.toMillis(false) / 1000
                            });

            while (cursor.moveToNext()) {
                Time startTime = new Time();
                startTime.set(cursor.getLong(cursor.getColumnIndexOrThrow("start_time")));
                Time endTime = new Time();
                endTime.set(cursor.getLong(cursor.getColumnIndexOrThrow("end_time")));

                Meeting m = new Meeting(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("title")),
                        startTime,
                        endTime
                        );
                records.add(m);
            }

            return records;
        } finally {
            db.close();
            DataBaseHelper.getInstance().close();
        }
    }

    public static int getMeetingCount() {
        SQLiteDatabase db = DataBaseHelper.getInstance().getReadableDatabase();

        try {
            Cursor cursor = db.rawQuery("SELECT COUNT(*) AS count FROM meeting", null);

            if (cursor.moveToNext()) {
                return cursor.getInt(cursor.getColumnIndexOrThrow("count"));
            }
            return 0;
        } finally {
            db.close();
            DataBaseHelper.getInstance().close();
        }
    }

    /**
     * @param email
     * @return
     */
    public static Meeting get(long id) {
        SQLiteDatabase db = DataBaseHelper.getInstance().getReadableDatabase();

        try {
            Cursor cursor = db
                    .rawQuery(
                            "SELECT id, user_id, title, (strftime('%s', start) * 1000) AS start_time, (strftime('%s', end) * 1000) AS end_time FROM meeting WHERE id == ? LIMIT 1",
                            new String[] {
                                "" + id
                            });

            if (cursor.moveToNext()) {
                Time startTime = new Time();
                startTime.set(cursor.getLong(cursor.getColumnIndexOrThrow("start_time")));
                Time endTime = new Time();
                endTime.set(cursor.getLong(cursor.getColumnIndexOrThrow("end_time")));

                return new Meeting(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("title")),
                        startTime,
                        endTime);
            }
            return null;
        } finally {
            db.close();
            DataBaseHelper.getInstance().close();
        }
    }

    /**
     * @param meeting
     * @return
     */
    public static Meeting store(Meeting meeting) {
        SQLiteDatabase db = DataBaseHelper.getInstance().getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("title", meeting.getTitle());
        value.put("user_id", meeting.getUserId());
        value.put("start", meeting.getStart().toMillis(false) / 1000);
        value.put("end", meeting.getEnd().toMillis(false) / 1000);
        final long id = db.insert("meeting", null, value);
        return MeetingDb.get(id);
    }
}
