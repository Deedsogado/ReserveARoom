package edu.ldsbc.reservearoom.dummy;

import android.content.res.Resources;

import java.util.ArrayList;
import java.util.HashMap;

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

    /* The adapter is never constructed or called, the ITEMS list is just accessed.
   * that's why it's empty when we run the app. so, we put database and network calls
   * inside the static {}.
   * */

     static {
        Resources res =  App.getContext().getResources();

         //* We need to fill in the list in these steps to make the app appear fast.
         // Loading data from database is slow. loading from Server is slower. do both asynchronously,
         // after displaying sample list.
         // 1. Check if a previous list has been added to the database
         //      If so, display the list from the database.
         //         begin downloading a fresh copy from the server.
         //         when fresh copy arrives, save it to database and display it.
         //      If not, it means that we have never downloaded a list before, and this is the very
         //             first time the app has launched.
         //         Display a sample content list so the app doesn't appear to be frozen.
         //         Begin downloading the list from the server on a separate thread, so it doesn't freeze
         //             the menu, and save it to the database. First get the list of each section, then
         //             get the rooms from each section page.
         //         Goto step 1.

         // get this app's MySql database.
         DatabaseHelper dbh = new DatabaseHelper();

         if (!dbh.isEmpty()) { // if the database is not empty, or is full,
             deleteAllItems(); // delete dummy items from sample list, not from database.
             ArrayList<String> list = dbh.getAllRooms(); // load list from database.
             for (int i = 0; i < list.size(); i++) { // for each room in database,
                 addItem(new RoomListItem(i, list.get(i))); // add it to the RoomList, which will be displayed in textView.
             }

             //now that we have displayed the list for the user, check the website for any changes.
             // Takes roughly 3 seconds, but happens on background thread.
             InternetHelper.getRoomCategories();

         } else { // database is empty.  this is the first time the app was launched.

             // populate fake list, and download from internet.
             addItem(new RoomListItem("1", "Downloading from Server,"));
             addItem(new RoomListItem("2", "Please wait..."));

             // The database is empty.  Start download from internet.
             InternetHelper.getRoomCategories();
         }
    }

    /**
     * Adds RoomListItem into the list. Remember to notify listview.
     * @param item room to add to listview.
     */
    public static void addItem(RoomListItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * Remove all items from list.
     */
    public static void deleteAllItems(){
        ITEMS.clear();
        ITEM_MAP.clear();
    }

    /** A roomList item representing a piece of content. */
    public static class RoomListItem {
        public String id;
        public String content;

        /**
         * overloaded constructor for string, string
         */
        public RoomListItem(String id, String content) {
            this.id = id;
            this.content = content;
        }

        /**
         * overloaded constructor for int, string
         */
         public RoomListItem(int id, String content) {
            this.id = String.valueOf(id);
            this.content = content;
        }

        /**
         * Get the item in the index position.
         * @param position
         * @return returns the RoomListItem in the given index.
         */
        public static RoomListItem get(int position) {
            return ITEM_MAP.get(((Integer)position).toString());
        }

        /**
         * Get name of the room.
         * @return
         */
        @Override
        public String toString() {
            return content;
        }
    }
}
