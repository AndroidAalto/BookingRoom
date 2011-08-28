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

import org.androidaalto.bookingroom.model.User;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class UserDb {
    
    private DataBaseHelper dbHelper;
    
    public UserDb(DataBaseHelper dbh) { 
        this.dbHelper = dbh;
    }  
    
    public int returnUserCount() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        try { 
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM user", null);

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
    public User get(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {
            User user = null;
            Cursor cursor = db.rawQuery("SELECT * FROM user WHERE email == '" + email + "' LIMIT 1", null);

            if ( cursor.moveToNext() ) {
                user = new User(cursor);
            }
            
            db.close();
            dbHelper.close();
            
            return user;
        } catch (Exception e) {
            Log.e("TEST", "captured exception" + e.toString());
            return null;
        } finally {
            db.close();
            dbHelper.close();
        }
    }

    /**
     * @param user
     * @return
     */
    public User store(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("Name", user.getName());
        value.put("email", user.getEmail());
        value.put("is_admin", user.is_admin());
        db.insert("user", null, value);
        return this.get(user.getEmail());
    }
}
