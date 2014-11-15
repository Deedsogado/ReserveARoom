package edu.ldsbc.reservearoom;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import edu.ldsbc.reservearoom.dummy.RoomListSampleContent;


/**
 * An activity representing a single Room detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link RoomListActivity}.
 * <p/>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link RoomDetailFragment}.
 */
public class RoomDetailActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detail);

        // Show the Up button in the action bar.
        getActionBar().setDisplayHomeAsUpEnabled(true);
        // Set the title in the action bar to the selected list item.
        getActionBar().setTitle(RoomListSampleContent.ITEM_MAP.get(getIntent().
                getStringExtra(RoomDetailFragment.ARG_ITEM_ID)).toString());


        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(RoomDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(RoomDetailFragment.ARG_ITEM_ID));
            RoomDetailFragment fragment = new RoomDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
             .add(R.id.room_detail_container, fragment)
             .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, RoomListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
