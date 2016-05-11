package edu.ldsbc.reservearoom.dummy;

import android.net.Uri;
import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import hirondelle.date4j.DateTime;

/**
 * Created by higle on 5/10/2016.
 * Will help download webpages from internet and parse them into usable strings.
 */
public class InternetHelper {
    private final String AVAILABLE_NOW = "https://ldsbcrooms.lib.byu.edu/availableNow.php";
    private final String RESULTS = "https://ldsbcrooms.lib.byu.edu/results.php?";

    public ArrayList<String> categories = new ArrayList<String>();

    public InternetHelper() {

    }


    public String getFirstPageURL() {
        // https://ldsbcrooms.lib.byu.edu/results.php?curDate=2016.05.10&dropDownSearch=1&q=&date=2016.05.10&time=7%3A00am&submit=Find

        Calendar today = Calendar.getInstance();
        int currentYear = today.get(Calendar.YEAR);
        int currentMonth = today.get(Calendar.MONTH) + 1;
        int currentDay = today.get(Calendar.DAY_OF_MONTH);

        String formattedDate = currentYear + "." + currentMonth + "." + currentDay;

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

    public ArrayList<String> getCategories(){
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                String tempHTML = "";
                try {
                    // connect to the website.
                    Document doc = Jsoup.connect(getFirstPageURL()).get();

                    // get the room categories from HTML
                    Elements options = doc.select("select#subarea option");
                    tempHTML = options.html();

                } catch (IOException e){
                    e.printStackTrace();
                }
                categories.add(tempHTML);

                for(int i = 0; i < categories.size(); i++) {
                    RoomListSampleContent.addItem(new RoomListSampleContent.RoomListItem(i, categories.get(i)));
                }
                // todo: update Textviews in RoomListFragment (from another thread?!?! )

                return tempHTML;
            }

        };

        return categories;
    }

    public String createResultsURL(){
        // Sample goal url:  https://ldsbcrooms.lib.byu.edu/results.php?q=&curDate=2014%2F10%2F21&dropDownSearch=&date=2014%2F10%2F21&subarea=Under+Development+%28Do+Not+Use%29&Submit=Go
        // Sample goal url:  https://ldsbcrooms.lib.byu.edu/results.php?q=&curDate=2016%2F05%2F10&dropDownSearch=1&date=2016%2F05%2F16&subarea=Group+Study+Rooms+%284th+Floor%29&Submit=Go

        StringBuilder tempURL = new StringBuilder(RESULTS);
        tempURL.append("q=");
        tempURL.append("&curDate=");

        Date today = new Date();
        String currentDate = today.toString();

        // citation: borrowing from http://stackoverflow.com/questions/19167954/use-uri-builder-in-android-or-create-url-with-variables
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https");
        builder.authority("ldsbcrooms.lib.byu.edu");
        builder.appendPath("results.php");
        builder.appendQueryParameter("q", "");
        builder.appendQueryParameter("curDate", currentDate);
        builder.appendQueryParameter("dropDownSearch", "1");
        builder.appendQueryParameter("date", currentDate);
        builder.appendQueryParameter("subarea", "");


        return "";
    }


}
