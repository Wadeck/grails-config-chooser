package grails.plugin.configChooser.popup

import groovy.transform.CompileStatic

/**
 * @author Wadeck Follonier, wfollonier@proactive-partners.ch
 */
@CompileStatic
interface IConfigChooserValue extends IChoiceValue {
	ConfigObject retrieveConfigMap()
}
