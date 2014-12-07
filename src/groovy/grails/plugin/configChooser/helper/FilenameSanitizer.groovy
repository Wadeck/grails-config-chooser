package grails.plugin.configChooser.helper

import groovy.transform.CompileStatic

/**
 * @author Wadeck Follonier, wfollonier@proactive-partners.ch
 */
@CompileStatic
class FilenameSanitizer {
	// source=http://stackoverflow.com/a/17745189/2567174
	public static String sanitize(String filename){
		String sanitizedFilename = filename?.replaceAll(/[^a-zA-Z0-9\\._]+/, '_')
		return sanitizedFilename
	}
}
