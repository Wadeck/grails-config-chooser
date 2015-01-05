package grails.plugin.configChooser.popup.test;

import grails.plugin.configChooser.popup.ChoiceValueCellRenderer;
import grails.plugin.configChooser.popup.IChooseData;

import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.ListCellRenderer;

/**
 * @author Wadeck Follonier, wfollonier@proactive-partners.ch
 */
public class ChooseDataDummy implements IChooseData<ChoiceValueDummy> {

	List<ChoiceValueDummy> values;
	ChoiceValueDummy selected;

	public ChooseDataDummy(List<ChoiceValueDummy> values) {
		this.values = values;
		// by default we select the first element
		setSelected(0);
	}

	public ChooseDataDummy setSelected(int index) {
		selected = values.get(index);
		return this;
	}

	public ChooseDataDummy setSelected(ChoiceValueDummy selected) {
		this.selected = selected;
		return this;
	}

	public ComboBoxModel<ChoiceValueDummy> getListModel() {
		return new DummyComboBoxModel();
	}

	@SuppressWarnings("unchecked")
	public ListCellRenderer<ChoiceValueDummy> getRenderer() {
		return new ChoiceValueCellRenderer();
	}

	public String getCurrentFileContent() {
		return selected.getContent();
	}

	public ChoiceValueDummy getSelectedItem() {
		return selected;
	}

	private class DummyComboBoxModel extends AbstractListModel<ChoiceValueDummy> implements ComboBoxModel<ChoiceValueDummy> {
		private static final long serialVersionUID = 1;

		public ChoiceValueDummy getElementAt(int index) {
			return values.get(index);
		}

		public int getSize() {
			return values.size();
		}

		public Object getSelectedItem() {
			return selected;
		}

		public void setSelectedItem(Object item) {
			selected = (ChoiceValueDummy) item;
		}
	}
}
