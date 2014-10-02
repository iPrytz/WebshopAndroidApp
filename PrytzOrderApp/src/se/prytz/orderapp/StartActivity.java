package se.prytz.orderapp;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.prytzorderapp.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class StartActivity extends Activity {
	private OrdersAdapter ordersAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.e("Start", "oncreate");
		setContentView(R.layout.main_layout);

		registerReceiver(new OrderBrodcastReceiver(), new IntentFilter(
				"ORDER_SERVICE"));
		Intent bgService = new Intent(this, Background.class);
		startService(bgService);

		new GetOrders().execute();
	}

	public class OrderBrodcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			new GetOrders().execute();
		}

	}

	private class GetOrders extends AsyncTask<Void, Void, JSONArray> {
		@Override
		protected JSONArray doInBackground(Void... params) {
			String response = "";
			try {
				response = new DefaultHttpClient().execute(new HttpGet(
						"http://" + Variables.ip + ":9000/mobile/order"),
						new BasicResponseHandler());
				return new JSONArray(response);
			} catch (Exception e) {
				throw new RuntimeException("Could not conect to server!"
						+ e.getMessage());
			}
		}

		@Override
		protected void onPostExecute(JSONArray orders) {
			super.onPostExecute(orders);

			if (ordersAdapter == null) {
				ListView ordersListView = (ListView) findViewById(R.id.lw_orders);
				ordersAdapter = new OrdersAdapter(orders);
				ordersListView.setAdapter(ordersAdapter);
			} else {
				ordersAdapter.setNewOrdersDataSet(orders);
				ordersAdapter.notifyDataSetChanged();
				toast("Orders updated!");
			}
		}
	}

	private class OrdersAdapter extends BaseAdapter {
		private JSONArray orders;

		public OrdersAdapter(JSONArray orders) {
			this.orders = orders;
		}

		public void setNewOrdersDataSet(JSONArray orders) {
			this.orders = orders;
		}

		@Override
		public int getCount() {
			return orders.length();
		}

		@Override
		public Object getItem(int index) {
			try {
				return orders.getJSONObject(index);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public long getItemId(int index) {
			return index;
		}

		@Override
		public View getView(int index, View convertView, ViewGroup parent) {
			View orderListItem = getLayoutInflater().inflate(
					R.layout.order_item, parent, false);
			TextView orderIdTW = (TextView) orderListItem
					.findViewById(R.id.tw_order_id);
			TextView userTW = (TextView) orderListItem
					.findViewById(R.id.tw_user_info);
			TextView addressTW = (TextView) orderListItem
					.findViewById(R.id.tw_address);
			TextView dateTW = (TextView) orderListItem
					.findViewById(R.id.tw_date);
			TextView costTW = (TextView) orderListItem
					.findViewById(R.id.tw_cost);
			TextView productsTW = (TextView) orderListItem
					.findViewById(R.id.tw_products);

			try {
				JSONObject order = orders.getJSONObject(index);
				orderIdTW.setText(order.getInt("id") + "");
				dateTW.setText("Date: " + order.getString("date"));
				JSONObject user = order.getJSONObject("user");
				userTW.setText(user.getString("email") + "\n"
						+ user.getString("firstname") + " "
						+ user.getString("surname"));
				addressTW.setText(user.getString("streetAddress") + "\n"
						+ user.getString("postcode") + " "
						+ user.getString("town"));
				costTW.setText("Total cost: " + order.getDouble("cost"));
				JSONArray productQt = order.getJSONArray("products");
				String productNames = "Products:\n";
				for (int i = 0; i < productQt.length(); i++) {
					JSONObject productQuantity = productQt.getJSONObject(i);
					JSONObject product = productQuantity
							.getJSONObject("product");
					productNames += productQuantity.getInt("quantity") + " "
							+ product.getString("name");
					if (i < productQt.length() - 1) {
						productNames += "\n";
					}
				}
				productsTW.setText(productNames);
			} catch (JSONException e) {
				Log.e("Error", e.getMessage());
			}
			return orderListItem;
		}
	}

	private void toast(String text) {
		Toast.makeText(getBaseContext(), text, Toast.LENGTH_SHORT).show();
	}
}
