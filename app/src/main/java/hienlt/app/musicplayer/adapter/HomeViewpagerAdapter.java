package hienlt.app.musicplayer.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;

import hienlt.app.musicplayer.ui.fragments.HomeFragment;

/**
 * Created by hienl_000 on 4/3/2016.
 */
public class HomeViewpagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> listFragment;
    private FragmentManager fm;

    public HomeViewpagerAdapter(FragmentManager fm) {
        super(fm);
        listFragment = new ArrayList<>();
        listFragment.add(new HomeFragment());
        this.fm = fm;
    }

    @Override
    public Fragment getItem(int position) {
        return listFragment.get(position);
    }

    @Override
    public int getCount() {
        return listFragment.size();
    }

    public void addFragment(Fragment fragment) {
        this.listFragment.add(fragment);
    }

    public Fragment getPagerFragment(ViewPager container, int position) {
        String name = makeFragmentTag(container.getId(), position);
        return  fm.findFragmentByTag(name);
    }


    public String makeFragmentTag(int viewId, int index) {
        return "android:switcher:" + viewId + ":" + index;
    }
}
