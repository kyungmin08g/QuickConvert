package io.github.quickconvert.service

import io.github.quickconvert.dto.FileResponseObject

interface Conversion { fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject }