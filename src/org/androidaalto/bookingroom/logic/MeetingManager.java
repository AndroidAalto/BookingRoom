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
import org.androidaalto.bookingroom.validation.ValidationException;
import org.androidaalto.bookingroom.validation.ValidationResult;

import android.text.format.Time;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hannu
 */
public class MeetingManager {
    private static final String TAG = "MeetingManager";
    private static final MeetingInfoValidator validator = new MeetingInfoValidator();

    public static MeetingInfo book(Time start, Time end, String title, String contactName,
            String contactMail) throws ValidationException {
        return book(new MeetingInfo(new UserInfo(contactName, contactMail), start, end, title));
    }

    /**
     * Books the meeting.
     * 
     * @param meetingInfo The meeting to be booked.
     * @return The meeting stored.
     * @throws ValidationException When the preconditions fail.
     */
    public static MeetingInfo book(MeetingInfo meetingInfo) throws ValidationException {
        final ValidationResult result = validator.validate(meetingInfo);
        if (result.hasErrors())
            throw new ValidationException(result, "There were validation errors in " + meetingInfo);
        Log.d(TAG, "Booking: " + meetingInfo);
        User user = UserDb.get(meetingInfo.getUser().getEmail());
        if (user == null)
            user = UserDb.store(new User(meetingInfo.getUser().getName(), meetingInfo.getUser()
                    .getEmail()));
        final Meeting meeting = MeetingDb.store(new Meeting(
                user.getId(),
                meetingInfo.getTitle(),
                meetingInfo.getStart(),
                meetingInfo.getEnd()));
        final MeetingInfo booked = new MeetingInfo(meeting.getId(), null, meeting.getStart(),
                meeting.getEnd(),
                meeting.getTitle());
        Log.d(TAG, "Booked: " + booked);
        return booked;
    }

    /**
     * Returns all the meetings from <code>from</code> time to
     * <code>offsetDays</code> forward.
     * 
     * @param from The time from which from the meetings should be returned.
     * @param offsetDays The amount of days forward from the <code>from</code>
     *            parameter the meetings should be returned.
     * @return List of meetings, an empty list if no results found.
     */
    public static List<MeetingInfo> getMeetings(Time from, int offsetDays) {
        Time end = new Time();
        end.set(from.toMillis(true) + ((long) offsetDays) * 86400000L);

        List<Meeting> meetings = MeetingDb.getMeetings(from, end);
        List<MeetingInfo> meetingInfos = new ArrayList<MeetingInfo>();
        for (Meeting meeting : meetings) {
            meetingInfos.add(new MeetingInfo(meeting.getId(), null, meeting.getStart(), meeting
                    .getEnd(),
                    meeting
                            .getTitle()));
        }
        Log.d(TAG, "Returning meetings: " + meetingInfos);
        return meetingInfos;
    }

    /**
     * Returns a meeting with its user data. If no meeting found for id, returns
     * <code>null</code>.
     * 
     * @param id The meeting ID
     * @return The meeting
     */
    public static MeetingInfo getMeeting(long id) {
        Meeting meeting = MeetingDb.get(id);
        if (meeting == null)
            return null;
        User user = UserDb.get(meeting.getUserId());
        return new MeetingInfo(meeting.getId(), new UserInfo(user.getId(), user.getName(),
                user.getEmail()), meeting.getStart(),
                meeting.getEnd(), meeting
                        .getTitle());
    }
}
