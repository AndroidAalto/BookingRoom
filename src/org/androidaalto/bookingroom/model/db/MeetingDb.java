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
import android.util.Log;

import java.util.ArrayList;

public class MeetingDb {
        
    public static ArrayList<Meeting> getMeetings(Time start, Time end) {
        
        SQLiteDatabase db = DataBaseHelper.getInstance().getReadableDatabase();
        ArrayList<Meeting> records = new ArrayList<Meeting>();
        
        try { 
            Log.e("TEST", "Hello");
            Log.e("TEST", ""+ start);
            Log.e("TEST", ""+ start.toMillis(true));
            Log.e("TEST", ""+ end.toMillis(true));
            
            Cursor cursor = db.rawQuery("SELECT * FROM meeting WHERE start > '" + start.toMillis(true) + "' AND end < '" + end.toMillis(true) + "'", null);

            while ( cursor.moveToNext() ) {
                Log.e("MeetingDB", "creating meeting object");
                
                Time startT = new Time();
                startT.set(cursor.getLong(3));
                Time endT = new Time();
                endT.set(cursor.getLong(4));
                
                
                Meeting m = new Meeting(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        startT,
                        endT
                );
                records.add(m);
            }
            
            db.close();
            DataBaseHelper.getInstance().close();
            return records;
        } catch (Exception e) {
            Log.e("TEST", "captured exception" + e.toString());
            return null;
        } finally {
            db.close();
            DataBaseHelper.getInstance().close();
        }        
    }
    
    public static int returnMeetingCount() {
        SQLiteDatabase db = DataBaseHelper.getInstance().getReadableDatabase();
        
        try { 
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM meeting", null);

            int i = 0;
            while ( cursor.moveToNext() ) {
                i = cursor.getInt(0);
            }
            
            db.close();
            DataBaseHelper.getInstance().close();
            return i;
        } catch (Exception e) {
            Log.e("TEST", "captured exception" + e.toString());
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
    public static Meeting get(long last_row_id) {
        SQLiteDatabase db = DataBaseHelper.getInstance().getReadableDatabase();

        try {
            Meeting meeting = null;
            Cursor cursor = db.rawQuery("SELECT * FROM meeting WHERE id == '" + last_row_id + "' LIMIT 1", null);

            if ( cursor.moveToNext() ) {
                Time startT = new Time();
                startT.set(cursor.getLong(3));
                Time endT = new Time();
                endT.set(cursor.getLong(4));
                
                meeting = new Meeting(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        startT,
                        endT
                );
            }
            
            db.close();
            DataBaseHelper.getInstance().close();
            
            return meeting;
        } catch (Exception e) {
            Log.e("TEST", "captured exception" + e.toString());
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
        value.put("start", meeting.getStart().toString());
        value.put("end", meeting.getEnd().toString());
        long last_row_id = db.insert("meeting", null, value);
        return MeetingDb.get(last_row_id);
    }
}
