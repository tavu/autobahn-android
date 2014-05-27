package autobahn.android.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import autobahn.android.BasicParametersFragment;
import autobahn.android.OptionalParametersFragment;

/**
 * Created by Nl0st on 24/4/2014.
 */
public class TabsAdapter extends FragmentPagerAdapter {

    public TabsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                return new BasicParametersFragment();
            case 1:
                return new OptionalParametersFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
