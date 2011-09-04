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

package org.androidaalto.bookingroom.logic;

import org.androidaalto.bookingroom.model.db.MeetingDb;
import org.androidaalto.bookingroom.validation.FieldError;
import org.androidaalto.bookingroom.validation.ObjectError;
import org.androidaalto.bookingroom.validation.ValidationResult;
import org.androidaalto.bookingroom.validation.Validator;

import android.text.format.Time;

/**
 * @author hannu
 */
public class MeetingInfoValidator implements Validator<MeetingInfo> {
    private static final long MAX_START_TIME_INCREASE_IN_MILLIS = 120960000;
    private static final int MAX_HOURS = 2;
    private static final long MAX_LENGTH_IN_MILLIS = MAX_HOURS * 60 * 60 * 1000;

    @Override
    public ValidationResult validate(MeetingInfo meetingInfo) {
        final ValidationResult errors = new ValidationResult();
        final long nowMillis = System.currentTimeMillis();
        final Time now = new Time();
        now.set(nowMillis);
        if (meetingInfo.getStart().before(now))
            errors.addError(new FieldError(meetingInfo, "start", "beforeNow",
                    "Starting time in past"));
        if (!meetingInfo.getStart().before(meetingInfo.getEnd()))
            errors.addError(new FieldError(meetingInfo, "end", "beforeStart",
                    "Ending time before starting time"));
        final Time maximumStartingTime = new Time();
        maximumStartingTime.set(nowMillis + MAX_START_TIME_INCREASE_IN_MILLIS);
        if (meetingInfo.getStart().after(maximumStartingTime))
            errors.addError(new FieldError(meetingInfo, "start", "afterMax",
                    "Starting time too far ahead in the future"));
        final Time maximumEndingTime = new Time();
        maximumEndingTime.set(meetingInfo.getStart().toMillis(true) + MAX_LENGTH_IN_MILLIS);
        if (meetingInfo.getEnd().after(maximumEndingTime))
            errors.addError(new FieldError(meetingInfo, "end", "tooLong", "Meeting can't be longer than " + MAX_HOURS  + " hours."));
        if (!MeetingDb.getMeetings(meetingInfo.getStart(), meetingInfo.getEnd()).isEmpty())
            errors.addError(new ObjectError(meetingInfo, "clashing", "Clashing meeting"));
        return errors;
    }
}
