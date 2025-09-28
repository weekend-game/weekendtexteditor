package game.weekend.texteditor;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Messages
 */
public class Mes {

	/**
	 * Set frame.
	 * 
	 * @param frame frame.
	 */
	public static void setJFrame(JFrame frame) {
		Mes.frame = frame;
	}

	/**
	 * Display an error message.
	 * 
	 * @param message message text.
	 */
	public static void err(String message) {
		JOptionPane.showMessageDialog(frame, message, WeekendTextEditor.APP_NAME, JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Display an informational message.
	 * 
	 * @param message message text.
	 */
	public static void inf(String message) {
		JOptionPane.showMessageDialog(frame, message, WeekendTextEditor.APP_NAME, JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Display an informational message.
	 * 
	 * @param message message text.
	 * @param title   frame title.
	 */
	public static void inf(String message, String title) {
		JOptionPane.showMessageDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Request confirmation.
	 * 
	 * @param message message text.
	 */
	public static int conf(String message) {
		return JOptionPane.showConfirmDialog(frame, message, WeekendTextEditor.APP_NAME,
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
	}

	private static JFrame frame;
}
