grails.project.work.dir = 'target'

// enable the run-war command line to avoid permgen space out of memory exception for java <8
grails.tomcat.jvmArgs = ["-Xms256m", "-Xmx1024m", "-XX:PermSize=512m", "-XX:MaxPermSize=512m"]
// for java >=8 (no more permGem support)
//grails.tomcat.jvmArgs = ["-Xms256m", "-Xmx1024m"]

grails.project.dependency.resolver = 'maven'
grails.project.dependency.resolution = {

	inherits 'global'
	log 'warn'

	repositories {
		grailsCentral()
		mavenLocal()
		mavenCentral()
	}

	plugins {

		build ":tomcat:7.0.55", {
			export = false
		}

		build(':release:3.0.1', ':rest-client-builder:2.0.3') {
			export = false
		}

		compile ':plugin-config:0.2.0'
	}
}

grails.project.repos.default = "artifactory"

grails.project.repos.artifactory.url = "http://artifactory-wadeck.rhcloud.com/artifactory/plugins-release-local"
grails.project.repos.artifactory.type = "maven"
// creditentials are stored in separate file, %HOME%/.grails/settings.groovy
// due to a bug, those lines must not be uncommented, they are just for information
// grails.project.repos.artifactory.username = "not-public"
// grails.project.repos.artifactory.password = "not-public"
