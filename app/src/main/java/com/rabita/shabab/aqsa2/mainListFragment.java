package com.rabita.shabab.aqsa2;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.FragmentActivity;
import java.util.zip.Inflater;

/**
 * Created by macbookair on 2/7/16.
 */
public class mainListFragment extends Fragment {


    private OnItemSelectedListener listener;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d("TAG","in onCreateView fragment");
        View view = inflater.inflate(R.layout.mainlist,container,false);
        return view;
    }

    public interface OnItemSelectedListener {
        public void onRssItemSelected(String link);
    }



    //helllo
   /* @Override
    public void onAttach(Context context) {
        super.onAttach((Activity) context);
        if (context instanceof OnItemSelectedListener) {
            listener = (OnItemSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implemenet MyListFragment.OnItemSelectedListener");
        }
    }*/
}
