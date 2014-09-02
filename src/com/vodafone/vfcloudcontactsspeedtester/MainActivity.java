package com.vodafone.vfcloudcontactsspeedtester;

import java.util.HashMap;
import java.util.Map.Entry;

//import com.vodafone.lib.sec.SecLib;
//import com.vodafone.lib.sec.Configuration.Env;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.vodafone.vfcloudcontactsspeedtester.R;

public class MainActivity extends Activity implements OnClickListener {

	public static ProgressDialog mProgressDialog;
	Dialog dialog;
	public static TextView textTimeAvgCloud;
	public static TextView textTimeEstablishConnection;
	public static TextView textPingCloud;
	public static TextView textConnectivity;
	private static TextView textTitle;

	private RadioGroup RadioFileSize;
	private RadioGroup RadioProposition;
	private RadioButton RadioButton_Cloud;
	private RadioButton RadioButton_Dropbox;
	private RadioButton RadioButton_Contacts;
	private RadioButton RadioButton_file_1;
	private RadioButton RadioButton_file_2;
	private RadioButton RadioButton_file_3;
	private RadioButton RadioButton_file_4;

	private Button buttonStartTest;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/** Initialise the SecLib API. **/
		// SecLib.init(this, Env.REFERENCE, true, true);
		// SecLib.setPackageName(this, "com.vodafone.addressbook");
		// //SecLib.setLogEnabled(true);
		// SecLib.setRoamingEnabled(true);
		// SecLib.setRegion("262");
		// SecLib.setUser("android", "Cloud&ContactsSpeedtester");

		textTimeAvgCloud = (TextView) findViewById(R.id.TextTimeAvgCloud);
		textTimeEstablishConnection = (TextView) findViewById(R.id.timetoestablishconnection);
		textPingCloud = (TextView) findViewById(R.id.TextPingCloud);
		textConnectivity = (TextView) findViewById(R.id.connectivitystatus);
		textTitle = (TextView) findViewById(R.id.TextTitle);
		RadioFileSize = (RadioGroup) findViewById(R.id.RadioFileSize);
		RadioProposition = (RadioGroup) findViewById(R.id.RadioProposition);
		RadioButton_Cloud = (RadioButton) findViewById(R.id.RadioButton_Cloud);
		RadioButton_Dropbox = (RadioButton) findViewById(R.id.RadioButton_Dropbox);
		RadioButton_Contacts = (RadioButton) findViewById(R.id.RadioButton_Contacts);
		RadioButton_file_1 = (RadioButton) findViewById(R.id.RadioButton_file_1);
		RadioButton_file_2 = (RadioButton) findViewById(R.id.RadioButton_file_2);
		RadioButton_file_3 = (RadioButton) findViewById(R.id.RadioButton_file_3);
		RadioButton_file_4 = (RadioButton) findViewById(R.id.RadioButton_file_4);
		buttonStartTest = (Button) findViewById(R.id.buttonRunTest);

		// *****
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setIndeterminate(false);
		mProgressDialog.setMax(100);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

		RadioButton_Cloud.setChecked(true);
		RadioButton_file_1.setChecked(true);
		RadioButton_Contacts.setOnClickListener(onRadioButtonClick);
		RadioButton_Cloud.setOnClickListener(onRadioButtonClick);
		RadioButton_Dropbox.setOnClickListener(onRadioButtonClick);
		buttonStartTest.setOnClickListener(this);

		Variables.InitializeFilesVector(); // Initialization of
											// contacts_files_vector -
											// cloud_files_vector -
											// dropbox_files_vector

		String VersionApp;
		try {
			VersionApp = this.getPackageManager().getPackageInfo(
					this.getPackageName(), 0).versionName;
			textTitle
					.setText("Download Speed test \n Vodafone Cloud & Contacts\n v. " + VersionApp);
			
			Variables.BEARER = WhichBearer(this);
			
		} catch (NameNotFoundException e) {
			showError("Error : NameNotFoundException " + e);
			e.printStackTrace();
		} catch (final Exception e) {
			showError("Generic Error: " + e);
			e.printStackTrace();
		}

	}

	@Override
	public void onClick(View view) {

		String proposition = (String) ((RadioButton) RadioProposition
				.findViewById(RadioProposition.getCheckedRadioButtonId()))
				.getText();
		String file_size = (String) ((RadioButton) RadioFileSize
				.findViewById(RadioFileSize.getCheckedRadioButtonId()))
				.getText();

		if (haveInternet(this)) {
			Variables.BEARER = WhichBearer(this);
			StartTest(proposition, file_size);
		} else {
			Toast.makeText(this, " No connectivity ", Toast.LENGTH_SHORT)
					.show();
		}

	}

	OnClickListener onRadioButtonClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			RadioButton rb = (RadioButton) v;

			if (rb.getText().equals("Contacts")) {
				RadioButton_file_3.setClickable(false);
				RadioButton_file_4.setClickable(false);
				RadioButton_file_1.setChecked(true);
				Toast.makeText(MainActivity.this,
						"25 & 80 MB not available for Contacts",
						Toast.LENGTH_SHORT).show();
			} else if (rb.getText().equals("Dropbox")) {
				Toast.makeText(MainActivity.this,
						"ICMP Traffic test might not work for Dropbox",
						Toast.LENGTH_LONG).show();
				RadioButton_file_3.setClickable(true);
				RadioButton_file_4.setClickable(true);
			} else {
				RadioButton_file_3.setClickable(true);
				RadioButton_file_4.setClickable(true);
			}

		}
	};

	public void StartTest(String Proposition, String file_size) {
		String url = "";
		String ip = "";
		HashMap<String, String> tmp_vector = new HashMap<String, String>();

		mProgressDialog.setMessage("Testing ICMP timing");
		textPingCloud.setText("Evaluation avg time for ICMP traffic ("
				+ Variables.NUMBER_OF_PING + " pdu)");

		if (Proposition.equals("Contacts")) {
			tmp_vector = Variables.contacts_files_vector;
			ip = Variables.IP_OM;
		} else if (Proposition.equals("Cloud")) {
			tmp_vector = Variables.cloud_files_vector;
			ip = Variables.IP_SNCR;
		} else if (Proposition.equals("Dropbox")) {
			tmp_vector = Variables.dropbox_files_vector;
			ip = Variables.IP_DROPBOX;
		}

		for (Entry<String, String> me : tmp_vector.entrySet()) {
			if (me.getKey().endsWith(file_size)) {
				url = me.getValue();
				break;
			}
		}

		Log.v("URL", url + " - " + ip);

		MultiplePing multPing = new MultiplePing(this, url);
		multPing.execute(ip);

	}

	public static boolean haveInternet(Context ctx) {

		NetworkInfo info = (NetworkInfo) ((ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();

		if (info == null || !info.isConnected()) {
			textConnectivity
					.setText("There's no connectivity, please check your settings");
			return false;
		}
		if (info.isRoaming()) {
			// here is the roaming option you can change it if you want to
			// disable internet while roaming, just return false
			textConnectivity
					.setText("You´re connected and in Roaming via 3G bearer");
			return true;
		}
		textConnectivity.setText("You´re connected");
		return true;
	}

	public static String WhichBearer(Context ctx) {
		String bearer = "";
		ConnectivityManager conMan = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		// mobile
		State mobile = conMan.getNetworkInfo(0).getState();
		// wifi
		State wifi = conMan.getNetworkInfo(1).getState();

		if (mobile == NetworkInfo.State.CONNECTED
				|| mobile == NetworkInfo.State.CONNECTING) {
			bearer = "3G";
			textConnectivity.setText("You´re connected via 3G/mobile bearer");
		} else if (wifi == NetworkInfo.State.CONNECTED
				|| wifi == NetworkInfo.State.CONNECTING) {
			bearer = "WiFi";
			textConnectivity.setText("You´re connected via WiFi bearer");
		}
		return bearer;

	}

	void showError(final String err) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(MainActivity.this, err, Toast.LENGTH_LONG)
						.show();
			}
		});

	}
}
