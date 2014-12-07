package grails.plugin.configChooser.helper
import groovy.transform.CompileStatic

/**
 * Normalize the filename for the most standard format
 * @author Wadeck Follonier, wfollonier@proactive-partners.ch
 */
@CompileStatic
class FilenameSanitizer {
	// source=http://stackoverflow.com/a/17745189/2567174
	/**
	 * @param filename The name to normalize
	 * @param substitute Can be null / '', or any valid character
	 * @return
	 */
	public static String sanitize(String filename, String substitute = '_'){
		def pattern = /[^a-zA-Z0-9\\._]+/

		if(!substitute){
			// manage the null case
			substitute = ''
		}

		if(substitute =~ pattern){
			throw new IllegalArgumentException('The substitute must be a valid string')
		}

		String sanitizedFilename = filename?.replaceAll(pattern, substitute)
		return sanitizedFilename
	}
}
