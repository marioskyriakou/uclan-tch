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

import android.app.ActionBar;
import android.app.Activity;
import android.content.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.*;
import org.codecyprus.android_client.Preferences;
import org.codecyprus.android_client.SerializableSession;
import org.codecyprus.android_client.sync.JsonParseException;
import org.codecyprus.android_client.sync.JsonParser;
import org.codecyprus.android_client.R;
import org.codecyprus.android_client.sync.SyncService;
import org.codecyprus.android_client.model.Category;
import org.json.JSONException;

import java.util.*;

/**
 * Created by Nearchos Paspallis on 19/12/13.
 */
public class ActivityCategories extends Activity
{
    public static final String TAG = "org.codecyprus.android_client.ui.ActivityCategories";

    private final IntentFilter intentFilter = new IntentFilter(SyncService.ACTION_CATEGORIES_COMPLETED);
    private ProgressReceiver progressReceiver;

    private ListView listView;

    private Category [] categories;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_categories);

        listView = (ListView) findViewById(R.id.activity_category_list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                final Category category = categories[position];
                final SerializableSession serializableSession = Preferences.getSession(ActivityCategories.this, category.getUUID());
                if(serializableSession != null)
                {
                    new DialogResumeOrClear(ActivityCategories.this, serializableSession, category).show();
                }
                else
                {
                    final Intent startQuizIntent = new Intent(ActivityCategories.this, ActivityStartQuiz.class);
                    startQuizIntent.putExtra(ActivityStartQuiz.EXTRA_CATEGORY, category);
                    startActivity(startQuizIntent);
                }
            }
        });

        final ActionBar actionBar = getActionBar();
        if(actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        progressReceiver = new ProgressReceiver();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        registerReceiver(progressReceiver, intentFilter);
        refresh();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        unregisterReceiver(progressReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        menu.add(R.string.REFRESH)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

//        menu.add(R.string.ENTER_CODE)
//                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        else if(getString(R.string.REFRESH).equals(item.getTitle()))
        {
            refresh();
            return true;
        }
        else if(getString(R.string.ENTER_CODE).equals(item.getTitle()))
        {
            Toast.makeText(this, R.string.Enter_code, Toast.LENGTH_SHORT).show(); //todo
            return true;
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }
    }

    private void refresh()
    {
        final Intent getCategoriesIntent = new Intent(this, SyncService.class);
        getCategoriesIntent.setAction(SyncService.ACTION_CATEGORIES);
        getCategoriesIntent.putExtra(SyncService.EXTRA_PARAMETERS, new HashMap<String, String>());
        setProgressBarIndeterminateVisibility(true);
        startService(getCategoriesIntent);
    }

    private class ProgressReceiver extends BroadcastReceiver
    {
        @Override public void onReceive(final Context context, final Intent intent)
        {
            final String payload = (String) intent.getSerializableExtra(SyncService.EXTRA_PAYLOAD);
            setProgressBarIndeterminateVisibility(false);

            if(payload != null)
            {
                try
                {
                    categories = JsonParser.parseGetCategories(payload);
                    // update the UI
                    listView.setAdapter(new CategoriesAdapter(ActivityCategories.this, categories));
                }
                catch (JsonParseException jsonpe)
                {
                    Log.e(TAG, jsonpe.getMessage());
                    new DialogError(context, jsonpe.getMessage()).show();
                }
                catch (JSONException jsone)
                {
                    Log.e(TAG, jsone.getMessage());
                    new DialogError(context, jsone.getMessage()).show();
                }
            }
        }
    }
}