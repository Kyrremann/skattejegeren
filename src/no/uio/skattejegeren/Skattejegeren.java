/* Skattejegeren -- Skattejegeren.
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
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;

public class Skattejegeren extends Activity {

    private final float DIST_ACCEPT = 10;
    private final float COMPASS_PUSH = 40;
    private String PLACE;
    private Json jSon;

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private MotherView mView;
    private float dir = 0.0f;
    private float[] compassValues = { 0 };
    private MediaPlayer player;
    private long dist = 100;
    private LocationManager locationManager;
    private LocationListener locationlistener;
    private Location loc;
    private Location lastKnownLocation;
    private String locationProvider;
    // private Dialog dialog;
    private int treasuresFound = 0;
    private TextView distance;

    private ArrayList<Treasure> curTrail;

    private Bitmap compassCircle;
    private Bitmap compassHand;

    private Database db;
    private ImageAdapter imageAdapter;

    Runnable onEverySecond = null;

    @Override
    protected void onCreate(Bundle icicle) {

        super.onCreate(icicle);

        PLACE = getIntent().getExtras().getString("PLACE");

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);

        db = new Database(this);
        Cursor cursor = db.open().getTable(PLACE);
        cursor.moveToFirst();
        treasuresFound = cursor.getInt(0);
        cursor.close();
        db.close();

        Resources res = this.getResources();
        compassCircle = BitmapFactory.decodeResource(res, R.drawable.compass);
        compassHand = BitmapFactory.decodeResource(res, R.drawable.hand);

        jSon = new Json(getAssets());
        String jsonString = jSon.getThemJSONs("courses.json");
        curTrail = jSon.jsonToTrail(jsonString, PLACE);

        player = MediaPlayer.create(this, R.raw.ding);
        player.setLooping(false);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        mView = new MotherView(this);
        setContentView(R.layout.skattejegeren);

        FrameLayout fl = (FrameLayout) findViewById(R.id.mamma);
        fl.addView(mView, 0);

        distance = (TextView) findViewById(R.id.distance);

        imageAdapter = new ImageAdapter(this, curTrail.size(), treasuresFound);
        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(imageAdapter);

        gridview.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView parent, View v, int pos, long id) {
                    if (pos < treasuresFound) {
                        Intent intent = new Intent(v.getContext(), Skatteinfo.class);
                        intent.putExtra("TITLE", curTrail.get(pos).getTitle());
                        intent.putExtra("TEXT", curTrail.get(pos).getDesc());
                        intent.putExtra("PICS", curTrail.get(pos).getPics());
                        startActivity(intent);
                    }
                }
            });

        locationManager = (LocationManager) this
            .getSystemService(Context.LOCATION_SERVICE);
        locationlistener = new MyLocationListener();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                                               0, locationlistener);
        locationManager.requestLocationUpdates(
                                               LocationManager.NETWORK_PROVIDER, 0, 0, locationlistener);
        // locationManager.requestLocationUpdates(
        // LocationManager.PASSIVE_PROVIDER, 0, 0, locationlistener);
        // locationProvider = LocationManager.NETWORK_PROVIDER;
        // lastKnownLocation = locationManager
        // .getLastKnownLocation(locationProvider);

        // LocationManager lm = (LocationManager) getApplication()
        // .getSystemService(Context.LOCATION_SERVICE);
        if (locationManager
            .isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("GPS");
            builder.setMessage(R.string.gpsMessage);
            builder.setPositiveButton("Skru på",
                                      new DialogInterface.OnClickListener() {

                                          @Override
                                          public void onClick(DialogInterface dialog, int which) {
                                              Intent myIntent = new Intent(
                                                                           Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                              startActivity(myIntent);
                                              dialog.cancel();
                                          }
                                      });

            builder.setNegativeButton("Nei takk",
                                      new DialogInterface.OnClickListener() {

                                          @Override
                                          public void onClick(DialogInterface dialog, int which) {
                                              dialog.cancel();
                                              finish();
                                          }
                                      });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        /*
         * Runnable showWaitDialog = new Runnable() {
         *
         * @Override public void run() {
         *
         * loc = new Location("hei"); loc.setLatitude(59.943065);
         * loc.setLongitude(10.718536);
         *
         * while (loc == null) { } // Wait for that GPS... dialog.dismiss(); }
         * };
         *
         * dialog = ProgressDialog.show(Skattejegeren.this, "Vennligst vent...",
         * "Venter på GPS-data...\nDette kan ta litt tid.", true);
         *
         * Thread t = new Thread(showWaitDialog); t.start();
         */
    }

    private final SensorEventListener mListener = new SensorEventListener() {
            public void onSensorChanged(SensorEvent event) {
                compassValues = event.values;
                if (mView != null)
                    mView.invalidate();
            }

            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };

    public void playFoundSound() {
        player.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mListener, mSensor,
                                        SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onStop() {
        mSensorManager.unregisterListener(mListener);
        super.onStop();
    }

    private class MotherView extends View {

        private Paint mPaint;
        private Path arrowPath;

        public MotherView(Context context) {
            super(context);

            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setStyle(Paint.Style.FILL);
            arrowPath = new Path();

            // Construct a wedge-shaped path
            arrowPath.moveTo(0, -100);
            arrowPath.lineTo(-70, 100);
            arrowPath.lineTo(0, 80);
            arrowPath.lineTo(70, 100);
            arrowPath.close();
        }

        @Override
        protected void onDraw(Canvas canvas) {

            int w = canvas.getWidth();
            int h = canvas.getHeight();
            int cx = w / 2;
            int cy = h / 2;

            if (loc == null) {
                distance.setText(R.string.gpsWait);
            } else {
                distance.setText(String.format("%d m",
                                               Math.max((int) dist - 10, 0)));

                // Draw that compass
                canvas.translate(cx, cy + COMPASS_PUSH);
                canvas.translate(-compassCircle.getWidth() / 2,
                                 -compassCircle.getHeight() / 2);
                canvas.drawBitmap(compassCircle, 0, 0, mPaint);

                // Back to center!
                canvas.translate(compassCircle.getWidth() / 2,
                                 compassCircle.getHeight() / 2);
                canvas.rotate(dir - compassValues[0]);
                canvas.translate(-compassHand.getWidth() / 2,
                                 -compassHand.getHeight() / 2);
                canvas.translate(0, -65);
                canvas.drawBitmap(compassHand, 0, 0, mPaint);
            }
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
        }
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            Log.d("GPS", "Working on it...");
            if (location != null && treasuresFound < curTrail.size()) {
                loc = location;
                Location target = curTrail.get(treasuresFound).getLoc();
                Log.d("GPS", "Looking for "
                      + curTrail.get(treasuresFound).getTitle());
                dist = (long) loc.distanceTo(target);
                dir = loc.bearingTo(target);
                mView.invalidate();
                if (dist < DIST_ACCEPT) {
                    playFoundSound();
                    if (lastTreasure()) {
                        startWinScreen();
                        startTreasure();
                        db.open();
                        db.setTable(PLACE, treasuresFound);
                        db.close();
                        finish();
                    } else {
                        imageAdapter.setFound(treasuresFound);
                        imageAdapter.notifyDataSetChanged();
                        startTreasure();
                    }
                    ++treasuresFound;
                    db.open();
                    db.setTable(PLACE, treasuresFound);
                    db.close();
                }
            }
        }

        private boolean lastTreasure() {
            if ((treasuresFound + 1) == curTrail.size())
                return true;
            return false;
        }

        private void startWinScreen() {
            Intent intent = new Intent(getApplicationContext(), WinScreen.class);
            intent.putExtra("PLACE", PLACE);
            startActivity(intent);
        }

        private void startTreasure() {
            Intent intent = new Intent(getApplicationContext(),
                                       Skatteinfo.class);
            intent.putExtra("TITLE", curTrail.get(treasuresFound).getTitle());
            intent.putExtra("TEXT", curTrail.get(treasuresFound).getDesc());
            intent.putExtra("PICS", curTrail.get(treasuresFound).getPics());
            startActivity(intent);
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }
}