package game.weekend.texteditor;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Сообщения вываваемые для пользователя.
 */
public class Messenger {

	/**
	 * Создать объект выдачи сообщений.
	 * 
	 * @param frame основное окно приложения.
	 */
	public Messenger(JFrame frame) {
		this.frame = frame;
	}

	/**
	 * Выдать сообщение об ошибке.
	 * 
	 * @param message текст сообщения.
	 */
	public void err(String message) {
		JOptionPane.showMessageDialog(frame, message, WeekendTextEditor.APP_NAME, JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Выдать информационное сообщение.
	 * 
	 * @param message текст сообщения.
	 */
	public void inf(String message) {
		JOptionPane.showMessageDialog(frame, message, WeekendTextEditor.APP_NAME, JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Выдать информационное сообщение.
	 * 
	 * @param message текст сообщения.
	 * @param title   заголовок окна.
	 */
	public void inf(String message, String title) {
		JOptionPane.showMessageDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Запросить подтверждение.
	 * 
	 * @param message текст сообщения.
	 */
	public int conf(String message) {
		return JOptionPane.showConfirmDialog(frame, message, WeekendTextEditor.APP_NAME,
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
	}

	private JFrame frame;
}
