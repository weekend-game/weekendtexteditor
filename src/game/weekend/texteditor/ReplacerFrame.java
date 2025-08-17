package game.weekend.texteditor;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

/**
 * Фрейм для указания подстроки, подстроки замены и аттрибутов поиска.
 */
@SuppressWarnings("serial")
public abstract class ReplacerFrame extends JDialog {

	/**
	 * Создать фрейм для указания подстроки, подстроки замены и аттрибутов поиска.
	 * 
	 * @param owner фрейм в котором расположена JEditorFrame.
	 */
	public ReplacerFrame(JFrame owner) {
		super(owner, Loc.get("replace"), false);

		// Размер и расположение по умолчанию
		Proper.setBounds(this, 370, 190, 460, 220);

		// Перехват нажатия Esc для закрытия фрейма
		InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		inputMap.put(KeyStroke.getKeyStroke("pressed ESCAPE"), "Exit");
		ActionMap aMap = getRootPane().getActionMap();
		aMap.put("Exit", new AbstractAction() {
			public void actionPerformed(ActionEvent actionEvent) {
				close();
			}
		});

		// Перезват закрытия фрейма для сохранения его размеров и расположния
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				Proper.saveBounds(ReplacerFrame.this);
			}
		});

		// Отображаю компоненты окна
		createComponents();
	}

	/**
	 * Отобразить компоненты окна.
	 */
	private void createComponents() {
		JLabel lblWhat = new JLabel(Loc.get("what") + ":");
		fldWhat = new JTextField(50);
		JLabel lblHow = new JLabel(Loc.get("how") + ":");
		fldHow = new JTextField(50);

		chkCase = new JCheckBox(Loc.get("case_sensitive"));

		opbUp = new JRadioButton(Loc.get("back"));
		opbDown = new JRadioButton(Loc.get("forward"));
		ButtonGroup bngDir = new ButtonGroup();
		bngDir.add(opbUp);
		bngDir.add(opbDown);
		JPanel panDir = new JPanel();
		panDir.setBorder(BorderFactory.createEtchedBorder());
		panDir.setLayout(new FlowLayout());
		panDir.add(opbUp);
		panDir.add(opbDown);

		JButton btnFind = new JButton(Loc.get("find_next"));
		getRootPane().setDefaultButton(btnFind);
		Dimension psFind = btnFind.getPreferredSize();
		psFind.width = 110;
		btnFind.setMinimumSize(psFind);
		btnFind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				find();
			}
		});

		JButton btnReplace = new JButton(Loc.get("replace"));
		Dimension psReplace = btnReplace.getPreferredSize();
		psReplace.width = 110;
		btnReplace.setMinimumSize(psReplace);
		btnReplace.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				replace();
			}
		});

		JButton btnReplaceAll = new JButton(Loc.get("replace_all"));
		Dimension psReplaceAll = btnReplaceAll.getPreferredSize();
		psReplaceAll.width = 110;
		btnReplaceAll.setMinimumSize(psReplaceAll);
		btnReplaceAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				replaceAll();
			}
		});

		JButton btnCancel = new JButton(Loc.get("cancel"));
		Dimension psCancel = btnCancel.getPreferredSize();
		psCancel.width = 110;
		btnCancel.setMinimumSize(psCancel);
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				close();
			}
		});

		GBL g = new GBL((JPanel) getContentPane(), true);

		g.addFixL(lblWhat, 1);
		g.addExtX(fldWhat, 5);
		g.addFixR(btnFind, 1);

		g.newLine();
		g.addFixL(lblHow, 1);
		g.addExtX(fldHow, 5);
		g.addFixR(btnReplace, 1);

		g.newLine();
		g.addHor(6);
		g.addFixR(btnReplaceAll, 1);

		g.newLine();
		g.addHor(6);
		g.addFixR(btnCancel, 1);

		g.newLine();
		g.addFixL(chkCase, 4);
		g.addFixL(panDir, 2);
	}

	/**
	 * Фокус в поле What.
	 */
	public void whatFocus() {
		fldWhat.requestFocus();
	}

	/**
	 * Собственно поиск. Будет определен в Action.
	 */
	public abstract void find();

	/**
	 * Собственно замена. Будет определен в Action.
	 */
	public abstract void replace();

	/**
	 * Собственно замена всего от курсора. Будет определен в Action.
	 */
	public abstract void replaceAll();

	/**
	 * Закрыть фрейм.
	 */
	public void close() {
		Proper.saveBounds(ReplacerFrame.this);
		dispose();
	}

	/**
	 * Получить подстроку для поиска в тексте.
	 * 
	 * @return подстрока для поиска.
	 */
	public String getPattern() {
		return fldWhat.getText();
	}

	/**
	 * Установить подстроку для поиска.
	 * 
	 * @param pattern подстрока для поиска.
	 */
	public void setPattern(String pattern) {
		fldWhat.setText(pattern);
	}

	/**
	 * Получить подстроку для замещения в тексте.
	 * 
	 * @return подстрока для замещения.
	 */
	public String getReplacer() {
		return fldHow.getText();
	}

	/**
	 * Установить подстроку для замещения.
	 * 
	 * @param replacer подстрока для замещения.
	 */
	public void setReplacer(String replacer) {
		fldHow.setText(replacer);
	}

	/**
	 * Получить признак поиска без учёта регистра.
	 * 
	 * @return признак поиска без учёта регистра.
	 */
	public boolean getCase() {
		return chkCase.isSelected();
	}

	/**
	 * Установить признак поиска без учёта регистра.
	 * 
	 * @param caseSensitive признак поиска без учёта регистра.
	 */
	public void setCase(boolean caseSensitive) {
		chkCase.setSelected(caseSensitive);
	}

	/**
	 * Получить признак поиска вниз.
	 * 
	 * @return признак поиска вниз.
	 */
	public boolean getFindDown() {
		return opbDown.isSelected();
	}

	/**
	 * Установить признак поиска вниз.
	 * 
	 * @param findDown признак поиска вниз.
	 */
	public void setFindDown(boolean findDown) {
		opbDown.setSelected(findDown);
	}

	/** Шаблон */
	protected JTextField fldWhat;
	/** Текст замены */
	protected JTextField fldHow;
	/** Учитывать регистр */
	private JCheckBox chkCase;
	/** Искать вверх */
	private JRadioButton opbUp;
	/** Искать вниз */
	private JRadioButton opbDown;
}
