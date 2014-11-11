package com.example.facebook_integration_android;

import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import org.json.JSONTokener;
import fb.android.DialogError;
import fb.android.Facebook;
import fb.android.FacebookError;
import fb.android.SessionStore;
import fb.android.Facebook.DialogListener;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

	Button fb_btn,fb_logout;
	Facebook mFacebook;
	private static final String[] PERMISSIONS = new String[] {
		"publish_stream", "read_stream", "offline_access", "email" };
	private static final String APP_ID = "1392931717639402";
	ProgressDialog dialog;
	String fb_id,name,mfbemail,mfirstname,mlastname,access_token,access_expires,musername;
	Bitmap fbpicture;
	TextView username,fname,lname,email,fbid;
	ImageView fb_profile_photo;
	RelativeLayout layout;
	int what = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		fb_btn=(Button)findViewById(R.id.button1);
		fb_logout=(Button)findViewById(R.id.logout);
		username=(TextView)findViewById(R.id.username);
		fname=(TextView)findViewById(R.id.fname);
		lname=(TextView)findViewById(R.id.lname);
		email=(TextView)findViewById(R.id.email);
		fbid=(TextView)findViewById(R.id.fbid);
		fb_profile_photo=(ImageView)findViewById(R.id.fb_profile_photo);
		layout=(RelativeLayout)findViewById(R.id.layout);
		
		layout.setVisibility(View.INVISIBLE);
		fb_logout.setVisibility(View.INVISIBLE);

		mFacebook = new Facebook(APP_ID);
		fb_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onFacebookClick();
			}
		});

		fb_logout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				fbLogout();
			}
		});

	}

	private void onFacebookClick() 
	{
		mFacebook.authorize(this, PERMISSIONS, -1,new FbLoginDialogListener());
	}
	private final class FbLoginDialogListener implements DialogListener {
		public void onComplete(Bundle values) {

			fb_btn.setClickable(true);
			getFbName();
		}

		public void onFacebookError(FacebookError error) {

		}

		public void onError(DialogError error) {

		}

		public void onCancel() {
		}
	}

	private void getFbName()
	{

		new Thread() {
			@Override
			public void run() {

				int what = 1;

				try {
					String me = mFacebook.request("me");
					Log.e("meeeeeeeeee", me);

					JSONObject jsonObj = (JSONObject) new JSONTokener(me)
					.nextValue();

					fb_id=jsonObj.getString("id");
					name = jsonObj.getString("name");
					mfbemail = jsonObj.getString("email");
					mfirstname=jsonObj.getString("first_name");
					mlastname=jsonObj.getString("last_name");
					String user_id=fb_id;

					access_token=mFacebook.getAccessToken();

					Log.d("facebook_imageid",user_id);
					URL url = new URL("https://graph.facebook.com/"+user_id+"/picture?type=large");
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					HttpURLConnection.setFollowRedirects(true);
					conn.setInstanceFollowRedirects(true);  
					fbpicture = BitmapFactory.decodeStream(conn.getInputStream());

					access_token = mFacebook.getAccessToken();

					Log.e("response", jsonObj.toString());
					Log.e("email", mfbemail);
					Log.e("fb_id", fb_id);
					Log.e("name",name);
					Log.e("first_name", mfirstname);
					Log.e("last_name",mlastname);


					setText(username,name);
					setText(fname,mfirstname);
					setText(lname,mlastname);
					setText(email,mfbemail);
					setText(fbid,fb_id);

					fb_profile_photo.setImageBitmap(fbpicture);


				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

		}.start();
		
		layout.setVisibility(View.VISIBLE);
		fb_logout.setVisibility(View.VISIBLE);
		fb_btn.setVisibility(View.INVISIBLE);
	}
	private void setText(final TextView text,final String value){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				text.setText(value);
			}
		});
	}

	private void fbLogout() {

		new Thread() {
			@Override
			public void run() {
				SessionStore.clear(MainActivity.this);
				try {
					what = 1;
					mFacebook.logout(MainActivity.this);

				} catch (Exception ex) {
					ex.printStackTrace();
				}

				mHandler.sendMessage(mHandler.obtainMessage(what));
			}
		}.start();
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			//mProgress.dismiss();

			if (what == 1) 
			{
				layout.setVisibility(View.INVISIBLE);
				fb_logout.setVisibility(View.INVISIBLE);
				fb_btn.setVisibility(View.VISIBLE);
				
				Toast.makeText(getApplicationContext(), "Successfully loged out from your facebook account", Toast.LENGTH_LONG).show();
				
			} else {

			}
		}
	};
}
