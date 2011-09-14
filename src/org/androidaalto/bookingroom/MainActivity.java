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

import org.androidaalto.bookingroom.model.User;
import org.androidaalto.bookingroom.model.db.DataBaseHelper;
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

public class MainActivity extends Activity {
    public static final String DEFAULT_ADMIN_EMAIL = "bookingroom_admin@aaltovg.com";
    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView title;
    private WeekView currentView;
    private Runnable screensaverLauncher;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBaseHelper.setContext(this.getBaseContext());
        setContentView(R.layout.main);

        title = (TextView) findViewById(R.id.title);
        currentView = (WeekView) findViewById(R.id.weekView);
        currentView.setTitleTextView(title);
        screensaverLauncher = new ScreensaverLauncher(this);

        if (UserDb.get(DEFAULT_ADMIN_EMAIL) == null) {
            User admin = new User(null, "bookingroom_admin", DEFAULT_ADMIN_EMAIL, "012345", -1,
                    true);
            UserDb.store(admin);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        DataBaseHelper.getInstance().close();
    }

    @Override
    public void onResume() {
        super.onResume();
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
            case R.id.aboutPopup:
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }
        return false;
    }

    public Runnable getScreensaverLauncher() {
        return screensaverLauncher;
    }

    private static final class ScreensaverLauncher implements Runnable {
        private final MainActivity mainActivity;

        public ScreensaverLauncher(MainActivity mainActivity) {
            this.mainActivity = mainActivity;
        }

        @Override
        public void run() {
            Log.d(TAG, "Starting screen saver!");
            mainActivity.startActivity(new Intent(mainActivity, Screensaver.class));
        }
    }
}
