package grails.plugin.configChooser.helper
import grails.plugin.configChooser.exception.ConfigChooserInvalidConfigurationException
import grails.plugin.configChooser.helper.save.NameWithPlaceholderSaveSystem
import grails.plugin.configChooser.popup.ChooseData
import grails.plugin.configChooser.popup.ChoosePopup
import grails.plugin.configChooser.popup.IConfigChooserValue
import grails.plugin.configChooser.popup.value.ConfigFileChoiceValue
import groovy.io.FileType
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import groovy.util.logging.Log4j

import java.util.regex.Pattern
/**
 * @author Wadeck Follonier, wfollonier@proactive-partners.ch
 */
@Log4j
@CompileStatic
class ModeChooseExecutor implements IConfigChooserExecutor {
	ConfigObject mergedConfig

	@Override
	ConfigObject chooseConfig() {
		List<File> configFileList = this.retrievePluginConfigurationForChoose(mergedConfig)

		IModeChooseSaveSystem saveSystem = new NameWithPlaceholderSaveSystem()

		saveSystem.init()

		String lastChoice = saveSystem.loadLastChoice()

		// blocking call, until the user replies
		IConfigChooserValue choice = this.displayChoicePopup(configFileList, lastChoice)
		if (!choice) {
			log.debug "No choice made, the user cancelled the choice, application must exit"
			// the mode was "choose" so without a choose, we must exit
			System.exit(0)
		}

		saveSystem.saveChoice(choice)

		ConfigObject result = choice.retrieveConfigMap()
		return result
	}

	private List<File> retrievePluginConfigurationForChoose(ConfigObject config) {
		File directoryFile = this.retrieveDirectoryFile(config)

		// priority : list > regex
		List<String> filenames = this.retrieveFilenames(config)

		List<File> configFileList = []
		if (filenames) {
			configFileList = this.retrieveFileListUsingFilenames(directoryFile, filenames)
		} else {
			Pattern pattern = this.retrievePattern(config)

			if (!pattern) {
				log.error "Neither 'grails.plugin.configChooser.regex' nor 'grails.plugin.configChooser.regex' are given, at least one of them is mandatory in order to use the configChooser plugin"
				throw new ConfigChooserInvalidConfigurationException("Neither the key 'grails.plugin.configChooser.list' nor 'grails.plugin.configChooser.regex' is not filled but one is required")
			}

			configFileList = this.retrieveFileListUsingPattern(directoryFile, pattern)
		}

		log.debug "ConfigFiles found : [${ configFileList.join(', ') }]"

		return configFileList
	}

	private File retrieveDirectoryFile(ConfigObject config) {
		String directoryName = this.retrieveDirectoryName(config)

		File directoryFile = new File(directoryName)

		if (!directoryFile.exists()) {
			log.error "The path found using 'grails.plugin.configChooser.directory' does not exist, value = $directoryName. It's is required to use configChooser"
			throw new ConfigChooserInvalidConfigurationException("The key 'grails.plugin.configChooser.directory' is not correctly set, the folder defined by the path does not exist (or the application does not have the right to read it)")
		} else if (!directoryFile.isDirectory()) {
			log.error "The path found using 'grails.plugin.configChooser.directory' is not a directory, value = $directoryName. It's is required to use configChooser"
			throw new ConfigChooserInvalidConfigurationException("The key 'grails.plugin.configChooser.directory' is not correctly set, the path defines a file and not a folder")
		}

		return directoryFile
	}

	/**
	 * Retrieve the config key 'grails.plugin.configChooser.directory'
	 */
	private String retrieveDirectoryName(ConfigObject config) {
		def configDirectory = this.getConfigDirectory(config)

		String directoryName = null
		if (configDirectory instanceof String) {
			directoryName = configDirectory
		} else {
			log.error "The mandatory config value assigned to 'grails.plugin.configChooser.directory' is not recognized, value = ${ configDirectory }"
			throw new ConfigChooserInvalidConfigurationException("The key 'grails.plugin.configChooser.directory' is not correctly set, a path to the directory containing the different configuration files is required")
		}

		return directoryName
	}

	private List<String> retrieveFilenames(ConfigObject config) {
		def configList = this.getConfigList(config)

		List<String> filenames = null
		if (configList instanceof String) {
			filenames = ((String) configList).tokenize('|')
		} else if (configList instanceof List) {
			filenames = (List<String>) configList
		} else {
			if (configList) {
				log.warn "The optional config value assigned to 'grails.plugin.configChooser.list' is not recognized, value = ${ configList }"
			}
		}

		return filenames
	}

	private Pattern retrievePattern(ConfigObject config) {
		def configRegex = this.getConfigRegex(config)

		Pattern pattern = null
		if (configRegex instanceof Pattern) {
			pattern = (Pattern) configRegex
		} else if (configRegex instanceof String) {
			pattern = Pattern.compile((String) configRegex)
		} else {
			if (configRegex) {
				log.warn "The optional config value assigned to 'grails.plugin.configChooser.regex' is not recognized, value = ${ configRegex }"
			}
		}

		return pattern
	}

	private List<File> retrieveFileListUsingFilenames(File directoryFile, List<String> filenames) {
		List<File> configFileList = []

		filenames.each { String filename ->
			File file = new File(directoryFile, filename)
			if (!file.exists()) {
				log.warn "The path ${ file.canonicalPath } does not exist, a file is required, please change the value of config key 'grails.plugin.configChooser.list'"
				return
			}
			if (file.isDirectory()) {
				log.warn "The path ${ file.canonicalPath } is a directory, a file is required, please change the value of config key 'grails.plugin.configChooser.list'"
				return
			}

			log.debug "Adding the configFile ${ file.canonicalPath }"
			configFileList << file
		}

		return configFileList
	}

	private List<File> retrieveFileListUsingPattern(File directoryFile, Pattern pattern) {
		List<File> configFileList = []

		directoryFile.eachFileMatch(FileType.FILES, pattern) { File file ->
			log.debug "Adding the configFile ${ file.canonicalPath }"
			configFileList << file
		}

		return configFileList
	}

	//TODO the choice should be done by command line too
	private IConfigChooserValue displayChoicePopup(List<File> configFileList, String lastChoice) {
		// TODO add configuration on the default action and timer
		ChoosePopup<IConfigChooserValue> dialog = new ChoosePopup<IConfigChooserValue>(5, true)

		List<ConfigFileChoiceValue> fileData = configFileList.collect { File file ->
			new ConfigFileChoiceValue(file)
		}

		ConfigFileChoiceValue previousChoice = null
		if(lastChoice){
			previousChoice = fileData.find{ ConfigFileChoiceValue value ->
				value.computeStringRepresentation() == lastChoice
			}
		}
		ChooseData data = new ChooseData((List<IConfigChooserValue>) fileData)
		if(previousChoice){
			data.setSelectedValue(previousChoice)
		}

		dialog.setData(data)

		dialog.askUser()

		if (dialog.wasCancelled()) {
			return null
		} else {
			return dialog.getSelectedValue()
		}
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	private def getConfigDirectory(ConfigObject config) {
		return config.grails.plugin.configChooser.directory
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	private def getConfigList(ConfigObject config) {
		return config.grails.plugin.configChooser.list
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	private def getConfigRegex(ConfigObject config) {
		return config.grails.plugin.configChooser.regex
	}
}
