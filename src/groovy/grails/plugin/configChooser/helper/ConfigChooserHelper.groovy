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
@Singleton
class ConfigChooserHelper {
	//TODO extensibility to do by adding listener

	/**
	 * When the instance is no more used, called during bootstrap
	 */
	void destroy() {
		log.debug 'destroying instance'
		instance = null
	}

	private ConfigObject chosenConfig
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
			chosenConfig = getUserChoice(application)
			alreadyChosen = true
		}

		if(chosenConfig){
			// in case of NONE, there is no choice
			mergeConfiguration(application, chosenConfig)
		}
	}

	private ConfigObject getUserChoice(GrailsApplication application) {
		ConfigObject mergedConfig = retrieveMergedConfig(application)

		ConfigChooserMode mode = retrieveMode(mergedConfig)
		if (mode == ConfigChooserMode.NONE) {
			log.debug "No choice desired for the configuration (mode=$mode)"
			return null
		}

		IConfigChooserExecutor executor
		switch (mode) {
			case ConfigChooserMode.CHOOSE: executor = new ModeChooseExecutor(mergedConfig: mergedConfig); break
			case ConfigChooserMode.DIRECT: executor = new ModeDirectExecutor(mergedConfig: mergedConfig); break
			default:
				// could happen if we do not update the code when we add a new mode
				throw new IllegalArgumentException("The mode is not recognized [mode=${ mode }]")
		}

		return executor.chooseConfig(application)
	}

	/**
	 * If not set, the mode is set to ConfigChooserMode.NONE
	 */
	private ConfigChooserMode retrieveMode(ConfigObject config) {
		def configMode = getConfigMode(config)

		ConfigChooserMode mode
		if (configMode instanceof String) {
			try {
				ConfigChooserMode tryMode = ConfigChooserMode.valueOf(configMode)
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
		ConfigObject config = application.config

		Map mergedConfig = config.merge(userChosenConfig)

		// also update the flat config
		Map<String, Object> flatConfig = application.flatConfig
		mergedConfig.each { key, value -> flatConfig[key as String] = value }

		long endTime = System.currentTimeMillis()

		log.debug "time to merge config: ${ endTime - startTime }ms"
	}

	// skip mandatory because the mergedConfig is added by pluginConfig dynamically
	@CompileStatic(TypeCheckingMode.SKIP)
	private ConfigObject retrieveMergedConfig(GrailsApplication application) {
		return application.mergedConfig
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	private getConfigMode(ConfigObject config) {
		return config.grails.plugin.configChooser.mode
	}
}
