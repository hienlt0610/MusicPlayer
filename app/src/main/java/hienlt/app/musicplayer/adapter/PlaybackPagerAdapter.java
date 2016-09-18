package hienlt.app.musicplayer.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;

import hienlt.app.musicplayer.models.Song;
import hienlt.app.musicplayer.ui.fragments.FragmentPlayer;
import hienlt.app.musicplayer.ui.fragments.LyricFragment;
import hienlt.app.musicplayer.utils.Common;

/**
 * Created by hienl_000 on 4/13/2016.
 */
public class PlaybackPagerAdapter extends FragmentPagerAdapter{

    private ArrayList<Fragment> list;
    private FragmentManager fm;
    public PlaybackPagerAdapter(FragmentManager fm) {
        super(fm);
        list = new ArrayList<>();
        list.add(new LyricFragment());
        this.fm = fm;
    }

    @Override
    public Fragment getItem(int position) {
        Common.showLog("getItem "+System.currentTimeMillis());
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String[] pageTitle = {"Lời bài hát"};
        return pageTitle[position];
    }
    public Fragment getPagerFragment(ViewPager container, int position) {
        String name = makeFragmentTag(container.getId(), position);
        return  fm.findFragmentByTag(name);
    }


    public String makeFragmentTag(int viewId, int index) {
        return "android:switcher:" + viewId + ":" + index;
    }
}
