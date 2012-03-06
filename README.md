HD Image Utils Plugin
=====================

HD Image Utils plugin provides a high quality image manipulation service, for scaling and cropping images. It uses the pure Java [java-image-scaling](http://code.google.com/p/java-image-scaling/) library, and avoids the lesser quality JAI and the native ImageMagick libraries. The inputs are byte arrays or streams (not java.io.File), so it is well suited to be used with the [mongofile plugin](http://github.com/quirklabs/grails-mongofile).

Operations
----------

The plugin defines a service called hdImageService, which can be wired as normal by Spring. The following operations are available:

```groovy
byte[] scale(InputStream inputStream, Integer width, Integer height)
```
Performs a high quality scale of an image to the specified dimensions. If either is left null, the aspect ratio of the image is maintained.

```groovy
byte[] crop(InputStream inputStream, int x, int y, int width, int height)
```
Performs a crop of an image to the specified dimensions. 

```groovy
byte[] crop(InputStream inputStream, int cropX, int cropY, int cropWidth, int cropHeight, int scaleWidth, int scaleHeight)
```
Performs a crop of an image to the specified dimensions, followed by a scale.

Example
-------

```groovy
def hdImageService

def save() {
	def userInstance = new User(params)
    def logo = request.getFile('logo') // Make sure you use <g:uploadForm> with <input type="file" name="logo" />

	if (logo.empty) {
        flash.message = 'Logo must be uploaded'
        render(view: "create", model: [userInstance: userInstance])
        return
    }

	//Scale to logo size 100x100
	byte[] logoBytes = hdImageService.scale(logo.getInputStream(), 100, 100)

	// Use mongofile plugin to save image
	userInstance.saveMongofile(logoBytes, logo.getOriginalFilename(), 'logo')

	...
}
```