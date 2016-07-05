package com.mit.utils;

import java.io.File;
import java.net.URL;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.mit.utils.ResourceUtils;

public class ConfigUtils {

	private static CompositeConfiguration CONFIG_PROPERTIES = null;

	public static final String ENV = System.getProperty("app_env",
			"development");

	private static Lock _lock = new ReentrantLock();
	private static String _confDir = "";

	private ConfigUtils() {
		_confDir = System.getProperty("confdir", "conf");
		init();
	}

	private static void init() {

		String fileName = ENV + ".properties";
		try {
			File file = null;
			if ((_confDir != null) && (!_confDir.isEmpty())) {
				file = new File(_confDir + "/" + fileName);
				System.out.println("Configuration load directory: "
						+ file.getPath());

				Configuration configProps = loadConfiguration(file);
				CONFIG_PROPERTIES = new CompositeConfiguration();
				CONFIG_PROPERTIES.addConfiguration(configProps);
			} else {
				URL resourceURL = ResourceUtils.getResourceAsURL(fileName);
				System.out.println("Configuration load directory: "
						+ resourceURL);

				Configuration configProps = new PropertiesConfiguration();

				if (resourceURL != null) {
					configProps = new PropertiesConfiguration(resourceURL);
				}

				CONFIG_PROPERTIES = new CompositeConfiguration();
				CONFIG_PROPERTIES.addConfiguration(configProps);
			}
		} catch (Exception e) {
			throw new IllegalStateException(
					"ConfigurationException, Unable to load: " + fileName);
		}
	}

//	public static void initLog4j() {
//		String fileName = "log4j-" + ENV + ".properties";
//		if (_confDir != null && !_confDir.isEmpty()) {
//			PropertyConfigurator.configure(_confDir + "/" + fileName);
//			System.out.println("Configuration load directory: " + _confDir
//					+ "/" + fileName);
//
//		} else {
//			URL resourceURL = ResourceUtils.getResourceAsURL(fileName);
//			System.out.println("Configuration load directory: " + resourceURL);
//
//			if (resourceURL != null) {
//				PropertyConfigurator.configure(resourceURL);
//			}
//		}
//	}

	private static Configuration loadConfiguration(File propsFile)
			throws Exception {
		if (propsFile.exists()) {
			return new PropertiesConfiguration(propsFile);
		}
		return new PropertiesConfiguration();
	}

	public static CompositeConfiguration getConfig() {
		if(CONFIG_PROPERTIES == null) {
			_lock.lock();
			try {
				if(CONFIG_PROPERTIES == null) {
					_confDir = System.getProperty("confdir", "conf");
					//initLog4j();
					init();
				}
			} finally {
				_lock.unlock();
			}
		}

		return CONFIG_PROPERTIES;
	}

	public static String genKey(String configName, String key) {
		return configName + "." + key;
	}
}
