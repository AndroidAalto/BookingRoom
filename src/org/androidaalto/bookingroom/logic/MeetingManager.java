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

    
    public static MeetingInfo book(MeetingInfo meetingInfo) {
        // TODO
        //if (meetingInfo.getStart().before(new Date()))
        //    throw new IllegalArgumentException("Invalid starting time");

        if (!MeetingDb.getMeetings(meetingInfo.getStart(), meetingInfo.getEnd()).isEmpty())
            throw new IllegalArgumentException("Clashing meeting");
        final User user = UserDb.get(meetingInfo.getUser().getEmail());
        final User newUser = UserDb.store(new User(
                meetingInfo.getUser().getName(), meetingInfo.getUser().getEmail()));
        final Meeting meeting = MeetingDb.store(new Meeting(
                user != null ? user.getId() : newUser.getId(),
                meetingInfo.getTitle(),
                meetingInfo.getStart(),
                meetingInfo.getEnd()));

        // TODO store meeting
        return new MeetingInfo(null, meeting.getStart(), meeting.getEnd(), meeting.getTitle());
    }


    public static  List<MeetingInfo> getMeetings(Time from, int offsetDays) {
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
