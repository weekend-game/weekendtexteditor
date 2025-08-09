package game.weekend.texteditor;

import java.awt.Component;
import java.util.ArrayList;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Работа с LookAndFeel-ами.
 */
public class LaF {

	/** L&amp;F по умолчанию */
	public static String DEFAULT_LAF = "javax.swing.plaf.metal.MetalLookAndFeel";

	/**
	 * Создать объект для работы с LookAndFeel-ами.
	 */
	public LaF() {
	}

	/**
	 * Установить указанный L&amp;F.
	 * 
	 * @param className L&amp;F.
	 */
	public void setLookAndFeel(String className) {
		try {
			UIManager.setLookAndFeel(className);

			for (Component c : components)
				SwingUtilities.updateComponentTreeUI(c);

			Proper.setProperty("LaF", className);
		} catch (Exception e) {
			setLookAndFeel(DEFAULT_LAF);
		}
	}

	/**
	 * Получить текущий L&amp;F.
	 * 
	 * @return текущий L&amp;F.
	 */
	public String getLookAndFeel() {
		return Proper.getProperty("LaF", DEFAULT_LAF);
	}

	/**
	 * Указать перечень компонентов для обнавления L&amp;F.
	 * 
	 * @param components перечень компонентов
	 */
	public void setUpdateComponents(Component... components) {
		for (Component c : components)
			this.components.add(c);
	}

	/**
	 * Добавить компонент для обновления L&amp;F.
	 * 
	 * @param component компонент
	 */
	public void addUpdateComponent(Component component) {
		components.add(component);
	}

	/**
	 * Удалить компонент из списка обновления L&amp;F.
	 * 
	 * @param component компонент
	 */
	public void removeUpdateComponent(Component component) {
		components.remove(component);
	}

	private ArrayList<Component> components = new ArrayList<Component>();
}
