package com.digitalglass.main.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
	private static final String FILE_LOCATION = "config.properties";
	private static Config configInstance = null;
	private Properties configFile;
	
	protected Config() {
		// Only to defeat instantiation
	}

	public static Config getInstance() {
		if (configInstance == null) {
			configInstance = new Config();
			
			configInstance.configFile = new Properties();
			try {
				configInstance.configFile.load(new FileInputStream(FILE_LOCATION));
			} catch (Exception e) {
				System.err.println("Unable to load configuration file at location " + FILE_LOCATION);
				System.exit(-1);
			}		
		}
		
		return configInstance;
	}
	
	public String getProperty(String key) {
		String value = this.configFile.getProperty(key);
		return value;
	}
	
	public void saveProperty(String key, String value) throws FileNotFoundException, IOException {
		this.configFile.setProperty(key, value);
		
		this.configFile.store(new FileOutputStream(FILE_LOCATION), null);
	}
}
