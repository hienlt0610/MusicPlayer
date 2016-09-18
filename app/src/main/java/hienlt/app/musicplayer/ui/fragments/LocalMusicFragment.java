package hienlt.app.musicplayer.ui.fragments;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.security.Key;

import hienlt.app.musicplayer.core.HLBaseFragment;
import hienlt.app.musicplayer.R;
import hienlt.app.musicplayer.adapter.SectionsPagerAdapter;
import hienlt.app.musicplayer.ui.activities.HomeActivity;
import hienlt.app.musicplayer.utils.Common;

/**
 * Created by hienl_000 on 4/20/2016.
 */
public class LocalMusicFragment extends HLBaseFragment implements SearchView.OnQueryTextListener {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private String KEY_PAGE = "page";
    private static final int PAGE_ALL_SONG = 0;
    private static final int PAGE_ALBUM = 1;
    private static final int PAGE_ARTIST = 2;

    public LocalMusicFragment() {
    }

    public static LocalMusicFragment newInstence(int page) {
        Bundle bundle = new Bundle();
        bundle.putInt("KEY_PAGE", page);
        LocalMusicFragment fragment = new LocalMusicFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int page = getArguments().getInt(KEY_PAGE, 0);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        if (page < 0 || page > mSectionsPagerAdapter.getCount() - 1)
            page = 0;
        mViewPager.setCurrentItem(page);
        ((HomeActivity) getActivity()).resideMenu.addIgnoredView(mViewPager);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_local_song;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                popCurrentFragment();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected String getActionbarName() {
        return "Nhạc trong máy";
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((HomeActivity) getActivity()).resideMenu.removeIgnoredView(mViewPager);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_local_music, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        SearchView searchView = (SearchView) MenuItemCompat
                .getActionView(menu.findItem(R.id.action_search));
        searchView.setFocusable(true);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        SongListFragment fragment = (SongListFragment) mSectionsPagerAdapter.getFragment(0);
        fragment.searchSong(newText);
        return false;
    }
}
