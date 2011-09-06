/**
   Copyright: 2011 Android Aalto

   This file is part of BookingRoom_jush.

   BookingRoom_jush is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 3 of the License, or
   (at your option) any later version.

   BookingRoom_jush is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with BookingRoom_jush. If not, see <http://www.gnu.org/licenses/>.
 */

package org.androidaalto.bookingroom.logic;

/**
 * @author jush
 */
public interface MeetingEventListener {
    public void onNewMeeting(Long meetingId);

    public void onDeleteMeeting(Long meetingId);

    public void onEditMeeting(Long meetingId);

}
