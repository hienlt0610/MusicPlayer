package hienlt.app.musicplayer.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.special.ResideMenu.ResideMenu;

import hienlt.app.musicplayer.core.HLBaseFragment;
import hienlt.app.musicplayer.R;
import hienlt.app.musicplayer.provider.SongProvider;
import hienlt.app.musicplayer.ui.activities.HomeActivity;
import hienlt.app.musicplayer.ui.activities.ScanActivity;
import hienlt.app.musicplayer.utils.Common;
import hienlt.app.musicplayer.utils.FragmentUtils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends HLBaseFragment implements View.OnClickListener{

    //Request Code
    public static int REQUEST_CODE_SCAN = 10;

    private LinearLayout btnMusicInDevice,btnMusicLike, btnRecent, btnFolder,btnPlaylist;
    private Button btnScanMusic;
    private TextView tvSongNum;
    private HomeActivity activity;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnMusicInDevice = (LinearLayout) view.findViewById(R.id.btnMusicInDevice);
        btnScanMusic = (Button) view.findViewById(R.id.btnScan);
        btnFolder = (LinearLayout) view.findViewById(R.id.btnFolder);
        btnMusicLike = (LinearLayout) view.findViewById(R.id.btnMusicLike);
        btnPlaylist = (LinearLayout) view.findViewById(R.id.btnPlaylist);
        btnRecent = (LinearLayout) view.findViewById(R.id.btnRecent);
        tvSongNum = (TextView) view.findViewById(R.id.tvSongNum);

        //set view
        tvSongNum.setText(SongProvider.getInstance(getActivity()).getSongNum() + " bài hát");

        //Set onclick view
        btnMusicLike.setOnClickListener(this);
        btnScanMusic.setOnClickListener(this);
        btnMusicInDevice.setOnClickListener(this);
        btnFolder.setOnClickListener(this);
        btnRecent.setOnClickListener(this);
        btnPlaylist.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                ((HomeActivity)getActivity()).resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
                break;
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    protected int getHomeAsUpIndicator() {
        return R.drawable.ic_act_menu;
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_home;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnMusicInDevice:
                FragmentUtils.addStackFragment(getFragmentManager(), LocalMusicFragment.newInstence(0), true, true);
                break;
            case R.id.btnPlaylist:
                FragmentUtils.addStackFragment(getFragmentManager(), new PlaylistFragment(), true, true);
                break;
            case R.id.btnMusicLike:
                FragmentUtils.addStackFragment(getFragmentManager(), new LikeListFragment(), true, true);
                break;
            case R.id.btnFolder:
                FragmentUtils.addStackFragment(getFragmentManager(), new FolderFragment(), true, true);
                break;
            case R.id.btnRecent:
                FragmentUtils.addStackFragment(getFragmentManager(), new RecentPlayFragment(), true, true);
                break;
            case R.id.btnScan:
                Intent iScan = new Intent(getActivity(), ScanActivity.class);
                getActivity().startActivityForResult(iScan, REQUEST_CODE_SCAN);
                break;
        }
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int songNums = SongProvider.getInstance(getActivity()).getSongNum();
            tvSongNum.setText(songNums+" bài hát");
        }
    };

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter(ScanActivity.ACTION_SCAN_SUCCESS);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, filter);
    }
}
