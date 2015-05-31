package com.example.criminal;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;

public class CrimeFragment extends Fragment {
	private static final String TAG = "CrimeFragment";
	public static final String EXTRA_CRIME_ID = "cirme_id";
	private static final String DIALOG_DATE = "date";
	private static final int REQUEST_DATE = 0;
	private static final int REQUEST_PHOTO = 1;

	private ImageButton mPhotoButton;

	private Crime mCrime;
	private EditText mTitleField;
	private Button mDateButton;
	private CheckBox mSolvedCheckBox;

	public void updateDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		mDateButton.setText(format.format(mCrime.getDate()));
		// mDateButton.setText(mCrime.getDate().toString());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		// UUID crimeId = (UUID)
		// getActivity().getIntent().getSerializableExtra(EXTRA_CRIME_ID);
		UUID crimeId = (UUID) getArguments().getSerializable(EXTRA_CRIME_ID);
		mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
		// mCrime = new Crime();
	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_crime, container, false);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if (NavUtils.getParentActivityName(getActivity()) != null) {
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
			}
		}

		mTitleField = (EditText) v.findViewById(R.id.crime_title);
		mTitleField.setText(mCrime.getTitle());
		mTitleField.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				mCrime.setTitle(arg0.toString());
			}

		});

		mDateButton = (Button) v.findViewById(R.id.crime_date);
		// SimpleDateFormat format = new
		// SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		// mDateButton.setText(format.format(mCrime.getDate()));
		updateDate();

		mDateButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FragmentManager fm = getActivity().getSupportFragmentManager();
				// DatePickerFragment dialog = new DatePickerFragment();
				DatePickerFragment dialog = DatePickerFragment
						.newInstance(mCrime.getDate());
				dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
				dialog.show(fm, DIALOG_DATE);

			}
		});
		// mDateButton.setEnabled(false);

		mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solvedCheckBox);
		mSolvedCheckBox.setChecked(mCrime.isSolved());
		mSolvedCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						mCrime.setSolved(isChecked);
					}

				});

		mPhotoButton = (ImageButton) v.findViewById(R.id.crime_imageButton);
		mPhotoButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getActivity(), CrimeCameraActivity.class);
				startActivityForResult(i, REQUEST_PHOTO);
			}
		});

		// If camera is not available,disable camera functionality
		PackageManager pm = getActivity().getPackageManager();
		if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)
				&& !pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
			mPhotoButton.setEnabled(false);
		}

		return v;
	}

	public static CrimeFragment newInstance(UUID crimeId) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_CRIME_ID, crimeId);

		CrimeFragment fragment = new CrimeFragment();
		fragment.setArguments(args);

		return fragment;

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode != Activity.RESULT_OK)
			return;

		if (requestCode == REQUEST_DATE) {
			Date date = (Date) data
					.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
			mCrime.setDate(date);
			// mDateButton.setText(mCrime.getDate().toString());
			updateDate();
		} else if (requestCode == REQUEST_PHOTO) {
			// Create a new Photo object and attach it to crime
			String filename = data
					.getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
			if (filename != null){
//				Log.i(TAG, "filename:" + filename);
				Photo p = new Photo(filename);
			    mCrime.setPhoto(p);
			    Log.d(TAG, "Crime:" + mCrime.getTitle() +"has a photo");
			}
			
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			// To be impletemented next
			if (NavUtils.getParentActivityName(getActivity()) != null) {
				NavUtils.navigateUpFromSameTask(getActivity());
			}
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}

	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		CrimeLab.get(getActivity()).saveCrimes();
	}

}
