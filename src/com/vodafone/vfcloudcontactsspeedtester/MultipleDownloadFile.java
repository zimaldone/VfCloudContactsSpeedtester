package com.vodafone.vfcloudcontactsspeedtester;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;
//import com.vodafone.lib.sec.SecLib;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class MultipleDownloadFile extends AsyncTask<String, Integer, String> {
	int downloadedSize;
	int bufferLength;
	Vector<Long> timeSpentMS = new Vector<Long>();
	Vector<BigDecimal> speedMbits = new Vector<BigDecimal>();
	long firstConnectionTime = 0;
	private boolean downloadResult = false;
	// public ArrayList<String> SequenceFile = new ArrayList<String>(); // this
	// is the file to be downloaded

	private int progress_bar_Values[] = new int[3];
	private Activity activity; // activity is defined as a global variable in
								// your AsyncTask

	// ************************************************************
	public MultipleDownloadFile(Activity activity1) {
		activity = activity1;
		;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		MainActivity.mProgressDialog.setProgress(0);
	}

	@Override
	protected String doInBackground(String... urlToDownload) {
		try {

			long freespace = Variables.getExternalAvailableSpaceInMB();
			// Log.v("Download Engine"," free space -> " + freespace +
			// " - path: " + Variables.getExternalpath());

			if (freespace > 90) { // check for free space in SDCard
				for (int i = 0; i < Variables.DOWNLOAD_ITERATIONS; i++) {
					// Log.v("Download Engine",
					// files_to_download_vector.get(SequenceFile.get(i)) +
					// " - size: " + files_to_download_vector.size() +
					// " - SequenceIteration: " + i);
					progress_bar_Values[0] = 0;
					progress_bar_Values[1] = i * 100 / Variables.DOWNLOAD_ITERATIONS;
					publishProgress((int) progress_bar_Values[0]);
					// Set File
					File SDCardRoot = Environment.getExternalStorageDirectory();
					File file = new File(SDCardRoot, "tmp_to_delete.dat");
					FileOutputStream fileOutput = new FileOutputStream(file);
					// setup object for connection
					URL url = new URL(urlToDownload[0]);
					HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
					urlConnection.setRequestMethod("GET");
					urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:7.0.1) Gecko/20100101 Firefox/7.0.12011-10-16 20:23:00");
					// urlConnection.addRequestProperty("Cache-Control", "no-cache");
					urlConnection.setUseCaches(false);
					urlConnection.setDoOutput(true);
					// get initial timestamp

					// String trnsID="";

					// SmapiEvent SmpEvnt = new SmapiEvent();
					// try {
					// //trnsID= SmpEvnt.logRequestEvents("request", "file n." +
					// Integer.toString(j) , "vf-backend", "HTTP/1.1",
					// url.toString(), "GET", "");
					// //SecLib.flush();
					// //Log.v("DOWNLOAD",SmpEvnt.logRequestEvents("request",
					// "file n." + Integer.toString(j) , "vf-backend",
					// "HTTP/1.1", url.toString(), "GET", ""));
					//
					// } catch (MalformedURLException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// }

					long initialTime = System.currentTimeMillis();
					// connect
					urlConnection.connect();
					if (i == 0) {
						firstConnectionTime = System.currentTimeMillis() - initialTime;
					}
					// Stream used for reading the data from internet
					InputStream inputStream = urlConnection.getInputStream();

					// is is the total size of the file which we are downloading
					long totalSize = urlConnection.getContentLength();

					byte[] buffer = new byte[1024];
					bufferLength = 0;
					downloadedSize = 0;

					while ((bufferLength = inputStream.read(buffer)) > 0) {
						publishProgress((int) (downloadedSize * 100 / totalSize));
						fileOutput.write(buffer, 0, bufferLength);
						downloadedSize += bufferLength;
					}

					urlConnection.disconnect();

					long endTime = System.currentTimeMillis();
					inputStream.close();
					publishProgress(100);
					fileOutput.flush();
					fileOutput.close();
					file.delete();
					timeSpentMS.add(i, endTime - initialTime);

					BigDecimal totalSizeMbit = new BigDecimal(totalSize * 8);
					totalSizeMbit = totalSizeMbit.divide(new BigDecimal(1048576));

					BigDecimal timeSpentSeconds = new BigDecimal(timeSpentMS.elementAt(i));
					timeSpentSeconds = timeSpentSeconds.divide(new BigDecimal(1000));

					speedMbits.add(i, (totalSizeMbit.divide(timeSpentSeconds,2, RoundingMode.HALF_UP))); // TO BE REVIEWED
					Log.v("download engine", urlToDownload[0] + " - size: "
							+ totalSizeMbit + " - Seconds: " + timeSpentSeconds
							+ " - Mbit/s: " + speedMbits.elementAt(i));

				}
				downloadResult = true;
			} else {
				downloadResult = false;
				showError("There's not sufficient space to download a temporary file\n Please free some space in SDCard");
			}
			// SecLib.flush();
			// Log.v("TIME",timeSpentstr + " ms spent");

		} catch (final MalformedURLException e) {
			showError("Error : MalformedURLException " + e);
			e.printStackTrace();
		} catch (final IOException e) {
			showError("Error : IOException " + e);
			e.printStackTrace();
		} catch (final Exception e) {
			showError("Generic Error: " + e);
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
		MainActivity.mProgressDialog
				.setSecondaryProgress(progress_bar_Values[1]);
	}

	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		long SumTime = 0;
		float SumMbits = 0;
		int i;

		try {
			if (downloadResult) {

				for (i = 0; i < timeSpentMS.size(); i++) {

					if (timeSpentMS.elementAt(i) != null
							|| timeSpentMS.elementAt(i) > 0) {
						SumTime += timeSpentMS.elementAt(i);
						SumMbits += speedMbits.elementAt(i).floatValue();
						// Log.v("FOUND",Long.toString(SumTime) + " - " +
						// Integer.toString(i));
					}
				}

				long deviation = CalculateStandardDeviation(timeSpentMS);
				Log.v("Download Engine", "SumTime: " + Long.toString(SumTime)
						+ " mdev: " + Long.toString(deviation) + " N. Req."
						+ Integer.toString(i) + " SumMbits: " + SumMbits);

				MainActivity.textTimeEstablishConnection
						.setText("Connection established in "
								+ Long.toString(firstConnectionTime) + " ms");

				BigDecimal Mbits = new BigDecimal(SumMbits);
				BigDecimal iteration = new BigDecimal(i);
				Mbits = Mbits.divide(iteration, 2, RoundingMode.HALF_UP);

				MainActivity.textTimeAvgCloud.setText("Time to execute "
						+ Integer.toString(i) + " requests \n"
						+ Long.toString(SumTime / i) + " ms average - "
						+ Long.toString(deviation) + " ms Mdev"
						+ "\n\n Average Speed -> " + Mbits + " Mbits");
			}
			MainActivity.mProgressDialog.dismiss();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			MainActivity.mProgressDialog.dismiss();
			showError("Generic Error: " + e);
			e.printStackTrace();
		}
	}

	private long CalculateStandardDeviation(Vector<Long> occurrencies) {
		long deviation = 0;
		try {
			int i;
			Statistics stats = new Statistics();

			for (i = 0; i < timeSpentMS.size(); i++) {
				stats.addValue(timeSpentMS.elementAt(i));
				// Log.v("Occurencies",Long.toString(timeSpentMS.elementAt(i)));
			}
			deviation = (long) stats.calculateStandardDeviation();
			// double expected = Math.sqrt(3.5);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			showError("Generic Error: " + e);
			e.printStackTrace();
		}
		return deviation;
	}

	void showError(final String err) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(activity, err, Toast.LENGTH_LONG).show();
			}
		});

	}
}