package grails.plugin.configChooser.helper

import grails.plugin.configChooser.exception.ConfigChooserInvalidConfigurationException
import grails.util.Environment
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import groovy.util.logging.Log4j

/**
 * @author Wadeck Follonier, wfollonier@proactive-partners.ch
 */
@Log4j
@CompileStatic
class ModeDirectExecutor implements IConfigChooserExecutor {
	ConfigObject mergedConfig

	@Override
	ConfigObject chooseConfig() {
		File directFile = this.retrieveDirectFile(mergedConfig)

		log.info "Loading directly the file ${ directFile.canonicalPath }"

		ConfigSlurper slurper = new ConfigSlurper(Environment.currentEnvironment.name)
		ConfigObject config = slurper.parse(directFile.toURI().toURL())

		ConfigObject result = config
		return result
	}

	private File retrieveDirectFile(ConfigObject config) {
		String filename = this.retrieveDirectFilename(config)

		File file = new File(filename)
		if (!file.exists()) {
			log.error "The path defined by 'grails.plugin.configChooser.directFile' does not exist"
			throw new ConfigChooserInvalidConfigurationException("The key 'grails.plugin.configChooser.directFile' must be a path to an existing file")
		} else if (file.isDirectory()) {
			log.error "The path defined by 'grails.plugin.configChooser.directFile' leads to a directory, a file is required"
			throw new ConfigChooserInvalidConfigurationException("The key 'grails.plugin.configChooser.directFile' must be a path to a file and not a directory")
		}

		return file
	}

	/**
	 * Retrieve the config key 'grails.plugin.configChooser.directFile'
	 */
	private String retrieveDirectFilename(ConfigObject config) {
		def configDirectFile = this.getConfigDirectFile(config)

		String directoryName = null
		if (configDirectFile instanceof String) {
			directoryName = configDirectFile
		} else {
			log.error "The mandatory config value assigned to 'grails.plugin.configChooser.directFile' is not recognized, value = ${ configDirectFile }"
			throw new ConfigChooserInvalidConfigurationException("The key 'grails.plugin.configChooser.directory' is not correctly set, a path to a file containing the configuration is required")
		}

		return directoryName
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	private def getConfigDirectFile(ConfigObject config) {
		return config.grails.plugin.configChooser.directFile
	}
}
