package grails.plugin.configChooser.helper.save

import grails.util.Holders

/**
 * @author Wadeck Follonier, wfollonier@proactive-partners.ch
 */
class NameWithPlaceholderSaveSystem extends AbstractModeChooseSaveSystem {
	@Override
	String computeSaveFileName() {
		def saveFilenameConfig = Holders.grailsApplication.mergedConfig.grails.plugin.configChooser.saveFilename

		String filename
		if (saveFilenameConfig instanceof String) {
			filename = (String) saveFilenameConfig
		} else {
			if (saveFilenameConfig) {
				// config provided but not readable
				log.warn "The configuration value for key='grails.plugin.configChooser.saveFilename' was not recognized [${ saveFilenameConfig }]"
			}

			filename = "#appName-#version.xml"
		}

		filename = filename.replaceAll(/#appName/, appName)
			.replaceAll(/#version/, version)
			.replaceAll(/#envName/, envName)

		return filename
	}

	@Override
	String computeSaveDirectory() {
		def directoryNameConfig = Holders.grailsApplication.mergedConfig.grails.plugin.configChooser.saveDirectory

		String directoryName
		if (directoryNameConfig instanceof String) {
			directoryName = (String) directoryNameConfig
		} else {
			if (directoryNameConfig) {
				// config provided but not readable
				log.warn "The configuration value for key='grails.plugin.configChooser.saveDirectory' was not recognized [${ directoryNameConfig }]"
			}

			directoryName = '.'
		}

		return directoryName
	}
}
