package grails.plugin.configChooser.popup;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 * @author Wadeck Follonier, wfollonier@proactive-partners.ch
 */
public abstract class AbstractCellRenderer<C> extends DefaultListCellRenderer {
	private static final long serialVersionUID = 1;

	@Override
	public Component getListCellRendererComponent(@SuppressWarnings("rawtypes") JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		@SuppressWarnings("unchecked")
		String cellLabel = getStringRepresentation((C) value);
		return super.getListCellRendererComponent(list, cellLabel, index, isSelected, cellHasFocus);
	}

	protected abstract String getStringRepresentation(C cell);
}
