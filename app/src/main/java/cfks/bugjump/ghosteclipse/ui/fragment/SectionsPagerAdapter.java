package cfks.bugjump.ghosteclipse.ui.fragment;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import cfks.bugjump.ghosteclipse.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final String[] TAB_TITLES = new String[]{"Java Edition","Bedrock Edition","Netease(BE)"};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
		Fragment fragment = new JEFragment();
        switch(position){
			case 0:
			    fragment = new JEFragment();
			    break;
			case 1:
			    fragment = new BEFragment();
			    break;
			case 2:
			    fragment = new NBEFragment();
			    break;
		}
		return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return TAB_TITLES[position];
    }

    @Override
    public int getCount() {
        return TAB_TITLES.length;
    }
}