package com.vodafone.vfcloudcontactsspeedtester;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

public class SingleDownloadFile extends AsyncTask<String, Integer, String> {
	int downloadedSize;
	ProgressDialog mProgressDialog;
	TextView textms;
	private String timeSpentstr;
	public String ExecutionTimeCall;

	public SingleDownloadFile(ProgressDialog pd) {
		mProgressDialog = pd;
	}

	public SingleDownloadFile(ProgressDialog mProgressDialog2, TextView textms2) {
		// TODO Auto-generated constructor stub
		mProgressDialog = mProgressDialog2;
		textms = textms2;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mProgressDialog.show();
		mProgressDialog.setProgress(0);

	}

	@Override
	protected String doInBackground(String... urlToDownload) {
		try {

			File SDCardRoot = Environment.getExternalStorageDirectory();
			File file = new File(SDCardRoot, "speedtest.file");
			FileOutputStream fileOutput = new FileOutputStream(file);
			// Log.v("URL",urlToDownload[0]);
			URL url = new URL(urlToDownload[0]);

			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection
					.setRequestProperty(
							"User-Agent",
							"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:7.0.1) Gecko/20100101 Firefox/7.0.12011-10-16 20:23:00");
			urlConnection.setDoOutput(true);
			// get initial timestamp
			long initialTime = System.currentTimeMillis();
			// connect
			urlConnection.connect();

			// Stream used for reading the data from the internet
			InputStream inputStream = urlConnection.getInputStream();

			// this is the total size of the file which we are downloading
			int totalSize = urlConnection.getContentLength();

			byte[] buffer = new byte[1024];
			int bufferLength = 0;

			while ((bufferLength = inputStream.read(buffer)) > 0) {
				publishProgress((int) (downloadedSize * 100 / totalSize));
				fileOutput.write(buffer, 0, bufferLength);
				downloadedSize += bufferLength;
			}
			urlConnection.disconnect();
			fileOutput.flush();
			fileOutput.close();
			long endTime = System.currentTimeMillis();
			file.delete();
			long timeSpent = endTime - initialTime;
			timeSpentstr = Long.toString(timeSpent);

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
		mProgressDialog.setProgress(progress[0]);
		// mProgressDialog.setSecondaryProgress(1);
	}

	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		mProgressDialog.dismiss();
		ExecutionTimeCall = timeSpentstr;
		// Log.v("test",ExecutionTimeCall);
		textms.setText("" + timeSpentstr + " ms spent");
	}
}
