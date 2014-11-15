package edu.ldsbc.reservearoom.dummy;

import android.app.Application;
import android.content.Context;
import android.widget.ListAdapter;

/**
 * Created by rossh_000 on 11/12/2014.
 * Follows advice found at http://stackoverflow.com/questions/4391720/how-can-i-get-a-resource-content-from-a-static-context
 * the purpose of this file is to provide global access to all resources, strings, and other .XML files to
 * every class in the app, regardless of whether it's an Activity, Fragment, Dialog, or regular java class.
 *
 * Typically, an resource can only be pulled from an activity class.
 */


/* String room = App.getContext().getResources().getString(R.string.room);
 * Color blue = App.getContext().getResources().getColor(R.color.blue);
  * */
public class App extends Application {

    /** makes universal application context, so any object has access to all resources. */
    private static Context mContext;

    /** current RoomListContent to be pulling from, either Sample or database. set by RoomListFragment */
    public static ListAdapter mListAdapter;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext() {
        return mContext;
    }

    /** Returns reference to our list adapter */
    public ListAdapter getRoomListAdapter() {
        return mListAdapter;
    }
}
