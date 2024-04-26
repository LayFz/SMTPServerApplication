package fakesmtp.core;

import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;


public enum Configuration {
	INSTANCE;

	// 默认配置
	private static final String CONFIG_FILE = "/configuration.properties";
	// 用户配置
	private static final String USER_CONFIG_FILE = "/fakeSMTP.properties";
	private final Properties config = new Properties();

	/**
	 * Opens the "{@code configuration.properties}" file and maps data.
	 */
	Configuration() {
		InputStream in = getClass().getResourceAsStream(CONFIG_FILE);
		try {
			// 加载默认设置
			config.load(in);
			in.close();
			// 重写用户设置
			loadFromUserProfile();
		} catch (IOException e) {
			LoggerFactory.getLogger(Configuration.class).error("", e);
		}
	}

	/**
	 * Returns the value of a specific entry from the "{@code configuration.properties}" file.
	 *
	 * @param key a string representing the key from a key/value couple.
	 * @return the value of the key, or an empty string if the key was not found.
	 */
	public String get(String key) {
		if (config.containsKey(key)) {
			return config.getProperty(key);
		}
		return "";
	}

	/**
	 * Sets the value of a specific entry.
	 *
	 * @param key a string representing the key from a key/value couple.
	 * @param value the value of the key.
	 */
	public void set(String key, String value) {
		config.setProperty(key, value);
	}

	/**
	 * Saves configuration to file.
	 *
	 * @param file file to save configuration.
	 * @throws IOException
	 */
	public void saveToFile(File file) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		try {
			config.store(fos, "Last user settings");
		} finally {
			IOUtils.closeQuietly(fos);
		}
	}

	/**
	 * Saves configuration to the {@code .fakesmtp.properties} file in user profile directory.
	 * Calls {@link Configuration#saveToFile(File)}.
	 *
	 * @throws IOException
	 */
	public void saveToUserProfile() throws IOException {
		saveToFile(new File(System.getProperty("user.home"), USER_CONFIG_FILE));
	}

	/**
	 * Loads configuration from file.
	 *
	 * @param file file to load configuration.
	 * @return INSTANCE.
	 * @throws IOException
	 */
	public Configuration loadFromFile(File file) throws IOException {
		if (file.exists() && file.canRead()) {
			FileInputStream fis = new FileInputStream(file);
			try {
				config.load(fis);
			} finally {
				IOUtils.closeQuietly(fis);
			}
		}
		return INSTANCE;
	}

	/**
	 * Loads configuration from the .fakesmtp.properties file in user profile directory.
	 * Calls {@link Configuration#loadFromFile(File)}.
	 *
	 * @return INSTANCE.
	 * @throws IOException
	 */
	public Configuration loadFromUserProfile() throws IOException {
		/** 	获取用户目录
				在 Windows 操作系统中，主目录通常位于 C:\Users\<用户名>，其中 <用户名> 是当前登录用户的用户名。
				在 macOS 操作系统中，主目录通常位于 /Users/<用户名>。
				在 Linux 操作系统中，主目录通常位于 /home/<用户名>。
		 **/
		return loadFromFile(new File(System.getProperty("user.home"), USER_CONFIG_FILE));
	}
}
