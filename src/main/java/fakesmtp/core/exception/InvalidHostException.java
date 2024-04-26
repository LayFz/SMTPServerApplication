package fakesmtp.core.exception;

import java.net.UnknownHostException;

public class InvalidHostException extends Exception {

	private static final long serialVersionUID = -8263018939961075449L;
	private final String host;

	public InvalidHostException(UnknownHostException e, String host) {
		setStackTrace(e.getStackTrace());
		this.host = host;
	}

	public String getHost() {
		return host;
	}
}
