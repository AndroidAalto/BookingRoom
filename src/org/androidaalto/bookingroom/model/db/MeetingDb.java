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
import android.util.Log;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MeetingDb {
    private DataBaseHelper dbHelper;
    
    public MeetingDb(DataBaseHelper dbh) { 
        this.dbHelper = dbh;
    }  
    
    public ArrayList<Meeting> getMeetings(Timestamp start, Timestamp end) {
        
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<Meeting> records = new ArrayList<Meeting>();
        
        try { 
            SimpleDateFormat s = new SimpleDateFormat("dd/MM/yyyy");
            Log.e("MeetingDB", ""+s.format(start));
            
            Cursor cursor = db.rawQuery("SELECT * FROM meeting WHERE start > '" + start + "' AND end < '" + end + "'", null);

            while ( cursor.moveToNext() ) {
                Log.e("MeetingDB", "creating meeting object");
                Meeting m = new Meeting(cursor);
                records.add(m);
            }
            
            db.close();
            dbHelper.close();
            return records;
        } catch (Exception e) {
            Log.e("TEST", "captured exception" + e.toString());
            return null;
        } finally {
            db.close();
            dbHelper.close();
        }        
    }
    
    public int returnMeetingCount() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        try { 
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM meeting", null);

            int i = 0;
            while ( cursor.moveToNext() ) {
                i = cursor.getInt(0);
            }
            
            db.close();
            dbHelper.close();
            return i;
        } catch (Exception e) {
            Log.e("TEST", "captured exception" + e.toString());
            return 0;
        } finally {
            db.close();
            dbHelper.close();
        }    
    }
    
    /**
     * @param email
     * @return
     */
    public Meeting get(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {
            Meeting meeting = null;
            Cursor cursor = db.rawQuery("SELECT * FROM meeting WHERE id == '" + id + "' LIMIT 1", null);

            if ( cursor.moveToNext() ) {
                meeting = new Meeting(cursor);
            }
            
            db.close();
            dbHelper.close();
            
            return meeting;
        } catch (Exception e) {
            Log.e("TEST", "captured exception" + e.toString());
            return null;
        } finally {
            db.close();
            dbHelper.close();
        }
    }

    /**
     * @param meeting
     * @return
     */
    public Meeting store(Meeting meeting) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("title", meeting.getTitle());
        value.put("user_id", meeting.getUserId());
        value.put("start", meeting.getStart().toString());
        value.put("end", meeting.getEnd().toString());
        db.insert("meeting", null, value);
        return meeting;
    }
}
