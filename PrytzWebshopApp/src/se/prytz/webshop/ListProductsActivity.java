package se.prytz.webshop;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.test1.R;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ListProductsActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_products);

		GetProducts listProducts = new GetProducts();
		listProducts.execute();

	}

	private void tToast(String s) {
		Context context = getApplicationContext();
		Toast toast = Toast.makeText(context, s, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 100);
		toast.show();
	}

	class GetProducts extends AsyncTask<Void, Void, JSONArray> {

		@Override
		protected JSONArray doInBackground(Void... params) {
			String myResponse = "";
			try {
				myResponse = new DefaultHttpClient().execute(new HttpGet(
						"http://" + ip + ":9000/mobile/product"),
						new BasicResponseHandler());
				return new JSONArray(myResponse);

			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		protected void onPostExecute(JSONArray result) {
			ListView listView = (ListView) findViewById(R.id.list_products_view);
			listView.setAdapter(new ProductAdapter(result));

		}
	}

	class ProductAdapter extends BaseAdapter {

		private JSONArray products;

		public ProductAdapter(JSONArray products) {
			this.products = products;
		}

		@Override
		public int getCount() {
			return products.length();
		}

		@Override
		public Object getItem(int index) {
			try {
				return products.getJSONObject(index);
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public long getItemId(int index) {
			return index;
		}

		@Override
		public View getView(int index, View convertView, ViewGroup parent) {

			RelativeLayout productView = (RelativeLayout) getLayoutInflater()
					.inflate(R.layout.list_products_item, parent, false);

			try {

				final JSONObject product = products.getJSONObject(index);
				TextView name = (TextView) productView
						.findViewById(R.id.list_products_name);
				TextView description = (TextView) productView
						.findViewById(R.id.list_products_description);
				TextView categories = (TextView) productView
						.findViewById(R.id.list_products_categories);
				name.setText(product.getString("name"));
				description.setText(product.getString("description"));

				Button buttonBuy = (Button) productView
						.findViewById(R.id.button_buy);

				String categoriesString = "";
				JSONArray categoriesArray = product.getJSONArray("categories");

				for (int i = 0; i < categoriesArray.length(); i++) {

					categoriesString += categoriesArray.getJSONObject(i)
							.get("name").toString();

					if (i < categoriesArray.length() - 1) {
						categoriesString += ", ";
					}
				}
				categories.setText(categoriesString);

				buttonBuy.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						CreateOrder createOrder = new CreateOrder();
						try {
							createOrder.execute(product.get("id").toString());

						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				});

			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			return productView;
		}

	}

	class CreateOrder extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			try {
				String response = "";
				HttpPost post = new HttpPost("http://" + ip
						+ ":9000/mobile/place-order");

				List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
				nameValuePair
						.add(new BasicNameValuePair("productId", params[0]));
				nameValuePair.add(new BasicNameValuePair("productQt", "1"));

				DefaultHttpClient client = new DefaultHttpClient();

				post.setEntity(new UrlEncodedFormEntity(nameValuePair));

				if (cookies != null) {
					for (Cookie cookie : cookies) {
						client.getCookieStore().addCookie(cookie);
					}
				}

				response = client.execute(post, new BasicResponseHandler());

				cookies = client.getCookieStore().getCookies();

				return response;
				// return params[0];

			} catch (Exception e) {
				Log.e("Error ordering product: ", e.getMessage());
				e.printStackTrace();
				return "Error!";
			}

		}

		@Override
		protected void onPostExecute(String result) {
			tToast(result);
		}

	}

}
