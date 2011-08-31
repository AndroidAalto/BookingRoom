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

import org.androidaalto.bookingroom.model.Meeting;
import org.androidaalto.bookingroom.model.User;
import org.androidaalto.bookingroom.model.db.MeetingDb;
import org.androidaalto.bookingroom.model.db.UserDb;

import android.text.format.Time;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hannu
 */
public class MeetingManager {
    private static final long MAX_START_TIME_INCREASE_IN_MILLIS = 120960000;
    private static final long MAX_LENGTH_IN_MILLIS = 720000;

    public static MeetingInfo book(MeetingInfo meetingInfo) {
        final long nowMillis = System.currentTimeMillis();
        final Time now = new Time();
        now.set(nowMillis);
        if (meetingInfo.getStart().before(now))
            throw new IllegalArgumentException("Starting time in past");
        if (!meetingInfo.getStart().before(meetingInfo.getEnd()))
            throw new IllegalArgumentException("Starting time not before ending time");
        final Time maximumStartingTime = new Time();
        maximumStartingTime.set(nowMillis + MAX_START_TIME_INCREASE_IN_MILLIS);
        if (meetingInfo.getStart().after(maximumStartingTime))
            throw new IllegalArgumentException("Starting time too far ahead in the future");
        final Time maximumEndingTime = new Time();
        maximumEndingTime.set(meetingInfo.getStart().toMillis(false) + MAX_LENGTH_IN_MILLIS);
        if (meetingInfo.getEnd().after(maximumEndingTime))
            throw new IllegalArgumentException("Too long of a meeting");
        if (!MeetingDb.getMeetings(meetingInfo.getStart(), meetingInfo.getEnd()).isEmpty())
            throw new IllegalArgumentException("Clashing meeting");

        User user = UserDb.get(meetingInfo.getUser().getEmail());
        if (user == null)
            user = UserDb.store(new User(meetingInfo.getUser().getName(), meetingInfo.getUser()
                    .getEmail()));
        final Meeting meeting = MeetingDb.store(new Meeting(
                user.getId(),
                meetingInfo.getTitle(),
                meetingInfo.getStart(),
                meetingInfo.getEnd()));
        return new MeetingInfo(null, meeting.getStart(), meeting.getEnd(), meeting.getTitle());
    }

    public static List<MeetingInfo> getMeetings(Time from, int offsetDays) {
        Time end = new Time();
        end.set(from.toMillis(true) + ((long) offsetDays) * 86400000L);

        List<Meeting> meetings = MeetingDb.getMeetings(from, end);
        List<MeetingInfo> meetingInfos = new ArrayList<MeetingInfo>();
        for (Meeting meeting : meetings) {
            meetingInfos.add(new MeetingInfo(null, meeting.getStart(), meeting.getEnd(), meeting
                    .getTitle()));
        }
        return meetingInfos;
    }
}
