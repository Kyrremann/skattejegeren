/* Skattejegeren -- Skatteinfo.
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

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

public class Skatteinfo extends Activity {

    private LinearLayout ll;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.skatteinfo);

        ll = (LinearLayout) findViewById(R.id.linearLayoutInfo);
        Bundle b = getIntent().getExtras();

        TextView tv = (TextView) findViewById(R.id.infoTitle);
        tv.setText(b.getString("TITLE"));

        tv = (TextView) findViewById(R.id.infoText);
        tv.setText(b.getString("TEXT"));

        String[] pics = b.getStringArray("PICS");
        for (String s : pics) {

            if (s.substring(0, 5).equals("video")) {
                final VideoView vw = new VideoView(this);
                vw.setPadding(10, 5, 10, 5);
                Log.d("info", "video");
                vw.setVideoURI(Uri.parse("android.resource://"
                                         + getPackageName() + "/raw/" + s));
                MediaController mc = new MediaController(this);
                vw.setMediaController(mc);
                vw.start();
                ll.addView(vw);
            } else {
                final ImageView imageView;
                imageView = new ImageView(this);
                imageView.setScaleType(ImageView.ScaleType.FIT_START);
                imageView.setAdjustViewBounds(true);
                imageView.setPadding(10, 5, 10, 5);
                imageView.setImageDrawable(getResources().getDrawable(
                                                                      getResources().getIdentifier("drawable/" + s,
                                                                                                   "drawable", getPackageName())));
                ll.addView(imageView);
            }
        }
        // this *is really* needed!
        ll.addView(new TextView(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}