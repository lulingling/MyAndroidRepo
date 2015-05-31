package com.example.criminal;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.view.WindowManager;

public class CrimeCameraActivity extends SingleFragmentActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		// Hide the window title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Hide the status bar and other OS-level chrome
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		super.onCreate(arg0);
	}

	@Override
	protected Fragment createFragment() {
		// TODO Auto-generated method stub
		return new CrimeCameraFragment();
	}

}
