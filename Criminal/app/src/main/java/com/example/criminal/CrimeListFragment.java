package com.example.criminal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint("NewApi")
public class CrimeListFragment extends ListFragment {

	// private static final String TAG = "CrimeListFragment";

	private ArrayList<Crime> mCrimes;
	private boolean mSubtitleVisible;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		getActivity().setTitle(R.string.crimes_title);
		mCrimes = CrimeLab.get(getActivity()).getCrimes();

		// ArrayAdapter<Crime> adapter = new ArrayAdapter<Crime>(getActivity(),
		// android.R.layout.simple_list_item_1, mCrimes);

		CrimeAdapter adapter = new CrimeAdapter(mCrimes);
		setListAdapter(adapter);

		setRetainInstance(true);
		mSubtitleVisible = false;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		Crime c = ((CrimeAdapter) getListAdapter()).getItem(position);
		// Log.d(TAG, c.getTitle() + "was clicked");

		Intent i = new Intent(getActivity(), CrimePagerActivity.class);
		i.putExtra(CrimeFragment.EXTRA_CRIME_ID, c.getId());
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
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd hh:mm:ss");
			dateTextView.setText(format.format(c.getDate()));

			CheckBox solvedCheckBox = (CheckBox) convertView
					.findViewById(R.id.crime_list_item_solvedCheckBox);
			solvedCheckBox.setChecked(c.isSolved());

			return convertView;
		}

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		((CrimeAdapter) getListAdapter()).notifyDataSetChanged();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_crime_list, menu);
		MenuItem showSubtitle = menu.findItem(R.id.menu_item_show_subtitle);
		if (mSubtitleVisible && showSubtitle != null) {
			showSubtitle.setTitle(R.string.hide_subtitle);
		}
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.menu_item_new_crime:
			Crime crime = new Crime();
			CrimeLab.get(getActivity()).addCrime(crime);
			Intent i = new Intent(getActivity(), CrimePagerActivity.class);
			i.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getId());
			startActivityForResult(i, 0);
			return true;
		case R.id.menu_item_show_subtitle:
			if (getActivity().getActionBar().getSubtitle() == null) {
				getActivity().getActionBar()
						.setSubtitle(R.string.show_subtitle);
				setRetainInstance(true);
				item.setTitle(R.string.hide_subtitle);
			} else {
				getActivity().getActionBar().setSubtitle(null);
				setRetainInstance(false);
				item.setTitle(R.string.show_subtitle);
			}
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		View v = super.onCreateView(inflater, container, savedInstanceState);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if (mSubtitleVisible) {
				getActivity().getActionBar().setSubtitle(R.string.subtitle);
			}
		}

		ListView listView = (ListView) v.findViewById(android.R.id.list);

		registerForContextMenu(listView);

		return v;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		getActivity().getMenuInflater().inflate(R.menu.crime_list_item_context,
				menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		int position = info.position;
		CrimeAdapter adapter = (CrimeAdapter) getListAdapter();
		Crime crime = adapter.getItem(position);

		switch (item.getItemId()) {
		case R.id.menu_item_delete_crime:
			CrimeLab.get(getActivity()).deleteCrime(crime);
			adapter.notifyDataSetChanged();
			return true;
		}
		return super.onContextItemSelected(item);
	}

}
