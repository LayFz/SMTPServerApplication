package fakesmtp.gui.tab;

import fakesmtp.gui.info.ClearAllButton;
import fakesmtp.model.EmailModel;
import fakesmtp.server.MailSaver;

import javax.swing.*;
import java.util.Observable;
import java.util.Observer;

public final class LastMailPane implements Observer {

	private final JScrollPane lastMailPane = new JScrollPane();
	private final JTextArea lastMailArea = new JTextArea();

	/**
	 * Creates the text area and disables the possibility to edit it.
	 */
	public LastMailPane() {
		lastMailArea.setEditable(false);
		lastMailPane.getViewport().add(lastMailArea, null);
	}

	/**
	 * Returns the JScrollPane object.
	 *
	 * @return the JScrollPane object.
	 */
	public JScrollPane get() {
		return lastMailPane;
	}

	/**
	 * Updates the content of the text area.
	 * <p>
	 * This method will be called by an observable element.
     * </p>
	 * <ul>
	 *   <li>If the observable is a {@link MailSaver} object, the text area will contain the content of the last received email;</li>
	 *   <li>If the observable is a {@link ClearAllButton} object, the text area will be cleared.</li>
	 * </ul>
	 *
	 * @param o the observable element which will notify this class.
	 * @param data optional parameters (an {@code EmailModel} object, for the case of a {@code MailSaver} observable).
	 */
	@Override
	public synchronized void update(Observable o, Object data) {
		if (o instanceof MailSaver) {
			EmailModel model = (EmailModel) data;
			lastMailArea.setText(model.getEmailStr());
		} else if (o instanceof ClearAllButton) {
			lastMailArea.setText("");
		}
	}
}
