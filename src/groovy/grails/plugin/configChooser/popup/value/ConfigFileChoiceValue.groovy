package grails.plugin.configChooser.popup.value

import grails.plugin.configChooser.popup.IConfigChooserValue
import grails.util.Environment
import groovy.transform.CompileStatic

/**
 * @author Wadeck Follonier, wfollonier@proactive-partners.ch
 */
@CompileStatic
class ConfigFileChoiceValue implements IConfigChooserValue {
	File file

	ConfigFileChoiceValue(File file) {
		this.file = file
	}

	String computeStringRepresentation() {
		return file.name
	}

	ConfigObject retrieveConfigMap() {
		return new ConfigSlurper(Environment.current.name).parse(file.toURI().toURL())
	}
}
