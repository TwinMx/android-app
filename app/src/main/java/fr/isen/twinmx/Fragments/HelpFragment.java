package fr.isen.twinmx.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.isen.twinmx.R;

/**
 * Created by pierredfc.
 */

public class HelpFragment extends Fragment {
    private View rootview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootview = inflater.inflate(R.layout.fragment_help, container, false);

        ((AppCompatActivity) this.getActivity()).getSupportActionBar().setTitle(getString(R.string.bnav_instruction));

        return this.rootview;
    }
}
