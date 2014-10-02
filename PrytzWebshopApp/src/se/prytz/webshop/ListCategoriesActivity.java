package se.prytz.webshop;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.example.test1.R;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ListCategoriesActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_categories);

		GetProducts listProducts = new GetProducts();
		listProducts.execute();

	}

	class GetProducts extends AsyncTask<Void, Void, JSONArray> {

		@Override
		protected JSONArray doInBackground(Void... params) {
			String myResponse = "";
			try {
				myResponse = new DefaultHttpClient().execute(new HttpGet(
						"http://" + ip + ":9000/mobile/category"),
						new BasicResponseHandler());
				return new JSONArray(myResponse);

			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		protected void onPostExecute(JSONArray result) {
			ListView listView = (ListView) findViewById(R.id.list_categories_view);
			listView.setAdapter(new ProductAdapter(result));

		}
	}

	class ProductAdapter extends BaseAdapter {

		private JSONArray categories;

		public ProductAdapter(JSONArray categories) {
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
			return index;
		}

		@Override
		public View getView(int index, View convertView, ViewGroup parent) {
			// View listProductItem =
			// getLayoutInflater().inflate(R.layout.list_products_item, parent,
			// false);
			// TextView productName = (TextView)
			// listProductItem.findViewById(R.id.list_products_name);
			// TextView productDescription = (TextView)
			// listProductItem.findViewById(R.id.list_products_description);
			RelativeLayout categoryView = (RelativeLayout) getLayoutInflater()
					.inflate(R.layout.list_categories_item, parent, false);

			try {
				JSONObject category = categories.getJSONObject(index);
				TextView name = (TextView) categoryView
						.findViewById(R.id.list_category_name);
				name.setText(category.getString("name"));

			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			return categoryView;
		}

	}

}
