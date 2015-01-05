import grails.plugin.configChooser.helper.ConfigChooserHelper

class ConfigChooserGrailsPlugin {
	def version = "0.5.1"
	def grailsVersion = "2.0 > *"
	def groupId = 'ch.wadeck'
	def loadAfter = ['pluginConfig']
	def pluginExcludes = [
		"web-app/**",
		// remove all the classes that are used to test
		"**/test/**"
	]
	def title = "Config Chooser Plugin"
	def description = 'Allow the developer to choose the configuration on the fly by using a JDialog poping with different config proposal. Useful when working with multiple database source'
	def documentation = "https://github.com/Wadeck/grails-config-chooser"
	def observe = ['pluginConfig'] // it watches config files and defaultConfig files
   def license = "APACHE"
	def developers = [
		[name: "Wadeck Follonier", email: "wadeck.follonier@gmail.com"]
	]
	def issueManagement = [system: "GITHUB", url: "https://github.com/Wadeck/grails-config-chooser"]
	def scm = [url: "https://github.com/Wadeck/grails-config-chooser"]

	def doWithWebDescriptor = { xml ->
		ConfigChooserHelper.instance.execute(application)
	}

	def doWithSpring = {
		ConfigChooserHelper.instance.execute(application)
	}

	def doWithDynamicMethods = { ctx ->
		ConfigChooserHelper.instance.execute(application)
	}

	def doWithApplicationContext = { ctx ->
		ConfigChooserHelper.instance.execute(application)
	}

	def onChange = { event ->
		println '[ConfigChooser] onChange'
	}

	def onConfigChange = { event ->
//		println '[ConfigChooser] onConfigChange'

		// normally at this point we lost the user choice
		ConfigChooserHelper.instance.execute(application)
		ConfigChooserHelper.instance.destroy()
	}

	def afterConfigMerge = { config, mergeCtx ->
		/* Validate that the application has provided all the required settings. */
//		println '[ConfigChooser] afterConfigMerge'
	}
}
