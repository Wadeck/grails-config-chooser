grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.fork = [
	// configure settings for compilation JVM, note that if you alter the Groovy version forked compilation is required
	//  compile: [maxMemory: 256, minMemory: 64, debug: false, maxPerm: 256, daemon:true],

	// configure settings for the test-app JVM, uses the daemon by default
	test   : false,
	// configure settings for the run-app JVM
	run    : false,
	// configure settings for the run-war JVM
	war    : [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve: false],
	// configure settings for the Console UI JVM
	console: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256]
]

// enable the run-war command line to avoid permgen space out of memory exception
// for java <8
grails.tomcat.jvmArgs = ["-Xms256m", "-Xmx1024m", "-XX:PermSize=512m", "-XX:MaxPermSize=512m"]
// for java >=8 (no more permGem support)
//grails.tomcat.jvmArgs = ["-Xms256m", "-Xmx1024m"]

grails.project.dependency.resolver = "maven" // or ivy
grails.project.dependency.resolution = {
	// inherit Grails' default dependencies
	inherits("global") {
		// uncomment to disable ehcache
		// excludes 'ehcache'
	}

	log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
	repositories {
		grailsCentral()
		mavenLocal()
		mavenCentral()
		// uncomment the below to enable remote dependency resolution
		// from public Maven repositories
//		mavenRepo "http://repository.codehaus.org"
//		mavenRepo "http://download.java.net/maven/2/"
//		mavenRepo "http://repository.jboss.com/maven2/"
	}

	dependencies {
		// specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
		// runtime 'mysql:mysql-connector-java:5.1.27'
	}

	plugins {
		// plugins for the build system only
		build ":tomcat:7.0.54"
		build(
			":release:3.0.1",
			":rest-client-builder:1.0.3",
		) {
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