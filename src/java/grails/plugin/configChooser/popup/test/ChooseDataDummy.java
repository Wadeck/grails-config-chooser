package grails.plugin.configChooser.popup.test;

import grails.plugin.configChooser.popup.ChoiceValueCellRenderer;
import grails.plugin.configChooser.popup.IChooseData;

import javax.swing.*;
import java.util.List;

/**
 * @author Wadeck Follonier, wfollonier@proactive-partners.ch
 */
public class ChooseDataDummy implements IChooseData<ChoiceValueDummy> {

	List<ChoiceValueDummy> values;
	ChoiceValueDummy selected;

	public ChooseDataDummy(List<ChoiceValueDummy> values) {
		this.values = values;
		// by default we select the first element
		this.selected = values.get(0);
	}

	public ChooseDataDummy setSelected(int index) {
		this.selected = values.get(index);
		return this;
	}

	public ChooseDataDummy setSelected(ChoiceValueDummy selected) {
		this.selected = selected;
		return this;
	}

	@Override
	public ComboBoxModel<ChoiceValueDummy> getListModel() {
		return new DummyComboBoxModel();
	}

	@SuppressWarnings("unchecked")
	@Override
	public ListCellRenderer<ChoiceValueDummy> getRenderer() {
		return new ChoiceValueCellRenderer();
	}

	@Override
	public String getCurrentFileContent() {
		return selected.getContent();
	}

	@Override
	public ChoiceValueDummy getSelectedItem() {
		return selected;
	}

	private class DummyComboBoxModel extends AbstractListModel<ChoiceValueDummy> implements ComboBoxModel<ChoiceValueDummy> {
		@Override
		public ChoiceValueDummy getElementAt(int index) {
			ChoiceValueDummy value = values.get(index);
			return value;
		}

		@Override
		public int getSize() {
			return values.size();
		}

		@Override
		public Object getSelectedItem() {
			return selected;
		}

		@Override
		public void setSelectedItem(Object item) {
			selected = (ChoiceValueDummy) item;
		}
	}
}
