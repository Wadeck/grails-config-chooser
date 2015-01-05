package grails.plugin.configChooser.popup.test;

import grails.plugin.configChooser.popup.ChoosePopup;

import java.util.Arrays;

/**
 * @author Wadeck Follonier, wfollonier@proactive-partners.ch
 */
public class ChoosePopupMain {
	public static void main(String[] args) {
		ChoosePopup<ChoiceValueDummy> dialog = new ChoosePopup<ChoiceValueDummy>(3, true);

		ChooseDataDummy data = new ChooseDataDummy(Arrays.asList(
			new ChoiceValueDummy("Fichier 1", "content = Contenu du fichier 1\ncontent2 = Contenu 2"),
			new ChoiceValueDummy("Fichier 2", "content = Contenu du fichier 2\nbonus = track"),
			new ChoiceValueDummy("Fichier 3", "content = Contenu du fichier 3")
		)).setSelected(1);

		dialog.setData(data);

		dialog.askUser();

		if (dialog.wasCancelled()) {
			System.out.println("Choose cancelled");
		} else {
			System.out.println("Selected option: " + dialog.getSelectedValue().computeStringRepresentation());
		}

		System.exit(0);
	}
}
