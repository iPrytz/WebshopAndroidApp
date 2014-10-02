package se.prytz.webshop;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.example.test1.R;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		// public void clickEvent(View v) {
		// // startActivity(new Intent(this, LoginActivity2.class));
		// // tToast("Login Correct");
		// }

		Button button = (Button) findViewById(R.id.button_login);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				LoginTask loginTask = new LoginTask(ip);

				EditText username = (EditText) findViewById(R.id.user_name);
				EditText password = (EditText) findViewById(R.id.user_password);

				boolean allInputIsOk = true;

				if (username.getText().toString().matches("")) {
					username.setError("Empty");
					allInputIsOk = false;
				} else {
					loginTask.setUsername(username.getText().toString());
				}
				if (password.getText().toString().matches("")) {
					password.setError("Empty");
					allInputIsOk = false;
				} else {
					loginTask.setPassword(password.getText().toString());
				}
				if (allInputIsOk) {
					loginTask.execute();
				}

			}
		});

	}

	private void tToast(String s) {
		Context context = getApplicationContext();
		Toast toast = Toast.makeText(context, s, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 100);
		toast.show();
	}

	class LoginTask extends AsyncTask<Void, Void, String> {
		private String ip;
		private String password;
		private String username;

		public LoginTask(String ip) {
			this.ip = ip;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		@Override
		protected String doInBackground(Void... params) {
			try {

				HttpPost post = new HttpPost("http://" + ip
						+ ":9000/mobile/login");
				DefaultHttpClient client = new DefaultHttpClient();

				List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
				nameValuePair.add(new BasicNameValuePair("email", username));
				nameValuePair.add(new BasicNameValuePair("password", password));
				post.setEntity(new UrlEncodedFormEntity(nameValuePair));

				String response = client.execute(post,
						new BasicResponseHandler());

				cookies = client.getCookieStore().getCookies();

				return response;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		protected void onPostExecute(String result) {
			tToast(result);

			if (result.equals("Logged in")) {
				loggedIn = true;

				Intent intent = new Intent(getApplicationContext(),
						ListProductsActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		}

	}

}
