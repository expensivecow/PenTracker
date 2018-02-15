package utils;

import java.io.FileInputStream;
import java.util.Properties;

public class Config {
	private Properties configFile;
	
	public Config(String fileLocation) {
		configFile = new Properties();
		try {
			configFile.load(new FileInputStream(fileLocation));
		} catch (Exception e) {
			System.err.println("Unable to load configuration file at location " + fileLocation);
			System.exit(-1);
		}
	}
	
	public String getProperty(String key) {
		String value = this.configFile.getProperty(key);
		return value;
	}
}
