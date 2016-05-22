package edu.ldsbc.reservearoom.dummy;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.URLUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ldsbc.reservearoom.RoomDetailFragment;
import edu.ldsbc.reservearoom.RoomListActivity;
import edu.ldsbc.reservearoom.RoomListFragment;
import hirondelle.date4j.DateTime;

/**
 * Created by Ross Higley on 5/10/2016.
 * Will help download webpages from internet and parse them into usable strings.
 * Finds lists of rooms, and times.
 */
public class InternetHelper {
//    private final String AVAILABLE_NOW = "https://ldsbcrooms.lib.byu.edu/availableNow.php";
//    private final String RESULTS = "https://ldsbcrooms.lib.byu.edu/results.php?";

    public static ArrayList<String> categories = new ArrayList<>(); // list of room groups
    public static ArrayList<String> roomNames = new ArrayList<>(); // list of room names.
    public static ArrayList<String> roomTimes = new ArrayList<>(); // list of times
    public static Document[] resultDocs; // local copy of webpages.
    public static String formattedDate; // selected date in YYYY.MM.DD format. 2014.12.25
    public static Calendar selectedDate; // selected date in Calendar object.

    /**
     * Uses background task to download list of categories.
     *
     * @return list of categories.
     */
    public static ArrayList<String> getRoomCategories() {
        getDate(); // Get today's date. Advances saturday and sunday to monday.

        // date is now formatted correctly. Start background thread to download webpage.
        new CategoriesFetchTask().execute("");// string goes to //doInBackground(params)
        return categories;
    }

    /**
     * Sets formattedDate to today's date, or the nearest monday.
     */
    private static void getDate(){
        // first, format today's date to pass in URL.
        Calendar today = Calendar.getInstance();

        // Site has no available rooms on Saturday or Sunday, so we better make sure today is a weekday.
        int weekday = today.get(Calendar.DAY_OF_WEEK); // get what day this is, Sunday, Monday, etc..

        if (weekday == Calendar.SUNDAY) { // if today is sunday,
            today.add(Calendar.DAY_OF_MONTH, 1); // make it monday.
        }
        int currentYear = today.get(Calendar.YEAR); // get year.
        int currentMonth = today.get(Calendar.MONTH) + 1; // get this month. the index starts at zero, so we add one.
        int currentDay = today.get(Calendar.DAY_OF_MONTH); // get this day in the month.

        formattedDate = currentYear + "." + currentMonth + "." + currentDay;
        selectedDate = today;

        if (App.DEBUG_MODE) {
            Log.i("Calendar", formattedDate);
        }
    }

    /**
     * Uses background task to download list of room names.
     *
     * @return list of room names.
     */
    private static ArrayList<String> getRoomNames() {
        new RoomNamesFetchTask().execute("");
        return roomNames;
    }


    /**
     * Uses background task to get list of times for the chosen room and date
     *
     * @param roomName the chosen room.
     * @param date     the chosen date. format to YYYY.MM.DD (2014.12.25)
     * @return list of times.
     */
    public static ArrayList<String> getRoomTimes(String roomName, String date){

        new RoomTimesFetchTask().execute(roomName, date);
        return roomTimes;
    }


    /**
     * Background thread to retrieve list of room categories.
     * Started by getRoomCategories().
     */
    private static class CategoriesFetchTask extends AsyncTask<String, Integer, String> {

        // taken from Suragch at http://stackoverflow.com/questions/9671546/asynctask-android-example
        @Override
        protected String doInBackground(String... params) { // runs on background thread. No access to GUI.
            String tempHTML = ""; // does nothing. we pass it along to fulfill overloaded methods.
            try {
                // connect to the website.
                Document doc = Jsoup.connect(getFirstPageURL()).get();

                // get the room categories from HTML
                Elements options = doc.select("select#subarea option");

                if (App.DEBUG_MODE) {
                    // write to log so we know what's going on.
                    Log.i("FetchCategories", "options.size: \n" + options.size() + "\n\n"
                            + "options.html: \n" + options.html() + "\n\n"
                            + "options.toString: \n" + options.toString() + "\n\n");
                }

                for (int i = 0; i < options.size(); i++) { // for every option in drop down list
                    Element option = options.get(i); // get category
                    String text = option.html(); // get text without html wrapping.
                    if (!text.isEmpty()) { // if text is not an empty string,
                        categories.add(text); // add it as a category.
                    }
                }
            } catch (IOException e) { // if something blows up, tell user.
                if (App.DEBUG_MODE) {
                    Log.e("FetchCategories", "Problem connecting to server.");
                    Log.e("FetchCategories", e.toString());
                }
                e.printStackTrace();
            }
            return tempHTML; // return is sent to on onPostExecute(result);
        }

        @Override
        protected void onPostExecute(String result) { // runs on main thread again. Has access to GUI.
            super.onPostExecute(result);
            // now we have the categories. go ahead and get the names, too.
            getRoomNames(); // happens on another background thread.
        }
    }

    /**
     * Downloads and saves local copy of the webpage for the specified date.
     *
     * @param newDate date to download copy of. format as YYYY.MM.DD (2014.12.25)
     */
    private static void downloadDocs(String newDate) {
        formattedDate = newDate; // set date we want to download to new date.
        String[] urls = getResultPageURLs();  // get all relevant URLS for this date.
        resultDocs = new Document[urls.length]; // clear local copies.

        for (int j = 0; j < urls.length; j++) { // for each url,
            try {
                // connect to the website.
                resultDocs[j] = Jsoup.connect(urls[j]).get(); // download the page at that url.
            } catch (IOException e) {
                if (App.DEBUG_MODE) {
                    Log.e("FetchDocs", "Error downloading documents from server.");
                    Log.e("FetchDocs", e.toString());
                }
                e.printStackTrace();
            }
        }
    }


    /**
     * Background thread to retrieve list of names.
     * Started by getRoomNames().
     */
    private static class RoomNamesFetchTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            downloadDocs(formattedDate); // get docs for the chosen date.

            for (int j = 0; j < categories.size(); j++) { // for each category of names
                // get the roomnames for this category from HTML
                Elements names = resultDocs[j].select("th.cell"); // <th class="cell">text</th>

                if (App.DEBUG_MODE) {
                    // write to log so we know what's going on.
                    Log.i("FetchNames", "names.size: \n" + names.size() + "\n\n"
                            + "names.text: \n" + names.text() + "\n\n"
                            + "names.html: \n" + names.html() + "\n\n"
                            + "names.toString: \n" + names.toString() + "\n\n");
                }

                // now we have all the names in one category. add them to the list.
                for (int i = 0; i < names.size(); i++) { // for each name in this category,
                    Element name = names.get(i); // get the HTML element containing the name.
                    String text = name.text(); // get text without html wrapping.
                    if (!text.isEmpty() && !roomNames.contains(text)) { // if text is not an empty string, and is not already in list of Names,
                        roomNames.add(text); // add it as a roomName
                    }
                }
            }

            // now that a new list is downloaded, add it to the database, but only if the list is not empty.
            if (roomNames.size() > 0) {
                DatabaseHelper dbh = new DatabaseHelper(); // get database.
                dbh.deleteAllRooms(); // delete previous list from database.

                RoomListSampleContent.deleteAllItems(); // clear current listView.

                for (int i = 0; i < roomNames.size(); i++) { // for each name in the list,
                    // add name to listview.
                    RoomListSampleContent.addItem(new RoomListSampleContent.RoomListItem(i, roomNames.get(i)));
                    dbh.insertRoom(roomNames.get(i)); // add name to database.
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) { // runs on main thread again. has access to GUI.
            super.onPostExecute(s);
            // tell the listview to refresh itself.
            RoomListFragment.adapter.notifyDataSetChanged();

            // now select the first item in the list if in TwoPaneMode
            RoomListFragment.chooseItem(0);

        }
    }

    /**
     * Background thread to get list of times for specific room name and date.
     * construct using:  new RoomTimesFetchTask().execute(roomName, date);
     * first parameter must be roomName.
     * second parameter must be date.
     * otherwise, app will crash.
     */
    private static class RoomTimesFetchTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... roomNames) {

            String roomName = roomNames[0]; // get name of room, which should be first parameter.
            String selectedDate = roomNames[1]; // get name of date, which should be second parameter.

            if (!selectedDate.equals(formattedDate)) { // if selectedDate is not same as current date, need to fetch new set of docs.
                downloadDocs(selectedDate);
            }
            // docs are now current. get times.

            // clear current list of times
            TimeListSampleContent.clearAllItems();


            for (int j = 0; j < resultDocs.length; j++) { // for each local copy of webpage,

                // already downloaded the webpages and stored them in resultDocs
                Document doc = resultDocs[j]; // get one of the webpages at a time.

                // find if the current doc has the roomName i'm looking for
                Elements valid = doc.select(":contains(" + roomName + ")");
                if (valid.size() > 0) { // if this page has the roomName,



                    int colIndex = 0; // chose index of empty cell in the header row of table.
                    Element table = doc.getElementsByTag("table").first(); // get first table on page.
                    Elements rows = table.children().first().children(); // get rows in table.
                    Elements cells = rows.first().children(); // get the cells in the first row of table.

                    for (int i = 1; i < cells.size(); i++) { // for each header cell
                        if (cells.get(i).text().equals(roomName)) { // if the header is the room we selected,
                            colIndex = i; // save the column index of the room.
                            break; // end the loop.
                        }
                    }

                    //now that we have room's column index, we can check all cells in that column!

                    for (int tr = 1; tr < rows.size() - 1; tr++) { // for each row except the header and footer.
                        cells = rows.get(tr).children(); // get this row's cells.
                        String time = cells.first().text(); // get time from first cell in row.
                        // get content of the cell in the same column as the room.
                        // if it contains a plus, make the timeListItem reservable.
                        boolean available = (cells.get(colIndex).text().contains("+")); // True if is +, False if not.

                        // add new items to the time list
                        TimeListSampleContent.addItem(new TimeListSampleContent.TimeListItem(time, available));
                    }
                }
            }

            // if the user choose a Saturday, Sunday, a far future date, or a past date, the website
            // will not have any times available. tell the user.
            if (TimeListSampleContent.ITEMS.isEmpty()) {
                TimeListSampleContent.addItem(new TimeListSampleContent.TimeListItem("No times available. "));
                TimeListSampleContent.addItem(new TimeListSampleContent.TimeListItem("Choose a date other than:"));
                TimeListSampleContent.addItem(new TimeListSampleContent.TimeListItem("Sundays "));
                TimeListSampleContent.addItem(new TimeListSampleContent.TimeListItem("Past dates "));
                TimeListSampleContent.addItem(new TimeListSampleContent.TimeListItem("Far future dates "));
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) { // runs on main thread again. has access to GUI.
            super.onPostExecute(s);
            RoomDetailFragment.timeAdapter.notifyDataSetChanged(); // tell listview of times to refresh itself.
        }
    }

    /**
     * Creates string URL to the first page we need to access to get the categories.
     * @return
     */
    public static String getFirstPageURL() {
        // https://ldsbcrooms.lib.byu.edu/results.php?curDate=2016.05.10&dropDownSearch=1&q=&date=2016.05.10&time=7%3A00am&submit=Find

        // Citation: borrowing from http://stackoverflow.com/questions/19167954/use-uri-builder-in-android-or-create-url-with-variables
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https");
        builder.authority("ldsbcrooms.lib.byu.edu");
        builder.appendPath("results.php");
        builder.appendQueryParameter("curDate", formattedDate);
        builder.appendQueryParameter("dropDownSearch", "1");
        builder.appendQueryParameter("q", "");
        builder.appendQueryParameter("date", formattedDate);
        builder.appendQueryParameter("time", "7:00am");
        builder.appendQueryParameter("submit", "Find");

        String myUrl = builder.build().toString();

        return myUrl;
    }

    /**
     * Creates string URLs to the webpages we need to get the room names and times.
     * @return returns array of URLs
     */
    public static String[] getResultPageURLs() {
        String[] urls = new String[categories.size()];

        for (int i = 0; i < categories.size(); i++) { // for each category, create a URL to the page with the names and times.
            // Sample goal url:  https://ldsbcrooms.lib.byu.edu/results.php?q=&curDate=2016%2F05%2F10&dropDownSearch=1&date=2016%2F05%2F16&subarea=Group+Study+Rooms+%284th+Floor%29&Submit=Go

            StringBuilder sb = new StringBuilder();
            sb.append("https://ldsbcrooms.lib.byu.edu/results.php?");
            sb.append("q=").append("").append("&");
            sb.append("curDate=").append(Uri.encode(formattedDate)).append("&");
            sb.append("dropDownSearch=").append("1").append("&");
            sb.append("date=").append(Uri.encode(formattedDate)).append("&");
            sb.append("subarea=").append(formatCategoryForTransport(categories.get(i))).append("&");
            sb.append("Submit=").append("Go");

            String tempURL = sb.toString();
            urls[i] = tempURL;
        }
        return urls;
    }


    /**
     * Changes the category names from human readable to PHP query format.
     * basically, replaces strange characters with their ASCII values.
     * @param subarea the category to format.
     * @return returns the PHP query formatted subarea.
     */
    public static String formatCategoryForTransport(String subarea) {
        // subarea:  Group Study Rooms (4th Floor)
        // I need:  Group+Study+Rooms+%284th+Floor%29
        String temp = subarea.replace(" ", "+");
        temp = temp.replace("(", "%28");
        temp = temp.replace(")", "%29");

        return temp;
    }

}
