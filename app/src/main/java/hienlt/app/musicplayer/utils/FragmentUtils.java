package hienlt.app.musicplayer.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import hienlt.app.musicplayer.R;

/**
 * Created by hienl_000 on 4/26/2016.
 */
public class FragmentUtils {

    /**
     * Replace the current Fragment to another Fragment
     *
     * @param frag
     * @param saveInBackstack
     * @param animate
     */
    public static void replaceFragment(FragmentManager manager, Fragment frag, boolean saveInBackstack, boolean animate) {
        String backStateName = ((Object) frag).getClass().getName();

        try {
            boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

            if (!fragmentPopped && manager.findFragmentByTag(backStateName) == null) {
                //fragment not in back stack, create it.
                FragmentTransaction transaction = manager.beginTransaction();

                if (animate) {
                    Common.showLog("Change Fragment: animate");
                    transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                }

                transaction.replace(R.id.container, frag, backStateName);

                if (saveInBackstack) {
                    Common.showLog("Change Fragment: addToBackTack " + backStateName);
                    transaction.addToBackStack(backStateName);
                } else {
                    Common.showLog("Change Fragment: NO addToBackTack");
                }

                transaction.commit();
            } else {
                // custom effect if fragment is already instanciated
            }
        } catch (IllegalStateException exception) {
            Common.showLog("Unable to commit fragment, could be activity as been killed in background. " + exception.toString());
        }
    }

    public static void addStackFragment(FragmentManager manager, Fragment frag, boolean saveInBackstack, boolean animate) {
        String backStateName = ((Object) frag).getClass().getName();
        try {
            boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);
            if (!fragmentPopped && manager.findFragmentByTag(backStateName) == null) {
                FragmentTransaction transaction = manager.beginTransaction();
                if (animate) {
                    Common.showLog("Change Fragment: animate");
                    transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                }

                Fragment currFrag = manager.findFragmentById(R.id.container);
                if(currFrag!=null){
                    transaction.hide(currFrag);
                }

                transaction.add(R.id.container, frag, backStateName);
                if (saveInBackstack) {
                    Common.showLog("Change Fragment: addToBackTack " + backStateName);
                    transaction.addToBackStack(backStateName);
                } else {
                    Common.showLog("Change Fragment: NO addToBackTack");
                }

                transaction.commit();
            }

        } catch (IllegalStateException exception) {
            Common.showLog("Unable to commit fragment, could be activity as been killed in background. " + exception.toString());
        }
    }


}
