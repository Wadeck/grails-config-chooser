package grails.plugin.configChooser.helper

import grails.plugin.configChooser.popup.IConfigChooserValue

import org.codehaus.groovy.grails.commons.GrailsApplication

/**
 * @author Wadeck Follonier, wfollonier@proactive-partners.ch
 */
interface IModeChooseSaveSystem {
	/** called before any other method */
	void init(GrailsApplication application)
	/**
	 * @return null meaning there is no save yet, String used to find the saved choice
	 */
	String loadLastChoice()
	void saveChoice(IConfigChooserValue value)
}
