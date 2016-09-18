package hienlt.app.musicplayer.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import hienlt.app.musicplayer.ui.fragments.AlbumListFragment;
import hienlt.app.musicplayer.ui.fragments.ArtistListFragment;
import hienlt.app.musicplayer.ui.fragments.SongListFragment;

/**
 * Created by hienl_000 on 4/20/2016.
 */
public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

    ArrayList<Pair<String, Fragment>> list;
    FragmentManager fm;

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;
        list = new ArrayList<>();
        list.add(new Pair<String, Fragment>("Tất cả",new SongListFragment()));
        list.add(new Pair<String, Fragment>("Album",new AlbumListFragment()));
        list.add(new Pair<String, Fragment>("Ca sĩ",new ArtistListFragment()));
    }


    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return list.get(position).second;
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return list.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return list.get(position).first;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
    }

    public Fragment getFragment(int position) {
        if (position >= list.size()) {
            return null;
        }
        return list.get(position).second;
    }
}
