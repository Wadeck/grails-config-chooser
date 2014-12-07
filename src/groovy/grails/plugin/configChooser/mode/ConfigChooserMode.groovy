package grails.plugin.configChooser.mode

import groovy.transform.CompileStatic

/**
 * @author Wadeck Follonier, wfollonier@proactive-partners.ch
 */
@CompileStatic
enum ConfigChooserMode {
	/** A popup will spawn and ask the user which configuration he wants to use */
	CHOOSE,
	/**
	 * The configuration source is selected in the config file, this allow a tertiary configuration
	 * 1) Config.groovy,
	 * 2) the normal external files defined in Config.groovy
	 * 3) and a special set of source defined in one of the second level source (the second level sources are fixed for everyone), which allow to change easily dataSource etc
	 */
	DIRECT,
	/** Like if the plugin is not installed */
	NONE
}