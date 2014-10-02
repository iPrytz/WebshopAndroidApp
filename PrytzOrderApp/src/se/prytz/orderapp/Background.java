package se.prytz.orderapp;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;

import com.example.prytzorderapp.R;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;

public class Background extends IntentService {
	private static int currentOrdersNr;
	private static int notificationId = 1;

	public Background() {
		super("");
	}

	private class getOrdersFromDb extends AsyncTask<Void, Void, JSONArray> {
		@Override
		protected JSONArray doInBackground(Void... params) {
			String response = "";
			try {
				DefaultHttpClient client = new DefaultHttpClient();
				response = client.execute(new HttpGet("http://" + Variables.ip
						+ ":9000/mobile/order"), new BasicResponseHandler());
				return new JSONArray(response);
			} catch (Exception e) {
				throw new RuntimeException("Couldn't update orders"
						+ e.getMessage());
			}
		}

		@Override
		protected void onPostExecute(JSONArray orders) {
			super.onPostExecute(orders);

			if (orders.length() != currentOrdersNr) {
				makeNewNotification(orders.length());
				Intent broadcast = new Intent();
				broadcast.setAction("ORDER_SERVICE");
				sendBroadcast(broadcast);
				currentOrdersNr = orders.length();

			}
		}
	}

	private void makeNewNotification(int newOrdersFromDb) {
		Intent intent = new Intent(this, StartActivity.class);
		PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
		Builder notificationBuilder = new NotificationCompat.Builder(this);
		notificationBuilder.setSmallIcon(R.drawable.ic_launcher);
		notificationBuilder.setContentTitle("New Orders Recieved!");
		notificationBuilder.setAutoCancel(true);
		notificationBuilder.addAction(R.drawable.ic_launcher, "Go to orders!",
				pi);
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify(notificationId++,
				notificationBuilder.build());
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			while (true) {
				new getOrdersFromDb().execute();

				Thread.sleep(5000);
			}
		} catch (Exception e) {
			Log.e("Exception", e.getMessage());
		}
	}

}
