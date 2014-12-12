package edu.ldsbc.reservearoom.dummy;

import android.content.Context;
import android.content.res.Resources;

import com.loopj.android.http.AsyncHttpClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ldsbc.reservearoom.R;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards, but modified for our use to create a list on the
 * first page of the app.
 * <p/>
 */
public class RoomListSampleContent {

    /** An array of roomList items. */
    public static ArrayList<RoomListItem> ITEMS = new ArrayList<RoomListItem>();

    /** A map of roomList items, by ID. */
    public static HashMap<String, RoomListItem> ITEM_MAP = new HashMap<String, RoomListItem>();

    /* The adapter is never constructed or called, it's ITEMS list is just accessed.
   * that's why it's empty when we run the app. so, we put database and network calls
   * inside the static {}.
   * */

     static {
        // Add items here.
        // addItem(new RoomListItem("1", "Item 1"));
        // addItem(new RoomListItem("2", "Item 2"));
        // addItem(new RoomListItem("3", "Item 3"));

        Resources res =  App.getContext().getResources();

        // To update, simply replace "room_418" with the name of the string in res/values/strings.xml
        // Remember to correct the translations in this file as well.
        // Group Study Rooms (4th floor)
        addItem(new RoomListItem("1", res.getString(R.string.room_418)));
        addItem(new RoomListItem("2", res.getString(R.string.room_419)));
        addItem(new RoomListItem("3", res.getString(R.string.room_420)));
        addItem(new RoomListItem("4", res.getString(R.string.room_421)));
        addItem(new RoomListItem("5", res.getString(R.string.scanner)));
        // Scanners
        addItem(new RoomListItem("6", res.getString(R.string.scanner_1)));
        addItem(new RoomListItem("7", res.getString(R.string.scanner_2)));
        addItem(new RoomListItem("8", res.getString(R.string.scanner_3)));
        // Under Development (Do Not Use)
        addItem(new RoomListItem("9", res.getString(R.string.test_room)));


        // TODO: The following:
         /* Use AsyncHttpClient and JSoup to:
          * 1. pull first webpage,
          * 2. extract the options, save the options to a static list,
          * 3. pull the webpages based on the options,
          * 4. extract the names, save each as a RoomListItem
          *
          * We will do steps 1 and 2 procedurally, and steps 3 and 4
          * in a loop based on the number of options.  */


         //* We need to fill in the list in these steps to make the app appear fast.
         // Loading data from database is slow. loading from Server is slower. do both asynchronously,
         // after displaying sample list.
         // 1. Check if a previous list has been added to the database
         //      If so, display the list from the database.
         //      If not, it means that we have never downloaded a list before, and this is the very
         //          first time the app has launched.
         //      Display a sample content list so the app doesn't appear to be frozen.
         //      Begin downloading the list from the server on a separate thread, so it doesn't freeze
         //          the menu, and save it to the database. First get the list of each section, then
         //          get the rooms from each section page.
         //      When the downloaded list is saved to the database, run step 1 again. Call it manually,
         //      not in a loop, because we don't know how how slow the network is, and don't want to
         //      create a paradox.
         //
         // Since the database will have content in it every time after the first time, it will not
         // ask for the list from the server again.  Fix by either having it dump the database every
         // two weeks, or by having a re-sync option in the action bar overflow, or both.  */
    }




    private static void addItem(RoomListItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /** A roomList item representing a piece of content. */
    public static class RoomListItem {
        public String id;
        public String content;

        // overloaded constructor for strings, strings
        public RoomListItem(String id, String content) {
            this.id = id;
            this.content = content;
        }

        // overloaded constructor for int, strings
        public RoomListItem(int id, String content) {
            this.id = String.valueOf(id);
            this.content = content;
        }

        public static RoomListItem get(int position) {

            return ITEM_MAP.get(((Integer)position).toString());
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
