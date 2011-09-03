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

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DataBaseHelper";

    private static final String DB_NAME = "booking.db";

    // Remember to increase whenever you want to call onUpdate
    private static final int DB_VERSION = 1;

    private static final String CREATE_MEETING_SQL =
            "CREATE TABLE meeting(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER," +
                    "title VARCHAR(100)," +
                    "start TIMESTAMP," +
                    "end TIMESTAMP)";

    private static final String CREATE_USER_SQL =
            "CREATE TABLE user(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name VARCHAR(30)," +
                    "email VARCHAR(30)," +
                    "password VARCHAR(50)," +
                    "salt VARCHAR(50)," +
                    "is_admin BOOLEAN default false)";

    private static DataBaseHelper instance;

    private DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static DataBaseHelper getInstance() {
        if (instance == null)
            throw new IllegalStateException("Instance has not been instantiated");
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "going to create tables");
        db.execSQL(CREATE_MEETING_SQL);
        db.execSQL(CREATE_USER_SQL);

        // Dummy values
        ContentValues value = new ContentValues();
        value.put("id", 1);
        value.put("name", "Test");
        value.put("email", "test@test.com");
        // value.put("password", "test");
        // value.put("salt", "test");
        value.put("is_admin", false);
        db.insert("user", null, value);

        value = new ContentValues();
        value.put("id", 1);
        value.put("user_id", 1);
        value.put("title", "Meeting");
        value.put("start", "1314519236000"); // Sun, 28 Aug 2011 08:13:56 GMT
        value.put("end", "1314522836000"); // Sun, 28 Aug 2011 09:13:56 GMT
        db.insert("meeting", null, value);

        value = new ContentValues();
        value.put("id", 2);
        value.put("user_id", 1);
        value.put("title", "Another Meeting");
        value.put("start", "1314608400000"); // Mon, 29 Aug 2011 09:00:00 GMT
        value.put("end", "1314615600000"); // Mon, 29 Aug 2011 11:00:00 GMT
        db.insert("meeting", null, value);

        Log.d("DBHELPER", "tables created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     * @return
     */
    public static void setContext(Context context) {
        instance = new DataBaseHelper(context);
    }

}
