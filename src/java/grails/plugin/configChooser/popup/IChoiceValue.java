package grails.plugin.configChooser.popup;

/**
 * Represents an abstract source of configuration (could be url, file, direct map, properties, etc.)
 * @author Wadeck Follonier, wfollonier@proactive-partners.ch
 */
public interface IChoiceValue {
	/**
	 * The String that will represent the value in the combobox
	 * Cached by ChooseDataModel
	 */
	String computeStringRepresentation();
}
