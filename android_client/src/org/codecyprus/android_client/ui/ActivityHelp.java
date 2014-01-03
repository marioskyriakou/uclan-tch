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
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import org.codecyprus.android_client.R;

/**
 * Created by Nearchos Paspallis on 19/12/13.
 */
public class ActivityHelp extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        final ActionBar actionBar = getActionBar();
        if(actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        final WebView webView = (WebView) findViewById(R.id.activity_about_webview);
        webView.setBackgroundColor(0x00000000);
        webView.loadUrl("file:///android_asset/about.html");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}