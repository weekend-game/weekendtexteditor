package game.weekend.texteditor;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JEditorPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.undo.UndoManager;

/**
 * Линейка меню и инструментальная линейка. А так же все Actions которые есть в
 * приложении.
 */
public class Act {

	/**
	 * Создать объект линейки меню и инструменальной линейки.
	 * 
	 * @param editor приложение.
	 * @param filer  работв с фвйлом.
	 */
	public Act(WeekendTextEditor editor, Filer filer, Finder finder, LaF laf, LastFiles lastFiles) {
		this.filer = filer;
		this.laf = laf;
		this.lastFiles = lastFiles;

		// Actions могут использоваться как в меню, так и в инструментальной линейке.
		// Так что лучше их создать и запомнить один раз в конструкторе.

		newFile = getActNew(filer);
		open = getActOpen(filer);
		save = getActSave(filer);
		saveAs = getActSaveAs(filer);
		exit = getActExit(editor);

		undo = getActUndo(editor);
		redo = getActRedo(editor);

		cut = getActCut(editor.getPane());
		copy = getActCopy(editor.getPane());
		paste = getActPaste(editor.getPane());
		selectAll = getActSelectAll(editor.getPane());
		find = getActFind(finder);
		findForward = getActFindForward(finder);
		findBack = getActFindBack(finder);

		toolbarOn = getActToolbarOn(editor);
		statusbarOn = getActStatusbarOn(editor);
		monoFont = getActMonoFont(editor);
		incFontSize = getActIncFontSize(editor);
		decFontSize = getActDecFontSize(editor);
		defFontSize = getActDefFontSize(editor);

		about = getActAbout(editor);
	}

	/**
	 * Получить меню приложения.
	 * 
	 * @return меню приложения.
	 */
	@SuppressWarnings("serial")
	public JMenuBar getMenuBar() {
		menu = new JMenuBar();

		refreshMenuFile();

		JMenu editMenu = new JMenu("Правка");
		editMenu.add(undo);
		editMenu.add(redo);
		editMenu.add(new JSeparator());
		editMenu.add(cut);
		editMenu.add(copy);
		editMenu.add(paste);
		editMenu.add(new JSeparator());
		editMenu.add(selectAll);
		editMenu.add(new JSeparator());
		editMenu.add(find);
		editMenu.add(findForward);
		editMenu.add(findBack);

		JMenu viewMenu = new JMenu("Вид");
		ButtonGroup btgLaf = new ButtonGroup();
		for (UIManager.LookAndFeelInfo lafi : UIManager.getInstalledLookAndFeels()) {
			JMenuItem mi = new JRadioButtonMenuItem();
			mi.setAction(new AbstractAction() {
				{
					putValue(Action.NAME, lafi.getName());
				}

				public void actionPerformed(ActionEvent ae) {
					laf.setLookAndFeel(lafi.getClassName());
				}
			});
			mi.setSelected(laf.getLookAndFeel().equals(lafi.getClassName()));
			btgLaf.add(mi);
			viewMenu.add(mi);
		}

		viewMenu.add(new JSeparator());

		JCheckBoxMenuItem i = null;

		i = new JCheckBoxMenuItem(toolbarOn);
		i.setSelected(Proper.getProperty("ToolbarON", "TRUE").equalsIgnoreCase("TRUE") ? true : false);
		viewMenu.add(i);

		i = new JCheckBoxMenuItem(statusbarOn);
		i.setSelected(Proper.getProperty("StatusbarON", "TRUE").equalsIgnoreCase("TRUE") ? true : false);
		viewMenu.add(i);

		viewMenu.add(new JSeparator());

		i = new JCheckBoxMenuItem(monoFont);
		i.setSelected(Proper.getProperty("MonoFont", "TRUE").equalsIgnoreCase("TRUE") ? true : false);
		viewMenu.add(i);

		viewMenu.add(new JSeparator());

		viewMenu.add(incFontSize);
		viewMenu.add(decFontSize);
		viewMenu.add(defFontSize);

		JMenu helpMenu = new JMenu("Справка");
		helpMenu.add(about);

		menu.add(fileMenu);
		menu.add(editMenu);
		menu.add(viewMenu);
		menu.add(helpMenu);

		return menu;
	}

	/**
	 * Получить Toolbar приложения.
	 * 
	 * @return Toolbar приложения.
	 */
	@SuppressWarnings("serial")
	public JToolBar getToolBar() {

		JToolBar toolBar = new JToolBar() {
			// Кнопки toolbar-а не должны брать на себя фокус.
			// Иначе теряется выделение текста в JEditorPane.
			@Override
			protected JButton createActionComponent(Action a) {
				JButton b = super.createActionComponent(a);
				b.setRequestFocusEnabled(false);
				return b;
			}
		};

		toolBar.setRollover(true);
		toolBar.setFloatable(false);

		toolBar.add(newFile);
		toolBar.add(open);
		toolBar.add(save);
		toolBar.addSeparator();
		toolBar.add(cut);
		toolBar.add(copy);
		toolBar.add(paste);
		toolBar.addSeparator();
		toolBar.add(find);
		toolBar.add(findForward);
		toolBar.add(findBack);

		return toolBar;
	}

	public JPopupMenu getPopupMenu() {
		if (popupMenu == null) {
			popupMenu = new JPopupMenu();

			popupMenu.add(newFile);
			popupMenu.add(open);
			popupMenu.add(save);
			popupMenu.add(saveAs);
			popupMenu.add(new JSeparator());
			popupMenu.add(undo);
			popupMenu.add(redo);
			popupMenu.add(new JSeparator());
			popupMenu.add(cut);
			popupMenu.add(copy);
			popupMenu.add(paste);
			popupMenu.add(new JSeparator());
			popupMenu.add(selectAll);
			popupMenu.add(new JSeparator());
			popupMenu.add(find);
			popupMenu.add(findForward);
			popupMenu.add(findBack);
		}

		return popupMenu;

	}

	/**
	 * Создать/пересоздать меню "Файл".
	 * <p>
	 * После открытия файла Filer добавляет его в список имен последних открытых
	 * файлов (LastFiles) и обновляет меню File меню. Поэтому нужен отдельный метод
	 * для создания/обновления этого меню.
	 */
	public void refreshMenuFile() {
		// Если меню ещё не создано, то создаю его
		if (fileMenu == null) {
			fileMenu = new JMenu("Файл");
			menu.add(fileMenu);
		} else
			// А иначе, очищаю от всех пунктов
			fileMenu.removeAll();

		fileMenu.add(newFile);
		fileMenu.add(open);
		fileMenu.add(save);
		fileMenu.add(saveAs);

		// Получаю список последних открытых файлов
		List<String> list = this.lastFiles.getList();

		// И если таковые были
		if (list.size() > 0) {
			// добавляю в меню сепаратор
			fileMenu.add(new JSeparator());

			// и список открытых файлов
			int i = 1;
			for (String s : list)
				fileMenu.add(getActOpenFile(filer, i++, s));
		}

		fileMenu.add(new JSeparator());
		fileMenu.add(exit);
	}

	/**
	 * Активировать/деактивировать пункт меню Copy.
	 * <p>
	 * Пункт меню Cut деактивирован. Но если пользователь выделит фрагмент текста,
	 * то его следует активировать, если пользователь сбросит выделение, то его
	 * следует деактивировать. Это реализуется слушателем на JEditorPane (см.
	 * конструктор WeekendTextEditor, фрагмент pane.addCaretListener...), который и
	 * вызывает этот метод.
	 * <p>
	 * 
	 * @param enabled true - активировать, flase деактивировать пункт меню Copy.
	 */
	public void setEnabledCut(boolean enabled) {
		cut.setEnabled(enabled);
	}

	/**
	 * Активировать/деактивировать пункт меню Copy.
	 * <p>
	 * то его следует активировать, если пользователь сбросит выделение, то его
	 * следует деактивировать. Это реализуется слушателем на JEditorPane (см.
	 * конструктор WeekendTextEditor, фрагмент pane.addCaretListener...), который и
	 * вызывает этот метод.
	 * <p>
	 * 
	 * @param enabled true - активировать, flase деактивировать пункт меню Copy.
	 */
	public void setEnabledCopy(boolean enabled) {
		copy.setEnabled(enabled);
	}

	public void setEnabledUndo(boolean enabled) {
		undo.setEnabled(enabled);
	}

	public void setEnabledRedo(boolean enabled) {
		redo.setEnabled(enabled);
	}

	private ImageIcon getImageIcon(String fileName) {
		return new ImageIcon(getClass().getResource(WeekendTextEditor.IMAGE_PATH + fileName));
	}

	/**
	 * "Создать"
	 * 
	 * @param filer управление файлом программы.
	 * 
	 * @return Action "Создать"
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActNew(Filer filer) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, "Создать");
				putValue(Action.SHORT_DESCRIPTION, "Создать новый файл");
				putValue(Action.SMALL_ICON, getImageIcon("new.gif"));
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				filer.newFile();
			}
		};
	}

	/**
	 * "Открыть..."
	 * 
	 * @param filer управление файлом программы.
	 * 
	 * @return Action "Открыть..."
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActOpen(Filer filer) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, "Открыть...");
				putValue(Action.SHORT_DESCRIPTION, "Открыть файл");
				putValue(Action.SMALL_ICON, getImageIcon("open.gif"));
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				filer.openFile();
			}
		};
	}

	/**
	 * "Сохранить"
	 * 
	 * @param filer управление файлом программы.
	 * 
	 * @return Action "Сохранить"
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActSave(Filer filer) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, "Сохранить");
				putValue(Action.SHORT_DESCRIPTION, "Сохранить файл");
				putValue(Action.SMALL_ICON, getImageIcon("save.gif"));
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				filer.saveFile();
			}
		};
	}

	/**
	 * "Сохранить как..."
	 * 
	 * @param filer управление файлом программы.
	 * 
	 * @return Action "Сохранить как..."
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActSaveAs(Filer filer) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, "Сохранить как...");
				putValue(Action.SHORT_DESCRIPTION, "Сохранить файл с другим имененм");
				putValue(Action.SMALL_ICON, getImageIcon("empty.gif"));
				putValue(Action.ACCELERATOR_KEY,
						KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				filer.saveAsFile();
			}
		};
	}

	/**
	 * Action для имен последних открытых файлов в меню приложения.
	 * 
	 * @param no   номер файла 1..N.
	 * @param name путь и имя файла.
	 * @return Action для открытия указанного файла.
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActOpenFile(Filer filer, int no, String name) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, "" + no + " " + name);
				putValue(Action.SHORT_DESCRIPTION, name);
				putValue(Action.SMALL_ICON, getImageIcon("empty.gif"));
				putValue(Action.MNEMONIC_KEY, KeyEvent.VK_0 + no);
			}

			public void actionPerformed(ActionEvent actionEvent) {
				filer.openFileByName(new File(name));
			}
		};
	}

	/**
	 * "Выход из программы"
	 * 
	 * @param editor приложение.
	 *
	 * @return Action "Выход из программы"
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActExit(WeekendTextEditor editor) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, "Выход");
				putValue(Action.SHORT_DESCRIPTION, "Выход из программы");
				putValue(Action.SMALL_ICON, getImageIcon("empty.gif"));
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				editor.close();
			}
		};
	}

	/**
	 * "Отменить"
	 * 
	 * @return Action "Отменить"
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActUndo(WeekendTextEditor editor) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, "Отменить");
				putValue(Action.SHORT_DESCRIPTION, "Отменить изменения");
				putValue(Action.SMALL_ICON, getImageIcon("empty.gif"));
				putValue(Action.ACCELERATOR_KEY,
						KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				UndoManager u = filer.getUndoManager();
				if (u.canUndo())
					u.undo();
			}
		};
	}

	/**
	 * "Повторить"
	 * 
	 * @return Action "Повторить"
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActRedo(WeekendTextEditor editor) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, "Повторить");
				putValue(Action.SHORT_DESCRIPTION, "Повторить изменения");
				putValue(Action.SMALL_ICON, getImageIcon("empty.gif"));
				putValue(Action.ACCELERATOR_KEY,
						KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				UndoManager u = filer.getUndoManager();
				if (u.canRedo())
					u.redo();
			}
		};
	}

	/**
	 * "Вырезать"
	 * 
	 * @return Action "Вырезать"
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActCut(JEditorPane pane) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, "Вырезать");
				putValue(Action.SHORT_DESCRIPTION, "Вырезать фрагмент");
				putValue(Action.SMALL_ICON, getImageIcon("cut.gif"));
				putValue(Action.ACCELERATOR_KEY,
						KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
				setEnabled(false);
			}

			public void actionPerformed(ActionEvent actionEvent) {
				pane.cut();
			}
		};
	}

	/**
	 * "Копировать"
	 * 
	 * @return Action "Копировать"
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActCopy(JEditorPane pane) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, "Копировать");
				putValue(Action.SHORT_DESCRIPTION, "Копировать фрагмент");
				putValue(Action.SMALL_ICON, getImageIcon("copy.gif"));
				putValue(Action.ACCELERATOR_KEY,
						KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
				setEnabled(false);
			}

			public void actionPerformed(ActionEvent actionEvent) {
				pane.copy();
			}
		};
	}

	/**
	 * "Вставить"
	 * 
	 * @return Action "Вставить"
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActPaste(JEditorPane pane) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, "Вставить");
				putValue(Action.SHORT_DESCRIPTION, "Вставить фрагмент");
				putValue(Action.SMALL_ICON, getImageIcon("paste.gif"));
				putValue(Action.ACCELERATOR_KEY,
						KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));

				// Далаю всегда активным. Но было бы полезно при получении фокуса окном,
				// проверить буфер обмена и уже на основании этого принимать решение.
				setEnabled(true);
			}

			public void actionPerformed(ActionEvent actionEvent) {
				pane.paste();
			}
		};
	}

	/**
	 * "Выделить всё"
	 * 
	 * @return Action "Выделить всё"
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActSelectAll(JEditorPane pane) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, "Выделить всё");
				putValue(Action.SHORT_DESCRIPTION, "Выделить всё");
				putValue(Action.SMALL_ICON, getImageIcon("empty.gif"));
				putValue(Action.ACCELERATOR_KEY,
						KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				pane.requestFocus();
				pane.selectAll();
			}
		};
	}

	/**
	 * "Поиск..."
	 * 
	 * @return Action "Поиск..."
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActFind(Finder finder) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, "Поиск...");
				putValue(Action.SHORT_DESCRIPTION, "Поиск записи по заданному критерию");
				putValue(Action.SMALL_ICON, getImageIcon("find.gif"));
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				finder.find();
			}
		};
	}

	/**
	 * "Продолжить поиск вперёд"
	 * 
	 * @return Action "Продолжить поиск вперёд"
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActFindForward(Finder finder) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, "Продолжить поиск вперёд");
				putValue(Action.SHORT_DESCRIPTION, "Продолжить поиск вперёд");
				putValue(Action.SMALL_ICON, getImageIcon("findforward.gif"));
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				finder.findForward();
			}
		};
	}

	/**
	 * "Продолжить поиск назад"
	 * 
	 * @return Action "Продолжить поиск назад"
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActFindBack(Finder finder) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, "Продолжить поиск назад");
				putValue(Action.SHORT_DESCRIPTION, "Продолжить поиск назад");
				putValue(Action.SMALL_ICON, getImageIcon("findback.gif"));
				putValue(Action.ACCELERATOR_KEY,
						KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				finder.findBack();
			}
		};
	}

	/**
	 * "Отображать инструментальную линейку"
	 * 
	 * @param editor приложение.
	 *
	 * @return Action "Отображать инструментальную линейку"
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActToolbarOn(WeekendTextEditor editor) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, "Отображать инструментальную линейку");
				putValue(Action.SHORT_DESCRIPTION, "Отображать инструментальную линейку");
				putValue(Action.SMALL_ICON, getImageIcon("empty.gif"));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				JCheckBoxMenuItem i = (JCheckBoxMenuItem) actionEvent.getSource();
				editor.setTooolbarON(i.isSelected());
			}
		};
	}

	/**
	 * "Отображать строку состояния"
	 * 
	 * @param editor приложение.
	 *
	 * @return Action "Отображать строку состояния"
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActStatusbarOn(WeekendTextEditor editor) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, "Отображать строку состояния");
				putValue(Action.SHORT_DESCRIPTION, "Отображать строку состояния");
				putValue(Action.SMALL_ICON, getImageIcon("empty.gif"));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				JCheckBoxMenuItem i = (JCheckBoxMenuItem) actionEvent.getSource();
				editor.setStatusbarON(i.isSelected());
			}
		};
	}

	/**
	 * "Использовать моноширинный шрифт"
	 * 
	 * @param editor приложение.
	 *
	 * @return Action "Использовать моноширинный шрифт"
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActMonoFont(WeekendTextEditor editor) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, "Использовать моноширинный шрифт");
				putValue(Action.SHORT_DESCRIPTION, "Использовать моноширинный шрифт");
				putValue(Action.SMALL_ICON, getImageIcon("empty.gif"));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				JCheckBoxMenuItem i = (JCheckBoxMenuItem) actionEvent.getSource();
				editor.setMonoFont(i.isSelected());
			}
		};
	}

	/**
	 * "Увеличить шрифт"
	 * 
	 * @param editor приложение.
	 *
	 * @return Action "Увеличить шрифт"
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActIncFontSize(WeekendTextEditor editor) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, "Увеличить шрифт");
				putValue(Action.SHORT_DESCRIPTION, "Увеличить шрифт");
				putValue(Action.SMALL_ICON, getImageIcon("empty.gif"));
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, InputEvent.CTRL_DOWN_MASK));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				editor.setFontSize(1);
			}
		};
	}

	/**
	 * "Уменьшить шрифт"
	 * 
	 * @param editor приложение.
	 *
	 * @return Action "Уменьшить шрифт"
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActDecFontSize(WeekendTextEditor editor) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, "Уменьшить шрифт");
				putValue(Action.SHORT_DESCRIPTION, "Уменьшить шрифт");
				putValue(Action.SMALL_ICON, getImageIcon("empty.gif"));
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, InputEvent.CTRL_DOWN_MASK));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				editor.setFontSize(-1);
			}
		};
	}

	/**
	 * "Размер шрифта по умолчанию"
	 * 
	 * @param editor приложение.
	 *
	 * @return Action "Размер шрифта по умолчанию"
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActDefFontSize(WeekendTextEditor editor) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, "Размер шрифта по умолчанию");
				putValue(Action.SHORT_DESCRIPTION, "Размер шрифта по умолчанию");
				putValue(Action.SMALL_ICON, getImageIcon("empty.gif"));
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_0, InputEvent.CTRL_DOWN_MASK));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				editor.setFontSize(12D);
			}
		};
	}

	/**
	 * "О программе"
	 * 
	 * @param editor приложение.
	 * 
	 * @return Action "О программе"
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActAbout(WeekendTextEditor editor) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, "О программе");
				putValue(Action.SHORT_DESCRIPTION, "О программе");
				putValue(Action.SMALL_ICON, getImageIcon("empty.gif"));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				String str = "\n" + WeekendTextEditor.APP_NAME + "\n" + WeekendTextEditor.APP_VERSION + "\n"
						+ WeekendTextEditor.APP_COPYRIGHT + "\n\n" + WeekendTextEditor.APP_OTHER + "\n\n";
				editor.inf(str, "О программе");
			}
		};
	}

	private JMenuBar menu;
	private JMenu fileMenu;

	private JPopupMenu popupMenu;

	private AbstractAction newFile;
	private AbstractAction open;
	private AbstractAction save;
	private AbstractAction saveAs;
	private AbstractAction exit;

	private AbstractAction undo;
	private AbstractAction redo;

	private AbstractAction cut;
	private AbstractAction copy;
	private AbstractAction paste;
	private AbstractAction selectAll;

	private AbstractAction find;
	private AbstractAction findForward;
	private AbstractAction findBack;

	private AbstractAction toolbarOn;
	private AbstractAction statusbarOn;
	private AbstractAction monoFont;
	private AbstractAction incFontSize;
	private AbstractAction decFontSize;
	private AbstractAction defFontSize;

	private AbstractAction about;

	private Filer filer;
	private LastFiles lastFiles;
	private LaF laf;
}
