package com.torrenttunes.launcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

public class Main {

	static Logger log = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

	@Option(name="-uninstall",usage="Uninstall torrenttunes-client.(WARNING, this deletes your library)")
	private boolean uninstall;

	@Option(name="-recopy",usage="Recopies your source folders")
	private boolean recopy;

	@Option(name="-installonly",usage="Only installs it, doesn't run it")
	private boolean installOnly;

	@Option(name="-loglevel", usage="Sets the log level [INFO, DEBUG, etc.]")     
	private String loglevel = "INFO";

	@Option(name="-sharedirectory", usage="Scans a directory to share")     
	private String shareDirectory = null;


	public void doMain(String[] args) throws IOException, InterruptedException  {

		parseArguments(args);

		// Copy launcher jar to .torrenttunes-client dir
		copyLauncherJar();

		// Check to see if the correct version exists, download the jar or update if necessary
		Updater.checkForUpdate();

		launchTorrentTunesClient(args);



	}

	private void launchTorrentTunesClient(String[] args) throws IOException, InterruptedException {
		ArrayList<String> cmd = new ArrayList<String>();
		cmd.add("java");
		cmd.add("-Djava.library.path=" + DataSources.LIBRARY_PATHS());
		cmd.add("-jar");
		cmd.add(DataSources.JAR_FILE());

		for (String arg : args) {cmd.add(arg);}

		ProcessBuilder b = new ProcessBuilder(cmd);
		b.inheritIO();
		Process p = b.start();


		p.waitFor();

	}

	private static void copyLauncherJar() {

		if (!DataSources.THIS_JAR().equals(DataSources.LAUNCHER_JAR())) {
			log.info("Copying launcher jar to " + DataSources.LAUNCHER_JAR() + " ...");
			// create the parents if necessary
			new File(DataSources.LAUNCHER_JAR()).mkdirs();
			try {
				log.info("This jar: " + DataSources.THIS_JAR() + " Launcher Jar: " + DataSources.LAUNCHER_JAR());
				java.nio.file.Files.copy(Paths.get(DataSources.THIS_JAR()), Paths.get(DataSources.LAUNCHER_JAR()), 
						StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void parseArguments(String[] args) {
		CmdLineParser parser = new CmdLineParser(this);

		try {

			parser.parseArgument(args);

		} catch (CmdLineException e) {
			// if there's a problem in the command line,
			// you'll get this exception. this will report
			// an error message.
			System.err.println(e.getMessage());
			System.err.println("java -jar torrenttunes-launcher [options...] arguments...");
			// print the list of available options
			parser.printUsage(System.err);
			System.err.println();
			System.exit(0);


			return;
		}
	}


	public static void main(String[] args) throws IOException, InterruptedException {
		new Main().doMain(args);

	}





}
