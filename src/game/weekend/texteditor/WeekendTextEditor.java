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
 * Приложение WeekendTextEditor.
 */
public class WeekendTextEditor {

	/** Название приложения */
	public static final String APP_NAME = "WeekendTextEditor";

	/** Версия */
	public static final String APP_VERSION = Loc.get("version") + " 01.20 " + Loc.get("from") + " 17.08.2025";

	/** Copyright */
	public static final String APP_COPYRIGHT = "(c) Weekend Game, 2025";

	/** Назначение */
	public static final String APP_OTHER = Loc.get("weekend_text_editor");

	/** Путь к пиктограммам */
	public static final String IMAGE_PATH = "/game/weekend/texteditor/images/";

	/** Строка состояния */
	public static final StatusBar status = new StatusBar();

	/**
	 * Создать приложение. Создаётся окно приложения, объекты необходимые для работы
	 * и элементы управления окна.
	 */
	public WeekendTextEditor() {
		// Хранитель настроек между сеансами работы приложения
		Proper.read(APP_NAME);

		// Frame приложения
		frame = new JFrame(APP_NAME);
		makeJFrame();

		// Сообщения вываваемые для пользователя
		Messenger messenger = new Messenger(frame);

		// Редактор текста
		editor = new Editor();
		frame.getContentPane().add(editor.getScrollPane(), BorderLayout.CENTER);

		// Хранитель имен последних открытых файлов (пяти, например)
		lastFiles = new LastFiles(5);

		// Look and Feels
		LaF laf = new LaF();

		// Поиск в открытом файле
		Finder finder = new Finder(editor.getPane(), frame, laf);

		// Замена в открытом файлеs
		Replacer replacer = new Replacer(editor.getPane(), frame, laf);

		// Работа с файлами
		filer = new Filer(this, editor, lastFiles, finder, replacer, messenger);

		// Работа с меню и инструментальной линейкой
		act = new Act(this, editor, filer, lastFiles, finder, replacer, laf, messenger);

		// Меню
		frame.setJMenuBar(act.getMenuBar());

		// Инструментальная линейка
		toolbarOn = Proper.getProperty("ToolbarON", "TRUE").equalsIgnoreCase("TRUE") ? true : false;
		toolbar = act.getToolBar();
		if (toolbarOn)
			frame.getContentPane().add(toolbar, BorderLayout.NORTH);

		// Строка состояния
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
	 * Настройка основного окна приложения.
	 */
	private void makeJFrame() {
		// Ничего не делать при попытке закрыть окно, но
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		// перехватить это событие
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// и вызвать этот метод. В нем будут сохраняться настройки
				close();
			}
		});

		// Для ContentPane ставлю менеджер расположения BorderLayout
		// (в середине будет JEditorPane для отображения выписки, сверху toolbar)
		Container cp = frame.getContentPane();
		cp.setLayout(new BorderLayout());

		// Восстанавливаю расположение и размеры фрейма, которые он имел в прошлом
		// сеансе работы
		Proper.setBounds(frame);
	}

	/**
	 * Запустить приложение.
	 *
	 * @param args не используется.
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
	 * Отображать инструментальную линейку.
	 * 
	 * @param toolbarON true - отображать, false - не отображать
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
	 * Отображать строку состояния.
	 * 
	 * @param statusbarOn отображать или не отображать
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
	 * Закрыть приложение.
	 * 
	 * Сохраняет всё, что нужно сохранить для восстановления при следующем запуске
	 */
	public void close() {
		Proper.saveBounds(frame);
		frame.dispose();
		lastFiles.save();
		Proper.save();
	}

	/**
	 * Получить основное окно приложения.
	 * 
	 * @return основное окно приложения.
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
