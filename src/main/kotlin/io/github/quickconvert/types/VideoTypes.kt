package io.github.quickconvert.types

import io.github.quickconvert.dto.FileResponseObject
import io.github.quickconvert.service.FFmpegProcess
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import java.io.*
import java.util.*

@Slf4j
object VideoTypes: FFmpegProcess {
    private val log = LoggerFactory.getLogger(this::class.java)

    enum class Conversion(val fileType: String): io.github.quickconvert.service.Conversion {
        MP4("mp4") {
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".")}.${this.fileType}"
                val command = """
                   ffmpeg -i pipe:0 -c:v libx264 -crf 18 -preset slow -c:a aac -b:a 192k -vf scale=1920:1080 -threads 4 $conversionFileName
                """
                return ffmpegProcess(command, fileName, conversionFileName, fileByteArray)
            }
        },
        MOV("mov") {
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".")}.${this.fileType}"
                val command = """
                   ffmpeg -i pipe:0 -c:v prores_ks -profile:v 3 -c:a pcm_s16le -vf scale=1920:1080 -threads 4 $conversionFileName
                """
                return ffmpegProcess(command, fileName, conversionFileName, fileByteArray)
            }
        },
        MKV("mkv") {
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".")}.${this.fileType}"
                val command = """
                   ffmpeg -i pipe:0 -c:v libx264 -crf 18 -preset slow -c:a aac -b:a 192k -vf scale=1920:1080 -threads 4 $conversionFileName
                """
                return ffmpegProcess(command, fileName, conversionFileName, fileByteArray)
            }
        },
        WEBM("webm") {
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".")}.${this.fileType}"
                val command = """
                   ffmpeg -i pipe:0 -c:v libvpx-vp9 -crf 15 -b:v 0 -c:a libopus -vf scale=1920:1080 -preset slow -threads 4 $conversionFileName
                """
                return ffmpegProcess(command, fileName, conversionFileName, fileByteArray)
            }
        },
        FLV("flv") {
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".")}.${this.fileType}"
                val command = """
                   ffmpeg -i pipe:0 -c:v libx264 -crf 18 -preset slow -c:a aac -b:a 192k -vf scale=1920:1080 -threads 4 $conversionFileName
                """
                return ffmpegProcess(command, fileName, conversionFileName, fileByteArray)
            }
        },
        AVI("avi") {
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".")}.${this.fileType}"
                val command = """
                   ffmpeg -i pipe:0 -c:v libx264 -crf 18 -preset slow -c:a libmp3lame -b:a 192k -vf scale=1920:1080 -threads 4 $conversionFileName
                """
                return ffmpegProcess(command, fileName, conversionFileName, fileByteArray)
            }
        },
        GIF("gif") {
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".")}.${this.fileType}"
                val command = """
                   ffmpeg -i pipe:0 -vf fps=15,scale=1920:1080:flags=lanczos -c:v gif $conversionFileName
                """
                return ffmpegProcess(command, fileName, conversionFileName, fileByteArray)
            }
        }
    }

    override fun ffmpegProcess(command: String, fileName: String, conversionFileName: String, fileByteArray: ByteArray): FileResponseObject {
        val processBuilder = ProcessBuilder(command.trim().split(" "))
        processBuilder.redirectErrorStream(true)
        val process = processBuilder.start()

        val inputStream = ByteArrayInputStream(fileByteArray)
        inputStream.copyTo(process.outputStream)
        process.outputStream.close()

        val conversionFile = File(conversionFileName)
        val fileBytes = conversionFile.readBytes()

        var outputFile: File? = null
        if (conversionFileName.substringAfterLast(".") == "mov") {
            outputFile = File.createTempFile("encoded", ".txt")
            outputFile.deleteOnExit()
            FileOutputStream(outputFile!!).use { process.inputStream.copyTo(it) }
        }

        return if (process.waitFor() != 0) {
            log.error("\u001B[31mffmpeg 프로세스를 실행하던 도중 문제가 발생했습니다.\u001B[37m")
            conversionFile.delete()
            return FileResponseObject("none", null)
        } else {
            log.info("\u001B[34mffmpeg 프로세스가 정상적으로 처리되어 {} 파일이 {} 파일로 변환되었습니다.\u001B[37m", fileName, conversionFileName)
            conversionFile.delete()

            if (outputFile != null) FileResponseObject(conversionFileName, Base64.getEncoder().encodeToString(outputFile.readBytes()))
            else FileResponseObject(conversionFileName, Base64.getEncoder().encodeToString(fileBytes))
        }
    }
}
