package grails.plugin.configChooser.popup;

/**
 * @author Wadeck Follonier, wfollonier@proactive-partners.ch
 */
public class ChoiceValueCellRenderer<V extends IChoiceValue> extends AbstractCellRenderer<V> {
	private static final long serialVersionUID = 1;

	@Override
	protected String getStringRepresentation(V cell) {
		return cell.computeStringRepresentation();
	}
}
