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

import edu.ldsbc.reservearoom.RoomDetailFragment;
import edu.ldsbc.reservearoom.RoomListFragment;
import hirondelle.date4j.DateTime;

/**
 * Created by higle on 5/10/2016.
 * Will help download webpages from internet and parse them into usable strings.
 */
public class InternetHelper {
    private final String AVAILABLE_NOW = "https://ldsbcrooms.lib.byu.edu/availableNow.php";
    private final String RESULTS = "https://ldsbcrooms.lib.byu.edu/results.php?";

    public static ArrayList<String> categories = new ArrayList<>();
    public static ArrayList<String> roomNames = new ArrayList<>();
    public static ArrayList<String> roomTimes = new ArrayList<>();
    public static Document[] resultDocs;
    public static String formattedDate;


    /**
     * Uses background task to download list of categories.
     * @return list of categories.
     */
    public static ArrayList<String> getRoomListNames(){
        // set today's date for building URL.
        Calendar today = Calendar.getInstance();
        int currentYear = today.get(Calendar.YEAR);
        int currentMonth = today.get(Calendar.MONTH) + 1;
        int currentDay = today.get(Calendar.DAY_OF_MONTH);

        formattedDate = currentYear + "." + currentMonth + "." + currentDay;

       new CategoriesFetchTask().execute("");// string goes to //doInBackground(params)
        return categories;
    }

    private static ArrayList<String> getRoomNames(){
        new RoomNamesFetchTask().execute("");
        return roomNames;
    }



    public static ArrayList<String> getRoomTimes(String roomName, String date){
        new RoomTimesFetchTask().execute(roomName, date);
        return roomTimes;
    }



    private static class CategoriesFetchTask extends AsyncTask<String, Integer, String> {

        // taken from Suragch at http://stackoverflow.com/questions/9671546/asynctask-android-example
        @Override
        protected String doInBackground(String... params) { // runs on background thread. No access to GUI.
            String tempHTML = "";
            try {
                // connect to the website.
                Document doc = Jsoup.connect(getFirstPageURL()).get();

                // get the room categories from HTML
                Elements options = doc.select("select#subarea option");

                // write to log so we know what's going on.
                Log.i("Deedsogado", "options.size: \n" + options.size() + "\n\n"
                + "options.html: \n" + options.html() +"\n\n"
                + "options.toString: \n" + options.toString() + "\n\n");

                for (int i = 0; i < options.size(); i++) {
                    Element option = options.get(i);
                    String text = option.html(); // returns text without html wrapping.
                    if (!text.isEmpty()) { // if text is not an empty string,
                        categories.add(text); // add it as a category.
                    }
                }

            } catch (IOException e){
                e.printStackTrace();
            }


            return tempHTML; // return is sent to on PostExecute(result);
        }

        @Override
        protected void onPostExecute(String result) { // runs on main thread again. Has access to GUI.
            super.onPostExecute(result);

            getRoomNames();
        }
    }

    private static void downloadDocs(String newDate) {


        formattedDate = newDate; // set date we want to download to new date. .
        String[] urls = getResultPageURLs();
        resultDocs = new Document[urls.length];

        for (int j = 0; j < urls.length; j++) {
            try {
                // connect to the website.
                resultDocs[j] = Jsoup.connect(urls[j]).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private static class RoomNamesFetchTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            downloadDocs(formattedDate);

                    for (int j = 0; j < categories.size(); j++) {
                        // get the roomnames for this category from HTML
                        Elements names = resultDocs[j].select("th.cell"); // <th class="cell">text</th>


                        // write to log so we know what's going on.
                        Log.i("Deedsogado", "names.size: \n" + names.size() + "\n\n"
                                + "names.text: \n" + names.text() + "\n\n"
                                + "names.html: \n" + names.html() + "\n\n"
                                + "names.toString: \n" + names.toString() + "\n\n");


                        for (int i = 0; i < names.size(); i++) {
                            Element name = names.get(i);
                            String text = name.text(); // returns text without html wrapping.
                            if (!text.isEmpty() && !roomNames.contains(text)) { // if text is not an empty string, and is not already in list of Names,
                                roomNames.add(text); // add it as a roomName
                            }
                        }

                    }

            // now that a new list is downloaded, add it to the database.
             DatabaseHelper dbh = new DatabaseHelper();
            dbh.deleteAllRooms();

            RoomListSampleContent.deleteAllItems(); // clear current listView.

              for(int i = 0; i < roomNames.size(); i++) {
                RoomListSampleContent.addItem(new RoomListSampleContent.RoomListItem(i, roomNames.get(i)));
                dbh.insertRoom(roomNames.get(i));
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            RoomListFragment.adapter.notifyDataSetChanged();
        }
    }








    private static class RoomTimesFetchTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... roomNames) {

            String roomName = roomNames[0];
            String selectedDate = roomNames[1];

            if (!selectedDate.equals(formattedDate)) { // if selectedDate is not same as current date, need to fetch new set of docs.

                downloadDocs(selectedDate);

            }

            // docs are now current. get times.
                for (int j = 0; j < resultDocs.length; j++) {

                    // already downloaded the webpages and stored them in resultDocs
                    Document doc = resultDocs[j];

                    // find if the current doc has the roomName i'm looking for
                    Elements valid = doc.select(":contains(" + roomName + ")");
                    if (valid.size() > 0) { // if this page has the roomName,

                        // find the column index of the cell with roomName.
                        // http://stackoverflow.com/questions/10730141/find-the-row-index-of-a-selected-row-element-in-jsoup
                        Elements table = doc.select("#grid  > tbody > tr");
                        int rowCount = 0;
                        for (Element e : table) {
                            if (e.text().contains(roomName)) {
                                Log.i("Deedsogado", "Row: " + rowCount);
                                Log.i("Deedsogado", e.toString());
                            }
                            rowCount++;
                        }

                        /**
                         * The above loop reports every row in table, because each cell has a hyperlink to the reservation page, with the name of the room.
                         * We can pull the cell with the right hyperlink and then parse those to collect times to populate our listView in RoomListSampleContent.
                         *
                         * */


                        String needToPause = rowCount + "";

                        /*

                        // find all cells in webpage directly below the cell with text = roomName.
                        // http://stackoverflow.com/questions/7864433/how-to-parse-the-cells-of-the-3rd-column-of-a-table
                        Elements names = doc.select("#grid td:eq(" + index + ")");


                        // write to log so we know what's going on.
                        Log.i("Deedsogado", "names.size: \n" + names.size() + "\n\n"
                                + "names.text: \n" + names.text() + "\n\n"
                                + "names.html: \n" + names.html() + "\n\n"
                                + "names.toString: \n" + names.toString() + "\n\n");

                        for (int i = 0; i < names.size(); i++) {
                            Element name = names.get(i);
                            String text = name.text(); // returns text without html wrapping.
                            if (!text.isEmpty() && !roomNames.contains(text)) { // if text is not an empty string, and is not already in list of Names,
                                roomNames.add(text); // add it as a roomName
                            }
                        }

                        */
                    }
                }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            RoomDetailFragment.timeAdapter.notifyDataSetChanged();
        }
    }

    public static String getFirstPageURL() {
        // https://ldsbcrooms.lib.byu.edu/results.php?curDate=2016.05.10&dropDownSearch=1&q=&date=2016.05.10&time=7%3A00am&submit=Find




        // citation: borrowing from http://stackoverflow.com/questions/19167954/use-uri-builder-in-android-or-create-url-with-variables
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https");
        builder.authority("ldsbcrooms.lib.byu.edu");
        builder.appendPath("results.php");
        builder.appendQueryParameter("curDate", formattedDate);
        builder.appendQueryParameter("dropDownSearch", "1");
        builder.appendQueryParameter("q", "");
        builder.appendQueryParameter("date", formattedDate);
        builder.appendQueryParameter("time", "7:00am" );
        builder.appendQueryParameter("submit", "Find");

        String myUrl = builder.build().toString();

        return myUrl;
    }

    public static String[] getResultPageURLs(){
        String[] urls = new String[categories.size()];

        for (int i = 0; i < categories.size(); i++) {

            // Sample goal url:  https://ldsbcrooms.lib.byu.edu/results.php?q=&curDate=2014%2F10%2F21&dropDownSearch=&date=2014%2F10%2F21&subarea=Under+Development+%28Do+Not+Use%29&Submit=Go
            // Sample goal url:  https://ldsbcrooms.lib.byu.edu/results.php?q=&curDate=2016%2F05%2F10&dropDownSearch=1&date=2016%2F05%2F16&subarea=Group+Study+Rooms+%284th+Floor%29&Submit=Go
            // Sample goal url:  https://ldsbcrooms.lib.byu.edu/results.php?q=&curDate=2016%2F05%2F11&dropDownSearch=0&date=2016%2F05%2F11&subarea=&Submit=Go

            StringBuilder sb = new StringBuilder();
            sb.append("https://ldsbcrooms.lib.byu.edu/results.php?");
            sb.append("q=").append("").append("&");
            sb.append("curDate=").append(Uri.encode(formattedDate)).append("&");
            sb.append("dropDownSearch=").append("1").append("&");
            sb.append("date=").append(Uri.encode(formattedDate)).append("&");
            sb.append("subarea=").append(formatCategoryForTransport(categories.get(i))).append("&");
            sb.append("Submit=").append("Go");

            String tempURL = sb.toString();


/*
            // citation: borrowing from http://stackoverflow.com/questions/19167954/use-uri-builder-in-android-or-create-url-with-variables
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https");
            builder.authority("ldsbcrooms.lib.byu.edu");
            builder.appendPath("results.php");
            builder.appendQueryParameter("q", "");
            builder.appendQueryParameter("curDate", formattedDate);
            builder.appendQueryParameter("dropDownSearch", "1");
            builder.appendQueryParameter("date", formattedDate);
            builder.appendQueryParameter("subarea", formatCategoryForTransport(categories.get(i)));
            builder.appendQueryParameter("submit", "Go");
            String tempURL = builder.build().toString();
*/
            urls[i] = tempURL;


        }
        return urls;
    }

    public static String getResultPageUrl(String roomName, String time) {

        return "";
    }

    public static String formatCategoryForTransport(String subarea) {
        // Uri builder makes: Group%20Study%20Rooms%20(4th%20Floor)
        // subarea:  Group Study Rooms (4th Floor)
        // I need:  Group+Study+Rooms+%284th+Floor%29
        String temp = subarea.replace(" ", "+");
        temp = temp.replace( "(" , "%28");
        temp = temp.replace( ")" , "%29");

        return temp;
    }




}
