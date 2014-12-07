import grails.plugin.configChooser.mode.ConfigChooserMode

grails {
	plugin {
		configChooser {
			/*
			 * Choose mode to have the popup to choose the configuration file among the filenames provided
			 */
//			mode = ConfigChooserMode.CHOOSE
//			// either directory + list or directory + regex
//			directory = 'C:/defaultGrailsProject/ConfigChooser'
////			list = ['ConfigChooser-config-v2.groovy', 'ConfigChooser-config-v3.groovy']
//			// list is also usable by concatening the filenames with pipe
////			list = 'ConfigChooser-config-v2.groovy|ConfigChooser-config-v3.groovy'
//			// regex allow the developper to select a set of files that is not necessarily predefined
//			regex = /ConfigChooser\-config\-v.*\.groovy/
////			regex = 'ConfigChooser\\-config\\-v.*\\.groovy'
//
//			saveDirectory = 'c:/defaultGrailsProject/save'
//			// placeholder available : #appName, #version, #envName
//			saveFilename = '#appName-#version.xml'

			/*
			 * Direct mode use directly the file in 'directFile'
			 */
//			mode = ConfigChooserMode.DIRECT
//			directFile = 'C:/defaultGrailsProject/ConfigChooser/ConfigChooser-config-v2.groovy'

			/*
			 * None mode to deactivate the plugin
			 */
			mode = ConfigChooserMode.NONE
		}
	}
}

//println 'ConfigChooserDefaultConfig.groovy read'