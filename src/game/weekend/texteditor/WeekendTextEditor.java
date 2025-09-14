package game.weekend.texteditor;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * WeekendTextEditor application.
 */
public class WeekendTextEditor {

	/** Application name */
	public static final String APP_NAME = "WeekendTextEditor";

	/** Version */
	public static final String APP_VERSION = "01.30";

	/** Date */
	public static final String APP_DATE = "14.09.2025";

	/** Copyright */
	public static final String APP_COPYRIGHT = "(c) Weekend Game, 2025";

	/** Purpose */
	public static final String APP_OTHER = "weekend_text_editor";

	/** Path to pictograms */
	public static final String IMAGE_PATH = "/game/weekend/texteditor/images/";

	/** Status bar */
	public static final StatusBar status = new StatusBar();

	/**
	 * Create an application. The application frame, objects required for operation,
	 * and frame controls are created.
	 */
	public WeekendTextEditor() {
		// Keeper of settings between application sessions
		Proper.read(APP_NAME);

		// Interface language
		Loc.setLanguage(Proper.getProperty("Language", "en"));

		// Application frame
		frame = new JFrame(APP_NAME);
		makeJFrame();

		// Messages
		Messenger messenger = new Messenger(frame);

		// Text editor
		editor = new Editor();
		frame.getContentPane().add(editor.getScrollPane(), BorderLayout.CENTER);

		// Keeper of names of the last opened files (five, for example)
		lastFiles = new LastFiles(5);

		// Look and Feels
		LaF laf = new LaF();

		// Search in an open file
		Finder finder = new Finder(editor.getPane(), frame, laf);

		// Replace in open file
		Replacer replacer = new Replacer(editor.getPane(), frame, laf);

		// Working with files
		filer = new Filer(this, editor, lastFiles, finder, replacer, messenger);

		// Working with menus and toolbars
		act = new Act(this, editor, filer, lastFiles, finder, replacer, laf, messenger);

		// Menu
		frame.setJMenuBar(act.getMenuBar());

		// Toolbar
		toolbarOn = Proper.getProperty("ToolbarON", "TRUE").equalsIgnoreCase("TRUE") ? true : false;
		toolbar = act.getToolBar();
		if (toolbarOn)
			frame.getContentPane().add(toolbar, BorderLayout.NORTH);

		// Status bar
		statusbarOn = Proper.getProperty("StatusbarON", "TRUE").equalsIgnoreCase("TRUE") ? true : false;
		statusbar = WeekendTextEditor.status.getPanel();
		if (statusbarOn)
			frame.getContentPane().add(statusbar, BorderLayout.SOUTH);

		editor.setAct(act);
		editor.setFiler(filer);
		filer.setAct(act);
		filer.newFile();

		laf.setUpdateComponents(frame, editor.getPopupMenu(), toolbar, statusbar);
		laf.setLookAndFeel(laf.getLookAndFeel());
	}

	/**
	 * Customizing the main application frame.
	 */
	private void makeJFrame() {
		// Do nothing when trying to close the window, but
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		// intercept this event
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// and call this method. It will save the settings
				close();
			}
		});

		// For ContentPane I set BorderLayout layout manager
		Container cp = frame.getContentPane();
		cp.setLayout(new BorderLayout());

		// I restore the position and size of the frame that it had in the previous work
		// session
		Proper.setBounds(frame);
	}

	/**
	 * Run application .
	 *
	 * @param args not used.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				WeekendTextEditor bv = new WeekendTextEditor();
				bv.getFrame().setVisible(true);
			}
		});
	}

	/**
	 * Display the toolbar.
	 * 
	 * @param toolbarON true - display, false - do not display
	 */
	public void setTooolbarON(boolean toolbarON) {
		this.toolbarOn = toolbarON;
		if (toolbarOn)
			frame.getContentPane().add(toolbar, BorderLayout.NORTH);
		else
			frame.getContentPane().remove(toolbar);

		frame.setVisible(true);
		Proper.setProperty("ToolbarON", toolbarOn ? "TRUE" : "FALSE");
	}

	/**
	 * Display status bar.
	 * 
	 * @param statusbarOn true - display, false - do not display
	 */
	public void setStatusbarON(boolean statusbarOn) {
		this.statusbarOn = statusbarOn;
		if (statusbarOn)
			frame.getContentPane().add(statusbar, BorderLayout.SOUTH);
		else
			frame.getContentPane().remove(statusbar);

		frame.setVisible(true);
		Proper.setProperty("StatusbarON", statusbarOn ? "TRUE" : "FALSE");
	}

	/**
	 * Close application.
	 * 
	 * Saves everything that needs to be saved for restoration on next startup
	 */
	public void close() {
		Proper.saveBounds(frame);
		frame.dispose();
		lastFiles.save();
		Proper.save();
	}

	/**
	 * Get the main application frame.
	 * 
	 * @return main application frame.
	 */
	public JFrame getFrame() {
		return frame;
	}

	private JFrame frame;
	private Editor editor;
	private JToolBar toolbar;
	private JPanel statusbar;
	private Act act;
	private Filer filer;
	private LastFiles lastFiles;

	private boolean toolbarOn;
	private boolean statusbarOn;
}
