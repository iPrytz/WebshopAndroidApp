package se.prytz.webshop;

import java.util.List;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import com.example.test1.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class BaseActivity extends Activity {

	static final String ip = "192.168.1.22";
	public static List<Cookie> cookies;
	private static MenuItem login;
	private static MenuItem logout;
	private static MenuItem createProducts;
	private static MenuItem createCategories;
	public static boolean loggedIn;

	private void tToast(String s) {
		Context context = getApplicationContext();
		Toast toast = Toast.makeText(context, s, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 100);
		toast.show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		login = menu.findItem(R.id.login_menu);
		logout = menu.findItem(R.id.logout_menu);
		createProducts = menu.findItem(R.id.create_products_menu);
		createCategories = menu.findItem(R.id.create_categories_menu);

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);

		if (loggedIn) {
			login.setVisible(false);
			logout.setVisible(true);
			createCategories.setVisible(true);
			createProducts.setVisible(true);
		} else {
			login.setVisible(true);
			logout.setVisible(false);
			createCategories.setVisible(false);
			createProducts.setVisible(false);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.login_menu:
			Intent intent = new Intent(getApplicationContext(),
					LoginActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;
		case R.id.logout_menu:
			new LogOutTask().execute();
			intent = new Intent(getApplicationContext(), LoginActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;
		case R.id.list_products_menu:
			startActivity(new Intent(this, ListProductsActivity.class));
			break;
		case R.id.list_categories_menu:
			startActivity(new Intent(this, ListCategoriesActivity.class));
			break;
		case R.id.create_products_menu:
			startActivity(new Intent(this, CreateProductsActivity.class));
			break;
		case R.id.create_categories_menu:
			startActivity(new Intent(this, CreateCategoriesActivity.class));
			break;
		default:
			startActivity(new Intent(this, LoginActivity.class));
			break;
		}

		return true;
	}

	class LogOutTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			try {
				HttpPost post = new HttpPost("http://" + ip
						+ ":9000/mobile/logout");

				DefaultHttpClient client = new DefaultHttpClient();

				if (cookies != null) {
					for (Cookie cookie : cookies) {
						client.getCookieStore().addCookie(cookie);
					}
				}

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
			loggedIn = false;
			tToast(result);
		}
	}

}
