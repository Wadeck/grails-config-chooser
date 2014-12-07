package grails.plugin.configChooser.popup;

import javax.swing.*;
import java.awt.*;

/**
 * @author Wadeck Follonier, wfollonier@proactive-partners.ch
 */
public abstract class AbstractCellRenderer<C> extends DefaultListCellRenderer {
	@Override
	@SuppressWarnings("unchecked")
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		C cell = (C) value;
		String cellLabel = this.getStringRepresentation(cell);
		return super.getListCellRendererComponent(list, cellLabel, index, isSelected, cellHasFocus);
	}

	protected abstract String getStringRepresentation(C cell);
}
