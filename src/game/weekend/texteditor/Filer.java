package game.weekend.texteditor;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Files;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

/**
 * Working with files.
 */
public class Filer {

	public static final Charset CHARSET = Charset.forName("UTF-8");

	/** File extension */
	public static final String EXTENSION = "txt";

	/** File name */
	public static final String DESCRIPTION = "*." + EXTENSION + " - " + Loc.get("text_file");

	/**
	 * Create a file handling object.
	 * 
	 * @param viewer the main object of the application.
	 */
	public Filer(WeekendTextEditor app, Editor editor, LastFiles lastFiles, Finder finder, Replacer replacer) {
		this.app = app;
		this.editor = editor;
		this.lastFiles = lastFiles;
		this.finder = finder;
		this.replacer = replacer;
	}

	/**
	 * Set the Act object.
	 */
	public void setAct(Act act) {
		this.act = act;
	}

	/**
	 * "New"
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
	 * "Open..."
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
	 * "Save"
	 */
	public void saveFile() {
		if (file != null)
			save(file);
		else
			saveAsFile();
	}

	/**
	 * "Save as..."
	 */
	public void saveAsFile() {
		File file = showSaveDialogue();
		if (file != null) {
			save(file);
		}
	}

	/**
	 * Open file by name
	 */
	public void openFileByName(File file) {
		if (!saveFileIfNecessary())
			return;

		open(file);
	}

	/**
	 * Open the specified file and display it.
	 * 
	 * @param file file to open
	 */
	public void open(File file) {
		if (file == null)
			return;

		if (!file.exists()) {
			// If the file is not found, then I delete it from the list of recently opened
			// files
			lastFiles.remove(file.getPath());

			// I am issuing a message about this unpleasant event.
			Mes.err(Loc.get("file") + " " + file.getPath() + " " + Loc.get("not_found") + ".");

		} else {
			try {
				String content = Files.readString(file.toPath(), CHARSET);
				this.file = file;

				// I'm passing on what I've read to the editor.
				editor.setText(content);

				// Display the name of the open file in the application title
				app.getFrame().setTitle(WeekendTextEditor.APP_NAME + " - " + file.getPath());

				// I remember it in the list of recently opened files
				lastFiles.put(file.getPath());

			} catch (IOException e) {
				Mes.err(Loc.get("failed_to_open_file") + " " + file.getPath() + ".\n" + e);
			}
		}

		act.refreshMenuFile();
	}

	/**
	 * Save text to the specified file.
	 * 
	 * @param file file to save text
	 */
	public void save(File file) {
		if (file == null)
			return;

		try {
			CharsetEncoder encoder = CHARSET.newEncoder();
			ByteBuffer output = encoder.encode(CharBuffer.wrap(editor.getPane().getText()));
			byte[] bytes = new byte[output.remaining()];
			output.get(bytes);

			Files.write(file.toPath(), bytes);

			this.file = file;
			editor.setChanged(false);

			// Display file name in application title
			app.getFrame().setTitle(WeekendTextEditor.APP_NAME + " - " + file.getPath());

			// I remember it in the list of recently opened files
			lastFiles.put(file.getPath());

			act.refreshMenuFile();

			WeekendTextEditor.status.showMessage(Loc.get("saved_to_file") + " " + file.getPath());

		} catch (IOException e) {
			Mes.err(Loc.get("failed_to_save_file") + " " + file.getPath() + ".\n" + e);
		}
	}

	public boolean saveFileIfNecessary() {
		if (!editor.isChanged())
			return true;

		int retVal = Mes
				.conf(Loc.get("the_text_has_been_changed") + ". " + Loc.get("do_you_want_to_save_the_changes") + "?");
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
	 * Get a file to open via the file open dialog.
	 * 
	 * @return the file specified by the user, or null if the user declined to open
	 *         the file.
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
	 * Get a standard dialog box for opening a program file, customized according to
	 * the needs of the program.
	 * 
	 * @param currentFile the current file being edited.
	 * 
	 * @return customized dialog box.
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
	 * Get a file to save the text through the file save dialog.
	 * 
	 * @return the file specified by the user, or null if the user declined to save
	 *         the file.
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
	 * Get a standard dialog box for saving to a file, customized according to the
	 * needs of the program.
	 * 
	 * @param currentFile the current file being edited or null.
	 * 
	 * @return customized dialog box.
	 */
	private JFileChooser getSaveChooser(File currentFile) {
		JFileChooser chooser = new JFileChooser();
		if (currentFile == null)
			chooser.setSelectedFile(new File("*." + EXTENSION));
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
	private Act act;
}
