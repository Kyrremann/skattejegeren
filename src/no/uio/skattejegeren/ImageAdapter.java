/* Skattejegeren -- ImageAdapter.
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

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private int maxTreasures, treasuresFound;

    private Integer notFoundImage = R.drawable.location_button_inactive;
    private Integer foundImage = R.drawable.location_button_found;
    private Integer activeImage = R.drawable.location_button_active;
    private Integer treasureImage = R.drawable.skatt;

    private ArrayList<ImageView> imageList;

    public ImageAdapter(Context c, int maxTreasures, int treasuresFound) {
        mContext = c;
        this.maxTreasures = maxTreasures;
        imageList = new ArrayList<ImageView>();
        this.treasuresFound = treasuresFound;
    }

    public int getCount() {
        return maxTreasures;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // Create a new ImageView for each item referenced by the Adapter
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(72, 72));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(4, 4, 4, 4);
        } else {
            imageView = (ImageView) convertView;
        }

        if (position < treasuresFound) {
            imageView.setImageResource(treasureImage);
        } else {
            imageView.setImageResource(notFoundImage);
        }

        imageList.add(imageView);
        return imageView;
    }

    public void setFound(int position) {
        Log.d("Pos:", "" + position);
        treasuresFound++;
        imageList.get(position).setImageResource(treasuresFound);
    }
}