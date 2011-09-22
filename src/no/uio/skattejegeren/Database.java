/* Skattejegeren -- Database.
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

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Database {// extends SQLiteOpenHelper {

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private Context context;
    private static final int DATABASE_VERSION = 25;
    private static final String DB_NAME = "SkatteJegeren";
    private static final String TABLE_NAME = "history";
    private static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME
        + " (_id TEXT, value NUMBER);";

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DB_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(TABLE_CREATE);
            db.execSQL("INSERT INTO history VALUES ('LOREM', '0')");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Logs that the database is being upgraded
            Log.w("UPGRADE", "Upgrading database from version " + oldVersion
                  + " to " + newVersion
                  + ", which will destroy all old data (not really)");

            // Kills the table and existing data
            db.execSQL("DROP TABLE IF EXISTS history");

            // Recreates the database with a new version
            onCreate(db);
        }
    }

    public Database(Context context) {
        this.context = context;
    }

    public Database open() throws SQLException {
        mDbHelper = new DatabaseHelper(context);
        mDb = mDbHelper.getWritableDatabase();
        if (mDb.getVersion() != DATABASE_VERSION)
            mDbHelper.onUpgrade(mDb, mDb.getVersion(), DATABASE_VERSION);
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    /**
     * Returns a cursor with an int to show how many of the selected course the
     * player have done
     *
     * @param course
     *            name of the course
     */
    public Cursor getTable(String course) {
        // return mDb.query(TABLE_NAME, new String[] {"_id", "value"}, "_id",
        // new String[] {course}, null, null, null);
        return mDb.rawQuery("SELECT value FROM history WHERE _id='" + course
                            + "'", null);
    }

    /**
     * Will update the selected course with a new number
     *
     * @param course
     * @param update
     */
    public void setTable(String course, int update) {
        // ContentValues content = new ContentValues(2);
        // content.put("_id", course);
        // content.put("value", update);
        // if (mDb.update(TABLE_NAME, content, null, null) == 0) {
        // mDb.insert(TABLE_NAME, null, content);
        // Log.d("SQLite", "INSERT " + course + " with " + update);
        // return;
        // }
        mDb.execSQL("UPDATE " + TABLE_NAME + " SET value ='" + update
                    + "' WHERE _id = '" + course + "'");
        Log.d("SQLite", "UPDATE " + course + " with " + update);
    }
}