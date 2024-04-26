package fakesmtp.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import java.util.Observable;


public final class SMTPLogsAppender<E> extends AppenderBase<E> {
	private SMTPLogsObservable observable = new SMTPLogsObservable();

	/**
	 * Receives a log from Logback, and sends it to the {@code LogsPane} object.
	 *
	 * @param event a Logback {@code ILoggingEvent} event.
	 */
	@Override
	protected void append(E event) {
		if (event instanceof ILoggingEvent) {
			ILoggingEvent loggingEvent = (ILoggingEvent) event;
			observable.notifyObservers(loggingEvent.getFormattedMessage());
		}
	}

	/**
	 * Returns the log observable object.
	 *
	 * @return the log observable object.
	 */
	public Observable getObservable() {
		return observable;
	}
}
