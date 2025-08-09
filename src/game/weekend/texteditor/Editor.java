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
 * Редактор текста.
 */
public class Editor {

	/**
	 * Создать объект редактора текста.
	 */
	public Editor() {
		pane = new JEditorPane();

		// Панель редактируемая
		pane.setEditable(true);

		// Помещаю её в JScrollPane
		spane = new JScrollPane();
		spane.getViewport().add(pane);

		// Размер шрифта
		fontSize = Proper.getProperty("FontSize", 12);

		// Использовать моноширинный шрифт
		monoFont = Proper.getProperty("MonoFont", "TRUE").equalsIgnoreCase("TRUE") ? true : false;
		setMonoFont(monoFont);

		// Перехват Ctrl+H для "Заменить", а не удаления символа перед курсором
		InputMap inputMap = pane.getInputMap(JComponent.WHEN_FOCUSED);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.CTRL_DOWN_MASK), "Replace");
		ActionMap aMap = pane.getActionMap();
		aMap.put("Replace", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			// Дело в том, что на момент создания Editor Act ещё не существует,
			// поэтому, только уже при нажатии кнопки можно обращаться к act.
			public void actionPerformed(ActionEvent actionEvent) {
				act.getReplaceAction().actionPerformed(null);
			}
		});

		// Перехватываю выделение/сброс выделения текста отображенной выписки
		pane.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent ce) {
				// Если имеется выделенный текст, то разрешить Cut и Copy иначе заблокировать.
				boolean enabled = pane.getSelectionStart() != pane.getSelectionEnd();
				if (act != null) {
					act.setEnabledCut(enabled);
					act.setEnabledCopy(enabled);
				}
			}
		});

		// Перехватываю событие Drag and Drop. На самом деле Drop.
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
	 * Установить объект управляющий действиями.
	 */
	public void setAct(Act act) {
		this.act = act;

		// Меню по правой клавише мыши
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
	 * Установить объект управляющий файлами.
	 */
	public void setFiler(Filer filer) {
		this.filer = filer;
	}

	/**
	 * Получть компонент редактирования текста программы.
	 * 
	 * @return компонент редактирования текста программы.
	 */
	public JEditorPane getPane() {
		return pane;
	}

	/**
	 * Получть JScrollPane.
	 * 
	 * @return JScrollPane.
	 */
	public JScrollPane getScrollPane() {
		return spane;
	}

	/**
	 * Получть JPopupMenu.
	 * 
	 * @return JPopupMenu.
	 */
	public JPopupMenu getPopupMenu() {
		return popupMenu;
	}

	/**
	 * Установить текст для редактирования.
	 * 
	 * @param arg - текст для редактирования.
	 */
	public void setText(String text) {
		pane.setText(text);
		pane.setCaretPosition(0);
		pane.requestFocus();

		// К новому тексту создается новый UndoManager.
		if (undoManager == null)
			undoManager = new UndoManager();
		else
			undoManager.discardAllEdits();

		pane.getDocument().removeUndoableEditListener(undoManager);
		pane.getDocument().addUndoableEditListener(undoManager);

		// Его функционал реализует функции меню "Отменить" и "Повторить",
		act.setEnabledUndo(undoManager.canUndo());
		act.setEnabledRedo(undoManager.canRedo());

		// Замена/установка слушателя документа для поддержания активности пунктов меню
		// "Отменить" и "Повторить"
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

		// Замена/установка слушателя документа для пределения наличия изменений
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
	 * Текст был изменён текст?
	 * 
	 * @return Да или Нет.
	 */

	public boolean isChanged() {
		return changed;
	}

	/**
	 * Установить признак наличия изменений в тексте?
	 * 
	 * @parameter changed признак наличия изменений в тексте.
	 */
	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	/**
	 * "Отменить"
	 */
	public void undo() {
		if (undoManager.canUndo())
			undoManager.undo();
	}

	/**
	 * "Повторить"
	 */
	public void redo() {
		if (undoManager.canRedo())
			undoManager.redo();
	}

	/**
	 * "Вырезать"
	 */
	public void cut() {
		pane.cut();
	}

	/**
	 * "Копировать"
	 */
	public void copy() {
		pane.copy();
	}

	/**
	 * "Вставить"
	 */
	public void paste() {
		pane.paste();
	}

	/**
	 * "Выделить всё"
	 */
	public void selectAll() {
		pane.requestFocus();
		pane.selectAll();
	}

	/**
	 * Использовать моноширинный шрифт.
	 * 
	 * @param monoFont true - использовать, false - не использовать
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
	 * Изменить размер шрифта.
	 * 
	 * @param step изменить размер шрифта на эту величину
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
	 * Установить размер шрифта.
	 * 
	 * @param size размер шрифта
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
