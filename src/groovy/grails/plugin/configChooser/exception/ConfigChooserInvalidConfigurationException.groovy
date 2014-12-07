package grails.plugin.configChooser.exception

import groovy.transform.CompileStatic

/**
 * There is a missing element in the configuration file
 * @author Wadeck Follonier, wfollonier@proactive-partners.ch
 */
@CompileStatic
class ConfigChooserInvalidConfigurationException extends Exception{
	public ConfigChooserInvalidConfigurationException(String message) {
		super(message)
	}
}
