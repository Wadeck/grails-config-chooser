package grails.plugin.configChooser.helper

/**
 * @author Wadeck Follonier, wfollonier@proactive-partners.ch
 */
interface IConfigChooserExecutor {
	/** The current configuration, before the choice */
	void setMergedConfig(ConfigObject mergedConfig)

	ConfigObject chooseConfig()
}