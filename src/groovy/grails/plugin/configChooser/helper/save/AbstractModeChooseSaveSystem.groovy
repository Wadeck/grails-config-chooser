package grails.plugin.configChooser.helper.save

import grails.plugin.configChooser.helper.FilenameSanitizer
import grails.plugin.configChooser.helper.IModeChooseSaveSystem
import grails.plugin.configChooser.popup.IConfigChooserValue
import grails.util.Environment
import grails.util.GrailsUtil
import grails.util.Holders
import grails.util.Metadata
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import groovy.util.logging.Log4j
import groovy.xml.MarkupBuilder

/**
 * @author Wadeck Follonier, wfollonier@proactive-partners.ch
 */
@Log4j
@CompileStatic
abstract class AbstractModeChooseSaveSystem implements IModeChooseSaveSystem {
	protected String envName
	protected String appName
	protected String version

	private File cachedFile

	@Override
	void init() {
		this.envName = Environment.getCurrentEnvironment().name

		Metadata meta = Holders.grailsApplication.metadata
		this.appName = meta.get(Metadata.APPLICATION_NAME)
		this.version = meta.get(Metadata.APPLICATION_VERSION)
	}

	/**
	 * The directory that will contains all the save files
	 */
	public abstract String computeSaveDirectory()

	/**
	 * The save file given the current application configuration
	 */
	public abstract String computeSaveFileName()

	@CompileStatic(TypeCheckingMode.SKIP)
	public String loadLastChoice() {
		File file = this.getFile()
		if (!file.exists()) {
			return null
		}

		try {
			XmlSlurper slurper = new XmlSlurper()
			def root = slurper.parse(file)

			String lastFileChosen = root.filename.text()

			log.debug "previous choice loaded: [${ lastFileChosen }]"
			return lastFileChosen
		} catch (e) {
			log.error "Error during reading last saved file", GrailsUtil.deepSanitize(e)
			return null
		}
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	public void saveChoice(IConfigChooserValue value) {
		File file = this.getFile()

		file.withWriter('UTF-8') { Writer writer ->
			def xml = new MarkupBuilder(writer)
			xml.setDoubleQuotes(true)
			xml.root(appName: appName, appVersion: version, envName: envName) {
				filename(value.computeStringRepresentation())
			}
		}
	}

	// warning this method cannot be private due to issue with CompileStatic and hierarchy call
	protected File getFile() {
		if (!cachedFile) {
			this.cachedFile = this.retrieveFile()
			log.debug "The cached file is [${ cachedFile.canonicalPath }]"
		}

		return this.cachedFile
	}

	private File retrieveFile() {
		String filename = this.computeSaveFileName()
		String sanitizedFilename = FilenameSanitizer.sanitize(filename)
		if (!sanitizedFilename) {
			throw new IllegalArgumentException("The file name is not valid [${ filename }]")
		}

		String directoryName = this.computeSaveDirectory()
		String sanitizedDirectoryName = FilenameSanitizer.sanitize(directoryName)
		if (!sanitizedDirectoryName) {
			throw new IllegalArgumentException("The directory name is not valid [${ directoryName }]")
		}

		File file = new File(directoryName, sanitizedFilename)
		File parent = file.getParentFile()
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
