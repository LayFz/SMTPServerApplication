package fakesmtp.client.model;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.spi.DirStateFactory.Result;

import fakesmtp.client.utilitiy.MyBase64;

public class Mail {
	String subject = "";
	String from = "";
	Vector<String> to_list = new Vector<String>();
	String content = "";
	String tag = "";

	public Mail(String subject, String from, Vector<String> to_list,
			String content) {
		super();
		this.subject = subject;
		this.from = from;
		this.to_list = to_list;
		this.content = content;
	}

	public Mail(Vector<String> lines) {
		super();
		initByLines(lines);
	}


	private void initByLines(Vector<String> lines) {
		boolean decodeWithBase64 = false;
		boolean isPreLine = false;
		boolean hasContent = false;
		String contentType = "";
		int firstFlag = 1;

		for (int i = 0; i < lines.size(); i++) {
			String buf = lines.get(i);
			isPreLine = false;
			if (buf.startsWith("Content-Type:")) {
				String regex = "Content-Type:\\s*(.*?);";
				Pattern p = Pattern.compile(regex);
				Matcher m = p.matcher(buf);
				if (m.find()) {
					contentType = m.group(1).trim();
				}
			}

			if (buf.startsWith("Subject:")) {
				boolean isUTF8 = false;
				String regex = "=\\?(?i)UTF-8\\?B\\?(.*)\\?=";
				Pattern p = Pattern.compile(regex);
				Matcher m = p.matcher(buf);
				while (m.find()) {
					isUTF8 = true;
					subject = MyBase64.getFromBASE64(m.group(1));
				}
				if (!isUTF8) subject = buf.substring(8);
			} else if (buf.startsWith("From:")) {
				from = buf.substring(5);
			}
			else if (buf.startsWith("To:")) {
				String emails = buf.substring(3);
				// 使用分号作为分隔符拆分字符串
				String[] emailArray = emails.split(";");
				// 打印拆分后的邮箱地址
				for (String email : emailArray) {
					this.to_list.add(email.trim()); // 使用trim()去除可能的空格
				}
			}
			else if (buf.endsWith("base64")) {
				if (!hasContent) {
					decodeWithBase64 = true;
					isPreLine = true;
				}
				hasContent = true;
			} else if (buf.startsWith("--")) {
				decodeWithBase64 = false;
			}
			// 解析base64 ，暂时不用此功能
//			if (decodeWithBase64 && !isPreLine) {
//				if (contentType.toLowerCase().contains("text/plain")) {
//					content += MyBase64.getFromBASE64(buf);
//				} else if (contentType.toLowerCase().contains("text/html")) {
//					content += MyBase64.getFromBASE64(buf);
//				}
//			}
			if (contentType.toLowerCase().contains("text/plain")) {
				if (firstFlag >= 2){
					content += buf;
				}
				firstFlag+=1;


			} else if (contentType.toLowerCase().contains("text/html")) {
				if (firstFlag >= 2){
					content += buf;
				}
				firstFlag+=1;

			}
		}
	}

//	private void initByLines(Vector<String> lines) {
//		//决定当前行是否解码
//		boolean decodeWithBase64 = false;
//		//排除前一行的干扰
//		boolean isPreLine = false;
//		//是否已经收集完内容  不要html
//		boolean hasContent = false;
//		for (int i = 0; i < lines.size(); i++) {
//			String buf = lines.get(i);
//			isPreLine = false;
//			if (buf.startsWith("Return-Path:")) {
//				String regex = "<(.*)>";
//				Pattern p = Pattern.compile(regex);
//				Matcher m = p.matcher(buf);
//				boolean cont = true;
//				while (m.find()) {
//					// System.out.println(m.group(1));
//					if (cont) {
//						to_list = new Vector<>();
//						to_list.addElement(m.group(1));
//						cont = false;
//					}
//
//				}
//			}else if (buf.startsWith("Subject:")) {
//				boolean isUTF8 = false;
//				String regex = "=\\?(?i)UTF-8\\?B\\?(.*)\\?=";
//				Pattern p = Pattern.compile(regex);
//				Matcher m = p.matcher(buf);
//				while (m.find()) {
//					isUTF8 = true;
//					subject = MyBase64.getFromBASE64(m.group(1));
//				}
//				if(!isUTF8) subject = buf.substring(8);
//
//			}else if (buf.startsWith("From:")) {
////				String regex = "From:\\s+(.*?)\\s+<(.*)>";
////				Pattern p = Pattern.compile(regex);
////				Matcher m = p.matcher(buf);
////				if (m.find()) {
////				if (m.find()) {
////					from = m.group(2); // 从匹配中获取邮箱地址部分
////				} else {
////					from = buf.substring(6); // 如果没有尖括号包裹的情况，仍然截取 From: 后的内容
////				}
//				from = buf.substring(5);
//			}else if (buf.endsWith("base64")) {
//				if(!hasContent){
//					decodeWithBase64 = true;
//					isPreLine = true;
//				}
//				hasContent = true;
//			}else if (buf.startsWith("--")) {
//				decodeWithBase64 = false;
//			}
//
//			if (decodeWithBase64 && !isPreLine) {
//				content += MyBase64.getFromBASE64(buf);
//			}
//
//		}
//
//	}

	public String getDataString() {
		String to = "";
		for (int i = 0; i < to_list.size(); i++) {
			to += to_list.get(i) + ";";
		}
		return "Date: " + new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z (zzz)", Locale.ENGLISH)
				.format(new Date()) + "\r\n" + "From:" + from + "\r\n" + "To:" + to + "\r\n"
				+ "Subject: " + subject + "\r\n" + "Content-Type: text/plain; charset=us-ascii" + "\r\n" + "\r\n" + content + "\r\n";
	}


	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public Vector<String> getTo_list() {
		return to_list;
	}

	public void setTo_list(Vector<String> to_list) {
		this.to_list = to_list;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		String to = "";
		for (String s : to_list) {
			to += s + ";";
		}
		return "主题:"+subject+"\r\n"+
				"来自:"+from+"\r\n"+
				"发送给:"+to+"\r\n"+
				"正文:"+content;
	}
	
	

}
