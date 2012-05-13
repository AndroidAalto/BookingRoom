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

import org.androidaalto.bookingroom.logic.MeetingManager;
import org.androidaalto.bookingroom.logic.UserInfo;
import org.androidaalto.bookingroom.logic.UserManager;
import org.androidaalto.bookingroom.model.User;
import org.androidaalto.bookingroom.model.db.DataBaseHelper;
import org.androidaalto.bookingroom.model.db.UserDb;
import org.androidaalto.bookingroom.services.GoogleCalendarService;
import org.androidaalto.bookingroom.view.WeekView;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    public static final String DEFAULT_ADMIN_EMAIL = "bookingroom_admin@aaltovg.com";
    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView title;
    private WeekView currentView;
    private Runnable screensaverLauncher;
    private Dialog dialog = null;

    /***
     * BroadcastReceiver for notifying of Meeting events through the main thread
     */
    final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MeetingManager.NEW_MEETING_ACTION)) {
                Long meetingID = intent.getLongExtra("meeting_id", -1);
                Log.d(TAG, "New meeting: " + meetingID);
                MeetingManager.triggerOnNewMeetingEvent(meetingID);
            }
        }
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBaseHelper.setContext(this.getBaseContext());
        MeetingManager.setAppContext(getApplicationContext());

        setContentView(R.layout.main);
        IntentFilter filter = new IntentFilter(MeetingManager.NEW_MEETING_ACTION);
        registerReceiver(mBroadcastReceiver, filter);
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
        GoogleCalendarService.stop();
        DataBaseHelper.getInstance().close();
        dismissChangePasswordDialog();
    }

    private void dismissChangePasswordDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startDataFetchService();
    }

    private void startDataFetchService() {
        GoogleCalendarService.start(getApplicationContext());
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
            case R.id.changeAdminPassword:
                showChangePasswordDialog();
                break;
        }
        return false;
    }

    private void showChangePasswordDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.changepassword);
        dialog.setTitle("Change administrator password");
        dialog.setCancelable(true);
        dialog.show();

        Button okBt = (Button) dialog.findViewById(R.id.passButtonOk);
        final EditText currentPass = (EditText) dialog.findViewById(R.id.currentpass);
        final EditText newPass = (EditText) dialog.findViewById(R.id.newpass);
        final EditText newPassAgain = (EditText) dialog.findViewById(R.id.newpassagain);

        okBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfo admin = UserManager.getUser(DEFAULT_ADMIN_EMAIL);
                if (admin == null) {
                    Toast error = Toast.makeText(MainActivity.this,
                            "Default admin user not found!", Toast.LENGTH_LONG);
                    error.show();
                    dismissChangePasswordDialog();
                } else {
                    String newPassStr = newPass.getText().toString();
                    if (!newPassStr.equals(newPassAgain.getText().toString())) {
                        Toast error = Toast.makeText(MainActivity.this,
                                "New passwords don't match!", Toast.LENGTH_LONG);
                        error.show();
                    } else {
                        if (currentPass.getText().toString().equals(admin.getPassword())) {
                            UserInfo newAdmin = new UserInfo(admin.getId(), admin.getName(), admin
                                    .getEmail(), newPassStr, admin.getSalt());
                            UserManager.updatePassword(newAdmin);
                            Toast error = Toast.makeText(MainActivity.this,
                                    "Admin password changed!", Toast.LENGTH_LONG);
                            error.show();
                            dismissChangePasswordDialog();
                        } else {
                            Toast error = Toast.makeText(MainActivity.this,
                                    "Invalid current password!", Toast.LENGTH_LONG);
                            error.show();
                        }
                    }
                }
            }
        });

        Button cancelBt = (Button) dialog.findViewById(R.id.passButtonCancel);
        cancelBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissChangePasswordDialog();
            }
        });
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
