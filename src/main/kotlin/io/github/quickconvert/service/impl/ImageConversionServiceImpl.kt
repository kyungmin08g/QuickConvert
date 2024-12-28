package io.github.quickconvert.service.impl

import io.github.quickconvert.dto.FileInfo
import io.github.quickconvert.dto.FileResponseObject
import io.github.quickconvert.service.ImageConversionService
import io.github.quickconvert.types.ImageTypes
import org.springframework.stereotype.Service

@Service
class ImageConversionServiceImpl : ImageConversionService() {
    override fun convertImage(fileInfo: FileInfo): FileResponseObject {
        return when (fileInfo.conversionType) {
            ImageTypes.Conversion.JPG.name -> ImageTypes.Conversion.JPG.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            ImageTypes.Conversion.PNG.name -> ImageTypes.Conversion.PNG.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            ImageTypes.Conversion.JPEG.name -> ImageTypes.Conversion.JPEG.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            ImageTypes.Conversion.GIF.name -> ImageTypes.Conversion.GIF.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            ImageTypes.Conversion.BMP.name -> ImageTypes.Conversion.BMP.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            ImageTypes.Conversion.TIFF.name -> ImageTypes.Conversion.TIFF.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            ImageTypes.Conversion.WEBP.name -> ImageTypes.Conversion.WEBP.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            ImageTypes.Conversion.PDF.name -> ImageTypes.Conversion.PDF.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            ImageTypes.Conversion.DOCX.name -> ImageTypes.Conversion.DOCX.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            ImageTypes.Conversion.TXT.name -> ImageTypes.Conversion.TXT.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            ImageTypes.Conversion.SVG.name -> ImageTypes.Conversion.SVG.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            ImageTypes.Conversion.ICO.name -> ImageTypes.Conversion.ICO.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            ImageTypes.Conversion.PSD.name -> ImageTypes.Conversion.PSD.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            else -> FileResponseObject("none", null)
        }
    }
}