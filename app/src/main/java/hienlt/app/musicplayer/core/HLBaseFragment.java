package hienlt.app.musicplayer.core;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hienlt.app.musicplayer.R;

/**
 * Created by hienl_000 on 4/23/2016.
 */
public abstract class HLBaseFragment extends Fragment {

    protected Toolbar toolbar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(getLayout(),container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        settingToolbar(view);
    }

    private void settingToolbar(View view) {
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        if (toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowTitleEnabled(true);
                if(getHomeAsUpIndicator()!=0){
                    actionBar.setHomeAsUpIndicator(getHomeAsUpIndicator());
                }
                actionBar.setTitle(getActionbarName());
            }
        }
    }

    protected HLBaseActivity getBaseActivity(){
        if(HLBaseActivity.class.isAssignableFrom(getActivity().getClass()))
            return (HLBaseActivity)getActivity();
        throw new ClassCastException("Activity không thuộc HLBaseActivity");
    }

    protected ActionBar getActionBar(){
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    protected Toolbar getToolBar(){
        return toolbar;
    }

    protected int getHomeAsUpIndicator(){
        return 0;
    }
    protected String getActionbarName(){
        return getActivity().getResources().getString(R.string.app_name);
    }
    protected void setActionBarName(String name){
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(name);
        }
    }

    protected abstract int getLayout();

    /**
     * Xóa fragment đang hiển thị
     */
    protected void popCurrentFragment(){
        getActivity().getSupportFragmentManager().popBackStack();
    }

}
