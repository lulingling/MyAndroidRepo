package com.example.criminal;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class CrimeCameraFragment extends Fragment {
	private static final String TAG = "CrimeCameraFragment";
	public static final String EXTRA_PHOTO_FILENAME = "photo_filename";

	private Camera mCamera;
	private SurfaceView mSurfaceView;
	private View mProgressContainer;

	private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {

		@Override
		public void onShutter() {
			// TODO Auto-generated method stub
			// Display the progress indicator
			mProgressContainer.setVisibility(View.VISIBLE);
		}
	};

	private Camera.PictureCallback mJpegCallback = new Camera.PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			// Create a file name
			String filename = UUID.randomUUID().toString() + ".jpg";
			// Save the jpeg data to disk
			FileOutputStream os = null;
			boolean success = true;
			try {
				os = getActivity().openFileOutput(filename,
						Context.MODE_PRIVATE);
				os.write(data);
			} catch (Exception e) {
				Log.e(TAG, "Error writing to file" + filename, e);
				success = false;
			} finally {
				try {
					if (os != null)
						os.close();
				} catch (Exception e) {
					Log.e(TAG, "Error closing file" + filename, e);
					success = false;
				}
			}
			// Set the photo filename on the result intent
			if (success) {
				// Log.i(TAG, "JPEG saved at" + filename);
				Intent i = new Intent();
				i.putExtra(EXTRA_PHOTO_FILENAME, filename);
				getActivity().setResult(Activity.RESULT_OK, i);
			}else{
				getActivity().setResult(Activity.RESULT_CANCELED);
			}

			getActivity().finish();
		}
	};

	private Size getBestSupportSize(List<Size> sizes, int width, int height) {

		Size bestSize = sizes.get(0);
		int largestArea = bestSize.width * bestSize.height;
		for (Size s : sizes) {
			int area = s.width * s.height;
			if (area > largestArea) {
				bestSize = s;
				largestArea = area;
			}
		}
		return bestSize;

	}

	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		View v = inflater.inflate(R.layout.fragment_crime_camera, container,
				false);

		mProgressContainer = v
				.findViewById(R.id.crime_camera_progressContainer);
		mProgressContainer.setVisibility(View.INVISIBLE);

		Button takePictureButton = (Button) v
				.findViewById(R.id.crime_camera_takePictureButton);
		takePictureButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// getActivity().finish();
				if (mCamera != null) {
					mCamera.takePicture(mShutterCallback, null, mJpegCallback);
				}

			}
		});

		mSurfaceView = (SurfaceView) v
				.findViewById(R.id.crime_camera_surfaceView);
		SurfaceHolder holder = mSurfaceView.getHolder();
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		holder.addCallback(new SurfaceHolder.Callback() {

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				// TODO Auto-generated method stub
				try {
					if (mCamera != null) {
						mCamera.setPreviewDisplay(holder);
					}
				} catch (IOException exception) {
					Log.e(TAG, "Error setting up preview display", exception);
				}
			}

			@Override
			public void surfaceDestroyed(SurfaceHolder arg0) {
				// TODO Auto-generated method stub
				if (mCamera != null) {
					mCamera.stopPreview();
				}
			}

			@Override
			public void surfaceChanged(SurfaceHolder arg0, int w, int h,
					int arg3) {
				// TODO Auto-generated method stub
				if (mCamera == null)
					return;

				Camera.Parameters parameters = mCamera.getParameters();
				// Size s = null;
				Size s = getBestSupportSize(
						parameters.getSupportedPreviewSizes(), w, h);
				parameters.setPreviewSize(s.width, s.height);
				s = getBestSupportSize(parameters.getSupportedPictureSizes(),
						w, h);
				parameters.setPictureSize(s.width, s.height);
				mCamera.setParameters(parameters);
				try {
					mCamera.startPreview();
				} catch (Exception e) {
					Log.e(TAG, "Couldn't start preview", e);
					mCamera.release();
					mCamera = null;
				}
			}
		});

		return v;
	}

	@SuppressLint("NewApi")
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			mCamera = Camera.open(0);
		} else {
			mCamera = Camera.open();
		}
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
	}

}
