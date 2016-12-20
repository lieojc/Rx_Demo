package utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import skycaster.cdradio_rxdemo.MainActivity;
import skycaster.cdradio_rxdemo.R;

/**
 *
 /**
 * Created by 郑力嘉 on 2016/12/14.
 */
public class SplashScreen extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		Handler x = new Handler();
		x.postDelayed(new splashhandler(), 2000);

	}
	class splashhandler implements Runnable{

		public void run() {
			startActivity(new Intent(getApplication(),MainActivity.class));
			SplashScreen.this.finish();
		}

	}
}