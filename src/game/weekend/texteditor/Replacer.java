package game.weekend.texteditor;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 * Замена текста отображенного в JEditorPane.
 */
public class Replacer {

	/**
	 * Создать объект для замены текста оображенного в JEditorPane.
	 *
	 * @param pane  собственно JEditorPane.
	 * @param frame фрейм в котором расположена JEditorPane.
	 * @param laf   объект LaF приложения
	 */
	public Replacer(JEditorPane pane, JFrame frame, LaF laf) {
		this.pane = pane;
		this.appFrame = frame;
		this.laf = laf;
		this.pattern = Proper.getProperty("Pattern", "");
		this.replacer = Proper.getProperty("Replacer", "");
		this.caseSensitive = Proper.getProperty("CaseSensitive", "FALSE").equalsIgnoreCase("TRUE") ? true : false;
	}

	/**
	 * Сбросить позицию начала поиска в исходное состояние
	 */
	public void resetPosition() {
		position = -1;
	}

	/**
	 * Отображение диалогового окна для указания шаблона поиска и затем замена
	 * указанного шаблона.
	 */
	@SuppressWarnings("serial")
	public void find() {
		if (replacerFrame == null) {
			replacerFrame = new ReplacerFrame(appFrame) {
				{
					setPattern(pattern);
					setReplacer(replacer);
					setCase(caseSensitive);
					setFindDown(true);
					laf.addUpdateComponent(this);
				}

				@Override
				public void find() {
					// Читаю атрибуты поиска
					pattern = getPattern();
					Proper.setProperty("Pattern", pattern);
					caseSensitive = getCase();
					Proper.setProperty("CaseSensitive", caseSensitive ? "TRUE" : "FALSE");

					// Поиск вперед или назад
					findDown = getFindDown();
					if (findDown)
						findForward();
					else
						findBack();

					// Курсор на поле шаблона поиска
					whatFocus();
				}

				@Override
				public void replace() {

					// Читаю атрибуты замены
					Proper.setProperty("Replacer", replacer);
					caseSensitive = getCase();
					Proper.setProperty("CaseSensitive", caseSensitive ? "TRUE" : "FALSE");

					// Выделение совпадает с шаблоном?
					if (selectionMatchPattern()) {
						int start = pane.getSelectionStart();
						// Заменить выделение
						pane.replaceSelection(replacer);
						// Выделить замену
						pane.select(start, start + pattern.length());
					} else {
						// Искать далее
						findDown = getFindDown();
						if (findDown)
							findForward();
						else
							findBack();
					}
				}

				@Override
				public void replaceAll() {

					// Читаю атрибуты замены
					Proper.setProperty("Replacer", replacer);
					caseSensitive = getCase();
					Proper.setProperty("CaseSensitive", caseSensitive ? "TRUE" : "FALSE");

					// Выделение НЕ совпадает с шаблоном?
					if (!selectionMatchPattern()) {
						// Искать далее
						findDown = getFindDown();
						if (findDown)
							findForward();
						else
							findBack();
					}

					while (selectionMatchPattern()) {
						// Заменить выделение
						pane.replaceSelection(replacer);

						// Искать далее
						findDown = getFindDown();
						if (findDown)
							findForward();
						else
							findBack();
					}
				}

				/**
				 * Выделение совпадает с шаблоном?
				 * 
				 * @return true/false
				 */
				private boolean selectionMatchPattern() {
					String selectedText = pane.getSelectedText();
					if (selectedText == null)
						return false;

					boolean found = false;
					if (caseSensitive)
						found = selectedText.equals(pattern);
					else
						found = selectedText.equalsIgnoreCase(pattern);
					return found;
				}

				@Override
				public void close() {
					super.close();
					laf.removeUpdateComponent(replacerFrame);
					replacerFrame = null;
				}

			};
		}
		replacerFrame.setVisible(true);
	}

	/**
	 * Искать текущую подстроку вперёд
	 */
	private void findForward() {
		try {
			String content = getContent();
			String pattern = getPattern();
			if (pattern.trim().length() > 0) {

				int carretPos = pane.getCaret().getMark();
				if (position == carretPos)
					++position;
				else
					position = carretPos;

				int i = content.indexOf(pattern, position);
				showResult(i);
			}
		} catch (BadLocationException ignored) {
		}
	}

	/**
	 * Искать текущую подстроку назад
	 */
	private void findBack() {
		try {
			String content = getContent();
			String pattern = getPattern();
			if (pattern.trim().length() > 0) {

				position = pane.getCaret().getDot();
				if (position != pane.getCaret().getMark())
					--position;

				int i = content.substring(0, position).lastIndexOf(pattern, position);
				showResult(i);
			}
		} catch (BadLocationException ignored) {
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

			// Сделать видимым на экране

			// Это устарело:
			// pane.scrollRectToVisible(pane.modelToView(position));
			// , но pane.modelToView2D(position) возвращает Rectangle2D, а не Rectangle.
			// Преобразование через () приводит, иногда к исключениям, поэтому делаю так:

			Rectangle2D rect2D = pane.modelToView2D(position);
			Rectangle rect = new Rectangle((int) rect2D.getX(), (int) rect2D.getY(), (int) rect2D.getWidth(),
					(int) rect2D.getHeight());
			pane.scrollRectToVisible(rect);

			pane.select(position, position + pattern.length());
		} else
			WeekendTextEditor.status.showMessage(Loc.get("unable_to_find") + " \"" + pattern + "\"");
	}

	private JFrame appFrame;
	private JEditorPane pane;
	private LaF laf;

	private ReplacerFrame replacerFrame;

	private String pattern = "";
	private String replacer = "";
	private boolean caseSensitive = false;
	private boolean findDown = true;

	private int position = -1;
}
