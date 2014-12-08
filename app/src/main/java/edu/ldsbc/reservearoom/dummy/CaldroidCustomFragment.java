package edu.ldsbc.reservearoom.dummy;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;

/**
 * Created by rossh_000 on 12/2/2014.
 */
public class CaldroidCustomFragment extends CaldroidFragment {

    @Override
    public CaldroidGridAdapter getNewDatesGridAdapter(int month, int year) {
        // TODO Auto-generated method stub
        return new CaldroidCustomAdapter(getActivity(), month, year, getCaldroidData(), extraData);
    }

}
