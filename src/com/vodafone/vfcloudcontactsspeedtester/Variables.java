package com.vodafone.vfcloudcontactsspeedtester;

import java.util.HashMap;

import android.os.Environment;
import android.os.StatFs;

;

public class Variables {

	public final static String IP_OM = "89.185.58.189";
	public final static String IP_SNCR = "93.174.170.89";
	public final static String IP_DROPBOX = "dl-web.dropbox.com";
	public final static int NUMBER_OF_PING = 5;

	public static String BEARER = "";
	public static int DOWNLOAD_ITERATIONS = 5;

	public final static long SIZE_KB = 1024L;
	public final static long SIZE_MB = SIZE_KB * SIZE_KB;
	public final static long SIZE_GB = SIZE_KB * SIZE_KB * SIZE_KB;

	private final static String om_file_test_32kb = "http://staging.adressbuch.vodafone.de/nettst/test32kb.dat"; // 0
	private final static String om_file_test_64kb = "http://staging.adressbuch.vodafone.de/nettst/test64kb.dat"; // 1
	private final static String om_file_test_128kb = "http://staging.adressbuch.vodafone.de/nettst/test128kb.dat"; // 2
	private final static String om_file_test_256kb = "http://staging.adressbuch.vodafone.de/nettst/test256kb.dat"; // 3
	private final static String om_file_test_512kb = "http://staging.adressbuch.vodafone.de/nettst/test512kb.dat"; // 4
	private final static String om_file_test_1MB = "http://staging.adressbuch.vodafone.de/nettst/test1mb.dat"; // 5
	private final static String om_file_test_2MB = "http://staging.adressbuch.vodafone.de/nettst/test2mb.dat"; // 6
	private final static String om_file_test_5MB = "http://staging.adressbuch.vodafone.de/nettst/test5mb.dat"; // 7
	private final static String om_file_test_10MB = "http://staging.adressbuch.vodafone.de/nettst/test10mb.dat";
	private final static String om_file_test_25MB = "http://staging.adressbuch.vodafone.de/nettst/test10mb.dat";// 8
	private final static String om_file_test_80MB = "http://staging.adressbuch.vodafone.de/nettst/test10mb.dat";// 8
	private final static String om_file_test_3G = "http://staging.adressbuch.vodafone.de/nettst/test3G.dat"; // 9
	private final static String om_file_test_WiFi = "http://staging.adressbuch.vodafone.de/nettst/testWiFi.dat"; // 10

	// private final static String sncr_file_test_2MB =
	// "https://cloud.vodafone.de/web/static/201406101205/webclient/platform.js";
	private final static String sncr_file_test_2MB = "https://cloud.vodafone.de/web/static/test-files/2Mb.7z";
	private final static String sncr_file_test_10MB = "https://cloud.vodafone.de/web/static/test-files/10Mb.7z";
	private final static String sncr_file_test_25MB = "https://cloud.vodafone.de/web/static/test-files/25Mb.7z";
	private final static String sncr_file_test_80MB = "https://cloud.vodafone.de/web/static/test-files/80MB_Test_File.rar";

	private final static String dropbox_file_test_2MB = "https://dl.dropboxusercontent.com/u/8539340/vf_test/2Mb.7z";
	private final static String dropbox_file_test_10MB = "https://dl.dropboxusercontent.com/u/8539340/vf_test/10Mb.7z";
	private final static String dropbox_file_test_25MB = "https://dl.dropboxusercontent.com/u/8539340/vf_test/25Mb.7z";
	private final static String dropbox_file_test_80MB = "https://dl.dropboxusercontent.com/u/16601205/80MB_Test_File.rar";

	public static HashMap<String, String> contacts_files_vector = new HashMap<String, String>();
	public static HashMap<String, String> cloud_files_vector = new HashMap<String, String>();
	public static HashMap<String, String> dropbox_files_vector = new HashMap<String, String>();

	/**
	 * @return it initialises the vectors for files download. it doesn´t retunr
	 *         any value
	 */
	public static void InitializeFilesVector() {

		contacts_files_vector.put("om_file_test_32Kb", om_file_test_32kb);
		contacts_files_vector.put("om_file_test_64Kb", om_file_test_64kb);
		contacts_files_vector.put("om_file_test_128Kb", om_file_test_128kb);
		contacts_files_vector.put("om_file_test_256Kb", om_file_test_256kb);
		contacts_files_vector.put("om_file_test_512Kb", om_file_test_512kb);
		contacts_files_vector.put("om_file_test_1MB", om_file_test_1MB);
		contacts_files_vector.put("om_file_test_2MB", om_file_test_2MB);
		contacts_files_vector.put("om_file_test_5MB", om_file_test_5MB);
		contacts_files_vector.put("om_file_test_10MB", om_file_test_10MB);
		contacts_files_vector.put("om_file_test_25MB", om_file_test_25MB);
		contacts_files_vector.put("om_file_test_80MB", om_file_test_80MB);
		contacts_files_vector.put("om_file_test_3G", om_file_test_3G);
		contacts_files_vector.put("om_file_test_WiFi", om_file_test_WiFi);

		cloud_files_vector.put("sncr_file_test_2MB", sncr_file_test_2MB);
		cloud_files_vector.put("sncr_file_test_10MB", sncr_file_test_10MB);
		cloud_files_vector.put("sncr_file_test_25MB", sncr_file_test_25MB);
		cloud_files_vector.put("sncr_file_test_80MB", sncr_file_test_80MB);

		dropbox_files_vector
				.put("dropbox_file_test_2MB", dropbox_file_test_2MB);
		dropbox_files_vector.put("dropbox_file_test_10MB",
				dropbox_file_test_10MB);
		dropbox_files_vector.put("dropbox_file_test_25MB",
				dropbox_file_test_25MB);
		dropbox_files_vector.put("dropbox_file_test_80MB",
				dropbox_file_test_80MB);
	}

	/**
	 * @return Number of bytes available on external storage
	 */
	public static long getExternalAvailableSpaceInBytes() {
		long availableSpace = -1L;
		try {
			StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
					.getPath());
			availableSpace = (long) stat.getAvailableBlocks()
					* (long) stat.getBlockSize();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return availableSpace;
	}

	/**
	 * @return Number of kilo bytes available on external storage
	 */
	public static long getExternalAvailableSpaceInKB() {
		return getExternalAvailableSpaceInBytes() / SIZE_KB;
	}

	/**
	 * @return Number of Mega bytes available on external storage
	 */
	public static long getExternalAvailableSpaceInMB() {
		return getExternalAvailableSpaceInBytes() / SIZE_MB;
	}

	/**
	 * @return giga bytes of bytes available on external storage
	 */
	public static long getExternalAvailableSpaceInGB() {
		return getExternalAvailableSpaceInBytes() / SIZE_GB;
	}

	/**
	 * @return Total number of available blocks on external storage
	 */
	public static long getExternalStorageAvailableBlocks() {
		long availableBlocks = -1L;
		try {
			StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
					.getPath());
			availableBlocks = stat.getAvailableBlocks();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return availableBlocks;
	}

	/**
	 * @return Total number of available blocks on external storage
	 */
	public static String getExternalpath() {
		String path_ext_dir = "";
		try {
			path_ext_dir = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return path_ext_dir;
	}
}
