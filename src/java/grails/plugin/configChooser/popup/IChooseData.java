package grails.plugin.configChooser.popup;

import javax.swing.*;

/**
 * @author Wadeck Follonier, wfollonier@proactive-partners.ch
 */
public interface IChooseData<T extends IChoiceValue> {
	String getCurrentFileContent();
	ComboBoxModel<T> getListModel();
	ListCellRenderer<T> getRenderer();

	/**
	 * Used to retrieve the selected item at the end of the dialog
	 */
	T getSelectedItem();
}
