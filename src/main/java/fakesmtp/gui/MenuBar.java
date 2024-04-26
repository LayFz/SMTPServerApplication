package fakesmtp.gui;

import fakesmtp.core.ArgsHandler;
import fakesmtp.core.I18n;
import fakesmtp.gui.listeners.ExitActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;


public final class MenuBar extends Observable {

	private final I18n i18n = I18n.INSTANCE;
	private final JMenuBar menuBar = new JMenuBar();
	private final MainFrame mainFrame;

	/**
	 * Creates the menu bar and the different menus (file / edit / help).
	 *
	 * @param mainFrame MainFrame class required for the closing action.
	 */
	public MenuBar(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		menuBar.add(createFileMenu());
		menuBar.add(createEditMenu());
	}

	/**
	 * Returns the JMenuBar object.
	 *
	 * @return the JMenuBar object.
	 */
	public JMenuBar get() {
		return menuBar;
	}

	/**
	 * Creates the file menu.
	 * <p>
	 * The file menu contains an "Exit" item, to quit the application.
	 * </p>
	 *
	 * @return the newly created file menu.
	 */
	private JMenu createFileMenu() {
		JMenu fileMenu = new JMenu(i18n.get("menubar.file"));
		fileMenu.setMnemonic(i18n.get("menubar.mnemo.file").charAt(0));

		JMenuItem exit = new JMenuItem(i18n.get("menubar.exit"));
		exit.setMnemonic(i18n.get("menubar.mnemo.exit").charAt(0));
		exit.addActionListener(new ExitActionListener(mainFrame));

		fileMenu.add(exit);
		return fileMenu;
	}

	/**
	 * Creates the edit menu.
	 * <p>
	 * The edit menu contains a "Messages location" item, to define the location of the incoming mails.
	 * </p>
	 *
	 * @return the newly created edit menu.
	 */
	private JMenu createEditMenu() {
		JMenu editMenu = new JMenu(i18n.get("menubar.edit"));
		editMenu.setMnemonic(i18n.get("menubar.mnemo.edit").charAt(0));

		JMenuItem mailsLocation = new JMenuItem(i18n.get("menubar.messages.location"));
		mailsLocation.setMnemonic(i18n.get("menubar.mnemo.msglocation").charAt(0));
		if (ArgsHandler.INSTANCE.memoryModeEnabled()) {
			mailsLocation.setEnabled(false);
		} else {
			mailsLocation.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					setChanged();
					notifyObservers();
				}
			});
		}

		editMenu.add(mailsLocation);
		return editMenu;
	}

}
