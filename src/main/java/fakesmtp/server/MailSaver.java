package fakesmtp.server;

import fakesmtp.core.ArgsHandler;
import fakesmtp.core.Configuration;
import fakesmtp.core.I18n;
import fakesmtp.model.EmailModel;
import fakesmtp.model.UIModel;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class MailSaver extends Observable {

	private static final Logger LOGGER = LoggerFactory.getLogger(MailSaver.class);
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	// This can be a static variable since it is Thread Safe
	private static final Pattern SUBJECT_PATTERN = Pattern.compile("^Subject: (.*)$");

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyhhmmssSSS");

	/**
	 * Saves incoming email in file system and notifies observers.
	 *
	 * @param from the user who send the email.
	 * @param to the recipient of the email.
	 * @param data an InputStream object containing the email.
	 */
	public void saveEmailAndNotify(String from, String to, InputStream data) {
		List<String> relayDomains = UIModel.INSTANCE.getRelayDomains();
		System.out.println(relayDomains);

		if (relayDomains != null) {
			boolean matches = false;
			for (String domain : relayDomains) {
				if (to.endsWith(domain)) {
					matches = true;
					break;
				}
			}

			if (!matches) {
				LOGGER.debug("Destination {} doesn't match relay domains", to);
				return;
			}
		}

		// 模拟存储，实际生产中会保存到数据库，与用户绑定文件，这里可以模拟接受用户的地址模拟文件保存
//		- 用户1
//				- 收件箱
//				   - 邮件1
//				   - 邮件2
//				- 已发送
//				   - 邮件3
//				   - 邮件4
//				- 草稿箱
//		- 用户2
//				- 收件箱
//				   - 邮件5
//				- 已发送
//				- 草稿箱

		EmailModel model = new EmailModel();
		model.setFrom(from);
		model.setTo(to);
		System.out.println(to);
		String mailContent = convertStreamToString(data);
		model.setSubject(getSubjectFromStr(mailContent));
		model.setEmailStr(mailContent);

		synchronized (getLock()) {
			String filePath = saveEmailToFile(mailContent,to);

			model.setReceivedDate(new Date());
			model.setFilePath(filePath);

			setChanged();
			notifyObservers(model);
		}
	}

	/**
	 * Deletes all received emails from file system.
	 */
	public void deleteEmails() {
		Map<Integer, String> mails = UIModel.INSTANCE.getListMailsMap();
		if (ArgsHandler.INSTANCE.memoryModeEnabled()) {
			return;
		}
		for (String value : mails.values()) {
			File file = new File(value);
			if (file.exists()) {
				try {
					if (!file.delete()) {
						LOGGER.error("Impossible to delete file {}", value);
					}
				} catch (SecurityException e) {
					LOGGER.error("", e);
				}
			}
		}
	}

	/**
	 * Returns a lock object.
	 * <p>
	 * This lock will be used to make the application thread-safe, and
	 * avoid receiving and deleting emails in the same time.
	 * </p>
	 *
	 * @return a lock object <i>(which is actually the current instance of the {@code MailSaver} object)</i>.
	 */
	public Object getLock() {
		return this;
	}

	/**
	 * Converts an {@code InputStream} into a {@code String} object.
	 * <p>
	 * The method will not copy the first 4 lines of the input stream.<br>
	 * These 4 lines are SubEtha SMTP additional information.
	 * </p>
	 *
	 * @param is the InputStream to be converted.
	 * @return the converted string object, containing data from the InputStream passed in parameters.
	 */
	private String convertStreamToString(InputStream is) {
		final long lineNbToStartCopy = 4; // Do not copy the first 4 lines (received part)
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName(I18n.UTF8)));
		StringBuilder sb = new StringBuilder();

		String line;
		long lineNb = 0;
		try {
			while ((line = reader.readLine()) != null) {
				if (++lineNb > lineNbToStartCopy) {
					sb.append(line).append(LINE_SEPARATOR);
				}
			}
		} catch (IOException e) {
			LOGGER.error("", e);
		}
		return sb.toString();
	}

	/**
	 * Saves the content of the email passed in parameters in a file.
	 *
	 * @param mailContent the content of the email to be saved.
	 * @return the path of the created file.
	 */
	private String saveEmailToFile(String mailContent,String from) {
		if (ArgsHandler.INSTANCE.memoryModeEnabled()) {
			return null;
		}
//		String filePath = String.format("%s%s%s", UIModel.INSTANCE.getSavePath(), File.separator,
//				dateFormat.format(new Date()));
		String filePath = String.format("%s%s%s", UIModel.INSTANCE.getSavePath(), File.separator,
				from.substring(0,3)+dateFormat.format(new Date()));// 取用户的前三位作为索引
		// Create file
		int i = 0;
		File file = null;
		while (file == null || file.exists()) {
			String iStr;
			if (i++ > 0) {
				iStr = Integer.toString(i);
			} else {
				iStr = "";
			}
			file = new File(filePath + iStr + Configuration.INSTANCE.get("emails.suffix"));
		}

		// Copy String to file
		try {
			FileUtils.writeStringToFile(file, mailContent);
		} catch (IOException e) {
			// If we can't save file, we display the error in the SMTP logs
			Logger smtpLogger = LoggerFactory.getLogger(org.subethamail.smtp.server.Session.class);
			smtpLogger.error("Error: Can't save email: {}", e.getMessage());
		}
		return file.getAbsolutePath();
	}

	/**
	 * Gets the subject from the email data passed in parameters.
	 *
	 * @param data a string representing the email content.
	 * @return the subject of the email, or an empty subject if not found.
	 */
	private String getSubjectFromStr(String data) {
		try {
			BufferedReader reader = new BufferedReader(new StringReader(data));

			String line;
			while ((line = reader.readLine()) != null) {
				 Matcher matcher = SUBJECT_PATTERN.matcher(line);
				 if (matcher.matches()) {
					 return matcher.group(1);
				 }
			}
		} catch (IOException e) {
			LOGGER.error("", e);
		}
		return "";
	}
}
