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

import org.androidaalto.bookingroom.logic.MeetingInfo;
import org.androidaalto.bookingroom.logic.MeetingManager;
import org.androidaalto.bookingroom.logic.UserInfo;
import org.androidaalto.bookingroom.validation.ObjectError;
import org.androidaalto.bookingroom.validation.ValidationException;
import org.androidaalto.bookingroom.validation.ValidationResult;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
    public static final String EXTRA_START_HOUR = "hour";
    public static final String EXTRA_DAY = "day";
    public static final String EXTRA_PIN = "pincode";

    AlertDialog.Builder alertDialog = null;
    Dialog dialog = null;
    EditText titleEdit, nameEdit, emailEdit, pinText;
    TimePicker startPicker, endPicker;
    TextView meetingHeader;
    Button buttonOk, buttonCancel, buttonDelete;

    private int day;
    private int month;
    private int year;

    private MeetingInfo mMeeting = null;

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
            Long meetingId = extras.getLong(EXTRA_ID);
            if (meetingId != 0) {
                // Get the meeting info with its user info.
                mMeeting  = MeetingManager.getMeeting(meetingId);
                setValuesForEditing(mMeeting);
            } else {
                setValuesForNew(extras.getInt(EXTRA_DAY), extras.getInt(EXTRA_START_HOUR));
            }
        }
        alertDialog = new AlertDialog.Builder(this);
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
                    if (mMeeting != null) {
                        UserInfo updateUser = new UserInfo((long) mMeeting.getUser().getId(), nameEdit.getText().toString(), emailEdit.getText().toString());
                        MeetingInfo updateMI = new MeetingInfo(
                                mMeeting.getId(),
                                updateUser,
                                start,
                                end,
                                titleEdit.getText().toString(),
                                mMeeting.getPin());
                        MeetingManager.update(updateMI);
                        Toast toast = Toast.makeText(getApplicationContext(), "Meeting updated", Toast.LENGTH_SHORT);
                        toast.show();
                        finish();
                    } else {
                        MeetingInfo myMI = MeetingManager.book(start, end, titleEdit.getText().toString(), nameEdit
                                .getText().toString(), emailEdit.getText().toString());

                        alertDialog.setTitle("Booking PIN code: " + myMI.getPin());
                        alertDialog.setMessage("Please don't forget the PIN code if you want to cancel this meeting.");
                        alertDialog.setNeutralButton("OK", new DialogInterface.OnClickListener() { 
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // If we reach this point then booking went ok
                                meetingActivity.finish();
                            }
                        });
                        alertDialog.show();
                    }

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
                popup();
              
            }
        });
        
    }

    private void popup() {
        dialog = new Dialog(MeetingActivity.this);
        dialog.setContentView(R.layout.editpin);
        dialog.setTitle("Introduce your pin code");
        dialog.setCancelable(true);
        dialog.show();

        Button pinButtonOk = (Button) dialog.findViewById(R.id.pinButtonOk);
        pinText = (EditText) dialog.findViewById(R.id.pincode);
        
        pinButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean check = checkPin(pinText.getText().toString());
                if ( check ) {
                    Bundle extras = getIntent().getExtras();
                    Long i = extras.getLong(EXTRA_ID);
                    if (i != null) {
                        Log.i(TAG, "id is " + i);
                        MeetingManager.delete(i);
                    }
                    Toast toast = Toast.makeText(getApplicationContext(), "Meeting deleted", Toast.LENGTH_SHORT);
                    toast.show();
                    finish();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Wrong pin", Toast.LENGTH_SHORT);
                    toast.show();
                }
                dialog.dismiss();
            }
        });
        
        Button pinButtonCancel = (Button) dialog.findViewById(R.id.pinButtonCancel);
        pinButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * @param int1
     * @param int2
     */
    private void setValuesForNew(int day, int startHour) {
        Time start = new Time();
        start.setJulianDay(day);
        start.hour = startHour;
        start.minute = 0;

        Time end = new Time();
        end.setJulianDay(day);
        end.hour = startHour + 1;
        if (end.hour > 23)
            end.hour = 0;
        end.minute = 0;

        setTimeValues(start, end);
    }

    private void setTimeValues(Time start, Time end) {
        meetingHeader.setText(meetingHeader.getText() + " - " + start.format("%d/%m/%Y"));

        startPicker.setCurrentHour(start.hour);
        startPicker.setCurrentMinute(start.minute);

        endPicker.setCurrentHour(end.hour);
        endPicker.setCurrentMinute(end.minute);

        day = start.monthDay;
        month = start.month;
        year = start.year;
    }

    /**
     * @param meetingId
     */
    private void setValuesForEditing(MeetingInfo meeting ) {
        // Existing meeting needs to have a title so we use that check to
        // display the delete button
        buttonDelete.setVisibility(View.VISIBLE);
        buttonOk.setText("Edit");
        setTimeValues(meeting.getStart(), meeting.getEnd());
        titleEdit.setText(meeting.getTitle());
        nameEdit.setText(meeting.getUser().getName());
        emailEdit.setText(meeting.getUser().getEmail());
    }
    
    private boolean checkPin(String userPin) {
        Bundle extras = getIntent().getExtras();
        Integer meetingId = extras.getInt(EXTRA_PIN);
        Log.d(TAG, "User pin code: " + userPin.toString() + " Pin code: " + meetingId.toString());
        
        return ( userPin.equals(meetingId.toString() ) ) ? true : false;
    }
}
