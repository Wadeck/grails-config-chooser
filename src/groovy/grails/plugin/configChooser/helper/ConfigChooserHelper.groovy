package grails.plugin.configChooser.helper

import grails.plugin.configChooser.exception.ConfigChooserInvalidConfigurationException
import grails.plugin.configChooser.mode.ConfigChooserMode
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import groovy.util.logging.Log4j
import org.codehaus.groovy.grails.commons.GrailsApplication

/**
 * Singleton managing the configuration choice
 * A the first call the instance is created and when the choice is done, the instance is killed
 * @author Wadeck Follonier, wfollonier@proactive-partners.ch
 */
@Log4j
@CompileStatic
class ConfigChooserHelper {
	//TODO extensibility to do by adding listener

	static private ConfigChooserHelper instance

	static public ConfigChooserHelper getInstance() {
		if (!instance) {
			instance = new ConfigChooserHelper()
		}

		return instance
	}

	/**
	 * When the instance is no more used, called during bootstrap
	 */
	public void destroy() {
		log.debug 'destroying instance'
		instance = null
	}

	private ConfigObject chosenConfig = null
	private boolean alreadyChosen = false

	/**
	 * Method called in the ConfigChooserGrailsPlugin#doWithWebDescriptor hook
	 */
	void execute(GrailsApplication application) throws ConfigChooserInvalidConfigurationException {
		log.info 'Loading ConfigChooser plugin bootstrap'

		if (alreadyChosen) {
			log.debug 'using cached config'
		} else {
			log.debug 'asking user for his choice'
			this.chosenConfig = this.getUserChoice(application)
			this.alreadyChosen = true
		}

		if(chosenConfig){
			// in case of NONE, there is no choice
			this.mergeConfiguration(application, chosenConfig)
		}
	}

	private ConfigObject getUserChoice(GrailsApplication application) {
		ConfigObject mergedConfig = this.retrieveMergedConfig(application)

		ConfigChooserMode mode = this.retrieveMode(mergedConfig)
		if (mode == ConfigChooserMode.NONE) {
			log.debug "No choice desired for the configuration (mode=$mode)"
			return null
		}

		ConfigObject result
		IConfigChooserExecutor executor
		if (mode == ConfigChooserMode.CHOOSE) {
			executor = new ModeChooseExecutor()
		} else if (mode == ConfigChooserMode.DIRECT) {
			executor = new ModeDirectExecutor()
		} else {
			// could happen if we do not update the code when we add a new mode
			throw new IllegalArgumentException("The mode is not recognized [mode=${ mode }]")
		}

		executor.setMergedConfig(mergedConfig)

		result = executor.chooseConfig()

		return result
	}

	/**
	 * If not set, the mode is set to ConfigChooserMode.NONE
	 * @param config
	 * @return
	 */
	private ConfigChooserMode retrieveMode(ConfigObject config) {
		def configMode = this.getConfigMode(config)

		ConfigChooserMode mode = null
		if (configMode instanceof String) {
			try {
				ConfigChooserMode tryMode = ConfigChooserMode.valueOf((String) configMode)
				if (tryMode) {
					mode = tryMode
				}
			} catch (IllegalArgumentException e) {
				log.error("Cannot find the correct ConfigChooserMode using '$configMode', please fill the config key 'grails.plugin.configChooser.mode' with one of those value : [${ ConfigChooserMode.values().join(', ') }]")
				throw new ConfigChooserInvalidConfigurationException("The key 'grails.plugin.configChooser.mode' is not filled with a recognized value")
			}
		} else if (configMode instanceof ConfigChooserMode) {
			mode = (ConfigChooserMode) configMode
		}

		if (!mode) {
			mode = ConfigChooserMode.NONE
		}

		log.debug "desired mode = $mode"

		return mode
	}

	private void mergeConfiguration(GrailsApplication application, ConfigObject userChosenConfig) {
		long startTime = System.currentTimeMillis()
		ConfigObject config = application.getConfig()

		Map mergedConfig = config.merge(userChosenConfig)

		// also update the flat config
		Map<String, Object> flatConfig = application.getFlatConfig()
		mergedConfig.each { String key, value ->
			flatConfig[key] = value
		}

		long endTime = System.currentTimeMillis()

		log.debug "time to merge config: ${ endTime - startTime }ms"
	}

	// skip mandatory because the mergedConfig is added by pluginConfig dynamically
	@CompileStatic(TypeCheckingMode.SKIP)
	private ConfigObject retrieveMergedConfig(GrailsApplication application) {
		ConfigObject config = application.getMergedConfig()
		return config
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	private def getConfigMode(ConfigObject config) {
		return config.grails.plugin.configChooser.mode
	}
}
