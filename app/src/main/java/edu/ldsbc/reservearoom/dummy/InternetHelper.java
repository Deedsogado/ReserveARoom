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

import edu.ldsbc.reservearoom.RoomListFragment;
import hirondelle.date4j.DateTime;

/**
 * Created by higle on 5/10/2016.
 * Will help download webpages from internet and parse them into usable strings.
 */
public class InternetHelper {
    private final String AVAILABLE_NOW = "https://ldsbcrooms.lib.byu.edu/availableNow.php";
    private final String RESULTS = "https://ldsbcrooms.lib.byu.edu/results.php?";

    public ArrayList<String> categories = new ArrayList<>();
    public ArrayList<String> roomNames = new ArrayList<>();

    public InternetHelper() {

        getCategories();

    }


    /**
     * Uses background task to download list of categories.
     * @return list of categories.
     */
    public ArrayList<String> getCategories(){
       new CategoriesFetchTask().execute("");// string goes to //doInBackground(params)
        return categories;
    }

    public ArrayList<String> getRoomNames(){
        new RoomNamesFetchTask().execute("");
        return roomNames;
    }

    private class CategoriesFetchTask extends AsyncTask<String, Integer, String> {

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



    private class RoomNamesFetchTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            String[] urls = getResultPageURLs();
            for (int j = 0; j < urls.length; j++) {
                try {
                    // connect to the website.
                    Document doc = Jsoup.connect(urls[j]).get();

                    // get the roomnames for this category from HTML
                    Elements names = doc.select("th.cell"); // <th class="cell">text</th>


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

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            // todo: remove this loop section once next background task is complete.
            for(int i = 0; i < roomNames.size(); i++) {
                RoomListSampleContent.addItem(new RoomListSampleContent.RoomListItem(i, roomNames.get(i)));
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            RoomListFragment.adapter.notifyDataSetChanged();
        }
    }

    public String getFirstPageURL() {
        // https://ldsbcrooms.lib.byu.edu/results.php?curDate=2016.05.10&dropDownSearch=1&q=&date=2016.05.10&time=7%3A00am&submit=Find

        Calendar today = Calendar.getInstance();
        int currentYear = today.get(Calendar.YEAR);
        int currentMonth = today.get(Calendar.MONTH) + 1;
        int currentDay = today.get(Calendar.DAY_OF_MONTH);

        String formattedDate = currentYear + "." + currentMonth + "." + currentDay;


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

    public String[] getResultPageURLs(){
        String[] urls = new String[categories.size()];

        for (int i = 0; i < categories.size(); i++) {
            Calendar today = Calendar.getInstance();
            int currentYear = today.get(Calendar.YEAR);
            int currentMonth = today.get(Calendar.MONTH) + 1;
            int currentDay = today.get(Calendar.DAY_OF_MONTH);

            String formattedDate = currentYear + "/" + currentMonth + "/" + currentDay;

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
