package se.prytz.webshop;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class CreateProductsActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.create_products);

		new GetCategoriesFromServer().execute();

		Button button = (Button) findViewById(R.id.button_submit_product);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				CreateProductOnServer createProductOnServer = new CreateProductOnServer(
						ip);

				EditText name = (EditText) findViewById(R.id.product_name);
				EditText price = (EditText) findViewById(R.id.product_price);
				EditText rrp = (EditText) findViewById(R.id.product_RRP);
				EditText description = (EditText) findViewById(R.id.product_description);
				Spinner categories = (Spinner) findViewById(R.id.product_categories);

				boolean allInputIsOk = true;

				if (name.getText().toString().matches("")) {
					name.setError("Empty");
					allInputIsOk = false;
				} else {
					createProductOnServer.setName(name.getText().toString());
				}
				if (price.getText().toString().matches("")) {
					price.setError("Empty");
					allInputIsOk = false;
				} else {
					createProductOnServer.setPrice(price.getText().toString());
				}
				if (rrp.getText().toString().matches("")) {
					rrp.setError("Empty");
					allInputIsOk = false;
				} else {
					createProductOnServer.setRRP(rrp.getText().toString());
				}
				if (description.getText().toString().matches("")) {
					description.setError("Empty");
					allInputIsOk = false;
				} else {
					createProductOnServer.setDescription(description.getText()
							.toString());
				}
				if (categories.getSelectedItem().toString().matches("")) {
					description.setError("Empty");
					allInputIsOk = false;
				} else {
					createProductOnServer.setCategory(categories
							.getSelectedItemId());
				}
				if (allInputIsOk) {
					createProductOnServer.execute();
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

	class GetCategoriesFromServer extends AsyncTask<Void, Void, JSONArray> {
		@Override
		protected JSONArray doInBackground(Void... params) {
			try {
				String response = new DefaultHttpClient().execute(new HttpGet(
						"http://" + ip + ":9000/mobile/category"),
						new BasicResponseHandler());
				return new JSONArray(response);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		protected void onPostExecute(JSONArray result) {
			Spinner spinner = (Spinner) findViewById(R.id.product_categories);
			spinner.setAdapter(new CategoryAdapter(result));
		}
	}

	class CategoryAdapter extends BaseAdapter implements SpinnerAdapter {
		private JSONArray categories;

		public CategoryAdapter(JSONArray categories) {
			this.categories = categories;
		}

		@Override
		public int getCount() {
			return categories.length();
		}

		@Override
		public Object getItem(int index) {
			try {
				return categories.getJSONObject(index);
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public long getItemId(int index) {
			try {
				return categories.getJSONObject(index).getInt("id");
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public View getView(int index, View convertView, ViewGroup parent) {
			TextView textView = new TextView(getApplicationContext());
			try {
				JSONObject category = categories.getJSONObject(index);
				textView.setText(category.getString("name"));
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
			textView.setTextSize(18);
			return textView;
		}
	}

	class CreateProductOnServer extends AsyncTask<Void, Void, String> {
		private String ip;
		private String name;
		private String rrp;
		private String price;
		private String description;
		private long categoryId;

		public CreateProductOnServer(String ip) {
			this.ip = ip;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public void setCategory(Long categoryId) {
			this.categoryId = categoryId;
		}

		public void setRRP(String rrp) {
			this.rrp = rrp;
		}

		public void setPrice(String price) {
			this.price = price;
		}

		@Override
		protected String doInBackground(Void... params) {
			String result = "";

			try {
				HttpPut put = new HttpPut("http://" + ip
						+ ":9000/mobile/products/add");

				List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
				nameValuePair.add(new BasicNameValuePair("name", name));
				nameValuePair.add(new BasicNameValuePair("price", price));
				nameValuePair.add(new BasicNameValuePair("rrp", rrp));
				nameValuePair.add(new BasicNameValuePair("desc", description));
				nameValuePair.add(new BasicNameValuePair("category", categoryId
						+ ""));

				DefaultHttpClient client = new DefaultHttpClient();

				if (cookies != null) {
					for (Cookie cookie : cookies) {
						client.getCookieStore().addCookie(cookie);
					}
				}

				put.setEntity(new UrlEncodedFormEntity(nameValuePair));

				result = client.execute(put, new BasicResponseHandler());
				return result;

			} catch (Exception e) {
				Log.e("Error creating product: ", e.getMessage());
				return "Error creating product: " + e.getMessage();
			}
		}

		@Override
		protected void onPostExecute(String result) {

			tToast(result);

		}

	}

}
