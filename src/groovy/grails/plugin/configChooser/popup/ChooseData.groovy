package grails.plugin.configChooser.popup

import groovy.transform.CompileStatic

import javax.swing.*

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
	private Map<IConfigChooserValue, String> valuesToContent
	/**
	 * Cache the string representation of the different value
	 */
	private Map<IConfigChooserValue, String> valuesToRepresentation
	/**
	 * Share the selectedOption with the comboModel and the fileContent
	 */
	private IConfigChooserValue selectedValue

	ChooseData(Collection<IConfigChooserValue> values) {
		this.init(values)
		this.selectedValue = values[0]
	}

	public setSelectedValue(IConfigChooserValue selectedValue) {
		this.selectedValue = selectedValue
	}

	/**
	 * We precompute the string representation but not the content
	 * because it could be more costly
	 */
	private void init(Collection<IConfigChooserValue> values) {
		this.values = values
		this.valuesToRepresentation = [:]
		this.valuesToContent = [:]

		values.each { IConfigChooserValue value ->
			valuesToRepresentation.put(value, value.computeStringRepresentation())
		}

		// sort using the label that will be displayed in the combobox
		values.sort { IConfigChooserValue value ->
			valuesToRepresentation[value]
		}
	}

	public String getCurrentFileContent() {
		if (!valuesToContent.containsKey(selectedValue)) {
			ConfigObject config = selectedValue.retrieveConfigMap()

			//TODO add content transformer
			String content = config.collect { String key, value ->
				"$key = $value"
			}.join('\n')

			// cache the content
			valuesToContent.put(selectedValue, content)
		}

		return valuesToContent[selectedValue]
	}

	public ComboBoxModel<IConfigChooserValue> getListModel() {
		ChooseComboBoxModel result = new ChooseComboBoxModel()
		return result;
	}

	@Override
	ListCellRenderer getRenderer() {
		return new ChoiceValueCellRenderer()
	}

	@Override
	IConfigChooserValue getSelectedItem() {
		return selectedValue
	}

	private class ChooseComboBoxModel extends AbstractListModel<IConfigChooserValue> implements ComboBoxModel<IConfigChooserValue> {
		@Override
		public IConfigChooserValue getElementAt(int index) {
			IConfigChooserValue value = values[index]
			return value
		}

		@Override
		public int getSize() {
			return values.size()
		}

		@Override
		public Object getSelectedItem() {
			return selectedValue
		}

		@Override
		public void setSelectedItem(Object item) {
			selectedValue = (IConfigChooserValue) item
		}
	}
}