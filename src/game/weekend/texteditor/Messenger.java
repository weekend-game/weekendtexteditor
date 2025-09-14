package game.weekend.texteditor;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Messages
 */
public class Messenger {

	/**
	 * Create a messages object.
	 * 
	 * @param frame main application frame.
	 */
	public Messenger(JFrame frame) {
		this.frame = frame;
	}

	/**
	 * Display an error message.
	 * 
	 * @param message message text.
	 */
	public void err(String message) {
		JOptionPane.showMessageDialog(frame, message, WeekendTextEditor.APP_NAME, JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Display an informational message.
	 * 
	 * @param message message text.
	 */
	public void inf(String message) {
		JOptionPane.showMessageDialog(frame, message, WeekendTextEditor.APP_NAME, JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Display an informational message.
	 * 
	 * @param message message text.
	 * @param title   frame title.
	 */
	public void inf(String message, String title) {
		JOptionPane.showMessageDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Request confirmation.
	 * 
	 * @param message message text.
	 */
	public int conf(String message) {
		return JOptionPane.showConfirmDialog(frame, message, WeekendTextEditor.APP_NAME,
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
	}

	private JFrame frame;
}
