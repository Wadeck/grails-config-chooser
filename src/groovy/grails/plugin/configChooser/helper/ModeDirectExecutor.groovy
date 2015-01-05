package grails.plugin.configChooser.helper

import grails.plugin.configChooser.exception.ConfigChooserInvalidConfigurationException
import grails.util.Environment
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import groovy.util.logging.Log4j

import org.codehaus.groovy.grails.commons.GrailsApplication

/**
 * @author Wadeck Follonier, wfollonier@proactive-partners.ch
 */
@Log4j
@CompileStatic
class ModeDirectExecutor implements IConfigChooserExecutor {
	ConfigObject mergedConfig

	ConfigObject chooseConfig(GrailsApplication application) {
		File directFile = retrieveDirectFile(mergedConfig)

		log.info "Loading directly the file ${ directFile.canonicalPath }"

		return new ConfigSlurper(Environment.current.name).parse(directFile.toURI().toURL())
	}

	private File retrieveDirectFile(ConfigObject config) {
		String filename = retrieveDirectFilename(config)

		File file = new File(filename)
		if (!file.exists()) {
			log.error "The path defined by 'grails.plugin.configChooser.directFile' does not exist"
			throw new ConfigChooserInvalidConfigurationException("The key 'grails.plugin.configChooser.directFile' must be a path to an existing file")
		}

		if (file.isDirectory()) {
			log.error "The path defined by 'grails.plugin.configChooser.directFile' leads to a directory, a file is required"
			throw new ConfigChooserInvalidConfigurationException("The key 'grails.plugin.configChooser.directFile' must be a path to a file and not a directory")
		}

		return file
	}

	/**
	 * Retrieve the config key 'grails.plugin.configChooser.directFile'
	 */
	private String retrieveDirectFilename(ConfigObject config) {
		def configDirectFile = getConfigDirectFile(config)

		if (configDirectFile instanceof String) {
			return configDirectFile
		}

		log.error "The mandatory config value assigned to 'grails.plugin.configChooser.directFile' is not recognized, value = ${ configDirectFile }"
		throw new ConfigChooserInvalidConfigurationException("The key 'grails.plugin.configChooser.directory' is not correctly set, a path to a file containing the configuration is required")
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	private getConfigDirectFile(ConfigObject config) {
		return config.grails.plugin.configChooser.directFile
	}
}
