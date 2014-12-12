package edu.ldsbc.reservearoom.dummy;

import android.content.res.Resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ldsbc.reservearoom.R;

/**
 * Created by rossh_000 on 12/8/2014.
 */
public class TimeListSampleContent {

    /** An array of roomList items. */
    public static List<TimeListItem> ITEMS = new ArrayList<TimeListItem>();

    /** A map of roomList items, by ID. */
    public static Map<String, TimeListItem> ITEM_MAP = new HashMap<String, TimeListItem>();

    /* The adapter is never constructed or called, it's ITEMS list is just accessed.
   * that's why it's empty when we run the app. so, we put database and network calls
   * inside the static {}.
   * */

    static {
        addItem(new TimeListItem("1", "1:00 pm", true));
        addItem(new TimeListItem("2", "1:30 pm", true));
        addItem(new TimeListItem("3", "2:00 pm", false));
        addItem(new TimeListItem("4", "2:30 pm", true));
        addItem(new TimeListItem("5", "3:00 pm", false));
        addItem(new TimeListItem("6", "3:30 pm", false));
        addItem(new TimeListItem("7", "4:00 pm", true));
        addItem(new TimeListItem("8", "4:30 pm", true));
        addItem(new TimeListItem("9", "5:00 pm", true));
        addItem(new TimeListItem("10", "5:30 pm", false));
        addItem(new TimeListItem("11", "6:00 pm", false));
        addItem(new TimeListItem("12", "6:30 pm", true));
        addItem(new TimeListItem("13", "7:00 pm", true));
        addItem(new TimeListItem("14", "7:30 pm", false));
        addItem(new TimeListItem("15", "8:00 pm", true));
        addItem(new TimeListItem("16", "8:30 pm", false));

    }



    private static void addItem(TimeListItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /** A roomList item representing a piece of content. */
    public static class TimeListItem {
        public String id;
        public String hour;
        public String text;
        public String plus;
        public Boolean reservable;

        // overloaded constructor for strings, strings
        public TimeListItem(String id, String hour, boolean reservable) {
            this.id = id;
            this.hour = hour;
            this.reservable = reservable;
            if (reservable) {
                this.text = "";
                this.plus = "(+)";
            } else {
                this.text = "Not available";
                this.plus= "";
            }
        }

        // overloaded constructor for int, strings
        public TimeListItem(int id, String hour, Boolean reservable) {
               this(String.valueOf(id), hour, reservable);
        }

        public TimeListItem get(int position) {

            return (TimeListItem)ITEMS.get(position);
        }

        @Override
        public String toString() {
            return hour;
        }

        public String toString(int field) {
            switch (field) {
                case 1: return hour;
                case 2: return text;
                case 3: return plus;
                default: return hour;
            }
        }

    }
}