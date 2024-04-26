package fakesmtp.server;

import org.subethamail.smtp.AuthenticationHandler;
import org.subethamail.smtp.RejectException;
import org.subethamail.smtp.util.Base64;




/*package*/ final class SMTPAuthHandler implements AuthenticationHandler {
	private static final String USER_IDENTITY = "User";
	private static final String PROMPT_USERNAME = "334 VXNlcm5hbWU6"; // VXNlcm5hbWU6 is base64 for "Username:"
	private static final String PROMPT_PASSWORD = "334 UGFzc3dvcmQ6"; // UGFzc3dvcmQ6 is base64 for "Password:"

	private int pass = 0; // 测试通道


	private String username;
	private String password;

	@Override
	public String auth(String clientInput) throws RejectException {
		if (clientInput.trim().equalsIgnoreCase("AUTH LOGIN")) {
			// 客户端请求使用LOGIN机制进行身份验证
			return PROMPT_USERNAME; // 发送给客户端的提示，要求输入用户名
		} else if (clientInput.trim().equalsIgnoreCase("AUTH PLAIN")) {
			// 客户端请求使用PLAIN机制进行身份验证
			return "334"; // 发送给客户端的提示，要求输入Base64编码的用户名和密码
		} else if (username == null) {
			// 客户端已经提供了用户名
			username = decodeBase64(clientInput);
			return PROMPT_PASSWORD; // 发送给客户端的提示，要求输入密码
		} else {
			// 客户端已经提供了密码
			password = decodeBase64(clientInput);
			// 验证
			if (isValidCredentials(username, password)) {
				return null; // 返回null表示验证成功
			} else {
				throw new RejectException(535, "Authentication credentials invalid");
			}
		}
	}

	private String decodeBase64(String input) {
		byte[] decodedBytes = Base64.decode(input);
		return new String(decodedBytes);
	}

	// 验证机制
	private boolean isValidCredentials(String username, String password) {
		return username.equals("123456@qq.com") && password.equals("123456");
	}


	/**
	 * Simulates an authentication process.
	 * <p>
	 * <ul>
	 *   <li>first prompts for username;</li>
	 *   <li>then, prompts for password;</li>
	 *   <li>finally, returns {@code null} to finish the authentication process;</li>
	 * </ul>
	 * </p>
	 *
	 * @return <code>null</code> if the authentication process is finished, otherwise a string to hand back to the client.
	 * @param clientInput The client's input, eg "AUTH PLAIN dGVzdAB0ZXN0ADEyMzQ="
	 */
	// 模拟无账户登录逻辑
//	@Override
//	public String auth(String clientInput) {// auth login
//		String prompt;
//		if (++pass == 1) {
//			prompt = SMTPAuthHandler.PROMPT_USERNAME;
//		} else if (pass == 2) {
//			prompt = SMTPAuthHandler.PROMPT_PASSWORD;
//		} else {
//			pass = 0;
//			prompt = null;
//
//		}
//		return prompt;
//	}


	/**
	 * If the authentication process was successful, this returns the identity
	 * of the user. The type defining the identity can vary depending on the
	 * authentication mechanism used, but typically this returns a String username.
	 * If authentication was not successful, the return value is undefined.
	 */
	@Override
	public Object getIdentity() {
		return SMTPAuthHandler.USER_IDENTITY;
	}

}
