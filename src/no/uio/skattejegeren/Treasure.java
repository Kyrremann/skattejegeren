/* Skattejegeren -- Treasure.
 * Copyright (C) 2011 Skattejegeren development team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package no.uio.skattejegeren;

import java.util.LinkedList;

import android.location.Location;

class Treasure {

    private String title;
    private Location loc;
    private String desc;
    private LinkedList<String> pics;

    public Treasure(String title, double lat, double lon, String desc) {
        this.title = title;
        loc = new Location("gps");
        pics = new LinkedList<String>();
        loc.setLatitude(lat);
        loc.setLongitude(lon);
        this.desc = desc;
    }

    public Location getLoc() {
        return loc;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String[] getPics() {
        String[] p = new String[pics.size()];
        int i = 0;
        for (String s : pics)
            p[i++] = s;
        return p;
    }

    public void addPic(String pic) {
        pics.add(pic);
    }
}
