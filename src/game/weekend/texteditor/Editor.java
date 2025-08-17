package game.weekend.texteditor;

import java.awt.Font;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.undo.UndoManager;

/**
 * Text editor.
 */
public class Editor {

	/**
	 * Create a text editor object.
	 */
	public Editor() {
		pane = new JEditorPane();

		// The panel is editable
		pane.setEditable(true);

		// I put it in a JScrollPane
		spane = new JScrollPane();
		spane.getViewport().add(pane);

		// Font size
		fontSize = Proper.getProperty("FontSize", 12);

		// Use monospaced font
		monoFont = Proper.getProperty("MonoFont", "TRUE").equalsIgnoreCase("TRUE") ? true : false;
		setMonoFont(monoFont);

		// Intercept Ctrl+H to "Replace" rather than delete the character before the
		// cursor
		InputMap inputMap = pane.getInputMap(JComponent.WHEN_FOCUSED);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.CTRL_DOWN_MASK), "Replace");
		ActionMap aMap = pane.getActionMap();
		aMap.put("Replace", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			// The thing is that at the time of creation the Editor, Act does not yet exist,
			// therefore, only when the button is pressed can you access the act.
			public void actionPerformed(ActionEvent actionEvent) {
				act.getReplaceAction().actionPerformed(null);
			}
		});

		// Intercepting text selection
		pane.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent ce) {
				// If there is selected text, then allow Cut and Copy otherwise block.
				boolean enabled = pane.getSelectionStart() != pane.getSelectionEnd();
				if (act != null) {
					act.setEnabledCut(enabled);
					act.setEnabledCopy(enabled);
				}
			}
		});

		// Intercepting the Drag and Drop event. Actually Drop.
		new DropTarget(pane, new DropTargetAdapter() {
			public void drop(DropTargetDropEvent e) {
				try {
					e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
					List<?> list = (List<?>) e.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
					File file = (File) list.get(0);
					if (filer != null)
						filer.open(file);
				} catch (Exception ignored) {
				}
			}
		});
	}

	/**
	 * Set the object that controls the actions.
	 */
	public void setAct(Act act) {
		this.act = act;

		// Context menu
		popupMenu = act.getPopupMenu();
		pane.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent me) {
				if (me.isPopupTrigger()) {
					popupMenu.show(me.getComponent(), me.getX(), me.getY());
				}
			}

			@Override
			public void mouseReleased(MouseEvent me) {
				if (me.isPopupTrigger()) {
					popupMenu.show(me.getComponent(), me.getX(), me.getY());
				}
			}
		});
	}

	/**
	 * Set the file manager object.
	 */
	public void setFiler(Filer filer) {
		this.filer = filer;
	}

	/**
	 * Get the program's text editing component.
	 * 
	 * @return text editing component.
	 */
	public JEditorPane getPane() {
		return pane;
	}

	/**
	 * Get JScrollPane.
	 * 
	 * @return JScrollPane.
	 */
	public JScrollPane getScrollPane() {
		return spane;
	}

	/**
	 * Get JPopupMenu.
	 * 
	 * @return JPopupMenu.
	 */
	public JPopupMenu getPopupMenu() {
		return popupMenu;
	}

	/**
	 * Set text for editing.
	 * 
	 * @param arg - text for editing.
	 */
	public void setText(String text) {
		pane.setText(text);
		pane.setCaretPosition(0);
		pane.requestFocus();

		// A new UndoManager is created for the new text.
		if (undoManager == null)
			undoManager = new UndoManager();
		else
			undoManager.discardAllEdits();

		pane.getDocument().removeUndoableEditListener(undoManager);
		pane.getDocument().addUndoableEditListener(undoManager);

		// Its functionality implements the "Undo" and "Redo" menu functions.,
		act.setEnabledUndo(undoManager.canUndo());
		act.setEnabledRedo(undoManager.canRedo());

		// Replace/install document listener to keep Undo and Redo menu items active
		pane.getDocument().addDocumentListener(new DocumentListener() {
			{
				pane.getDocument().removeDocumentListener(this);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				act.setEnabledUndo(undoManager.canUndo());
				act.setEnabledRedo(undoManager.canRedo());
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				act.setEnabledUndo(undoManager.canUndo());
				act.setEnabledRedo(undoManager.canRedo());
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				act.setEnabledUndo(undoManager.canUndo());
				act.setEnabledRedo(undoManager.canRedo());
			}
		});

		// Replace/install document listener to detect changes
		pane.getDocument().addDocumentListener(new DocumentListener() {
			{
				pane.getDocument().removeDocumentListener(this);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				changed = true;
				pane.getDocument().removeDocumentListener(this);
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				changed = true;
				pane.getDocument().removeDocumentListener(this);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				changed = true;
				pane.getDocument().removeDocumentListener(this);
			}
		});

		setChanged(false);
	}

	/**
	 * Has the text been changed?
	 * 
	 * @return true/false.
	 */

	public boolean isChanged() {
		return changed;
	}

	/**
	 * Set the flag for changes in the text.
	 * 
	 * @parameter changed the flag of the presence of changes in the text.
	 */
	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	/**
	 * "Undo"
	 */
	public void undo() {
		if (undoManager.canUndo())
			undoManager.undo();
	}

	/**
	 * "Redo"
	 */
	public void redo() {
		if (undoManager.canRedo())
			undoManager.redo();
	}

	/**
	 * "Cut"
	 */
	public void cut() {
		pane.cut();
	}

	/**
	 * "Copy"
	 */
	public void copy() {
		pane.copy();
	}

	/**
	 * "Paste"
	 */
	public void paste() {
		pane.paste();
	}

	/**
	 * "Select all"
	 */
	public void selectAll() {
		pane.requestFocus();
		pane.selectAll();
	}

	/**
	 * Use monospaced font
	 * 
	 * @param monoFont true/false.
	 */
	public void setMonoFont(boolean monoFont) {
		this.monoFont = monoFont;
		if (monoFont)
			pane.setFont(new Font("Monospaced", Font.PLAIN, fontSize));
		else
			pane.setFont(new Font("Serif", Font.PLAIN, fontSize));
		Proper.setProperty("MonoFont", monoFont ? "TRUE" : "FALSE");
	}

	/**
	 * Change font size.
	 * 
	 * @param step change the font size to this value.
	 */
	public void changeFontSize(int step) {
		if (fontSize <= 6 && step < 0)
			return;

		if (fontSize >= 64 && step > 0)
			return;

		fontSize += step;
		if (monoFont)
			pane.setFont(new Font("Monospaced", Font.PLAIN, fontSize));
		else
			pane.setFont(new Font("Serif", Font.PLAIN, fontSize));
		Proper.setProperty("FontSize", fontSize);
	}

	/**
	 * Set font size.
	 * 
	 * @param size font size.
	 */
	public void setFontSize(int size) {
		fontSize = size;
		if (monoFont)
			pane.setFont(new Font("Monospaced", Font.PLAIN, fontSize));
		else
			pane.setFont(new Font("Serif", Font.PLAIN, fontSize));
		Proper.setProperty("FontSize", fontSize);
	}

	private Act act;
	private Filer filer;

	private JEditorPane pane;
	private JScrollPane spane;
	private JPopupMenu popupMenu;

	private boolean monoFont;
	private int fontSize = 14;
	private UndoManager undoManager;
	private boolean changed = false;
}
