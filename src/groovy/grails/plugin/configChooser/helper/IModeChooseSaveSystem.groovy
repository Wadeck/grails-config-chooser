package grails.plugin.configChooser.helper

import grails.plugin.configChooser.popup.IConfigChooserValue

/**
 * @author Wadeck Follonier, wfollonier@proactive-partners.ch
 */
interface IModeChooseSaveSystem {
	/** called before any other method */
	public void init()
	/**
	 * @return null meaning there is no save yet, String used to find the saved choice
	 */
	public String loadLastChoice()
	public void saveChoice(IConfigChooserValue value)
}
