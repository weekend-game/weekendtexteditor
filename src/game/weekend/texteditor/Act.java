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
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

/**
 * Menu bar, context menu and toolbar. And also all Actions that are in the
 * application.
 */
public class Act {

	/**
	 * Create a menu bar, context menu and toolbar object.
	 * 
	 * @param app       applicateion.
	 * @param editor    editing panel.
	 * @param filer     working with file.
	 * @param lastFiles last opened files.
	 * @param finder    finder.
	 * @param replacer  replacer.
	 * @param laf       L&F.
	 * @param messenger issuing messages.
	 */
	public Act(WeekendTextEditor app, Editor editor, Filer filer, LastFiles lastFiles, Finder finder, Replacer replacer,
			LaF laf, Messenger messenger) {

		this.filer = filer;
		this.lastFiles = lastFiles;
		this.laf = laf;
		this.messenger = messenger;

		// Actions can be used both in the menu and in the toolbar, so it is better to
		// create and remember them once in the designer.

		newFile = getActNew(filer);
		open = getActOpen(filer);
		save = getActSave(filer);
		saveAs = getActSaveAs(filer);
		exit = getActExit(app);

		undo = getActUndo(editor);
		redo = getActRedo(editor);

		cut = getActCut(editor);
		copy = getActCopy(editor);
		paste = getActPaste(editor);
		selectAll = getActSelectAll(editor);

		find = getActFind(finder);
		findForward = getActFindForward(finder);
		findBack = getActFindBack(finder);
		replace = getActReplace(replacer);

		toolbarOn = getActToolbarOn(app);
		statusbarOn = getActStatusbarOn(app);
		monoFont = getActMonoFont(editor);
		incFontSize = getActIncFontSize(editor);
		decFontSize = getActDecFontSize(editor);
		defFontSize = getActDefFontSize(editor);

		about = getActAbout(app);
	}

	/**
	 * Get the application menu.
	 * 
	 * @return the application menu.
	 */
	@SuppressWarnings("serial")
	public JMenuBar getMenuBar() {
		menu = new JMenuBar();

		refreshMenuFile();

		JMenu editMenu = new JMenu(Loc.get("edit"));
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
		editMenu.add(replace);

		JMenu viewMenu = new JMenu(Loc.get("view"));
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

		JMenu helpMenu = new JMenu(Loc.get("help"));
		helpMenu.add(about);

		menu.add(fileMenu);
		menu.add(editMenu);
		menu.add(viewMenu);
		menu.add(helpMenu);

		return menu;
	}

	/**
	 * Get the application toolbar.
	 * 
	 * @return the application toolbar.
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
		toolBar.add(replace);

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
			popupMenu.add(replace);
		}

		return popupMenu;

	}

	/**
	 * Create/recreate the File menu.
	 * <p>
	 * After opening a file, Filer adds it to the list of names of the last opened
	 * files (LastFiles) and updates the File menu. Therefore, a separate method is
	 * needed to create/update this menu.
	 */
	public void refreshMenuFile() {
		// If the menu has not yet been created, then I will create it
		if (fileMenu == null) {
			fileMenu = new JMenu(Loc.get("file"));
			menu.add(fileMenu);
		} else
			// Otherwise, I clear all points
			fileMenu.removeAll();

		fileMenu.add(newFile);
		fileMenu.add(open);
		fileMenu.add(save);
		fileMenu.add(saveAs);

		// I get a list of recently opened files
		List<String> list = this.lastFiles.getList();

		// And if there were any
		if (list.size() > 0) {
			// I add a separator to the menu
			fileMenu.add(new JSeparator());

			// and a list of open files
			int i = 1;
			for (String s : list)
				fileMenu.add(getActOpenFile(filer, i++, s));
		}

		fileMenu.add(new JSeparator());
		fileMenu.add(exit);
	}

	/**
	 * Activate/deactivate the Cut menu item.
	 * <p>
	 * The Cut menu item is disabled. But if the user selects a text fragment, Cut
	 * should be activated; if the user clears the selection, Cut should be
	 * disabled. This is implemented by a listener on the JEditorPane (see the
	 * Editor constructor, pane.addCaretListener...), which calls this method.
	 * <p>
	 * 
	 * @param enabled true - activate, flase - deactivate the Copy menu item.
	 */
	public void setEnabledCut(boolean enabled) {
		cut.setEnabled(enabled);
	}

	/**
	 * Get Action for "Replace..."
	 * 
	 * @return action for "Replace..."
	 */
	public AbstractAction getReplaceAction() {
		return replace;
	}

	/**
	 * Activate/deactivate the Copy menu item.
	 * <p>
	 * The Copy menu item is disabled. But if the user selects a text fragment, Copy
	 * should be activated; if the user clears the selection, Copy should be
	 * disabled. This is implemented by a listener on the JEditorPane (see the
	 * Editor constructor, pane.addCaretListener...), which calls this method.
	 * <p>
	 * 
	 * @param enabled true - activate, flase - deactivate the Copy menu item.
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
	 * "New"
	 * 
	 * @param filer program file management.
	 * 
	 * @return Action "New".
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActNew(Filer filer) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, Loc.get("new"));
				putValue(Action.SHORT_DESCRIPTION, Loc.get("create_new_file"));
				putValue(Action.SMALL_ICON, getImageIcon("new.gif"));
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				filer.newFile();
			}
		};
	}

	/**
	 * "Open..."
	 * 
	 * @param filer program file management.
	 * 
	 * @return Action "Open..."
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActOpen(Filer filer) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, Loc.get("open") + "...");
				putValue(Action.SHORT_DESCRIPTION, Loc.get("open_file"));
				putValue(Action.SMALL_ICON, getImageIcon("open.gif"));
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				filer.openFile();
			}
		};
	}

	/**
	 * "Save"
	 * 
	 * @param filer program file management.
	 * 
	 * @return Action "Save"
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActSave(Filer filer) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, Loc.get("save"));
				putValue(Action.SHORT_DESCRIPTION, Loc.get("save_file"));
				putValue(Action.SMALL_ICON, getImageIcon("save.gif"));
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				filer.saveFile();
			}
		};
	}

	/**
	 * "Save as..."
	 * 
	 * @param filer program file management.
	 * 
	 * @return Action "Save as..."
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActSaveAs(Filer filer) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, Loc.get("save_as") + "...");
				putValue(Action.SHORT_DESCRIPTION, Loc.get("save_file_with_a_different_name"));
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
	 * Action for names of recently opened files in the application menu.
	 * 
	 * @param no   file number 1..N.
	 * @param name path and file name.
	 * @return Action to open the specified file.
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
	 * "Exiting the program"
	 * 
	 * @param viewer application.
	 *
	 * @return Action "Exiting the program"
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActExit(WeekendTextEditor app) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, Loc.get("exit"));
				putValue(Action.SHORT_DESCRIPTION, Loc.get("exiting_the_program"));
				putValue(Action.SMALL_ICON, getImageIcon("empty.gif"));
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				app.close();
			}
		};
	}

	/**
	 * "Undo"
	 * 
	 * @return Action "Undo"
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActUndo(Editor editor) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, Loc.get("undo"));
				putValue(Action.SHORT_DESCRIPTION, Loc.get("undo_changes"));
				putValue(Action.SMALL_ICON, getImageIcon("empty.gif"));
				putValue(Action.ACCELERATOR_KEY,
						KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				editor.undo();
			}
		};
	}

	/**
	 * "Redo"
	 * 
	 * @return Action "Redo"
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActRedo(Editor editor) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, Loc.get("redo"));
				putValue(Action.SHORT_DESCRIPTION, Loc.get("redo_changes"));
				putValue(Action.SMALL_ICON, getImageIcon("empty.gif"));
				putValue(Action.ACCELERATOR_KEY,
						KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				editor.redo();
			}
		};
	}

	/**
	 * "Cut"
	 * 
	 * @return Action "Cut"
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActCut(Editor editor) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, Loc.get("cut"));
				putValue(Action.SHORT_DESCRIPTION, Loc.get("cut_fragment"));
				putValue(Action.SMALL_ICON, getImageIcon("cut.gif"));
				putValue(Action.ACCELERATOR_KEY,
						KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
				setEnabled(false);
			}

			public void actionPerformed(ActionEvent actionEvent) {
				editor.cut();
			}
		};
	}

	/**
	 * "Copy"
	 * 
	 * @return Action "Copy"
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActCopy(Editor editor) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, Loc.get("copy"));
				putValue(Action.SHORT_DESCRIPTION, Loc.get("copy_fragment"));
				putValue(Action.SMALL_ICON, getImageIcon("copy.gif"));
				putValue(Action.ACCELERATOR_KEY,
						KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
				setEnabled(false);
			}

			public void actionPerformed(ActionEvent actionEvent) {
				editor.copy();
			}
		};
	}

	/**
	 * "Paste"
	 * 
	 * @return Action "Paste"
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActPaste(Editor editor) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, Loc.get("paste"));
				putValue(Action.SHORT_DESCRIPTION, Loc.get("paste_fragment"));
				putValue(Action.SMALL_ICON, getImageIcon("paste.gif"));
				putValue(Action.ACCELERATOR_KEY,
						KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));

				// I always keep it active. But it would be useful to check the clipboard when
				// the window receives focus and make a decision based on that.
				setEnabled(true);
			}

			public void actionPerformed(ActionEvent actionEvent) {
				editor.paste();
			}
		};
	}

	/**
	 * "Select all"
	 * 
	 * @return Action "Select all"
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActSelectAll(Editor editor) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, Loc.get("select_all"));
				putValue(Action.SHORT_DESCRIPTION, Loc.get("select_all"));
				putValue(Action.SMALL_ICON, getImageIcon("empty.gif"));
				putValue(Action.ACCELERATOR_KEY,
						KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				editor.selectAll();
			}
		};
	}

	/**
	 * "Find..."
	 * 
	 * @return Action "Find..."
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActFind(Finder finder) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, Loc.get("find") + "...");
				putValue(Action.SHORT_DESCRIPTION, Loc.get("find") + "...");
				putValue(Action.SMALL_ICON, getImageIcon("find.gif"));
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				finder.find();
			}
		};
	}

	/**
	 * "Continue finding forward"
	 * 
	 * @return Action "Continue finding forward"
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActFindForward(Finder finder) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, Loc.get("continue_finding_forward"));
				putValue(Action.SHORT_DESCRIPTION, Loc.get("continue_finding_forward"));
				putValue(Action.SMALL_ICON, getImageIcon("findforward.gif"));
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				finder.findForward();
			}
		};
	}

	/**
	 * "Continue finding backward"
	 * 
	 * @return Action "Continue finding backward"
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActFindBack(Finder finder) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, Loc.get("continue_finding_backward"));
				putValue(Action.SHORT_DESCRIPTION, Loc.get("continue_finding_backward"));
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
	 * "Replace..."
	 * 
	 * @return Action "Replace..."
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActReplace(Replacer replacer) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, Loc.get("replace") + "...");
				putValue(Action.SHORT_DESCRIPTION, Loc.get("replace_found_with_specified_string"));
				putValue(Action.SMALL_ICON, getImageIcon("replace.gif"));
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_DOWN_MASK));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				replacer.find();
			}
		};
	}

	/**
	 * "Show toolbar"
	 * 
	 * @param editor application.
	 *
	 * @return Action "Show toolbar"
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActToolbarOn(WeekendTextEditor editor) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, Loc.get("show_toolbar"));
				putValue(Action.SHORT_DESCRIPTION, Loc.get("show_toolbar"));
				putValue(Action.SMALL_ICON, getImageIcon("empty.gif"));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				JCheckBoxMenuItem i = (JCheckBoxMenuItem) actionEvent.getSource();
				editor.setTooolbarON(i.isSelected());
			}
		};
	}

	/**
	 * "Show status bar"
	 * 
	 * @param editor application.
	 *
	 * @return Action "Show status bar"
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActStatusbarOn(WeekendTextEditor editor) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, Loc.get("show_status_bar"));
				putValue(Action.SHORT_DESCRIPTION, Loc.get("show_status_bar"));
				putValue(Action.SMALL_ICON, getImageIcon("empty.gif"));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				JCheckBoxMenuItem i = (JCheckBoxMenuItem) actionEvent.getSource();
				editor.setStatusbarON(i.isSelected());
			}
		};
	}

	/**
	 * "Use monospaced font"
	 * 
	 * @return Action "Use monospaced font"
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActMonoFont(Editor editor) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, Loc.get("use_monospaced_font"));
				putValue(Action.SHORT_DESCRIPTION, Loc.get("use_monospaced_font"));
				putValue(Action.SMALL_ICON, getImageIcon("empty.gif"));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				JCheckBoxMenuItem i = (JCheckBoxMenuItem) actionEvent.getSource();
				editor.setMonoFont(i.isSelected());
			}
		};
	}

	/**
	 * "Increase font size"
	 *
	 * @return Action "Increase font size"
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActIncFontSize(Editor editor) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, Loc.get("increase_font_size"));
				putValue(Action.SHORT_DESCRIPTION, Loc.get("increase_font_size"));
				putValue(Action.SMALL_ICON, getImageIcon("empty.gif"));
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, InputEvent.CTRL_DOWN_MASK));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				editor.changeFontSize(1);
			}
		};
	}

	/**
	 * "Decrease font size"
	 *
	 * @return Action "Decrease font size"
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActDecFontSize(Editor editor) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, Loc.get("decrease_font_size"));
				putValue(Action.SHORT_DESCRIPTION, Loc.get("decrease_font_size"));
				putValue(Action.SMALL_ICON, getImageIcon("empty.gif"));
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, InputEvent.CTRL_DOWN_MASK));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				editor.changeFontSize(-1);
			}
		};
	}

	/**
	 * "Default font size"
	 * 
	 * @return Action "Default font size"
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActDefFontSize(Editor editor) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, Loc.get("default_font_size"));
				putValue(Action.SHORT_DESCRIPTION, Loc.get("default_font_size"));
				putValue(Action.SMALL_ICON, getImageIcon("empty.gif"));
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_0, InputEvent.CTRL_DOWN_MASK));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				editor.setFontSize(12);
			}
		};
	}

	/**
	 * "About..."
	 * 
	 * @param viewer приложение.
	 * 
	 * @return Action "About..."
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActAbout(WeekendTextEditor editor) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, Loc.get("about") + "...");
				putValue(Action.SHORT_DESCRIPTION, Loc.get("about") + "...");
				putValue(Action.SMALL_ICON, getImageIcon("empty.gif"));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				String str = "\n" + WeekendTextEditor.APP_NAME + "\n" + WeekendTextEditor.APP_VERSION + "\n"
						+ WeekendTextEditor.APP_COPYRIGHT + "\n\n" + WeekendTextEditor.APP_OTHER + "\n\n";
				messenger.inf(str, Loc.get("about"));
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
	private AbstractAction replace;

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
	private Messenger messenger;
}
