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

package org.androidaalto.bookingroom.model;

import android.database.Cursor;

/**
 * @author hannu
 */
public class User {
    private final Integer id;
    private final String name;
    private final String email;
    private final String password;
    private final int salt;
    private final Boolean admin;

    public User(String name, String email) {
        this(null, name, email, null, -1, false);
    }

    public User(Integer id, String name, String email, String password, int salt, Boolean admin) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.salt = salt;
        this.admin = admin;
    }

    /**
     * @param cursor
     */
    public User(Cursor cursor) {
        this.id = cursor.getInt(0);
        this.name = cursor.getString(1);
        this.email = cursor.getString(2);
        this.password = "";
        this.salt = 0;
        this.admin = false;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public int getSalt() {
        return salt;
    }
    
    public Boolean is_admin() {
        return admin;
    }
}
