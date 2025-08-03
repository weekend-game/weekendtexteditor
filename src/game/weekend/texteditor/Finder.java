package game.weekend.texteditor;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 * Поиск текста оображенного в JEditorPane.
 */
public class Finder {

	/**
	 * Создать объект для поиска текста оображенного в JEditorPane.
	 *
	 * @param pane  собственно JEditorPane.
	 * @param frame фрейм в котором расположена JEditorPane.
	 */
	public Finder(JEditorPane pane, JFrame frame) {
		this.pane = pane;
		this.appFrame = frame;

		this.pattern = Proper.getProperty("Pattern", "");

		this.caseSensitive = Proper.getProperty("CaseSensitive", "FALSE").equalsIgnoreCase("TRUE") ? true : false;
	}

	/**
	 * Сбросить позицию начала поиска в исходное состояние
	 */
	public void resetPosition() {
		position = -1;
	}

	/**
	 * Отображение диалогового окна для указания шаблона поиска и затем поиск
	 * указанного шаблона.
	 */
	@SuppressWarnings("serial")
	public void find() {
		if (finderFrame == null) {
			finderFrame = new FinderFrame(appFrame) {
				{
					resetPosition();
					setPattern(pattern);
					setCase(caseSensitive);
					setFindDown(true);
				}

				@Override
				public void find() {
					pattern = getPattern();
					Proper.setProperty("Pattern", pattern);

					caseSensitive = getCase();
					Proper.setProperty("CaseSensitive", caseSensitive ? "TRUE" : "FALSE");

					findDown = getFindDown();

					if (findDown) {
						findForward();
					} else {
						findBack();
					}

					whatFocus();
				}

				@Override
				public void close() {
					super.close();
					finderFrame = null;
				}
			};
		}
		finderFrame.setVisible(true);
	}

	/**
	 * Искать текущую подстроку вперёд
	 */
	public void findForward() {
		try {
			String content = getContent();
			String pattern = getPattern();
			if (pattern.trim().length() > 0) {
				int i = content.indexOf(pattern, position + 1);
				showResult(i);
			}
		} catch (BadLocationException e) {
			System.out.println(e);
		}
	}

	/**
	 * Искать текущую подстроку назад
	 */
	public void findBack() {
		try {
			String content = getContent();
			String pattern = getPattern();
			if (pattern.trim().length() > 0) {
				int i = content.lastIndexOf(pattern, position - 1);
				showResult(i);
			}
		} catch (BadLocationException e) {
			System.out.println(e);
		}
	}

	/**
	 * Получить подстроку для поиска.
	 * 
	 * @return подстрока для поиска.
	 */
	private String getPattern() {
		if (!caseSensitive) {
			return pattern.toUpperCase();
		} else {
			return pattern;
		}
	}

	/**
	 * Получить текст отображенный в JEditorPane.
	 * 
	 * @return текст отображенный в JEditorPane.
	 * @throws BadLocationException унаследованное исключение.
	 */
	private String getContent() throws BadLocationException {
		Document d = pane.getDocument();
		String s = d.getText(0, d.getLength());
		if (!caseSensitive) {
			s = s.toUpperCase();
		}
		return s;
	}

	/**
	 * Выделить найденную подстроку в JEditorPane.
	 * 
	 * @param i позиция в которой начинается найденная подстрока.
	 * @throws BadLocationException унаследованное исключение.
	 */
	private void showResult(int i) throws BadLocationException {
		if (i >= 0) {
			position = i;
			WeekendTextEditor.status.showMessage("");
		} else
			WeekendTextEditor.status.showMessage("Не удаётся найти \"" + pattern + "\"");

		if (position >= 0) {
			// Это устарело:
			// pane.scrollRectToVisible(pane.modelToView(position));
			// , но pane.modelToView2D(position) возвращает Rectangle2D, а не Rectangle.
			// Преобразование через () приводит, иногда к исключениям, поэтому делаю так:

			Rectangle2D rect2D = pane.modelToView2D(position);

			Rectangle rect = new Rectangle((int) rect2D.getX(), (int) rect2D.getY(), (int) rect2D.getWidth(),
					(int) rect2D.getHeight());

			pane.scrollRectToVisible(rect);
			pane.select(position, position + pattern.length());
			pane.requestFocus();
		}
	}

	private JFrame appFrame;
	private JEditorPane pane;

	private FinderFrame finderFrame;

	private String pattern = "";
	private boolean caseSensitive = false;
	private boolean findDown = true;

	private int position = -1;
}
