package edu.ldsbc.reservearoom;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import edu.ldsbc.reservearoom.dummy.RoomListSampleContent;
import edu.ldsbc.reservearoom.dummy.TimeAdapter;
import edu.ldsbc.reservearoom.dummy.TimeListSampleContent;

/**
 * A fragment representing a single Room detail screen.
 * This fragment is either contained in a {@link RoomListActivity}
 * in two-pane mode (on tablets) or a {@link RoomDetailActivity}
 * on handsets.
 */
public class RoomDetailFragment extends Fragment {

    CaldroidFragment calDroid = new CaldroidFragment();
    Bundle args = new Bundle();
    Calendar cal = Calendar.getInstance();
    Date selectedDate = cal.getTime(); // will be Date for currently selected date. update inside onSelectDate();
    String selectedDateAsLong = "SelectedDateAsLong";


    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private RoomListSampleContent.RoomListItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RoomDetailFragment() {
    }

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM'. 'dd"); // Dec. 4


    /** Create listener for Caldroid. not declared in activity because methods are overloaded
     * inside the listener, not the activity. If we need the activity as context to do something, use
     * getActivity() **/

    final CaldroidListener listener = new CaldroidListener() {

        @Override // happens when user short presses a date.
        public void onSelectDate(Date date, View view) {
            // Make toast appear. will be removed later.
            Toast.makeText(getActivity(), date.toString(),
                    Toast.LENGTH_SHORT).show();

            // set color of newly selected cell
            calDroid.clearSelectedDates();
            calDroid.setSelectedDates(date, date);
            selectedDate = date;
            calDroid.refreshView();

            // make TextView above change to the date chosen.
            TextView roomDetailText = (TextView) getActivity().findViewById(R.id.room_detail);
            roomDetailText.setText(simpleDateFormat.format(date));

            // Add selected date to bundle to be passed along later.
            getActivity().getIntent().putExtra("vDate", simpleDateFormat.format(selectedDate));

        }

        @Override // happens on screen rotate or when user swipes left or right.
        public void onChangeMonth(int month, int year) {
            String text = "month: " + month + " year: " + year;
            Toast.makeText(getActivity(), text,
                    Toast.LENGTH_SHORT).show();
        }

        @Override // happens when user long presses a date.
        public void onLongClickDate(Date date, View view) {
            Toast.makeText(getActivity(),
                    "Long click " + date.toString(),
                    Toast.LENGTH_SHORT).show();
        }

        @Override // happens on screen rotations and when room is selected.
        public void onCaldroidViewCreated() {
            calDroid.setSelectedDates(selectedDate, selectedDate);
            calDroid.refreshView();
            TextView roomDetailText = (TextView) getActivity().findViewById(R.id.room_detail);
            roomDetailText.setText(simpleDateFormat.format(selectedDate));
            Toast.makeText(getActivity(),
                    "Caldroid view is created",
                    Toast.LENGTH_SHORT).show();

            // Add selected date to bundle to be passed along later.
            getActivity().getIntent().putExtra("vDate", simpleDateFormat.format(selectedDate));
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

     //   if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the content specified by the fragment
            // arguments.
     //     mItem = RoomListSampleContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

     //   }

        // If Activity is created after rotation
        if (savedInstanceState != null) {
            calDroid.restoreStatesFromKey(savedInstanceState,
                    "CALDROID_SAVED_STATE");
            long savedSelectedDate = savedInstanceState.getLong(selectedDateAsLong);
            selectedDate.setTime(savedSelectedDate);
            calDroid.refreshView();
        }

        // If activity is created from fresh
        else {
            Bundle args = new Bundle();
            Calendar cal = Calendar.getInstance();
            args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
            args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
            args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
            args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, false);
            calDroid.setArguments(args);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_room_detail, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listView);

        // Show today's date in the TextView.
        ((TextView) rootView.findViewById(R.id.room_detail)).setText(simpleDateFormat.format(new Date()));

        // Now make list items do things when clicked.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                // get actual object.
                TimeListSampleContent.TimeListItem timeSlot = (TimeListSampleContent.TimeListItem) parent.getItemAtPosition(position);
                // Only do things if the time is reservable.
                if (timeSlot.reservable) {
                    // prepare the time to pass in intent later.
                    TextView textView = (TextView) itemClicked.findViewById(R.id.hour);
                    String time = textView.getText().toString();
                    // launch new activity to verify results of room, date, and time chosen.
                    Intent launchVerifyActivity = new Intent(getActivity(), RoomVerifyActivity.class);
                    Intent passedAlong = getActivity().getIntent();

                    // on tablets, passedAlong was never passed (no new activity launched).
                    // the selected room is in fragment's arguments instead of activity's bundle.

                    launchVerifyActivity.putExtra("vRoom", RoomListSampleContent.ITEM_MAP.get(getArguments()
                           .getString(RoomDetailFragment.ARG_ITEM_ID)).toString()); //Room

                    launchVerifyActivity.putExtra("vDate",
                            passedAlong.getStringExtra("vDate")); //added to passedAlong by listener.onSelectDate();

                    launchVerifyActivity.putExtra("vTime",
                            time);

                    startActivity(launchVerifyActivity);
                } else {
                    Log.i("ReserveARoom", "Time is not reservable.");
                }
            }
        });

        // Show items in the ListView
        listView.setAdapter(new TimeAdapter<TimeListSampleContent.TimeListItem>(
                getActivity(),
                R.layout.time_list_item, // id of the list item layout.
                R.id.hour,  // id of textView inside our list item layout (hour)
                R.id.reservable,
                R.id.plus,
                TimeListSampleContent.ITEMS)); // Here is where we specify where the data is coming from.

        // Show Calendar and time in top bar of Caldroid.
        // Actually, this is simply telling Caldroid what today's month and day is when it launches.
        cal.setTime(selectedDate);
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
//        args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, false);
//        args.putBoolean(CaldroidFragment.SQUARE_TEXT_VIEW_CELL, true);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            args.putBoolean(CaldroidFragment.SQUARE_TEXT_VIEW_CELL, true);

        }
        calDroid.setArguments(args);

        // Link caldroid in the fragment to the listener, either in RoomListActivity or RoomDetailActivity
        //(actually it's defined in this file, not the activities.)
        calDroid.setCaldroidListener(listener);

        FragmentActivity act = (FragmentActivity) getActivity();
        android.support.v4.app.FragmentTransaction t = act
                .getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar1, calDroid);
        t.commit();





        return rootView;
    }

    /** Save state of caldroid **/
    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);

        if (calDroid != null) {

            outState.putLong(selectedDateAsLong, selectedDate.getTime());
            calDroid.saveStatesToKey(outState, "CALDROID_SAVED_STATE");
        }


    }
}
