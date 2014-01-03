/*
 * This file is part of UCLan-THC server.
 *
 *     UCLan-THC server is free software: you can redistribute it and/or
 *     modify it under the terms of the GNU General Public License as
 *     published by the Free Software Foundation, either version 3 of
 *     the License, or (at your option) any later version.
 *
 *     UCLan-THC server is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.codecyprus.android_client.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import org.codecyprus.android_client.Preferences;
import org.codecyprus.android_client.R;
import org.codecyprus.android_client.SerializableSession;
import org.codecyprus.android_client.model.Category;

/**
 * Created by Nearchos Paspallis on 24/12/13.
 */
public class CategoriesAdapter extends ArrayAdapter<Category>
{
    private final LayoutInflater layoutInflater;

    public CategoriesAdapter(final Context context, final Category [] categories)
    {
        super(context, R.layout.list_item_category, categories);

        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override public View getView(final int position, final View convertView, final ViewGroup parent)
    {
        final View view = convertView != null ? convertView : layoutInflater.inflate(R.layout.list_item_category, null);

        final Category category = getItem(position);
        final SerializableSession serializableSession = Preferences.getSession(getContext(), category.getUUID());

        // Creates a ViewHolder and store references to the two children views we want to bind data to.
        assert view != null;
        view.findViewById(R.id.list_item_category_active).setVisibility(serializableSession == null ? View.GONE : View.VISIBLE);
        final TextView categoryName = (TextView) view.findViewById(R.id.list_item_category_name);
        final TextView categoryValidUntil = (TextView) view.findViewById(R.id.list_item_category_valid_until);

        // Bind the data efficiently with the holder.
        categoryName.setText(category.getName());
        categoryValidUntil.setText(getContext().getString(R.string.Valid_until,category.getValidUntil()));

        return view;
    }
}