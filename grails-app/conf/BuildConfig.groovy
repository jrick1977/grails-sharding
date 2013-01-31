grails.project.work.dir = 'target'
grails.project.source.level = 1.6

grails.project.dependency.resolution = {

  inherits 'global'
  log 'warn'

  repositories {
    grailsCentral()
    mavenLocal()
    mavenCentral()
  }

  dependencies {
    compile('c3p0:c3p0:0.9.1')
  }

  plugins {
    runtime ":hibernate:$grailsVersion"
  }
}
