package com.artytheartist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

	public class SplashActivity extends Activity { 

	@Override 
	protected void onCreate(Bundle savedInstanceState) { 
	super.onCreate(savedInstanceState); 
	setContentView(R.layout.changepass);

//	setContentView(R.layout.splash_activity_view); 

//	Handler handler = new Handler(); 
//
//	handler.postDelayed(new Runnable() 
//	{ 
//	@Override 
//	public void run() { 
//
//	Intent intent = new Intent(SplashActivity.this, MainActivity.class); 
//	SplashActivity.this.startActivity(intent); 
	finish(); 
//	} 
//
//	}, 2000); 
	}

}
