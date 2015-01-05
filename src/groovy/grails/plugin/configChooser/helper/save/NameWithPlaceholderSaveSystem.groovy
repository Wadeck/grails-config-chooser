package grails.plugin.configChooser.helper.save

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import groovy.util.logging.Slf4j

/**
 * @author Wadeck Follonier, wfollonier@proactive-partners.ch
 */
@CompileStatic
@Slf4j
class NameWithPlaceholderSaveSystem extends AbstractModeChooseSaveSystem {

	String computeSaveFileName() {
		def saveFilenameConfig = configChooserConfig.saveFilename

		String filename
		if (saveFilenameConfig instanceof String) {
			filename = saveFilenameConfig
		} else {
			if (saveFilenameConfig) {
				// config provided but not readable
				log.warn "The configuration value for key='grails.plugin.configChooser.saveFilename' was not recognized [${ saveFilenameConfig }]"
			}

			filename = "#appName-#version.xml"
		}

		return filename
			.replaceAll(/#appName/, appName)
			.replaceAll(/#version/, version)
			.replaceAll(/#envName/, envName)
	}

	String computeSaveDirectory() {
		def directoryNameConfig = configChooserConfig.saveDirectory

		if (directoryNameConfig instanceof String) {
			return directoryNameConfig
		}

		if (directoryNameConfig) {
			// config provided but not readable
			log.warn "The configuration value for key='grails.plugin.configChooser.saveDirectory' was not recognized [${ directoryNameConfig }]"
		}

		return '.'
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	private ConfigObject getConfigChooserConfig() {
		grailsApplication.mergedConfig.grails.plugin.configChooser
	}
}
