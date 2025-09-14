package game.weekend.texteditor;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 * Search for text displayed in a JEditorPane.
 */
public class Finder {

	/**
	 * Create an object to search for text displayed in a JEditorPane.
	 *
	 * @param pane  JEditorPane itself.
	 * @param frame frame containing the JEditorPane.
	 * @param laf   LaF
	 */
	public Finder(JEditorPane pane, JFrame frame, LaF laf) {
		this.pane = pane;
		this.appFrame = frame;
		this.laf = laf;
		this.pattern = Proper.getProperty("Pattern", "");
		this.caseSensitive = Proper.getProperty("CaseSensitive", "FALSE").equalsIgnoreCase("TRUE") ? true : false;
	}

	/**
	 * Reset the search start position to its original state.
	 */
	public void resetPosition() {
		position = -1;
	}

	/**
	 * Displays a dialog box for specifying a search pattern, and then searches for
	 * the specified pattern.
	 */
	@SuppressWarnings("serial")
	public void find() {
		if (finderFrame == null) {
			finderFrame = new FinderFrame(appFrame) {
				{
					setPattern(pattern);
					setCase(caseSensitive);
					setFindDown(true);
					laf.addUpdateComponent(this);
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
					laf.removeUpdateComponent(finderFrame);
					finderFrame = null;
				}
			};
		}
		finderFrame.setVisible(true);
	}

	/**
	 * Search current line forward.
	 */
	public void findForward() {
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
	 * Search backwards for the current line.
	 */
	public void findBack() {
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
	 * Get the string to search for.
	 * 
	 * @return search string.
	 */
	private String getPattern() {
		if (!caseSensitive) {
			return pattern.toUpperCase();
		} else {
			return pattern;
		}
	}

	/**
	 * Get the text displayed in the JEditorPane.
	 * 
	 * @return text displayed in JEditorPane.
	 * @throws BadLocationException inherited exception.
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
	 * Select the found string in JEditorPane.
	 * 
	 * @param i the position at which the found string begins.
	 * @throws BadLocationException inherited exception.
	 */
	private void showResult(int i) throws BadLocationException {
		if (i >= 0) {
			position = i;
			WeekendTextEditor.status.showMessage("");

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

	private FinderFrame finderFrame;

	private String pattern = "";
	private boolean caseSensitive = false;
	private boolean findDown = true;

	private int position = -1;
}
