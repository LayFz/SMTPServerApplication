package fakesmtp.gui;

import fakesmtp.core.Configuration;
import fakesmtp.core.I18n;
import fakesmtp.gui.info.SaveMsgField;
import fakesmtp.model.UIModel;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Observable;
import java.util.Observer;

public final class DirChooser extends Observable implements Observer {

	private final JFileChooser dirChooser = new JFileChooser();
	private Component parent = null;

	/**
	 * Creates a {@code JFileChooser} component and sets it to be for directories only.
	 *
	 * @param parent the component from where the chooser will be launched <i>(should be the main panel of the application)</i>.
	 */
	public DirChooser(Component parent) {
		this.parent = parent;
		dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		dirChooser.setDialogTitle(String.format(I18n.INSTANCE.get("dirchooser.title"),
			Configuration.INSTANCE.get("application.name")));
		dirChooser.setApproveButtonText(I18n.INSTANCE.get("dirchooser.approve.btn"));
	}

	/**
	 * Opens the folder selection.
	 * <p>
	 * This method will be called by an {@code Observable} element:
     * </p>
	 * <ul>
	 *   <li>The {@link MenuBar};</li>
	 *   <li>Or the {@link SaveMsgField}.</li>
	 * </ul>
	 *
	 * @param o the observable element which will notify this class.
	 * @param arg optional parameters (not used).
	 */
	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof MenuBar || o instanceof SaveMsgField) {
			openFolderSelection();
		}
	}

	/**
	 * Opens the folder selection dialog and notify observers once the directory is selected.
	 * <p>
	 * The only observer notified is the {@link SaveMsgField}.
	 * </p>
	 */
	private void openFolderSelection() {
		File filePath = new File(Configuration.INSTANCE.get("emails.default.dir"));
		dirChooser.setCurrentDirectory(filePath);

		int result = dirChooser.showOpenDialog(parent);

		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedDir = dirChooser.getSelectedFile();
			if (selectedDir != null) {
				UIModel.INSTANCE.setSavePath(selectedDir.getAbsolutePath());
				setChanged();
				notifyObservers();
			}
		}
	}
}
