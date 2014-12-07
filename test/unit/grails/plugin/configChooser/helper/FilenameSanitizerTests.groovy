package grails.plugin.configChooser.helper

/**
 * @author Wadeck Follonier, wfollonier@proactive-partners.ch
 */
class FilenameSanitizerTests extends GroovyTestCase{
	void testBase(){
		assert 'toto.txt' == FilenameSanitizer.sanitize('toto.txt')
		assert 'tata.txt' == FilenameSanitizer.sanitize('tata.txt')
		assert 'titi.txt' == FilenameSanitizer.sanitize('titi.txt')
	}

	void testIllegalSubstitute(){
		shouldFail(IllegalArgumentException){
			FilenameSanitizer.sanitize('titi.txt', '¬')
		}
	}

	void testDefaultSubstitute(){
		assert 'titi_2.txt' == FilenameSanitizer.sanitize('titi?2.txt')
	}

	void testOtherSubstitute(){
		assert 'titia2.txt' == FilenameSanitizer.sanitize('titi?2.txt', 'a')
		assert 'titiR2.txt' == FilenameSanitizer.sanitize('titi?2.txt', 'R')
		assert 'titi32.txt' == FilenameSanitizer.sanitize('titi?2.txt', '3')
	}

	void testSpecialSubstitute(){
		assert 'titi2.txt' == FilenameSanitizer.sanitize('titi?2.txt', '')
		assert 'titi2.txt' == FilenameSanitizer.sanitize('titi?2.txt', null)
	}

	void testSpecialCharacter(){
		assert 'a' == FilenameSanitizer.sanitize('!', 'a')
		assert 'a' == FilenameSanitizer.sanitize('%', 'a')
		// spaces are illegal character in certain system
		assert 'helloa_ab' == FilenameSanitizer.sanitize('hello % _ b', 'a')
		assert 'toato.txt' == FilenameSanitizer.sanitize('to?to.txt', 'a')
	}

	void testRemoveMultipleAtOnce() {
		assert 'a' == FilenameSanitizer.sanitize('+"*ç%&/()=?`¦@#°§¬|¢´~[]{}è¨éà$,-ü!öä£;:', 'a')
	}
}
