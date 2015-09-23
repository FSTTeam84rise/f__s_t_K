package com.fstrise.ilovekara.animation;

import android.app.Activity;

import com.fstrise.ilovekara.R;

public class ActivityAnimator {
	
	
	public void fadeAnimation(Activity a)
	{
		a.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}
	
	
}
