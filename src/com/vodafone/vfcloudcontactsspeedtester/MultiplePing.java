package com.vodafone.vfcloudcontactsspeedtester;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.Vector;
//import com.vodafone.lib.sec.SecLib;
import android.app.Activity;
import android.os.AsyncTask;

public class MultiplePing extends AsyncTask<String, Integer, String> {
	int downloadedSize;
	private String urlToDownload;
	private String timeSpentstr;
	private String mdev;
	private static Activity activity;
	Vector<String> line = new Vector<String>();

	public MultiplePing(Activity activity1, String urlDownload) {
		activity = activity1;
		urlToDownload = urlDownload;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		MainActivity.mProgressDialog.show();
		MainActivity.mProgressDialog.setProgress(0);
		MainActivity.mProgressDialog.setSecondaryProgress(0);
	}

	@Override
	protected String doInBackground(String... IpToPing) {
		String TimesParsed;

		try {
			Process p = new ProcessBuilder("sh").redirectErrorStream(true)
					.start();

			DataOutputStream os = new DataOutputStream(p.getOutputStream());
			os.writeBytes("ping -c " + Variables.NUMBER_OF_PING + " "
					+ IpToPing[0] + '\n');
			os.flush();

			// Close the terminal
			os.writeBytes("exit\n");
			os.flush();

			// read ping reply
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					p.getInputStream()));

			int i = 0;
			line.add(i, reader.readLine());

			while (line.elementAt(i) != null) {
				// bufferArray[i]=line;
				// Log.v("Test Ping " + Integer.toString(i), line.elementAt(i));
				int IndexSeq = line.elementAt(i).indexOf("seq=");
				int IndexTTL = line.elementAt(i).indexOf("ttl=");

				if (line.elementAt(i).length() > 0 && IndexSeq > 0) {
					int IndexPerc = Integer.parseInt(line.elementAt(i)
							.substring(IndexSeq + 4, IndexTTL).trim());
					publishProgress((int) (IndexPerc * 100 / Variables.NUMBER_OF_PING));
					// Log.v("Test Ping ", line);
				}

				int SummaryLine = line.elementAt(i).indexOf("min/avg/max/mdev");
				int IndexMDEV = line.elementAt(i).indexOf("/mdev");
				int IndexMS = line.elementAt(i).indexOf("ms");

				if (line.elementAt(i).length() > 0 && SummaryLine > 0) {
					TimesParsed = line.elementAt(i)
							.substring(IndexMDEV + 8, IndexMS).trim();
					String[] ArrTiming = TimesParsed.split("/");
					timeSpentstr = ArrTiming[1];
					mdev = ArrTiming[3];
					// Log.v("Timing","min->" + ArrTiming[0] + " avg-> " +
					// ArrTiming[1] + " max-> " + ArrTiming[2] + " Mdev-> " +
					// ArrTiming[3]);
					// Log.v("Test Ping ", TimesParsed);

				}

				i++;
				line.add(i, reader.readLine());
			}
			// Log.v("Test Ping - Summary Times",
			// Integer.toString(line.size()));

		} catch (final MalformedURLException e) {
			// showError("Error : MalformedURLException " + e);
			e.printStackTrace();
		} catch (final IOException e) {
			// showError("Error : IOException " + e);
			e.printStackTrace();
		} catch (final Exception e) {
			// showError("Error : Please check your internet connection " + e);
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		super.onProgressUpdate(progress);
		if (!MainActivity.mProgressDialog.isShowing()) {
			MainActivity.mProgressDialog.show();
		}
		MainActivity.mProgressDialog.setProgress(progress[0]);
	}

	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		MainActivity.textPingCloud
				.setText("Evaluation avg time for ICMP traffic ("
						+ Variables.NUMBER_OF_PING + " pdu)\n" + timeSpentstr
						+ " ms +/-" + mdev);
		MainActivity.mProgressDialog.dismiss();

		// SmapiEvent SmpEvnt = new SmapiEvent();
		// try {
		// SmpEvnt.logClientInternalEvents("client", "ping_test", true,
		// "ping_test", "main_activity", "multiple-ping-tag", line);
		// SecLib.flush();
		// } catch (MalformedURLException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		MainActivity.mProgressDialog.setMessage("Testing network capabilities");
		MultipleDownloadFile multdownlfile = new MultipleDownloadFile(activity);
		multdownlfile.execute(urlToDownload);
	}

}
