package grails.plugin.configChooser.popup

import groovy.transform.CompileStatic

import javax.swing.AbstractListModel
import javax.swing.ComboBoxModel
import javax.swing.ListCellRenderer

/**
 * The data model for the combobox and the textpane of the popup
 */
@CompileStatic
class ChooseData implements IChooseData<IConfigChooserValue> {
	private Collection<IConfigChooserValue> values
	/**
	 * Contain the file names and their respective content
	 * Serve as cache
	 */
	private Map<IConfigChooserValue, String> valuesToContent = [:]
	/**
	 * Cache the string representation of the different value
	 */
	private Map<IConfigChooserValue, String> valuesToRepresentation = [:]
	/**
	 * Share the selectedOption with the comboModel and the fileContent
	 */
	IConfigChooserValue selectedValue

	ChooseData(Collection<IConfigChooserValue> values) {
		this.values = values
		init()
		selectedValue = this.values[0]
	}

	/**
	 * We precompute the string representation but not the content
	 * because it could be more costly
	 */
	private void init() {

		values.each { IConfigChooserValue value -> valuesToRepresentation[value] = value.computeStringRepresentation() }

		// sort using the label that will be displayed in the combobox
		values.sort { IConfigChooserValue value -> valuesToRepresentation[value] }
	}

	String getCurrentFileContent() {
		if (!valuesToContent.containsKey(selectedValue)) {
			ConfigObject config = selectedValue.retrieveConfigMap()

			//TODO add content transformer
			String content = config.collect { key, value -> "$key = $value" }.join('\n')

			// cache the content
			valuesToContent[selectedValue] = content
		}

		return valuesToContent[selectedValue]
	}

	ComboBoxModel<IConfigChooserValue> getListModel() {
		return new ChooseComboBoxModel()
	}

	ListCellRenderer<?> getRenderer() {
		return new ChoiceValueCellRenderer()
	}

	IConfigChooserValue getSelectedItem() {
		return selectedValue
	}

	private class ChooseComboBoxModel extends AbstractListModel<IConfigChooserValue> implements ComboBoxModel<IConfigChooserValue> {
		IConfigChooserValue getElementAt(int index) {
			return values[index]
		}

		int getSize() {
			return values.size()
		}

		def getSelectedItem() {
			return selectedValue
		}

		void setSelectedItem(Object item) {
			selectedValue = (IConfigChooserValue) item
		}
	}
}