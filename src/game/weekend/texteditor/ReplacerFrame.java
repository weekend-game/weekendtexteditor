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
 * A frame for specifying the substring, replacement substring, and search
 * attributes.
 */
@SuppressWarnings("serial")
public abstract class ReplacerFrame extends JDialog {

	/**
	 * Create a frame to specify the substring, replacement substring, and search
	 * attributes.
	 * 
	 * @param owner the frame in which the JEditorFrame is located.
	 */
	public ReplacerFrame(JFrame owner) {
		super(owner, Loc.get("replace"), false);

		// Default size and location
		Proper.setBounds(this, 370, 190, 460, 220);

		// Intercept Esc press to close frame
		InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		inputMap.put(KeyStroke.getKeyStroke("pressed ESCAPE"), "Exit");
		ActionMap aMap = getRootPane().getActionMap();
		aMap.put("Exit", new AbstractAction() {
			public void actionPerformed(ActionEvent actionEvent) {
				close();
			}
		});

		// Intercepting the closing of a frame to save its size and position
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				Proper.saveBounds(ReplacerFrame.this);
			}
		});

		// Displaying frame components
		createComponents();
	}

	/**
	 * Display frame components.
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
		g.addExtH(fldWhat, 5);
		g.addFixR(btnFind, 1);

		g.newLine();
		g.addFixL(lblHow, 1);
		g.addExtH(fldHow, 5);
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
	 * Focus in the What field.
	 */
	public void whatFocus() {
		fldWhat.requestFocus();
	}

	/**
	 * The actual search will be defined in Action.
	 */
	public abstract void find();

	/**
	 * The replacement itself will be defined in Action.
	 */
	public abstract void replace();

	/**
	 * Actually replacing everything from the cursor will be defined in Action.
	 */
	public abstract void replaceAll();

	/**
	 * Close frame.
	 */
	public void close() {
		Proper.saveBounds(ReplacerFrame.this);
		dispose();
	}

	/**
	 * Get a substring to search for in text.
	 * 
	 * @return substring to search for.
	 */
	public String getPattern() {
		return fldWhat.getText();
	}

	/**
	 * Set the substring to search for.
	 * 
	 * @param pattern substring to search for.
	 */
	public void setPattern(String pattern) {
		fldWhat.setText(pattern);
	}

	/**
	 * Get a substring to replace in the text.
	 * 
	 * @return substring to replace.
	 */
	public String getReplacer() {
		return fldHow.getText();
	}

	/**
	 * Set the substring to replace.
	 * 
	 * @param replacer substring to replace.
	 */
	public void setReplacer(String replacer) {
		fldHow.setText(replacer);
	}

	/**
	 * Get the case-insensitive search flag.
	 * 
	 * @return case-insensitive search flag.
	 */
	public boolean getCase() {
		return chkCase.isSelected();
	}

	/**
	 * Set the search flag to be case-insensitive.
	 * 
	 * @param caseSensitive case-insensitive search flag.
	 */
	public void setCase(boolean caseSensitive) {
		chkCase.setSelected(caseSensitive);
	}

	/**
	 * Get the search down flag.
	 * 
	 * @return search down flag.
	 */
	public boolean getFindDown() {
		return opbDown.isSelected();
	}

	/**
	 * Set the search down flag.
	 * 
	 * @param findDown search down flag.
	 */
	public void setFindDown(boolean findDown) {
		opbDown.setSelected(findDown);
	}

	/** Pattern */
	protected JTextField fldWhat;
	/** Replacement text */
	protected JTextField fldHow;
	/** Case sensitive */
	private JCheckBox chkCase;
	/** Search up */
	private JRadioButton opbUp;
	/** Search down */
	private JRadioButton opbDown;
}
