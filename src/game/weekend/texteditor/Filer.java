package game.weekend.texteditor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

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
	 */
	public Filer(WeekendTextEditor app, Editor editor, LastFiles lastFiles, Finder finder, Replacer replacer,
			Messenger messenger) {
		this.app = app;
		this.editor = editor;
		this.lastFiles = lastFiles;
		this.finder = finder;
		this.replacer = replacer;
		this.messenger = messenger;
	}

	/**
	 * Установить объект управляющий действиями.
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

		app.getFrame().setTitle(WeekendTextEditor.APP_NAME);

		file = null;

		editor.setText("");

		if (finder != null)
			finder.resetPosition();
		if (replacer != null)
			replacer.resetPosition();
	}

	/**
	 * Реализация "Открыть..."
	 */
	public void openFile() {
		if (!saveFileIfNecessary())
			return;

		File file = showOpenDialogue();
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
		if (file == null)
			return;

		if (!file.exists()) {
			// Если файл не обнаружился, то удаляю его из списка последних открытых файлов
			lastFiles.remove(file.getPath());

			// Выдаю сообщение об этом неприятном событии
			messenger.err("Файл " + file.getPath() + " не найден.");

		} else {
			try {
				String content = Files.readString(file.toPath());
				this.file = file;

				// Передаю прочитанное редактору
				editor.setText(content);

				// Отображаю имя открытого файла в заголовке приложения
				app.getFrame().setTitle(WeekendTextEditor.APP_NAME + " - " + file.getPath());

				// Запоминаю его в списке последних открытых файлов
				lastFiles.put(file.getPath());

			} catch (IOException e) {
				messenger.err("Не удалось открыть файл " + file.getPath() + ".\n" + e);
			}
		}

		act.refreshMenuFile();
	}

	/**
	 * Сохранить текст в указнный файл.
	 * 
	 * @param file файл для сохранения текста
	 */
	public void save(File file) {
		if (file == null)
			return;

		try {
			Files.write(file.toPath(), editor.getPane().getText().getBytes());
			this.file = file;
			editor.setChanged(false);

			// Отображаю имя файла в заголовке приложения
			app.getFrame().setTitle(WeekendTextEditor.APP_NAME + " - " + file.getPath());

			// Запоминаю его в списке последних открытых файлов
			lastFiles.put(file.getPath());

			act.refreshMenuFile();

			WeekendTextEditor.status.showMessage("Сохранено в файл " + file.getPath());

		} catch (IOException e) {
			messenger.err("Не удалось сохранить файл " + file.getPath() + ".\n" + e);
		}
	}

	private boolean saveFileIfNecessary() {
		if (!editor.isChanged())
			return true;

		int retVal = messenger.conf("Текст был изменён. Вы хотите сохранить изменения в файле?");
		if (retVal == JOptionPane.YES_OPTION) {
			saveFile();
			return !editor.isChanged();
		}
		if (retVal == JOptionPane.NO_OPTION) {
			return true;
		}
		return false;
	}

	/**
	 * Получить файл для открытия посредством диалога открытия файла.
	 * 
	 * @return файл указанный пользователем или null, если пользователь отказался от
	 *         открытия файла.
	 */
	private File showOpenDialogue() {
		JFileChooser chooser = getOpenChooser(file);
		int result = chooser.showOpenDialog(app.getFrame());

		File selectedFile = null;
		if (result == JFileChooser.APPROVE_OPTION) {
			selectedFile = chooser.getSelectedFile();
		}
		return selectedFile;
	}

	/**
	 * Получить стандартное диалоговое окно для открытия файла настроенное в
	 * соответствии с нуждами программы.
	 * 
	 * @param currentFile текущий редактируемый файл.
	 * 
	 * @return настроенное диалоговое окно.
	 */
	private JFileChooser getOpenChooser(File currentFile) {
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

	/**
	 * Получить файл для сохранения текста посредством диалога сохранения файла.
	 * 
	 * @return файл указанный пользователем или null, если пользователь отказался от
	 *         сохранения файла.
	 */
	private File showSaveDialogue() {
		JFileChooser chooser = getSaveChooser(file);
		int result = chooser.showSaveDialog(app.getFrame());

		File selectedFile = null;
		if (result == JFileChooser.APPROVE_OPTION)
			selectedFile = chooser.getSelectedFile();
		return selectedFile;
	}

	/**
	 * Получить стандартное диалоговое окно для сохранения в файл настроенное в
	 * соответствии с нуждами программы.
	 * 
	 * @param currentFile текущий редактируемый файл или null.
	 * 
	 * @return настроенное диалоговое окно.
	 */
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

	private File file = null;

	private WeekendTextEditor app;
	private Editor editor;
	private LastFiles lastFiles;
	private Finder finder;
	private Replacer replacer;
	private Messenger messenger;
	private Act act;
}
