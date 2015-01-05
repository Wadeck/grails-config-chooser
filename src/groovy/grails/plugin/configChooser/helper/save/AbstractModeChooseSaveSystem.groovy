package grails.plugin.configChooser.helper.save

import grails.plugin.configChooser.helper.FilenameSanitizer
import grails.plugin.configChooser.helper.IModeChooseSaveSystem
import grails.plugin.configChooser.popup.IConfigChooserValue
import grails.util.Environment
import grails.util.GrailsUtil
import grails.util.Metadata
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import groovy.util.logging.Slf4j
import groovy.xml.MarkupBuilder

import org.codehaus.groovy.grails.commons.GrailsApplication

/**
 * @author Wadeck Follonier, wfollonier@proactive-partners.ch
 */
@Slf4j
@CompileStatic
abstract class AbstractModeChooseSaveSystem implements IModeChooseSaveSystem {
	protected String envName = Environment.current.name
	protected String appName = Metadata.current.getApplicationName()
	protected String version = Metadata.current.getApplicationVersion()
	protected GrailsApplication grailsApplication

	private File cachedFile

	void init(GrailsApplication application) {
		grailsApplication = application
	}

	/**
	 * The directory that will contains all the save files
	 */
	abstract String computeSaveDirectory()

	/**
	 * The save file given the current application configuration
	 */
	abstract String computeSaveFileName()

	@CompileStatic(TypeCheckingMode.SKIP)
	String loadLastChoice() {
		if (!file.exists()) {
			return null
		}

		try {
			def root = new XmlSlurper().parse(file)

			String lastFileChosen = root.filename.text()

			log.debug "previous choice loaded: [${ lastFileChosen }]"
			return lastFileChosen
		} catch (e) {
			log.error "Error during reading last saved file", GrailsUtil.deepSanitize(e)
			return null
		}
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	void saveChoice(IConfigChooserValue value) {
		file.withWriter('UTF-8') { Writer writer ->
			def xml = new MarkupBuilder(writer)
			xml.doubleQuotes = true
			xml.root(appName: appName, appVersion: version, envName: envName) {
				filename(value.computeStringRepresentation())
			}
		}
	}

	// warning this method cannot be private due to issue with CompileStatic and hierarchy call
	protected File getFile() {
		if (!cachedFile) {
			cachedFile = retrieveFile()
			log.debug "The cached file is [${ cachedFile.canonicalPath }]"
		}

		return cachedFile
	}

	private File retrieveFile() {
		String filename = computeSaveFileName()
		String sanitizedFilename = FilenameSanitizer.sanitize(filename)
		if (!sanitizedFilename) {
			throw new IllegalArgumentException("The file name is not valid [${ filename }]")
		}

		String directoryName = computeSaveDirectory()
		String sanitizedDirectoryName = FilenameSanitizer.sanitize(directoryName)
		if (!sanitizedDirectoryName) {
			throw new IllegalArgumentException("The directory name is not valid [${ directoryName }]")
		}

		File file = new File(directoryName, sanitizedFilename)
		File parent = file.parentFile
		if (!parent.exists()) {
			parent.mkdirs()
			if (!parent.exists()) {
				log.warn "Impossible to create the path for the choice save file with path [${ file.canonicalPath }]"
			}
		}

		if (file.isDirectory()) {
			throw new IllegalArgumentException("The file is actually a directory [path=${ file.canonicalPath }]")
		}

		return file
	}
}
