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

	@Override
	String computeStringRepresentation() {
		return file.name
	}

	@Override
	ConfigObject retrieveConfigMap() {
		ConfigSlurper slurper = new ConfigSlurper(Environment.currentEnvironment.name)
		ConfigObject config = slurper.parse(file.toURI().toURL())

		return config
	}
}
