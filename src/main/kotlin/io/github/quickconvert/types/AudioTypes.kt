package io.github.quickconvert.types

import io.github.quickconvert.dto.FileResponseObject
import io.github.quickconvert.service.FFmpegProcess
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStreamReader
import java.util.*

@Slf4j
@Component
object AudioTypes : FFmpegProcess {
    private val log = LoggerFactory.getLogger(this::class.java)
    private lateinit var stompTemplate: SimpMessagingTemplate

    @Autowired
    fun init(template: SimpMessagingTemplate) { this.stompTemplate = template }

    enum class Conversion(val fileType: String) : io.github.quickconvert.service.Conversion {
        MP3("mp3") {
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".").replace(" ", "")}.${this.fileType}"
                val filename = "${fileName.substringBeforeLast(".").replace(" ", "")}.${fileName.substringAfterLast(".")}"

                val restorationFile = File(filename).also { it.createNewFile(); it.writeBytes(fileByteArray) }
                val errorCommand = when(fileName.substringAfterLast(".")) {
                    "mp3" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:a libmp3lame -b:a 320k restoration-$filename"""
                    "wav" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:a pcm_s16le restoration-$filename"""
                    "flac" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:a flac restoration-$filename"""
                    "aac" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:a aac -b:a 320k restoration-$filename"""
                    else -> "error"
                }
                val restorationFileByteArray = processErrorConvert(errorCommand, filename)
                val command = """
                   ffmpeg -i ${restorationFile.absolutePath} -b:a 320k $conversionFileName
                """
                return ffmpegProcess(command, filename, conversionFileName, restorationFileByteArray)
            }
        },
        WAV("wav") {
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".").replace(" ", "")}.${this.fileType}"
                val filename = "${fileName.substringBeforeLast(".").replace(" ", "")}.${fileName.substringAfterLast(".")}"

                val restorationFile = File(filename).also { it.createNewFile(); it.writeBytes(fileByteArray) }
                val errorCommand = when(fileName.substringAfterLast(".")) {
                    "mp3" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:a libmp3lame -b:a 320k restoration-$filename"""
                    "wav" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:a pcm_s16le restoration-$filename"""
                    "flac" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:a flac restoration-$filename"""
                    "aac" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:a aac -b:a 320k restoration-$filename"""
                    else -> "error"
                }
                val restorationFileByteArray = processErrorConvert(errorCommand, filename)
                val command = """
                   ffmpeg -i ${restorationFile.absolutePath} -c:a pcm_s16le $conversionFileName
                """
                return ffmpegProcess(command, filename, conversionFileName, restorationFileByteArray)
            }
        },
        FLAC("flac") {
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".").replace(" ", "")}.${this.fileType}"
                val filename = "${fileName.substringBeforeLast(".").replace(" ", "")}.${fileName.substringAfterLast(".")}"

                val restorationFile = File(filename).also { it.createNewFile(); it.writeBytes(fileByteArray) }
                val errorCommand = when(fileName.substringAfterLast(".")) {
                    "mp3" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:a libmp3lame -b:a 320k restoration-$filename"""
                    "wav" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:a pcm_s16le restoration-$filename"""
                    "flac" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:a flac restoration-$filename"""
                    "aac" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:a aac -b:a 320k restoration-$filename"""
                    else -> "error"
                }
                val restorationFileByteArray = processErrorConvert(errorCommand, filename)
                val command = """
                   ffmpeg -i ${restorationFile.absolutePath} -c:a flac -compression_level 12 $conversionFileName
                """
                return ffmpegProcess(command, filename, conversionFileName, restorationFileByteArray)
            }
        },
        AAC("aac") {
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".").replace(" ", "")}.${this.fileType}"
                val filename = "${fileName.substringBeforeLast(".").replace(" ", "")}.${fileName.substringAfterLast(".")}"

                val restorationFile = File(filename).also { it.createNewFile(); it.writeBytes(fileByteArray) }
                val errorCommand = when(fileName.substringAfterLast(".")) {
                    "mp3" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:a libmp3lame -b:a 320k restoration-$filename"""
                    "wav" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:a pcm_s16le restoration-$filename"""
                    "flac" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:a flac restoration-$filename"""
                    "aac" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:a aac -b:a 320k restoration-$filename"""
                    else -> "error"
                }
                val restorationFileByteArray = processErrorConvert(errorCommand, filename)
                val command = """
                   ffmpeg -i ${restorationFile.absolutePath} -c:a aac -b:a 320k $conversionFileName
                """
                return ffmpegProcess(command, filename, conversionFileName, restorationFileByteArray)
            }
        }
    }

    fun processErrorConvert(command: String, fileName: String): ByteArray {
        val process = ProcessBuilder(command.trim().split(" ")).also { it.redirectErrorStream(true) }.start()
//        process.inputStream.bufferedReader().useLines { lines -> lines.forEach { println(it) }}
        process.outputStream.close()
        val convertFile = File("restoration-${fileName}")

        val processInputStreamBytes = process.inputStream.readBytes()
        val outputLogs = File.createTempFile("ffmpegRestorationLogs", ".txt").apply {
            this.deleteOnExit()
            this.writeBytes(processInputStreamBytes)
        }

        ByteArrayInputStream(processInputStreamBytes).bufferedReader().useLines { input ->
            input.forEach {
                // 파일 변환 크기 구하기
                var lastConvertSize: String? = null
                BufferedReader(InputStreamReader(ByteArrayInputStream(outputLogs.readBytes()))).use { lines ->
                    lines.readLines().forEach { line ->
                        if (line.contains("out#0")) {
                            val lastConvertSizeKB = line.substringAfterLast("audio:").replace(" ", "").substringBefore("KiB").toFloat() / 1.024f
                            val lastConvertSizeMB = lastConvertSizeKB / 1024.0f
                            val lastConvertSizeGB = lastConvertSizeMB / 1024.0f

                            lastConvertSize = if (lastConvertSizeKB >= 1024) { // MB일 경우
                                if (lastConvertSizeMB >= 1024) "${lastConvertSizeGB.toString().let { it.substring(0, it.lastIndexOf(".") + 3) }}GB" // GB일 경우
                                else "${lastConvertSizeMB.toString().let { it.substring(0, it.lastIndexOf(".") + 3) }}MB"
                            } else "${lastConvertSizeKB.toString().let { it.substring(0, it.lastIndexOf(".") + 3) }}KB" // KB일 경우
                        }
                    }

                    // 실시간 변환 중인 파일의 크기 구하기
                    if (it.contains("size=")) {
                        // 각각의 크기
                        val convertSizeKB = it.substringAfterLast("size=").replace(" ", "").substringBeforeLast("KiB").toFloat() / 1.024f
                        val convertSizeMB = convertSizeKB / 1024.0f
                        val convertSizeGB = convertSizeMB / 1024.0f

                        val content = if (convertSizeKB >= 1024) { // MB일 경우
                            if (convertSizeMB >= 1024) { // GB일 경우
                                "Restoration${convertSizeGB.toString().let {
                                    it.substring(0, it.lastIndexOf(".") + 3)
                                }}GB / $lastConvertSize"
                            } else {
                                "Restoration${convertSizeMB.toString().let {
                                    it.substring(0, it.lastIndexOf(".") + 3)
                                }}MB / $lastConvertSize"
                            }
                        } else { // MB와 GB가 아닐 경우
                            "Restoration${convertSizeKB.toString().let {
                                if ((it.lastIndexOf(".") + 1).toString().length < 3) it.substring(0, it.lastIndexOf(".") + 2)
                                else it.substring(0, it.lastIndexOf(".") + 3)
                            }}KB / $lastConvertSize"
                        }

                        stompTemplate.convertAndSend("/sub/1", content)
                        Thread.sleep(200)
                    }
                }
            }
        }

        stompTemplate.convertAndSend("/sub/1", "upload")
        Thread.sleep(1000)
        return if (process.waitFor() != 0) {
            log.error("\u001B[31mffmpeg 프로세스를 실행하던 손상된 파일을 복구하던 도중 문제가 발생했습니다.\u001B[0m")
            "1".toByteArray()
        } else {
            log.info("\u001B[34mffmpeg 프로세스가 정상적으로 처리되어 손상된 {} 파일이 복구돤 {} 파일로 변환되었습니다.\u001B[0m", fileName, "restoration-$fileName")
            convertFile.readBytes()
        }
    }

    // 2024년 12월 22일 04시 02분에 처음 알았는데 원본 데이터만 변환 가능하다. 왜냐하면 원본 파일이 아니면 일부 데이터가 손상되었을 위험이 있기 때문에 ffmpeg가 에러를 이르키기 때문이다. (자세한 건 아닌데 내 방식으로 정리)
    override fun ffmpegProcess(command: String, fileName: String, conversionFileName: String, fileByteArray: ByteArray): FileResponseObject {
        val process = ProcessBuilder(command.trim().split(" ")).apply { this.redirectErrorStream(true) }.start()
        process.outputStream.close()

        val processInputStreamBytes = process.inputStream.readBytes()
        val outputLogs = File.createTempFile("ffmpegLogs", ".txt").apply {
            this.deleteOnExit()
            this.writeBytes(processInputStreamBytes)
        }

        ByteArrayInputStream(processInputStreamBytes).bufferedReader().useLines { input ->
            input.forEach {
                // 파일 변환 크기 구하기
                var lastConvertSize: String? = null
                BufferedReader(InputStreamReader(ByteArrayInputStream(outputLogs.readBytes()))).use { lines ->
                    lines.readLines().forEach { line ->
                        if (line.contains("out#0")) {
                            val lastConvertSizeKB = line.substringAfterLast("audio:").replace(" ", "").substringBefore("KiB").toFloat() / 1.024f
                            val lastConvertSizeMB = lastConvertSizeKB / 1024.0f
                            val lastConvertSizeGB = lastConvertSizeMB / 1024.0f

                            lastConvertSize = if (lastConvertSizeKB >= 1024) { // MB일 경우
                                if (lastConvertSizeMB >= 1024) "${lastConvertSizeGB.toString().let { it.substring(0, it.lastIndexOf(".") + 3) }}GB" // GB일 경우
                                else "${lastConvertSizeMB.toString().let { it.substring(0, it.lastIndexOf(".") + 3) }}MB"
                            } else "${lastConvertSizeKB.toString().let { it.substring(0, it.lastIndexOf(".") + 3) }}KB" // KB일 경우
                        }
                    }

                    // 실시간 변환 중인 파일의 크기 구하기
                    if (it.contains("size=")) {
                        // 각각의 크기
                        val convertSizeKB = it.substringAfterLast("size=").replace(" ", "").substringBeforeLast("KiB").toFloat() / 1.024f
                        val convertSizeMB = convertSizeKB / 1024.0f
                        val convertSizeGB = convertSizeMB / 1024.0f

                        val content = if (convertSizeKB >= 1024) { // MB일 경우
                            if (convertSizeMB >= 1024) { // GB일 경우
                                "${convertSizeGB.toString().let {
                                    it.substring(0, it.lastIndexOf(".") + 3)
                                }}GB / $lastConvertSize"
                            } else {
                                "${convertSizeMB.toString().let {
                                    it.substring(0, it.lastIndexOf(".") + 3)
                                }}MB / $lastConvertSize"
                            }
                        } else { // MB와 GB가 아닐 경우
                            "${convertSizeKB.toString().let {
                                if ((it.lastIndexOf(".") + 1).toString().length < 3) it.substring(0, it.lastIndexOf(".") + 2)
                                else it.substring(0, it.lastIndexOf(".") + 3)
                            }}KB / $lastConvertSize"
                        }

                        stompTemplate.convertAndSend("/sub/1", content)
                        Thread.sleep(200)
                    }
                }
            }
        }

        val conversionFile = File(conversionFileName)
        val fileBytes = conversionFile.readBytes()

        return if (process.waitFor() != 0) {
            log.error("\u001B[31mffmpeg 프로세스를 실행하던 도중 문제가 발생했습니다.\u001B[0m")
            File(fileName).delete()
            File("restoration-${fileName}").delete()
            conversionFile.delete()
            return FileResponseObject("none", null)
        } else {
            log.info("\u001B[34mffmpeg 프로세스가 정상적으로 처리되어 {} 파일이 {} 파일로 변환되었습니다.\u001B[0m", fileName, conversionFileName)
            File(fileName).delete()
            File("restoration-${fileName}").delete()
            conversionFile.delete()
            FileResponseObject(conversionFileName, Base64.getEncoder().encodeToString(fileBytes))
        }
    }
}