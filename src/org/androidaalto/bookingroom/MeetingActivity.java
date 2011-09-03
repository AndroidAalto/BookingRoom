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

import android.app.Activity;
import android.os.Bundle;
import android.text.format.Time;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

public class MeetingActivity extends Activity {

    EditText titleEdit, nameEdit, emailEdit;
    TimePicker startPicker, endPicker;
    TextView meetingHeader;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meeting);
        
        meetingHeader   = (TextView)    findViewById(R.id.meetingHeader);
        startPicker     = (TimePicker)  findViewById(R.id.startPicker);
        endPicker       = (TimePicker)  findViewById(R.id.endPicker);
        titleEdit       = (EditText)    findViewById(R.id.titleEdit);
        nameEdit        = (EditText)    findViewById(R.id.nameEdit);
        emailEdit       = (EditText)    findViewById(R.id.emailEdit);
        
        startPicker.setIs24HourView(true);
        endPicker.setIs24HourView(true);

        Bundle extras = getIntent().getExtras();
        
        if ( extras != null ) {
            Integer myDay = extras.getInt("day");
            Integer myHour = extras.getInt("hour");

            Time myTime = new Time();
            myTime.setJulianDay(myDay);
            meetingHeader.setText(meetingHeader.getText() + " - " + myTime.monthDay + "/" + (myTime.month + 1) + "/" + myTime.year);

            startPicker.setCurrentHour(myHour.intValue());
            startPicker.setCurrentMinute(00);
            
            // One hour meeting by default
            endPicker.setCurrentHour(startPicker.getCurrentHour() + 1);
            endPicker.setCurrentMinute(00);
            
            if ( extras.getString("title") != null ) {
                titleEdit.setText(extras.getString("title"));
            }
            
            if ( extras.getString("name") != null ) {
                nameEdit.setText(extras.getString("name"));
            }
            
            if ( extras.getString("email") != null ) {
                emailEdit.setText(extras.getString("email"));
            }

        }
    }
}
