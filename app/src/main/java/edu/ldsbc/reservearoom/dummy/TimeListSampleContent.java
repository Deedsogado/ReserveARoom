package edu.ldsbc.reservearoom.dummy;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ldsbc.reservearoom.R;

/**
 * Created by Ross Higley on 12/8/2014.
 */
public class TimeListSampleContent extends View {

    /**
     * An array of roomList items.
     */
    public static List<TimeListItem> ITEMS = new ArrayList<TimeListItem>();

    /**
     * A map of roomList items, by ID.
     */
    public static Map<String, TimeListItem> ITEM_MAP = new HashMap<String, TimeListItem>();

    /* The adapter is never constructed or called, the ITEMS list is just accessed.
   * that's why it's empty when we run the app. so, we put database and network calls
   * inside the static {}.
   */
    static {
        addItem(new TimeListItem("Downloading from Server"));
        addItem(new TimeListItem("Please wait..."));
    }

    /**
     * Dumnmy callback. Needed for Adapter, but not us.
     *
     * @param context
     */
    public TimeListSampleContent(Context context) {
        super(context);
    }

    /**
     * Add Time to list that appears next to calendar.
     *
     * @param item
     */
    public static void addItem(TimeListItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * remove all time items from list.
     */
    public static void clearAllItems() {
        ITEMS.clear();
        ITEM_MAP.clear();
    }



    /**
     * Locates and returns timeListItem object based on it's name.
     *
     * @param name the name of the timeListItem.
     * @return the timeListItem object.
     */
    public static TimeListItem find(String name) {
        return ITEM_MAP.get(name);
    }

    /**
     * A roomList item representing a piece of content.
     */
    public static class TimeListItem {
        public String id;
        public String hour;
        public String text;
        public String plus;
        public Boolean reservable;

        /**
         * Constructor. Creates TimeListItem based on name and whether it's reservable.
         *
         * @param name
         * @param reservable
         */
        public TimeListItem(String name, boolean reservable) {
            this.id = name;
            this.hour = name;
            this.reservable = reservable;
            if (reservable) {
                this.text = "";
                this.plus = "(+)";
            } else {
                this.text = "Not available";
                this.plus = "";
            }
        }

        /**
         * Constructor. Creates TimeListItem to give message to user, but doesn't create a timeslot.
         *
         * @param message message to show to user.
         */
        public TimeListItem(String message) {
            this.id = message;
            this.hour = "";
            this.reservable = false;
            this.text = message;
            this.plus = "";
        }

        /**
         * Returns name of this timeslot.
         *
         * @return
         */
        @Override
        public String toString() {
            return hour;
        }

        /**
         * returns different properties based on the given field number.
         * 1 returns hour (name).
         * 2 returns text ("not available" or Message).
         * 3 returns plus ("+" or "").
         *
         * @param field index of property to retrieve.
         */
        public String toString(int field) {
            switch (field) {
                case 1:
                    return hour;
                case 2:
                    return text;
                case 3:
                    return plus;
                default:
                    return hour;
            }
        }

    }
}