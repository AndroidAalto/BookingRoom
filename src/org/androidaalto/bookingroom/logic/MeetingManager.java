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

import android.content.Context;
import android.content.Intent;
import android.text.format.Time;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * @author hannu
 */
public class MeetingManager {
    private static final String TAG = "MeetingManager";
    private static final MeetingInfoValidator validator = new MeetingInfoValidator();

    private static Set<MeetingEventListener> listeners = new HashSet<MeetingEventListener>();
    
    private static Context appContext = null;
    
    public static String NEW_MEETING_ACTION = "org.androidaalto.bookingroom.new_meeting_action";

    public static MeetingInfo book(Time start, Time end, String title, String contactName,
            String contactMail) throws ValidationException {
        return book(new MeetingInfo(new UserInfo(contactName, contactMail), start, end, title, generatePin()));
    }

    public static MeetingInfo bookAsAdmin(Time start, Time end, String title, String contactName,
            String contactMail) throws ValidationException {
        return bookAsAdmin(new MeetingInfo(new UserInfo(contactName, contactMail), start, end, title, generatePin()));
    }

    /**
     * Books the meeting. Synchronously calls <code>onNewMeeting</code> to all
     * the registered listeners.
     * 
     * @param meetingInfo The meeting to be booked.
     * @return The meeting stored.
     * @throws ValidationException When the preconditions fail.
     */
    public static MeetingInfo book(MeetingInfo meetingInfo) throws ValidationException {
//        final ValidationResult result = validator.fullValidate(meetingInfo);
//        if (result.hasErrors())
//            throw new ValidationException(result, "There were validation errors in " + meetingInfo);
        final MeetingInfo booked = doBook(meetingInfo);
        return booked;
    }

    /**
     * Stores the meeting event without doing any validation
     * @return The meeting stored
     */
    public static MeetingInfo storeEvent(Time start, Time end, String title, String contactName,
            String contactMail){
        final MeetingInfo booked = doBook(new MeetingInfo(new UserInfo(contactName, contactMail), start, end, title, generatePin()));
        return booked;
    }

    /**
     * Books the meeting. Synchronously calls <code>onNewMeeting</code> to all
     * the registered listeners.
     * 
     * @param meetingInfo The meeting to be booked.
     * @return The meeting stored.
     * @throws ValidationException When the preconditions fail.
     */
    public static MeetingInfo bookAsAdmin(MeetingInfo meetingInfo) throws ValidationException {
        final ValidationResult result = validator.minimumValidate(meetingInfo);
        if (result.hasErrors())
            throw new ValidationException(result, "There were validation errors in " + meetingInfo);
        final MeetingInfo booked = doBook(meetingInfo);
        return booked;
    }

    private static MeetingInfo doBook(MeetingInfo meetingInfo) {
        Log.d(TAG, "Booking: " + meetingInfo);
        User user = UserDb.get(meetingInfo.getUser().getEmail());
        if (user == null)
            user = UserDb.store(new User(meetingInfo.getUser().getName(), meetingInfo.getUser()
                    .getEmail()));
        final Meeting meeting = MeetingDb.store(new Meeting(
                user.getId(),
                meetingInfo.getTitle(),
                meetingInfo.getStart(),
                meetingInfo.getEnd(), meetingInfo.getPin()));
        final MeetingInfo booked = new MeetingInfo(meeting.getId(), null, meeting.getStart(),
                meeting.getEnd(),
                meeting.getTitle(), meeting.getPin());
        sendNewMeetingIntent(booked.getId());
        Log.d(TAG, "Booked: " + booked);
        return booked;
    }

    /**
     * @param id
     */
    private static void sendNewMeetingIntent(Long id) {
        if (appContext == null) {
            Log.e(TAG, "Application context was not set. Unable to send new intents");
            return;
        }
        Intent newMeetingIntent = new Intent(NEW_MEETING_ACTION);
        newMeetingIntent.putExtra("meeting_id", id.longValue());
        appContext.sendBroadcast(newMeetingIntent);
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
            meetingInfos.add(new MeetingInfo(
                    meeting.getId(),
                    null,
                    meeting.getStart(),
                    meeting.getEnd(),
                    meeting.getTitle(), meeting.getPin()));
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
        return new MeetingInfo(meeting.getId(), new UserInfo(user.getId(),
                user.getName(),
                user.getEmail()), meeting.getStart(),
                meeting.getEnd(), meeting
                        .getTitle(), meeting.getPin());
    }

    /**
     * @param id Meeting id
     */
    public static void delete(long id) {
        // Makes sure that the meeting still exists
        Meeting meeting = MeetingDb.get(id);
        if (meeting != null) {
            // Delete
            int i = MeetingDb.delete(id);
            triggerOnDeleteMeetingEvent(id);
            Log.d(TAG, "Deleted rows: " + i);
        }
    }

    public static void addMeetingEventListener(MeetingEventListener listener) {
        listeners.add(listener);
    }

    public static void deleteMeetingEventListener(MeetingEventListener listener) {
        listeners.remove(listener);
    }

    public static void triggerOnNewMeetingEvent(Long meetingId) {
        for (MeetingEventListener listener : listeners) {
            listener.onNewMeeting(meetingId);
        }
    }

    private static void triggerOnDeleteMeetingEvent(Long meetingId) {
        for (MeetingEventListener listener : listeners) {
            listener.onDeleteMeeting(meetingId);
        }
    }

    private static void triggerOnEditMeetingEvent(Long meetingId) {
        for (MeetingEventListener listener : listeners) {
            listener.onEditMeeting(meetingId);
        }
    }

    /**
     * @param meeting
     */
    public static void update(MeetingInfo meetingInfo) throws ValidationException {
        final ValidationResult result = validator.fullValidate(meetingInfo);
        if (result.hasErrors())
            throw new ValidationException(result, "There were validation errors in " + meetingInfo);

        UserDb.update(meetingInfo.getUser());
        MeetingDb.update(meetingInfo);

        triggerOnEditMeetingEvent(meetingInfo.getId());
    }
    
    /*
     * Generate random pin from 1000 to 9999
     */
    private static int generatePin() {
        Random rand = new Random();
        int r = rand.nextInt(9000) + 1000;
        Log.d(TAG, "My rand " + r);
        return r;
    }

    public static void setAppContext(Context appContext) {
        MeetingManager.appContext = appContext;
    }

}
