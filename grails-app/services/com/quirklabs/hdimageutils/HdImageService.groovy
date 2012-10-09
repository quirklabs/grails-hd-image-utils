package com.quirklabs.hdimageutils

import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.awt.image.RasterFormatException

import javax.imageio.ImageIO
import javax.imageio.ImageReader
import javax.imageio.stream.ImageInputStream
import javax.imageio.stream.MemoryCacheImageInputStream

import com.sun.image.codec.jpeg.JPEGCodec
import com.sun.image.codec.jpeg.JPEGImageDecoder

import com.mortennobel.imagescaling.ResampleOp
import com.mortennobel.imagescaling.AdvancedResizeOp

class HdImageService {
    byte[] scale(InputStream inputStream, Integer width, Integer height) {
        HdImage hdImage = getHdImage(inputStream)
        hdImage.image = scale(hdImage.image, width, height)
        writeBytes(hdImage.image, hdImage.format)
    }
    
    byte[] crop(InputStream inputStream, int cropX, int cropY, int cropWidth, int cropHeight) {
        HdImage hdImage = getHdImage(inputStream)
        hdImage.image = crop(hdImage.image, cropX, cropY, cropWidth, cropHeight)
        writeBytes(hdImage.image, hdImage.format)
    }
    
    byte[] cropAndScale(InputStream inputStream, int cropX, int cropY, int cropWidth, int cropHeight, int scaleWidth, int scaleHeight) {
        HdImage hdImage = getHdImage(inputStream)
        hdImage.image = crop(hdImage.image, cropX, cropY, cropWidth, cropHeight)
        hdImage.image = scale(hdImage.image, scaleWidth, scaleHeight)
        writeBytes(hdImage.image, hdImage.format)
    }
    
    private HdImage getHdImage(InputStream inputStream) {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)
        bufferedInputStream.mark(0)
        
        String format = getFormat(bufferedInputStream)
        bufferedInputStream.reset()
        
        BufferedImage image = getBufferedImage(bufferedInputStream)
        new HdImage(image: image, format: format)
    }
    
    String getFormat(byte[] fileContents) {
        getFormat(new ByteArrayInputStream(fileContents))
    }
    
    String getFormat(InputStream inputStream) {
        String imageFormat = null
        ImageInputStream imageStream = new MemoryCacheImageInputStream(inputStream)
        Iterator readers = ImageIO.getImageReaders(imageStream)
        if(readers.hasNext()) {
          ImageReader reader = (ImageReader)readers.next()
          imageFormat = reader.getFormatName().toLowerCase()
        } else {
          log.warn( "Could not read image, format not recognised" )
        }
        
        imageFormat
    }

    byte[] writeBytes(BufferedImage image, String format) {
        ByteArrayOutputStream baos = (ByteArrayOutputStream)write(image, format)
        baos.toByteArray()
    }

    OutputStream write(BufferedImage image, String format) {
        ImageIO.setUseCache(false)  // Disable disk-based cache, see http://developer.java.sun.com/developer/bugParade/bugs/4513817.html
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        ImageIO.write(image, format, baos)
        baos
    }

    BufferedImage getBufferedImage(byte[] fileContents) {
        try {
          return ImageIO.read(new ByteArrayInputStream(fileContents))
        } catch( IllegalArgumentException e ) {
          /* most probably an invalid color profile - solution: recreate the image first */
          bufferedInputStream.reset()
          JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(new ByteArrayInputStream(fileContents))
          return decoder.decodeAsBufferedImage()
        }
    }
    
    BufferedImage getBufferedImage(InputStream inputStream) {
        BufferedImage image = null
        
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)
        bufferedInputStream.mark(0)
        
        try {
          image = ImageIO.read(bufferedInputStream)
        } catch( IllegalArgumentException e ) {
          /* most probably an invalid color profile - solution: recreate the image first */
          bufferedInputStream.reset()
          JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(bufferedInputStream)
          image = decoder.decodeAsBufferedImage()
        }
        
        image
    }

    BufferedImage scale(BufferedImage image, Integer width, Integer height) {
        if(width == null && height == null) {
            return image
        }
        
        if(width == null) {
            double ratio = image.getHeight() / height
            width = image.getWidth() / ratio
        }
        
        if(height == null) {
            double ratio = image.getWidth() / width
            height = image.getHeight() / ratio
        }
        
        ResampleOp resampleOp = new ResampleOp(width, height)
        resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.VerySharp)
        resampleOp.filter(image, null)
    }
    
    BufferedImage crop(BufferedImage image, int x, int y, int width, int height) {
      BufferedImage cropImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
      Graphics2D g2d = cropImage.createGraphics()

      try {
        BufferedImage subImage = image.getSubimage(x, y, width, height)
        g2d.drawImage(subImage, null, 0, 0)
      }
      catch( RasterFormatException e ) {
        log.error("Could not crop image", e)
      }

      g2d.dispose()
      cropImage
    }
}

class HdImage {
    BufferedImage image
    String format
}
