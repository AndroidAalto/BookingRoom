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

/**
 * @author hannu
 */
public class UserInfo {
    private final Long id;
    private final String name;
    private final String email;
    private final String password;
    private final Integer salt;

    public UserInfo(Long id, String name, String email) {
        this(id, name, email, null, null);
    }

    public UserInfo(String name, String email) {
        this(null, name, email, null, null);
    }

    public UserInfo(Long id, String name, String email, String password, Integer salt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.salt = salt;
    }

    public Long getId() {
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

    public Integer getSalt() {
        return salt;
    }
}
