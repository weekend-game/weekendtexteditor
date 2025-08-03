package game.weekend.texteditor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.Document;
import javax.swing.undo.UndoManager;

/**
 * Работа с файлами.
 */
public class Filer {

	/** Расширение файлов */
	public static final String EXTENSION = "txt";

	/** Название файлов */
	public static final String DESCRIPTION = "*.txt - Текстовый файл";

	/**
	 * Создать объект работы с файлами.
	 * 
	 * @param editor основной объект приложения.
	 */
	public Filer(WeekendTextEditor editor, LastFiles lastFiles, Finder finder) {
		this.editor = editor;
		this.lastFiles = lastFiles;
		this.finder = finder;
	}

	/**
	 * Получить UndoManager.
	 * 
	 * @return UndoManager.
	 */
	public UndoManager getUndoManager() {
		return undoManager;
	}

	/**
	 * Уствновить для Filer ссылку на Act. Т.к. Filer управляетс элементами
	 * меню/toolbar-а.
	 * 
	 * @param act объект класса Act.
	 */
	public void setAct(Act act) {
		this.act = act;
	}

	/**
	 * Реализация "Создать"
	 */
	public void newFile() {
		if (!saveFileIfNecessary())
			return;

		editor.getFrame().setTitle(WeekendTextEditor.APP_NAME);

		file = null;

		editor.getPane().setText("");
		editor.getPane().setCaretPosition(0);
		editor.getPane().requestFocus();

		finder.resetPosition();

		addFileChangedListener();
	}

	/**
	 * Реализация "Открыть..."
	 */
	public void openFile() {
		if (!saveFileIfNecessary())
			return;

		File file = showDialogue();
		if (file != null) {
			open(file);
		}
	}

	/**
	 * Реализация "Сохранить"
	 */
	public void saveFile() {
		if (file != null)
			save(file);
		else
			saveAsFile();
	}

	/**
	 * Реализация "Сохранить как..."
	 */
	public void saveAsFile() {
		File file = showSaveDialogue();
		if (file != null) {
			save(file);
		}
	}

	/**
	 * Открыть файл по имени
	 */
	public void openFileByName(File file) {
		if (!saveFileIfNecessary())
			return;

		open(file);
	}

	/**
	 * Открыть указнный файл и отобразить его.
	 * 
	 * @param file открываемый файл
	 */
	public void open(File file) {
		if (file != null) {
			if (file.exists()) {
				this.file = file;

				// Отображаю имя открытого файла в заголовке приложения
				editor.getFrame().setTitle(WeekendTextEditor.APP_NAME + " - " + file.getPath());

				// Запоминаю его в списке последних открытых файлов
				lastFiles.put(file.getPath());

				try {
					String content = new String(Files.readAllBytes(file.toPath()));
					editor.getPane().setText(content);
					editor.getPane().setCaretPosition(0);
					editor.getPane().requestFocus();
					addFileChangedListener();
				} catch (IOException e) {
					editor.err("Не удалось открыть файл " + file.getPath() + ".\n" + e);
				}

			} else {
				// Если файл не открылся, то удаляю его из списка последних открытых файлов
				lastFiles.remove(file.getPath());

				// Выдаю сообщение об этом неприятном событии
				editor.err("Файл " + file.getPath() + " не найден.");
			}

			editor.refreshMenuFile();
		}
	}

	/**
	 * Сохранить текст в указнный файл.
	 * 
	 * @param file файл для сохранения текста
	 */
	public void save(File file) {
		if (file != null) {
			try {
				Files.write(file.toPath(), editor.getPane().getText().getBytes());

				this.file = file;

				// Отображаю имя файла в заголовке приложения
				editor.getFrame().setTitle(WeekendTextEditor.APP_NAME + " - " + file.getPath());

				// Запоминаю его в списке последних открытых файлов
				lastFiles.put(file.getPath());

				editor.refreshMenuFile();

				addFileChangedListener();
			} catch (IOException e) {
				editor.err("Не удалось сохранить файл " + file.getPath() + ".\n" + e);
			}
		}
	}

	/**
	 * Получить файл для открытия посредством диалога открытия файла.
	 * 
	 * @return файл указанный пользователем или null, если пользователь отказался от
	 *         открытия файла.
	 */
	private File showDialogue() {
		JFileChooser chooser = getChooser(file);
		int result = chooser.showOpenDialog(editor.getFrame());

		File selectedFile = null;
		if (result == JFileChooser.APPROVE_OPTION) {
			selectedFile = chooser.getSelectedFile();
		}
		return selectedFile;
	}

	/**
	 * Получить файл для сохранения текста посредством диалога сохранения файла.
	 * 
	 * @return файл указанный пользователем или null, если пользователь отказался от
	 *         сохранения файла.
	 */
	private File showSaveDialogue() {
		JFileChooser chooser = getSaveChooser(file);
		int result = chooser.showSaveDialog(editor.getFrame());

		File selectedFile = null;
		if (result == JFileChooser.APPROVE_OPTION)
			selectedFile = chooser.getSelectedFile();
		return selectedFile;
	}

	/**
	 * Получить стандартное диалоговое окно для открытия файла программы настроенное
	 * в соответствии с нуждами программы.
	 * 
	 * @param currentFile текущий редактируемый файл.
	 * 
	 * @return настроенное диалоговое окно.
	 */
	private JFileChooser getChooser(File currentFile) {
		JFileChooser chooser = new JFileChooser();
		String curDir = (currentFile == null) ? "." : currentFile.getPath();
		chooser.setCurrentDirectory(new File(curDir));

		chooser.setFileFilter(new FileFilter() {

			@Override
			public boolean accept(File file) {
				if (file.getName().toLowerCase().endsWith("." + EXTENSION)) {
					return true;
				}
				if (file.isDirectory()) {
					return true;
				}
				return false;
			}

			@Override
			public String getDescription() {
				return DESCRIPTION;
			}
		});

		return chooser;
	}

	private JFileChooser getSaveChooser(File currentFile) {
		JFileChooser chooser = new JFileChooser();
		if (currentFile == null)
			chooser.setSelectedFile(new File("*.txt"));
		else
			chooser.setSelectedFile(currentFile);

		chooser.setFileFilter(new FileFilter() {

			@Override
			public boolean accept(File file) {
				if (file.getName().toLowerCase().endsWith("." + EXTENSION)) {
					return true;
				}
				if (file.isDirectory()) {
					return true;
				}
				return false;
			}

			@Override
			public String getDescription() {
				return DESCRIPTION;
			}
		});

		return chooser;
	}

	private boolean saveFileIfNecessary() {
		if (!fileChanged())
			return true;

		int retVal = editor.conf("Текст был изменён. Вы хотите сохранить изменения в файле?");
		if (retVal == JOptionPane.YES_OPTION) {
			saveFile();
			return !fileChanged();
		}
		if (retVal == JOptionPane.NO_OPTION) {
			return true;
		}
		return false;
	}

	private void addFileChangedListener() {

		// Понадобится работа с документом
		Document doc = editor.getPane().getDocument();

		// Замена UndoManager
		if (undoManager == null)
			undoManager = new UndoManager();

		doc.removeUndoableEditListener(undoManager);
		undoManager.discardAllEdits();
		doc.addUndoableEditListener(undoManager);

		if (act != null) {
			act.setEnabledUndo(undoManager.canUndo());
			act.setEnabledRedo(undoManager.canRedo());
		}

		// Замена слушателя документа
		doc.addDocumentListener(new DocumentListener() {
			{
				doc.removeDocumentListener(this);
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
	}

	private boolean fileChanged() {
		return undoManager == null ? false : undoManager.canUndo();
	}

	private File file = null;
	private WeekendTextEditor editor;
	private LastFiles lastFiles;
	private Finder finder;
	private Act act;
	private UndoManager undoManager;
}
