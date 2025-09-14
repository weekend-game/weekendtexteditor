package game.weekend.texteditor;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 * Replace the text displayed in a JEditorPane.
 */
public class Replacer {

	/**
	 * Create an object to replace the text displayed in the JEditorPane.
	 *
	 * @param pane  JEditorPane itself.
	 * @param frame the frame in which the JEditorPane is located.
	 * @param laf   LaF
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
	 * Reset the search start position to its original state.
	 */
	public void resetPosition() {
		position = -1;
	}

	/**
	 * Displays a dialog box for specifying a search pattern and then replaces the
	 * specified pattern.
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
					// Search attributes
					pattern = getPattern();
					Proper.setProperty("Pattern", pattern);
					caseSensitive = getCase();
					Proper.setProperty("CaseSensitive", caseSensitive ? "TRUE" : "FALSE");

					// Search forward or backward
					findDown = getFindDown();
					if (findDown)
						findForward();
					else
						findBack();

					// Focus on the search template field
					whatFocus();
				}

				@Override
				public void replace() {

					// The replacement attributes
					Proper.setProperty("Replacer", replacer);
					caseSensitive = getCase();
					Proper.setProperty("CaseSensitive", caseSensitive ? "TRUE" : "FALSE");

					// Does the selection match the pattern?
					if (selectionMatchPattern()) {
						int start = pane.getSelectionStart();
						// Replace selection
						pane.replaceSelection(replacer);
						// Select replacement
						pane.select(start, start + pattern.length());
					} else {
						// Search further
						findDown = getFindDown();
						if (findDown)
							findForward();
						else
							findBack();
					}
				}

				@Override
				public void replaceAll() {

					// Replacement attributes
					Proper.setProperty("Replacer", replacer);
					caseSensitive = getCase();
					Proper.setProperty("CaseSensitive", caseSensitive ? "TRUE" : "FALSE");

					// The selection does NOT match the template?
					if (!selectionMatchPattern()) {
						// Search further
						findDown = getFindDown();
						if (findDown)
							findForward();
						else
							findBack();
					}

					while (selectionMatchPattern()) {
						// Replace selection
						pane.replaceSelection(replacer);

						// Search further
						findDown = getFindDown();
						if (findDown)
							findForward();
						else
							findBack();
					}
				}

				/**
				 * Does the selection match the pattern?
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
	 * Search current substring forward
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
	 * Search current substring backwards
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
	 * Get the substring to search for.
	 * 
	 * @return substring to search for.
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
	 * Select the found substring in JEditorPane.
	 * 
	 * @param i the position at which the found substring begins.
	 * @throws BadLocationException inherited exception.
	 */
	private void showResult(int i) throws BadLocationException {
		if (i >= 0) {
			position = i;
			WeekendTextEditor.status.showMessage("");

			// Make visible on screen
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
