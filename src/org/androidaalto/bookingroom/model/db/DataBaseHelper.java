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
    private static String TAG = "DataBaseHelper";

    // The Android's default system path of your application database.
    // private static String DB_PATH =
    // "/data/data/net.jrcandroid.wordtrainer/databases/";
    private static String DB_NAME = "booking.db";

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
                    "is_admin BOOLEAN default false)";

    private final Context myContext;

    /**
     * Constructor Takes and keeps a reference of the passed context in order to
     * access to the application assets and resources.
     * 
     * @param context
     */
    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.myContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DBHELPER", "going to create tables");
        db.execSQL(CREATE_MEETING_SQL);
        db.execSQL(CREATE_USER_SQL);
        
        // Dummy values
        ContentValues value = new ContentValues();
        value.put("Name", "Test");
        value.put("email","test@test.com");
        value.put("is_admin", false);
        db.insert("user",null,value);
        
        value = new ContentValues();
        value.put("user_id", 1);
        value.put("title", "Meeting");
        value.put("start", "1314519236");
        value.put("end", "1314522836");
        db.insert("meeting", null, value);
        
        value = new ContentValues();
        value.put("user_id", 1);
        value.put("title", "Meeting");
        value.put("start", "1314608400");
        value.put("end", "1314615600");
        db.insert("meeting", null, value);
        
        Log.d("DBHELPER", "tables created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}

