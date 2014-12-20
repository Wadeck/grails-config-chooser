import grails.util.Environment
import org.apache.log4j.DailyRollingFileAppender
import org.apache.log4j.Layout
import org.apache.log4j.Logger
import org.apache.log4j.RollingFileAppender
// locations to search for config files that get merged into the main config;
// config files can be ConfigSlurper scripts, Java properties files, or classes
// in the classpath in ConfigSlurper format

grails.config.locations = ["file:C:/defaultGrailsProject/${appName}/${appName}-config.groovy"]

// in order to test easily the plugin
grails.config.locations << ConfigChooserDefaultConfig
//grails.config.locations << 'file:grails-app/config/ConfigChooserDefaultConfig.groovy'

//println "From the defaultConfig : grailsApplication.mergedConfig.grails.plugin.configChooser.mode=${grailsApplication.mergedConfig.grails.plugin.configChooser.mode}"
//println "before config.saveFlag = ${config.saveFlag}, config.saveValue = ${config.saveValue}"
//if(!config.saveFlag){
//	config.saveFlag = true
//	boolean ok = JOptionPane.showConfirmDialog(null, 'Titre', 'Message', JOptionPane.YES_NO_OPTION) == 0
//	config.saveValue = ok
//	if(ok){
//		println 'ok'
//	}else{
//		println 'not ok'
//	}
//}
//println "after config.saveFlag = ${config.saveFlag}, config.saveValue = ${config.saveValue}"

// required for log4j configuration without warning
def applicationName = appName

// log4j configuration
log4j = {
	appenders {
		// Generate the pattern to use for logging
		String gbPattern = '%d{dd.MM.yy HH:mm:ss,SSS} [%5p] %-40.40c{2} %m%n'
		Layout logLayout = pattern(conversionPattern: gbPattern)

		//'null' name:'stacktrace'

		console name: 'stdout', layout: logLayout

		String logDirectory = config.log.directory ?: "C:/defaultGrailsProject/${ applicationName }"
		String logFilename = config.log.filename ?: applicationName

		appender new DailyRollingFileAppender(name: 'logfile', file: logDirectory + File.separator + logFilename + ".log", datePattern: "'.'yyyy-MM-dd", layout: logLayout)
		appender new RollingFileAppender(name: 'stacktrace', file: logDirectory + File.separator + "_stacktrace.log", layout: logLayout)
	}

	root {
		if (Environment.getCurrentEnvironment() in [Environment.DEVELOPMENT]) {
			info('stdout', 'logfile')
		} else if (Environment.getCurrentEnvironment() in [Environment.TEST]) {
			warn('logfile')
		} else {
			error('logfile')
		}
		additivity = false
	}

	error 'org.codehaus.groovy.grails.web.servlet',  //  controllers
		'org.codehaus.groovy.grails.web.pages', //  GSP
		'org.codehaus.groovy.grails.web.sitemesh', //  layouts
		'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
		'org.codehaus.groovy.grails.web.mapping', // URL mapping
		'org.codehaus.groovy.grails.commons', // core / classloading
		'org.codehaus.groovy.grails.plugins', // plugins
		'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
		'org.springframework',
		'org.hibernate',
		'net.sf.ehcache.hibernate',
		'net.sf.jmimemagic'

	if (Environment.getCurrentEnvironment() in [Environment.DEVELOPMENT, Environment.TEST]) {
		debug 'grails.app'

		warn 'org.mortbay.log',
			'org.apache.ddlutils'

	} else {
		warn 'org.mortbay.log',
			'org.apache.ddlutils',
			'grails.app'
	}

	if (Environment.getCurrentEnvironment() == Environment.DEVELOPMENT) {
		// debug sql queries
		// trace 'org.hibernate.SQL' //, 'org.hibernate.type'

		// debug all security concerns
		// debug 'org.springframework.security'
	}
}

environments {
	def logger = Logger.getRootLogger()
	production {
		logger.removeAppender('stdout')
	}
}

//grails.project.repos.bintray.portal = "grailsCentral"
