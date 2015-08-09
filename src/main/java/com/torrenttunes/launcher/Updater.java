package com.torrenttunes.launcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Updater {

	static final Logger log = LoggerFactory.getLogger(Updater.class);




	public static void checkForUpdate() {

		log.info("Checking for update...");
		String htmlStr = httpGetString(DataSources.FETCH_LATEST_RELEASE_URL());
		//		log.info(DataSources.FETCH_LATEST_RELEASE_URL());
		//		log.info(htmlStr);

		String tagName = htmlStr.split("/tchoulihan/torrenttunes-client/releases/tag/")[1].split("\"")[0];

		String foundVersion = null;
		

		try {
			foundVersion = readFile(DataSources.INSTALLED_VERSION_FILE()).trim();
		} catch (IOException e) {
			log.info("Installation " + DataSources.INSTALLED_VERSION_FILE() + " Not found.");
		}

		log.info("Current Tag #: " + foundVersion);
		log.info("Latest Tag #: " + tagName);

		if (foundVersion == null || !foundVersion.equals(tagName)) {
			downloadAndInstallJar(tagName);

		} else {
			log.info("No updates found");

		}





	}
	public static void downloadAndInstallJar(String tagName) {
		log.info("Update found, Downloading...");

		try {
			// Download the jar
			String downloadUrl = "https://github.com/tchoulihan/torrenttunes-client/releases/download/" + tagName + 
					"/torrenttunes-client.jar";
//			log.info(downloadUrl);

			httpSaveFile(downloadUrl, DataSources.TEMP_JAR_PATH());

			Thread.sleep(1000);
			log.info("sleeping for a second...");

			// Run the shortcut install script, recopying the source files, and only installing
			log.info("Installing update...");
			ArrayList<String> cmd = new ArrayList<String>();
			cmd.add("java");
			cmd.add("-jar");
			cmd.add(DataSources.TEMP_JAR_PATH());
			cmd.add("-recopy");
			cmd.add("-installonly");
			//			cmd.add("&>log.out");
			//			cmd.add("");

			//			String cmd = "java -jar " + DataSources.TEMP_JAR_PATH() + " -recopy -installonly";
			ProcessBuilder b = new ProcessBuilder(cmd);
			b.inheritIO();
			Process p = b.start();
			
		
			p.waitFor();
			
			new File(DataSources.TEMP_JAR_PATH()).delete();
			log.info("Deleted temporary install jar: " + DataSources.TEMP_JAR_PATH());
		

			//			
			//			
			////			 Delete the temp download filefile
			//			new File(DataSources.TEMP_JAR_PATH()).delete();
			//			
			//			cmd.clear();
			//			cmd.add("java");
			//			cmd.add("-jar");
			//			cmd.add(DataSources.JAR_FILE());
			////			cmd = "java -jar " + DataSources.JAR_FILE();
			//			ProcessBuilder b2 = new ProcessBuilder(cmd);
			//			b2.start();
			//		
			//			System.exit(0);




		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static final String httpGetString(String url) {
		String res = "";
		try {
			URL externalURL = new URL(url);

			URLConnection yc = externalURL.openConnection();
			//			yc.setRequestProperty("User-Agent", USER_AGENT);

			BufferedReader in = new BufferedReader(
					new InputStreamReader(
							yc.getInputStream()));
			String inputLine;

			while ((inputLine = in.readLine()) != null) 
				res+="\n" + inputLine;
			in.close();

			return res;
		} catch(IOException e) {}
		return res;
	}

	public static String readFile(String path) throws IOException {
		String s = null;

		byte[] encoded;

		encoded = java.nio.file.Files.readAllBytes(Paths.get(path));

		s = new String(encoded, Charset.defaultCharset());

		return s;
	}
	

	public static final void httpSaveFile(String urlString, String savePath) throws IOException {
//		log.info("url string = " + urlString);

		URL url = new URL(urlString);

		URLConnection uc = url.openConnection();

		InputStream input = uc.getInputStream();
		byte[] buffer = new byte[4096];
		int n = - 1;

		OutputStream output = new FileOutputStream(savePath);
		while ( (n = input.read(buffer)) != -1) {

			output.write(buffer, 0, n);

		}
		output.close();

	}

}
