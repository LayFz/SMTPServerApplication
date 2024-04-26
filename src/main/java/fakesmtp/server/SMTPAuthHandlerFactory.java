package fakesmtp.server;

import org.subethamail.smtp.AuthenticationHandler;
import org.subethamail.smtp.AuthenticationHandlerFactory;

import java.util.ArrayList;
import java.util.List;


/*package*/ final class SMTPAuthHandlerFactory implements AuthenticationHandlerFactory {
	private static final String LOGIN_MECHANISM = "LOGIN";
	private static final String PLAIN_MECHANISM = "PLAIN";

	@Override
	public AuthenticationHandler create() {
		return new SMTPAuthHandler();
	}

	@Override
	public List<String> getAuthenticationMechanisms() {
		List<String> result = new ArrayList<String>();
		result.add(SMTPAuthHandlerFactory.LOGIN_MECHANISM);
		result.add(SMTPAuthHandlerFactory.PLAIN_MECHANISM);
		return result;
	}
}
