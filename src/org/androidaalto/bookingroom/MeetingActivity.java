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
import org.androidaalto.bookingroom.validation.ObjectError;
import org.androidaalto.bookingroom.validation.ValidationException;
import org.androidaalto.bookingroom.validation.ValidationResult;
import org.androidaalto.bookingroom.view.WeekView;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.List;

public class MeetingActivity extends Activity {
    
    private static final String TAG = MeetingActivity.class.getSimpleName();

    public static final String EXTRA_ID = "id";
    public static final String EXTRA_END_TIME = "end";
    public static final String EXTRA_CONTACT_EMAIL = "email";
    public static final String EXTRA_CONTACT_NAME = "name";
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_START_HOUR = "hour";
    public static final String EXTRA_DAY = "day";
    public static final String EXTRA_START_TIME = "start";

    EditText titleEdit, nameEdit, emailEdit;
    TimePicker startPicker, endPicker;
    TextView meetingHeader;
    Button buttonOk, buttonCancel, buttonDelete;

    private int day;
    private int month;
    private int year;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meeting);

        meetingHeader = (TextView) findViewById(R.id.meetingHeader);
        startPicker = (TimePicker) findViewById(R.id.startPicker);
        endPicker = (TimePicker) findViewById(R.id.endPicker);
        titleEdit = (EditText) findViewById(R.id.titleEdit);
        nameEdit = (EditText) findViewById(R.id.nameEdit);
        emailEdit = (EditText) findViewById(R.id.emailEdit);
        buttonOk = (Button) findViewById(R.id.buttonOK);
        buttonCancel = (Button) findViewById(R.id.buttonCancel);
        buttonDelete = (Button) findViewById(R.id.buttonDelete);

        startPicker.setIs24HourView(true);
        endPicker.setIs24HourView(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // Existing meeting needs to have a title so we use that check to display the delete button
            if ( extras.getString(EXTRA_TITLE) != null ) {
                buttonDelete.setVisibility(View.VISIBLE);
            }
            
            Time start = new Time();
            // Use full date if we've it. Otherwise, use day and hour
            String startStr = extras.getString(EXTRA_START_TIME);
            if (startStr != null) {
                start.parse(startStr);
            } else {
                start.setJulianDay(extras.getInt(EXTRA_DAY));
                start.hour = extras.getInt(EXTRA_START_HOUR);
                start.minute = 0;
            }
            day = start.monthDay;
            month = start.month;
            year = start.year;

            Time end = new Time();
            // Use full date if we've it. Otherwise set the end to one hour
            // after start
            String endStr = extras.getString(EXTRA_END_TIME);
            if (endStr != null) {
                end.parse(endStr);
            } else {
                end.setJulianDay(extras.getInt(EXTRA_DAY));
                end.hour = extras.getInt(EXTRA_START_HOUR) + 1;
                if (end.hour > 23)
                    end.hour = 0;
                end.minute = 0;
            }

            meetingHeader.setText(meetingHeader.getText() + " - " + start.format("%d/%m/%Y"));

            startPicker.setCurrentHour(start.hour);
            startPicker.setCurrentMinute(start.minute);

            endPicker.setCurrentHour(end.hour);
            endPicker.setCurrentMinute(end.minute);

            titleEdit.setText(extras.getString(EXTRA_TITLE));
            nameEdit.setText(extras.getString(EXTRA_CONTACT_NAME));
            emailEdit.setText(extras.getString(EXTRA_CONTACT_EMAIL));
        }

        final Activity meetingActivity = this;
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Time start = new Time();
                start.set(0, startPicker.getCurrentMinute(), startPicker.getCurrentHour(), day,
                        month, year);
                start.normalize(true);

                Time end = new Time();
                end.set(0, endPicker.getCurrentMinute(), endPicker.getCurrentHour(), day, month,
                        year);
                end.normalize(true);

                // Add one day to the end date if is earlier than start
                if (end.before(start)) {
                    end.monthDay++;
                    end.normalize(true);
                }

                try {
                    MeetingManager.book(start, end, titleEdit.getText().toString(), nameEdit
                            .getText().toString(), emailEdit.getText().toString());
                    // If we reach this point then booking went ok
                    meetingActivity.finish();
                } catch (ValidationException e) {
                    // Initially set a generic error message
                    String errorMessage = "Please check all the fields!";
                    ValidationResult result = e.getErrors();
                    List<ObjectError> errors = result.getErrors();
                    if (!errors.isEmpty())
                        errorMessage = errors.get(0).getMessage();
                    Toast.makeText(meetingActivity, errorMessage, Toast.LENGTH_LONG).show();
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meetingActivity.finish();
            }
        });
        
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extras = getIntent().getExtras();
                Long i = extras.getLong(EXTRA_ID);
                if ( i != null ) {
                    Log.i(TAG, "id is " + i);
                    MeetingManager.delete(i);
                    meetingActivity.finish();
                }
            }
        });
    }
}
