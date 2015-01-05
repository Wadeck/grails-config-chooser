import grails.plugin.configChooser.helper.ConfigChooserHelper

class ConfigChooserBootStrap {

	def grailsApplication

	def init = {
		ConfigChooserHelper.instance.destroy()
//		ConfigChooserHelper.instance.execute(grailsApplication)
//		log.info 'Loading ConfigChooser plugin bootstrap'
//		println "\nLoading ConfigChooser plugin bootstrap"
//
//		def configChooserConfig = grailsApplication.mergedConfig.grails.plugin.configChooser
//
//		// 'mergedConfig' provided by plugin-config
//		def configMode = configChooserConfig.mode
//
//		ConfigChooserMode mode
//		if (configMode instanceof String) {
//			try {
//				ConfigChooserMode tryMode = ConfigChooserMode.valueOf(configMode)
//				if (tryMode) {
//					mode = tryMode
//				}
//			} catch (IllegalArgumentException e) {
//				log.error("Cannot find the correct ConfigChooserMode using '${ configMode }', please fill the config key 'grails.plugin.configChooser.mode' with one of those value : [${ ConfigChooserMode.values().join(', ') }]")
//			}
//		} else if (configMode instanceof ConfigChooserMode) {
//			mode = configMode
//		}
//
//		println "desired mode = ${ mode }"
//
//		if (mode == ConfigChooserMode.CHOOSE) {
//			def configDirectory = configChooserConfig.directory
//			def configRegex = configChooserConfig.regex
//			def configList = configChooserConfig.list
//
//			println "choosing with params : directory=$configDirectory, list=$configList, regex=$configRegex"
//
//			String directoryName
//			if (configDirectory instanceof String) {
//				directoryName = configDirectory
//			} else {
//				log.error "The mandatory config value assigned to 'grails.plugin.configChooser.directory' is not recognized, value = ${ configDirectory }"
//				return
//			}
//
//			File directoryFile = new File(directoryName)
//			if (!directoryFile.exists()) {
//				log.error "The path found using 'grails.plugin.configChooser.directory' does not exist, value = ${ configDirectory }. It's is required to use configChooser"
//				return
//			}
//			if (!directoryFile.isDirectory()) {
//				log.error "The path found using 'grails.plugin.configChooser.directory' is not a directory, value = ${ configDirectory }. It's is required to use configChooser"
//				return
//			}
//
//			// priority : list > regex
//			List<String> fileNames
//			if (configList instanceof String) {
//				fileNames = configList.tokenize('|')
//			} else if (configList instanceof List) {
//				fileNames = configList
//			} else {
//				if (configList) {
//					log.warn "The optional config value assigned to 'grails.plugin.configChooser.list' is not recognized, value = ${ configList }"
//				}
//			}
//
//			Pattern pattern
//			if (!fileNames) {
//				if (configRegex instanceof Pattern) {
//					pattern = configRegex
//				} else if (configRegex instanceof String) {
//					pattern = Pattern.compile(configRegex)
//				} else {
//					if (configRegex) {
//						log.warn "The optional config value assigned to 'grails.plugin.configChooser.regex' is not recognized, value = ${ configRegex }"
//					}
//				}
//			}
//
//			if (!fileNames && !pattern) {
//				log.error "Neither 'grails.plugin.configChooser.regex' nor 'grails.plugin.configChooser.regex' are given, at least one of them is mandatory in order to use the configChooser plugin"
//				return
//			}
//
//			List<File> configFileList = []
//			if (fileNames) {
//				fileNames.each { String fileName ->
//					File file = new File(directoryFile, fileName)
//					if (!file.exists()) {
//						log.warn "The path ${ file.canonicalPath } does not exist, a file is required, please change the value of config key 'grails.plugin.configChooser.list'"
//						return
//					}
//					if (file.isDirectory()) {
//						log.warn "The path ${ file.canonicalPath } is a directory, a file is required, please change the value of config key 'grails.plugin.configChooser.list'"
//						return
//					}
//
//					log.debug "Adding the configFile ${ file.canonicalPath }"
//					configFileList << file
//				}
//			} else if (pattern) {
//				directoryFile.eachFileMatch(FileType.FILES, pattern) { File file ->
//					if (!file.exists()) {
//						log.warn "The path ${ file.canonicalPath } does not exist, a file is required, please change the value of config key 'grails.plugin.configChooser.regex'"
//						return
//					}
//					if (file.isDirectory()) {
//						log.warn "The path ${ file.canonicalPath } is a directory, a file is required, please change the value of config key 'grails.plugin.configChooser.regex'"
//						return
//					}
//
//					log.debug "Adding the configFile ${ file.canonicalPath }"
//					configFileList << file
//				}
//			}
//
//			println "ConfigFiles found : [${ configFileList.join(', ') }]"
//
//			ChoosePopup<IChooseConfigValue> dialog = new ChoosePopup<IChooseConfigValue>()
//
//			List<ChooseFileValue> fileData = configFileList.collect { new ChooseFileValue(it) }
//
//			ChooseData data = new ChooseData(fileData)
//			dialog.data = data
//
//			dialog.askUser()
//
//			if (dialog.wasCancelled()) {
//				log.warn 'User decided to cancel the launch'
//				System.exit(0)
//			}
//
//			IChooseConfigValue value = dialog.selectedValue
//			log.info("Selected option: ${value.computeStringRepresentation()}")
//
//			log.warn "test 1 content.version=${ grailsApplication.config.content.version }"
//
//			grailsApplication.config.merge(value.retrieveConfigMap())
//
//			log.warn "test 2 content.version=${ grailsApplication.config.content.version }"
//		}
//
//		log.info 'End ConfigChooser plugin bootstrap'
	}
}
