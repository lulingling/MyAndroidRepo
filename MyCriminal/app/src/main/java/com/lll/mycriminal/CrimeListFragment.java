package com.lll.mycriminal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.example.criminal.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class CrimeListFragment extends ListFragment {

    private static final String TAG = "CrimeListFragment";

    private ArrayList<Crime> mCrimes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.crimes_title);
        mCrimes = CrimeLab.get(getActivity()).getCrimes();

        // ArrayAdapter<Crime> adapter = new ArrayAdapter<Crime>(getActivity(),
        // android.R.layout.simple_list_item_1, mCrimes);

        CrimeAdapter adapter = new CrimeAdapter(mCrimes);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        Crime c = ((CrimeAdapter) getListAdapter()).getItem(position);
//		Log.d(TAG, c.getTitle() + "was clicked");

        Intent i = new Intent(getActivity(), CrimeActivity.class);
        startActivity(i);
    }

    private class CrimeAdapter extends ArrayAdapter<Crime> {

        public CrimeAdapter(ArrayList<Crime> crimes) {
            super(getActivity(), 0, crimes);
            // TODO Auto-generated constructor stub
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // If we weren't given a view ,inflate one
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(
                        R.layout.list_item_crime, null);
            }

            // Configure the view for this Crime.
            Crime c = getItem(position);

            TextView titleTextView = (TextView) convertView
                    .findViewById(R.id.crime_list_item_titleTextView);
            titleTextView.setText(c.getTitle());

            TextView dateTextView = (TextView) convertView
                    .findViewById(R.id.crime_list_item_dateTextView);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            dateTextView.setText(format.format(c.getDate()));

            CheckBox solvedCheckBox = (CheckBox) convertView
                    .findViewById(R.id.crime_list_item_solvedCheckBox);
            solvedCheckBox.setChecked(c.isSolved());

            return convertView;
        }

    }

}
