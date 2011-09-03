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

public class UserDb {
    public static int getUserCount() {
        SQLiteDatabase db = DataBaseHelper.getInstance().getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) AS count FROM user", null);

            if (cursor.moveToNext()) {
                return cursor.getInt(cursor.getColumnIndexOrThrow("count"));
            }
            return 0;
        } finally {
            if ( cursor != null ) {
                cursor.close();
            }
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

        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM user WHERE email == ? LIMIT 1",
                    new String[] {
                        email
                    });

            if (cursor.moveToNext()) {
                return new User(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("email")),
                        cursor.getString(cursor.getColumnIndexOrThrow("password")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("salt")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("is_admin")) > 0);
            }
            return null;
        } finally {
            if ( cursor != null ) {
                cursor.close();
            }
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
