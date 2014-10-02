package se.prytz.webshop;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.example.test1.R;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreateCategoriesActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_categories);

		Button button = (Button) findViewById(R.id.button_submit_category);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				CreateCategoryOnServer createCategoryOnServer = new CreateCategoryOnServer(
						ip);

				EditText name = (EditText) findViewById(R.id.category_name);
				EditText responsible = (EditText) findViewById(R.id.category_responsible);

				boolean allInputIsOk = true;

				if (name.getText().toString().matches("")) {
					name.setError("Empty");
					allInputIsOk = false;
				} else {
					createCategoryOnServer.setName(name.getText().toString());
				}
				if (responsible.getText().toString().matches("")) {
					name.setError("Empty");
					allInputIsOk = false;
				} else {
					createCategoryOnServer.setResponsible(responsible.getText()
							.toString());
				}

				if (allInputIsOk) {
					createCategoryOnServer.execute();
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

	class CreateCategoryOnServer extends AsyncTask<Void, Void, Boolean> {
		private String ip;
		private String categoryName;
		private String responsible;

		public CreateCategoryOnServer(String ip) {
			this.ip = ip;
		}

		public void setName(String name) {
			this.categoryName = name;
		}

		public void setResponsible(String responsible) {
			this.responsible = responsible;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				HttpPut put = new HttpPut("http://" + ip
						+ ":9000/mobile/categories/add");

				List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
				nameValuePair.add(new BasicNameValuePair("category",
						categoryName));
				nameValuePair.add(new BasicNameValuePair("staff", responsible));

				put.setEntity(new UrlEncodedFormEntity(nameValuePair));

				new DefaultHttpClient()
						.execute(put, new BasicResponseHandler());
				return true;

			} catch (Exception e) {
				Log.e("Error creating product: ", e.getMessage());
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean success) {
			if (success == true) {
				tToast("Category " + categoryName + " created!");
			} else {
				tToast("Error!");
			}
		}

	}

}
