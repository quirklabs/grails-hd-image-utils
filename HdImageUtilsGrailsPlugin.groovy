class HdImageUtilsGrailsPlugin {
    def version = "0.5"
    def grailsVersion = "1.3 > *"
    def dependsOn = [:]
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]
    
    def title = "Hd Image Utils Plugin" 
    def author = "Craig Raw"
    def authorEmail = "craig@quirk.biz"
    def description = "High quality image manipulation plugin, for scaling and cropping images. Uses the pure Java [java-image-scaling library|http://code.google.com/p/java-image-scaling/]"
    def documentation = "https://github.com/quirklabs/grails-hd-image-utils/blob/master/README.md"
    def license = "APACHE"
    def organization = [ name: "Quirk Labs", url: "http://www.quirklabs.co.za" ]
    def issueManagement = [ system: "github", url: "https://github.com/quirklabs/grails-hd-image-utils/issues" ]
    def scm = [ url: "https://github.com/quirklabs/grails-hd-image-utils" ]
}
