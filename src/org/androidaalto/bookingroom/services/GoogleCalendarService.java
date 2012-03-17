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
package org.androidaalto.bookingroom.services;

import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonParser;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import org.androidaalto.bookingroom.R;
import org.androidaalto.bookingroom.logic.MeetingManager;
import org.androidaalto.bookingroom.validation.ValidationException;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Time;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class GoogleCalendarService extends Service {
    private static final String REFRESH_TOKEN_URL = "https://accounts.google.com/o/oauth2/token";
    private static String LOG_TAG = GoogleCalendarService.class.getCanonicalName();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.d(LOG_TAG, "GoogleCalendarService.onStart()");
        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    setUp();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Unable to fetch google calendar data", e);
                }
            }
        };
        new Thread(r).start();
        super.onStart(intent, startId);
    }

    public void setUp() throws IOException {
        HttpTransport httpTransport = new NetHttpTransport();
        JacksonFactory jsonFactory = new JacksonFactory();

        // The clientId and clientSecret are copied from the API Access tab on
        // the Google APIs Console
        String clientId = getString(R.string.client_id);
        String clientSecret = getString(R.string.client_secret);
        String refreshToken = getString(R.string.refresh_token);

        String accessToken = refreshToken(httpTransport, clientId, clientSecret, refreshToken);

        GoogleAccessProtectedResource accessProtectedResource = new GoogleAccessProtectedResource(
                accessToken, httpTransport, jsonFactory, clientId, clientSecret,
                refreshToken);

        Calendar service = Calendar.builder(httpTransport, jsonFactory)
                .setApplicationName("BOOKING_ROOM")
                .setHttpRequestInitializer(accessProtectedResource)
                .build();
        
        String boardRoomId = getString(R.string.board_room_calendar_id);
        Events events = service.events().list(boardRoomId).execute();

        while (true) {
          for (Event event : events.getItems()) {
            Log.d(LOG_TAG, event.toPrettyString());
            Time start = new Time();
            start.parse3339(event.getStart().getDateTime().toStringRfc3339());
            Time end = new Time();
            end.parse3339(event.getEnd().getDateTime().toStringRfc3339());
            start.normalize(true);
            try {
                MeetingManager.book(start , end, event.getSummary(), "fake", "fake@fake.com");
            } catch (ValidationException e) {
                Log.e(LOG_TAG, "Unable to add calendar event " + event.getSummary(), e);
            }
          }
          String pageToken = events.getNextPageToken();
          if (pageToken != null && pageToken.length() != 0) {
            events = service.events().list(boardRoomId).setPageToken(pageToken).execute();
          } else {
            break;
          }
        }
    }

    /**
     * @param httpTransport
     * @return
     * @throws IOException
     */
    private String refreshToken(HttpTransport httpTransport, String clientId, String clientSecret,
            String refreshToken) throws IOException {
        HttpRequestFactory reqFactory = httpTransport.createRequestFactory();
        StringBuilder sb = new StringBuilder();
        sb.append("client_id=");
        sb.append(clientId);
        sb.append("&client_secret=");
        sb.append(clientSecret);
        sb.append("&refresh_token=");
        sb.append(refreshToken);
        sb.append("&grant_type=refresh_token");
        final byte[] refreshContent = sb.toString().getBytes();
        HttpContent content = // new UrlEncodedContent(refreshContent);
        new HttpContent() {

            @Override
            public void writeTo(OutputStream out) throws IOException {
                out.write(refreshContent);
            }

            @Override
            public boolean retrySupported() {
                return true;
            }

            @Override
            public String getType() {
                return "application/x-www-form-urlencoded";
            }

            @Override
            public long getLength() throws IOException {
                return refreshContent.length;
            }

            @Override
            public String getEncoding() {
                // TODO Auto-generated method stub
                return null;
            }
        };
        try {
            HttpRequest req = reqFactory.buildPostRequest(new GenericUrl(REFRESH_TOKEN_URL),
                    content);
            HttpResponse resp = req.execute();
            InputStream respContentIS = resp.getContent();
            JacksonFactory fac = new JacksonFactory();
            JsonParser jparser = fac.createJsonParser(respContentIS);
            GenericJson jsonContent = jparser.parse(GenericJson.class, null);
            return (String) jsonContent.get("access_token");
        } catch (IOException e) {
            throw e;
        }
    }

}
