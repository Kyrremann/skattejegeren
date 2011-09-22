/* Skattejegeren -- Json.
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.AssetManager;
import android.util.Log;

public class Json {

    private AssetManager asset;

    public Json(AssetManager asset) {
        this.asset = asset;
    }

    public String getThemJSONs(final String jsonFileName) {
        String s = null;
        AssetManager assetManager = asset;

        try {
            InputStream is = assetManager.open(jsonFileName);
            s = getWords(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return s;
    }

    public ArrayList<Treasure> jsonToTrail(String jsonString, String place) {

        ArrayList<Treasure> trail = new ArrayList<Treasure>();

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray course = jsonObject.getJSONArray(place);

            for (int i = 0; i < course.length(); ++i) {
                JSONObject o = course.getJSONObject(i);
                String title = o.getString("title");
                double lat = o.getDouble("lat");
                double lon = o.getDouble("long");
                String desc = o.getString("description");
                Treasure t = new Treasure(title, lat, lon, desc);

                JSONArray pics = o.getJSONArray("pics");

                for (int j = 0; j < pics.length(); ++j) {
                    String s = pics.getString(j);
                    t.addPic(s);
                }

                trail.add(t);
            }
        } catch (JSONException jsone) {
            Log.d("json", jsone.getMessage());
        }

        return trail;
    }

    public String getWords(InputStream aFile) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(
                                                                            aFile));
            try {
                String line = null;
                while ((line = input.readLine()) != null) {
                    sb.append(line);
                }
            } finally {
                input.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return sb.toString();
    }
}
