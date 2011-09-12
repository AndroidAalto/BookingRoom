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

package org.androidaalto.bookingroom;

import org.androidaalto.bookingroom.model.Meeting;
import org.androidaalto.bookingroom.model.User;
import org.androidaalto.bookingroom.model.db.DataBaseHelper;
import org.androidaalto.bookingroom.model.db.MeetingDb;
import org.androidaalto.bookingroom.model.db.UserDb;
import org.androidaalto.bookingroom.view.WeekView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView title;
    private WeekView currentView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBaseHelper.setContext(this.getBaseContext());
        setContentView(R.layout.main);

        title = (TextView) findViewById(R.id.title);
        currentView = (WeekView) findViewById(R.id.weekView);
        currentView.setTitleTextView(title);
    }

    @Override
    public void onPause() {
        super.onPause();
        DataBaseHelper.getInstance().close();
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "" + UserDb.getUserCount());
        Log.d(TAG, "" + MeetingDb.getMeetingCount());
        User myUser = UserDb.get("test@test.com");
        if (myUser != null) {
            Log.d(TAG, "" + myUser.getName());
        }

        Time start = new Time();
        start.set(04, 9 - 1, 2011); // Months are 0-11
        Time end = new Time();
        end.set(11, 9 - 1, 2011); // Months are 0-11

        List<Meeting> meetings = MeetingDb.getMeetings(start, end);

        if (meetings.size() > 0) {
            for (Meeting m : meetings) {
                Log.d(TAG, "Id: " + m.getId() + " Title: " + m.getTitle());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.week_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.goToday:

                Time now = new Time();
                now.setToNow();
                now.normalize(true);
                currentView.setSelectedDay(now);
                break;
            case R.id.goScreensaver:
                startActivity(new Intent(this, Screensaver.class));
                break;
        }
        return false;
    }
}
