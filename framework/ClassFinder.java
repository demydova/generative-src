//////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////
//Author: 		Ganna Demydova
//Description: 	class scanning of package for classes
//Version		00.04 12.03.2017
//////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////

package framework;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ClassFinder {

	// secondary variables
	private static final char PKG_SEPARATOR = '.';
	private static final char DIR_SEPARATOR = '/';
	private static final String CLASS_FILE_SUFFIX = ".class";
	private static final String BAD_PACKAGE_ERROR = "Unable to get resources from path '%s'. Are you sure the package '%s' exists?";

	// main parsing function, looking for classes within given path/package
	public static List<String> find(String Dir, String scannedPackage) {
		File scannedDir = new File(Dir + scannedPackage);
		List<String> classes = new ArrayList<String>();
		// list all the files within given path and add to the list, if the file
		// is class
		for (File file : scannedDir.listFiles()) {
			classes.addAll(find(file, scannedPackage));
		}
		return classes;
	}

	// supporting parsing function
	private static List<String> find(File file, String scannedPackage) {
		List<String> classes = new ArrayList<String>();
		String resource = scannedPackage + PKG_SEPARATOR + file.getName();
		// if file is directory - recursive call
		if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				classes.addAll(find(child, resource));
			}
			// if file is not directory - add the name of classe
		} else if (resource.endsWith(CLASS_FILE_SUFFIX)) {
			int endIndex = resource.length() - CLASS_FILE_SUFFIX.length();
			String className = resource.substring(0, endIndex);
			classes.add(className);
		}
		return classes;
	}
}
