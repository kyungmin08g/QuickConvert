package io.github.quickconvert.types

import io.github.quickconvert.dto.FileResponseObject
import io.github.quickconvert.service.FFmpegProcess
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component
import java.io.*
import java.util.*

@Slf4j
@Component
object VideoTypes: FFmpegProcess {
    private val log = LoggerFactory.getLogger(this::class.java)
    private lateinit var stompTemplate: SimpMessagingTemplate

    @Autowired
    fun init(template: SimpMessagingTemplate) { this.stompTemplate = template }

    enum class Conversion(val fileType: String): io.github.quickconvert.service.Conversion {
        MP4("mp4") {
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".").replace(" ", "")}.${this.fileType}"
                val filename = "${fileName.substringBeforeLast(".").replace(" ", "")}.${fileName.substringAfterLast(".")}"

                val restorationFile = File(filename).also { it.createNewFile(); it.writeBytes(fileByteArray) }
                val errorCommand = when(fileName.substringAfterLast(".")) {
                    "mp4" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libx264 -preset slow -crf 18 -c:a aac -b:a 192k -strict experimental restoration-$filename"""
                    "mov" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v prores_ks -profile:v 3 -c:a aac -b:a 192k restoration-$filename"""
                    "mkv" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libx265 -crf 18 -preset slow -c:a libopus -b:a 192k restoration-$filename"""
                    "webm" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libvpx-vp9 -crf 30 -b:v 1M -c:a libopus -b:a 192k restoration-$filename"""
                    "flv" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libx264 -preset slow -crf 18 -c:a aac -b:a 192k restoration-$filename"""
                    "avi" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libx264 -preset slow -crf 18 -c:a libmp3lame -b:a 192k restoration-$filename"""
                    "gif" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -vf "fps=20,scale=640:-1:flags=lanczos" -c:v gif restoration-$filename"""
                    else -> "error"
                }
                val restorationFileByteArray = processErrorConvert(errorCommand, filename)

                val command = """
                   ffmpeg -i ${restorationFile.absolutePath} -c:v libx264 -crf 18 -preset slow -c:a aac -b:a 192k -vf scale=1920:1080 -threads 4 -f mp4 $conversionFileName
                """
                return ffmpegProcess(command, filename, conversionFileName, restorationFileByteArray)
            }
        },
        MOV("mov") {
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".").replace(" ", "")}.${this.fileType}"
                val filename = "${fileName.substringBeforeLast(".").replace(" ", "")}.${fileName.substringAfterLast(".")}"

                val restorationFile = File(filename).also { it.createNewFile(); it.writeBytes(fileByteArray) }
                val errorCommand = when(fileName.substringAfterLast(".")) {
                    "mp4" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libx264 -preset slow -crf 18 -c:a aac -b:a 192k -strict experimental restoration-$filename"""
                    "mov" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v prores_ks -profile:v 3 -c:a aac -b:a 192k restoration-$filename"""
                    "mkv" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libx265 -crf 18 -preset slow -c:a libopus -b:a 192k restoration-$filename"""
                    "webm" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libvpx-vp9 -crf 30 -b:v 1M -c:a libopus -b:a 192k restoration-$filename"""
                    "flv" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libx264 -preset slow -crf 18 -c:a aac -b:a 192k restoration-$filename"""
                    "avi" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libx264 -preset slow -crf 18 -c:a libmp3lame -b:a 192k restoration-$filename"""
                    "gif" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -vf "fps=20,scale=640:-1:flags=lanczos" -c:v gif restoration-$filename"""
                    else -> "error"
                }
                val restorationFileByteArray = processErrorConvert(errorCommand, filename)

                val command = """
                   ffmpeg -i ${restorationFile.absolutePath} -c:v libx264 -crf 18 -preset slow -c:a aac -b:a 192k -vf scale=1920:1080 -threads 4 -f mov $conversionFileName
                """
                return ffmpegProcess(command, filename, conversionFileName, restorationFileByteArray)
            }
        },
        MKV("mkv") {
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".").replace(" ", "")}.${this.fileType}"
                val filename = "${fileName.substringBeforeLast(".").replace(" ", "")}.${fileName.substringAfterLast(".")}"

                val restorationFile = File(filename).also { it.createNewFile(); it.writeBytes(fileByteArray) }
                val errorCommand = when(fileName.substringAfterLast(".")) {
                    "mp4" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libx264 -preset slow -crf 18 -c:a aac -b:a 192k -strict experimental restoration-$filename"""
                    "mov" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v prores_ks -profile:v 3 -c:a aac -b:a 192k restoration-$filename"""
                    "mkv" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libx265 -crf 18 -preset slow -c:a libopus -b:a 192k restoration-$filename"""
                    "webm" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libvpx-vp9 -crf 30 -b:v 1M -c:a libopus -b:a 192k restoration-$filename"""
                    "flv" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libx264 -preset slow -crf 18 -c:a aac -b:a 192k restoration-$filename"""
                    "avi" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libx264 -preset slow -crf 18 -c:a libmp3lame -b:a 192k restoration-$filename"""
                    "gif" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -vf "fps=20,scale=640:-1:flags=lanczos" -c:v gif restoration-$filename"""
                    else -> "error"
                }
                val restorationFileByteArray = processErrorConvert(errorCommand, filename)
                val command = """
                   ffmpeg -i ${restorationFile.absolutePath} -c:v libx264 -crf 18 -preset slow -c:a aac -b:a 192k -vf scale=1920:1080 -threads 4 -f mkv $conversionFileName
                """
                return ffmpegProcess(command, filename, conversionFileName, restorationFileByteArray)
            }
        },
        WEBM("webm") {
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".").replace(" ", "")}.${this.fileType}"
                val filename = "${fileName.substringBeforeLast(".").replace(" ", "")}.${fileName.substringAfterLast(".")}"

                val restorationFile = File(filename).also { it.createNewFile(); it.writeBytes(fileByteArray) }
                val errorCommand = when(fileName.substringAfterLast(".")) {
                    "mp4" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libx264 -preset slow -crf 18 -c:a aac -b:a 192k -strict experimental restoration-$filename"""
                    "mov" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v prores_ks -profile:v 3 -c:a aac -b:a 192k restoration-$filename"""
                    "mkv" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libx265 -crf 18 -preset slow -c:a libopus -b:a 192k restoration-$filename"""
                    "webm" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libvpx-vp9 -crf 30 -b:v 1M -c:a libopus -b:a 192k restoration-$filename"""
                    "flv" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libx264 -preset slow -crf 18 -c:a aac -b:a 192k restoration-$filename"""
                    "avi" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libx264 -preset slow -crf 18 -c:a libmp3lame -b:a 192k restoration-$filename"""
                    "gif" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -vf "fps=20,scale=640:-1:flags=lanczos" -c:v gif restoration-$filename"""
                    else -> "error"
                }
                val restorationFileByteArray = processErrorConvert(errorCommand, filename)
                val command = """
                   ffmpeg -i ${restorationFile.absolutePath} -c:v libvpx-vp9 -crf 15 -b:v 0 -c:a libopus -vf scale=1920:1080 -preset slow -threads 4 -f webm $conversionFileName
                """
                return ffmpegProcess(command, filename, conversionFileName, restorationFileByteArray)
            }
        },
        FLV("flv") {
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".").replace(" ", "")}.${this.fileType}"
                val filename = "${fileName.substringBeforeLast(".").replace(" ", "")}.${fileName.substringAfterLast(".")}"

                val restorationFile = File(filename).also { it.createNewFile(); it.writeBytes(fileByteArray) }
                val errorCommand = when(fileName.substringAfterLast(".")) {
                    "mp4" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libx264 -preset slow -crf 18 -c:a aac -b:a 192k -strict experimental restoration-$filename"""
                    "mov" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v prores_ks -profile:v 3 -c:a aac -b:a 192k restoration-$filename"""
                    "mkv" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libx265 -crf 18 -preset slow -c:a libopus -b:a 192k restoration-$filename"""
                    "webm" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libvpx-vp9 -crf 30 -b:v 1M -c:a libopus -b:a 192k restoration-$filename"""
                    "flv" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libx264 -preset slow -crf 18 -c:a aac -b:a 192k restoration-$filename"""
                    "avi" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libx264 -preset slow -crf 18 -c:a libmp3lame -b:a 192k restoration-$filename"""
                    "gif" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -vf "fps=20,scale=640:-1:flags=lanczos" -c:v gif restoration-$filename"""
                    else -> "error"
                }
                val restorationFileByteArray = processErrorConvert(errorCommand, filename)
                val command = """
                   ffmpeg -i ${restorationFile.absolutePath} -c:v libx264 -crf 18 -preset slow -c:a aac -b:a 192k -vf scale=1920:1080 -threads 4 -f flv $conversionFileName
                """
                return ffmpegProcess(command, filename, conversionFileName, restorationFileByteArray)
            }
        },
        AVI("avi") {
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".").replace(" ", "")}.${this.fileType}"
                val filename = "${fileName.substringBeforeLast(".").replace(" ", "")}.${fileName.substringAfterLast(".")}"

                val restorationFile = File(filename).also { it.createNewFile(); it.writeBytes(fileByteArray) }
                val errorCommand = when(fileName.substringAfterLast(".")) {
                    "mp4" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libx264 -preset slow -crf 18 -c:a aac -b:a 192k -strict experimental restoration-$filename"""
                    "mov" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v prores_ks -profile:v 3 -c:a aac -b:a 192k restoration-$filename"""
                    "mkv" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libx265 -crf 18 -preset slow -c:a libopus -b:a 192k restoration-$filename"""
                    "webm" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libvpx-vp9 -crf 30 -b:v 1M -c:a libopus -b:a 192k restoration-$filename"""
                    "flv" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libx264 -preset slow -crf 18 -c:a aac -b:a 192k restoration-$filename"""
                    "avi" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libx264 -preset slow -crf 18 -c:a libmp3lame -b:a 192k restoration-$filename"""
                    "gif" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -vf "fps=20,scale=640:-1:flags=lanczos" -c:v gif restoration-$filename"""
                    else -> "error"
                }
                val restorationFileByteArray = processErrorConvert(errorCommand, filename)
                val command = """
                   ffmpeg -i ${restorationFile.absolutePath} -c:v libx264 -crf 18 -preset slow -c:a libmp3lame -b:a 192k -vf scale=1920:1080 -threads 4 -f avi $conversionFileName
                """
                return ffmpegProcess(command, filename, conversionFileName, restorationFileByteArray)
            }
        },
        GIF("gif") {
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".").replace(" ", "")}.${this.fileType}"
                val filename = "${fileName.substringBeforeLast(".").replace(" ", "")}.${fileName.substringAfterLast(".")}"

                val restorationFile = File(filename).also { it.createNewFile(); it.writeBytes(fileByteArray) }
                val errorCommand = when(fileName.substringAfterLast(".")) {
                    "mp4" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libx264 -preset slow -crf 18 -c:a aac -b:a 192k -strict experimental restoration-$filename"""
                    "mov" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v prores_ks -profile:v 3 -c:a aac -b:a 192k restoration-$filename"""
                    "mkv" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libx265 -crf 18 -preset slow -c:a libopus -b:a 192k restoration-$filename"""
                    "webm" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libvpx-vp9 -crf 30 -b:v 1M -c:a libopus -b:a 192k restoration-$filename"""
                    "flv" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libx264 -preset slow -crf 18 -c:a aac -b:a 192k restoration-$filename"""
                    "avi" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libx264 -preset slow -crf 18 -c:a libmp3lame -b:a 192k restoration-$filename"""
                    "gif" -> """ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -vf "fps=20,scale=640:-1:flags=lanczos" -c:v gif restoration-$filename"""
                    else -> "error"
                }
                val restorationFileByteArray = processErrorConvert(errorCommand, filename)
                val command = """
                   ffmpeg -i ${restorationFile.absolutePath} -vf fps=15,scale=1920:1080:flags=lanczos -c:v gif $conversionFileName
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
                        if (line.contains("Lsize=")) {
                            val lastConvertSizeKB = line.substringAfterLast("Lsize=").replace(" ", "").substringBeforeLast("KiB").toFloat() / 1.024f
                            val lastConvertSizeMB = lastConvertSizeKB / 1024.0f
                            val lastConvertSizeGB = lastConvertSizeMB / 1024.0f

                            lastConvertSize = if (lastConvertSizeKB >= 1024) { // MB일 경우
                                if (lastConvertSizeMB >= 1024) "${lastConvertSizeGB.toString().let { it.substring(0, it.lastIndexOf(".") + 3) }}GB" // GB일 경우
                                else "${lastConvertSizeMB.toString().let { it.substring(0, it.lastIndexOf(".") + 3) }}MB"
                            } else { // KB일 경우
                                "${lastConvertSizeKB.toString().let { it.substring(0, it.lastIndexOf(".") + 3) }}KB"
                            }
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
            log.info("\u001B[34mffmpeg 프로세스가 정상적으로 처리되어 손상된 {} 파일이 복구돤 {} 파일로 변환되었습니다.1\u001B[0m", fileName, "restoration-$fileName")
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
                        if (line.contains("Lsize=")) {
                            val lastConvertSizeKB = line.substringAfterLast("Lsize=").replace(" ", "").substringBeforeLast("KiB").toFloat() / 1.024f
                            val lastConvertSizeMB = lastConvertSizeKB / 1024.0f
                            val lastConvertSizeGB = lastConvertSizeMB / 1024.0f

                            lastConvertSize = if (lastConvertSizeKB >= 1024) { // MB일 경우
                                if (lastConvertSizeMB >= 1024) "${lastConvertSizeGB.toString().let { it.substring(0, it.lastIndexOf(".") + 3) }}GB" // GB일 경우
                                else "${lastConvertSizeMB.toString().let { it.substring(0, it.lastIndexOf(".") + 3) }}MB"
                            } else { // KB일 경우
                                "${lastConvertSizeKB.toString().let { it.substring(0, it.lastIndexOf(".") + 3) }}KB"
                            }
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
