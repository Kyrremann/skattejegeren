/* Skattejegeren -- WinScreen.
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

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WinScreen extends Activity {

    private ImageAdapter imageAdapter;
    private String PLACE;
    private LinearLayout ll;
    private ArrayList<Treasure> curTrail;
    private Json jSon;
    private TextView header, info;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.skatteinfo);

        PLACE = getIntent().getExtras().getString("PLACE");
        jSon = new Json(getAssets());
        String jsonString = jSon.getThemJSONs("courses.json");
        curTrail = jSon.jsonToTrail(jsonString, PLACE);

        header = (TextView) findViewById(R.id.infoTitle);
        header.setText(R.string.win_header);

        info = (TextView) findViewById(R.id.infoText);
        info.setText(R.string.winner);

        ll = (LinearLayout) findViewById(R.id.infoMamma);
        imageAdapter = new ImageAdapter(this, curTrail.size(), curTrail.size());
        GridView gridview = new GridView(this);
        gridview.setAdapter(imageAdapter);
        gridview.setNumColumns(4);
        gridview.setVerticalSpacing(2);
        gridview.setHorizontalSpacing(2);
        gridview.setGravity(Gravity.CENTER);
        gridview.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                                                  LayoutParams.WRAP_CONTENT));

        gridview.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView parent, View v, int pos, long id) {
                    Intent intent = new Intent(v.getContext(), Skatteinfo.class);
                    intent.putExtra("TITLE", curTrail.get(pos).getTitle());
                    intent.putExtra("TEXT", curTrail.get(pos).getDesc());
                    intent.putExtra("PICS", curTrail.get(pos).getPics());
                    startActivity(intent);
                }
            });

        ll.addView(gridview, 1);
    }
}