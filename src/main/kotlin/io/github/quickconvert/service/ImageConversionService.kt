package io.github.quickconvert.service

abstract class ImageConversionService {
    abstract fun conversionPNG(fileName: String, conversionType: String)
}