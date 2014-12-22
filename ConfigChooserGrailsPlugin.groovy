import grails.plugin.configChooser.helper.ConfigChooserHelper

class ConfigChooserGrailsPlugin {
	// the plugin version
	def version = "0.5.1"
	// the version or versions of Grails the plugin is designed for
	def grailsVersion = "2.0 > *"

	def groupId = 'ch.wadeck'

	def loadAfter = ['pluginConfig']

	// resources that are excluded from plugin packaging
	def pluginExcludes = [
		"grails-app/views/**",
		"web-app/**",
		// remove all the classes that are used to test
		"**/test/**"
	]

	// Headline display name of the plugin
	def title = "Config Chooser Plugin"
	def author = "Wadeck Follonier"
	def authorEmail = "wadeck.follonier@gmail.com"
	def description = '''\
Allow the developper to choose the configuration on the fly by using a JDialog poping with different config proposal. Useful when working with multiple database source
'''

	// URL to the plugin's documentation
	def documentation = "https://github.com/Wadeck/grails-config-chooser"

	// it watches config files and defaultConfig files
	def observe = ['pluginConfig']

	// Extra (optional) plugin metadata

	// License: one of 'APACHE', 'GPL2', 'GPL3'
//    def license = "APACHE"

	// Details of company behind the plugin (if there is one)
//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]

	// Any additional developers beyond the author specified above.
	def developers = [
		[name: "Wadeck Follonier", email: "wadeck.follonier@gmail.com"]
	]

	// Location of the plugin's issue tracker.
	def issueManagement = [system: "GITHUB", url: "https://github.com/Wadeck/grails-config-chooser"]

	// Online location of the plugin's browseable source code.
	def scm = [url: "https://github.com/Wadeck/grails-config-chooser"]

	def doWithWebDescriptor = { xml ->
		ConfigChooserHelper.getInstance().execute(application)
	}

	def doWithSpring = {
		ConfigChooserHelper.getInstance().execute(application)
	}

	def doWithDynamicMethods = { ctx ->
		ConfigChooserHelper.getInstance().execute(application)
	}

	def doWithApplicationContext = { ctx ->
		ConfigChooserHelper.getInstance().execute(application)
	}

	def onChange = { event ->
		// watching is modified and reloaded. The event contains: event.source,
		// event.application, event.manager, event.ctx, and event.plugin.
		println '[ConfigChooser] onChange'
	}

	def onConfigChange = { event ->
		// The event is the same as for 'onChange'.
//		println '[ConfigChooser] onConfigChange'

		// normally at this point we lost the user choice
		ConfigChooserHelper.getInstance().execute(application)
		ConfigChooserHelper.getInstance().destroy()
	}

	def onShutdown = { event ->
		// Implement code that is executed when the application shuts down (optional)
	}

	def afterConfigMerge = { config, mergeCtx ->
		/* Validate that the application has provided all the required settings. */
//		println '[ConfigChooser] afterConfigMerge'
	}
}
