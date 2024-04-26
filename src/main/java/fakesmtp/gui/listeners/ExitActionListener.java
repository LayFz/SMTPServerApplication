package fakesmtp.gui.listeners;

import fakesmtp.gui.MainFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ExitActionListener implements ActionListener {

	private final MainFrame mainFrame;

	/**
	 * MainFrame is used for closing.
	 *
	 * @param mainFrame MainFrame window that will be closed.
	 */
	public ExitActionListener(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		mainFrame.close();
		System.exit(0);
	}
}
