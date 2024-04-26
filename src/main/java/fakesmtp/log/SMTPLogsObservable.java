package fakesmtp.log;

import java.util.Observable;


public final class SMTPLogsObservable extends Observable {
	/**
	 * Notify the {@code LogsPane} object when a new log is received.
	 *
	 * @param arg a String representing the received log.
	 */
	@Override
	public void notifyObservers(Object arg) {
		setChanged();
		super.notifyObservers(arg);
	}
}
