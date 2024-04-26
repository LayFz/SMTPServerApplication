package fakesmtp.gui.info;

import fakesmtp.core.ArgsHandler;
import fakesmtp.core.I18n;
import fakesmtp.gui.DirChooser;
import fakesmtp.model.UIModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Observable;
import java.util.Observer;

public final class SaveMsgField extends Observable implements Observer {

	private final JTextField saveMsgField = new JTextField(UIModel.INSTANCE.getSavePath());

	/**
	 * Creates a text field and adds a mouse listener, to display the directory chooser dialog when a user clicks on the field.
	 * <p>
	 * The text field will be disabled by default to avoid the user to type any folder directly.<br>
	 * Instead, he can use the directory chooser dialog to select the path he wants.
	 * </p>
	 */
	public SaveMsgField() {
		saveMsgField.setToolTipText(I18n.INSTANCE.get("savemsgfield.tooltip"));

		// Disable edition but keep the same background color
		Color bg = saveMsgField.getBackground();
		saveMsgField.setEditable(false);
		saveMsgField.setBackground(bg);

		if (!ArgsHandler.INSTANCE.memoryModeEnabled()) {
			// Add a MouseListener
			saveMsgField.addMouseListener(new MouseListener() {
				@Override
				public void mouseClicked(MouseEvent e) {
				}

				@Override
				public void mousePressed(MouseEvent e) {
					openFolderSelection();
				}

				@Override
				public void mouseReleased(MouseEvent e) {
				}

				@Override
				public void mouseEntered(MouseEvent e) {
				}

				@Override
				public void mouseExited(MouseEvent e) {
				}

				private void openFolderSelection() {
					setChanged();
					notifyObservers();
				}
			});
		}
	}

	/**
	 * Returns the JTextField object.
	 *
	 * @return the JTextField object.
	 */
	public JTextField get() {
		return saveMsgField;
	}

	/**
	 * Updates the content of the JTextField with the new directory value.
	 * <p>
	 * Once a directory has been chosen by the {@link DirChooser}, the latter will
	 * notify this class, so that it can update its content.
	 * </p>
	 *
	 * @param o the observable element which will notify this class.
	 * @param arg optional parameters (not used).
	 */
	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof DirChooser) {
			saveMsgField.setText(UIModel.INSTANCE.getSavePath());
		}
	}
}
