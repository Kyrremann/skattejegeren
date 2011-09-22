/* Skattejegeren -- Main.
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
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class Main extends Activity {

    private TextView someGame, someSite;
    private final String PLACE = "PLACE";
    private Typeface font;
    private Database db;
    private Cursor c;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        db = new Database(getApplicationContext());
        db.open();

        c = db.getTable("LOREM");
        c.moveToFirst();
        Log.d("NULL?", "" + c.getColumnCount());
        c.close();
        db.close();

        someGame = (TextView) findViewById(R.id.someGame);
        someSite = (TextView) findViewById(R.id.someSite);
        try {
            font = Typeface.createFromAsset(getAssets(),
                                            "alternategothicno1_webfont.ttf");
        } catch (Exception e) {
            Log.d("Exception", e.toString());
            font = Typeface.DEFAULT;
        }

        someGame.setTypeface(font);
        someGame.setTextSize(35);
        someGame.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(),
                                               Skattejegeren.class);
                    intent.putExtra(PLACE, "LOREM");
                    startActivity(intent);
                }
            });

        someSite.setTypeface(font);
        someSite.setTextSize(35);
        someSite.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                                               Uri.parse("https://duckduckgo.com/"));
                    startActivity(intent);
                }
            });
    }
}