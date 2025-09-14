package game.weekend.texteditor;

import java.awt.Component;
import java.util.ArrayList;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Working with Look and feels.
 */
public class LaF {

	/** Default L&amp;F */
	public static String DEFAULT_LAF = "javax.swing.plaf.metal.MetalLookAndFeel";

	/**
	 * Create an object for working with Look and feels.
	 */
	public LaF() {
	}

	/**
	 * Set the specified L&amp;F.
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
	 * Get current L&amp;F.
	 * 
	 * @return current L&amp;F.
	 */
	public String getLookAndFeel() {
		return Proper.getProperty("LaF", DEFAULT_LAF);
	}

	/**
	 * Specify the list of components to update.
	 * 
	 * @param components list of components
	 */
	public void setUpdateComponents(Component... components) {
		for (Component c : components)
			this.components.add(c);
	}

	/**
	 * Add component for renewal L&amp;F.
	 * 
	 * @param component component
	 */
	public void addUpdateComponent(Component component) {
		components.add(component);
	}

	/**
	 * Delete component from list of components for L&amp;F renewal.
	 * 
	 * @param component component
	 */
	public void removeUpdateComponent(Component component) {
		components.remove(component);
	}

	private ArrayList<Component> components = new ArrayList<Component>();
}
