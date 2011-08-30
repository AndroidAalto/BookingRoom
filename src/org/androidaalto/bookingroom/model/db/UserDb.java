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

    public static int returnUserCount() {
        SQLiteDatabase db = DataBaseHelper.getInstance().getReadableDatabase();
        Log.e("UDB", ""+ db.toString());
        
        try { 
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM user", null);

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
    public static User get(String email) {
        SQLiteDatabase db = DataBaseHelper.getInstance().getReadableDatabase();

        try {
            User user = null;
            Cursor cursor = db.rawQuery("SELECT * FROM user WHERE email == '" + email + "' LIMIT 1", null);

            if ( cursor.moveToNext() ) {
                user = new User(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(4),
                        false //TODO
                );
            }
            
            db.close();
            DataBaseHelper.getInstance().close();
            
            return user;
        } catch (Exception e) {
            Log.e("TEST", "captured exception" + e.toString());
            return null;
        } finally {
            db.close();
            DataBaseHelper.getInstance().close();
        }
    }

    /**
     * @param user
     * @return
     */
    public static User store(User user) {
        SQLiteDatabase db = DataBaseHelper.getInstance().getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("Name", user.getName());
        value.put("email", user.getEmail());
        value.put("is_admin", user.isAdmin());
        db.insert("user", null, value);
        return UserDb.get(user.getEmail());
    }
}
