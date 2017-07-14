package com.artytheartist;
 
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
 
public class QuizWebActivity extends Activity {
 
	private WebView webView;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.infowebview);
		final ProgressDialog pd = ProgressDialog.show(this, "", getText(R.string.loading),true);
		webView = (WebView) findViewById(R.id.infoview);
	    webView.setWebViewClient(new WebViewClient() {
	            @Override
	            public void onPageFinished(WebView view, String url) {
	                if(pd.isShowing()&&pd!=null)
	                {
	                    pd.dismiss();
	                }
	            }
	        });		
		
	    // Quiz requires javascript to work properly
		webView.getSettings().setJavaScriptEnabled(true);
		//  Url used to open quiz
		webView.loadUrl("http://artytheartist.com/jsquizexample.html");

		setTitle(R.string.app_name);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    // Check if the key event was the Back button and if there's history
	    if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
	        webView.goBack();
	        return true;
	    }
	    // If it wasn't the Back key or there's no web page history, bubble up to the default
	    // system behavior (probably exit the activity)
	    return super.onKeyDown(keyCode, event);

	}
	


}