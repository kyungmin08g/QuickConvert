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
                val command = """
                   ffmpeg -i pipe:0 -c:v libx264 -crf 18 -preset slow -c:a aac -b:a 192k -vf scale=1920:1080 -threads 4 -f mp4 $conversionFileName
                """
                return ffmpegProcess(command, fileName, conversionFileName, fileByteArray)
            }
        },
        MOV("mov") {
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".").replace(" ", "")}.${this.fileType}"
                val command = """
                   ffmpeg -i pipe:0 -c:v prores_ks -profile:v 3 -c:a pcm_s16le -vf scale=1920:1080 -threads 4 -f mov $conversionFileName
                """
                return ffmpegProcess(command, fileName, conversionFileName, fileByteArray)
            }
        },
        MKV("mkv") {
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".").replace(" ", "")}.${this.fileType}"
                val command = """
                   ffmpeg -i pipe:0 -c:v libx264 -crf 18 -preset slow -c:a aac -b:a 192k -vf scale=1920:1080 -threads 4 -f mkv $conversionFileName
                """
                return ffmpegProcess(command, fileName, conversionFileName, fileByteArray)
            }
        },
        WEBM("webm") {
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".").replace(" ", "")}.${this.fileType}"
                val command = """
                   ffmpeg -i pipe:0 -c:v libvpx-vp9 -crf 15 -b:v 0 -c:a libopus -vf scale=1920:1080 -preset slow -threads 4 -f webm $conversionFileName
                """
                return ffmpegProcess(command, fileName, conversionFileName, fileByteArray)
            }
        },
        FLV("flv") {
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".").replace(" ", "")}.${this.fileType}"
                val command = """
                   ffmpeg -i pipe:0 -c:v libx264 -crf 18 -preset slow -c:a aac -b:a 192k -vf scale=1920:1080 -threads 4 -f flv $conversionFileName
                """
                return ffmpegProcess(command, fileName, conversionFileName, fileByteArray)
            }
        },
        AVI("avi") {
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".").replace(" ", "")}.${this.fileType}"
                val command = """
                   ffmpeg -i pipe:0 -c:v libx264 -crf 18 -preset slow -c:a libmp3lame -b:a 192k -vf scale=1920:1080 -threads 4 -f avi $conversionFileName
                """
                return ffmpegProcess(command, fileName, conversionFileName, fileByteArray)
            }
        },
        GIF("gif") {
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".").replace(" ", "")}.${this.fileType}"
                val command = """
                   ffmpeg -i pipe:0 -vf fps=15,scale=1920:1080:flags=lanczos -c:v gif $conversionFileName
                """
                return ffmpegProcess(command, fileName, conversionFileName, fileByteArray)
            }
        }
    }

    // 2024년 12월 22일 04시 02분에 처음 알았는데 원본 데이터만 변환 가능하다. 왜냐하면 원본 파일이 아니면 일부 데이터가 손상되었을 위험이 있기 때문에 ffmpeg가 에러를 이르키기 때문이다. (자세한 건 아닌데 내 방식으로 정리)
    override fun ffmpegProcess(command: String, fileName: String, conversionFileName: String, fileByteArray: ByteArray): FileResponseObject {
        val process = ProcessBuilder(command.trim().split(" ")).apply { this.redirectErrorStream(true) }.start()
        ByteArrayInputStream(fileByteArray).apply { this.copyTo(process.outputStream) }
        process.outputStream.close()

        // 프로세스의 Input를 소비하지 않기 위함. process.input 한번 호출되면 읽고나서 한번 더 호출하면 더 이상 읽을 내용리 없어 에러남. 그래서 그 내용을 저장하기 위해서 Byte로 저징하고 쓸 수 있게 만듦.
        val processInputStreamBytes = process.inputStream.readBytes()
        val outputLogs = File.createTempFile("ffmpegLogs", ".txt").apply {
            this.deleteOnExit()
            this.writeBytes(processInputStreamBytes)
        }

        BufferedReader(InputStreamReader(ByteArrayInputStream(processInputStreamBytes))).use { input ->

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
                        } else "${lastConvertSizeKB.toString().let { it.substring(0, it.lastIndexOf(".") + 3) }}KB" // KB일 경우
                    }
                }
            }

            // 실시간 변환 중인 파일의 크기 구하기
            input.readLines().forEach { line ->
                if (line.contains("size=")) {
                    // 각각의 크기
                    val convertSizeKB = line.substringAfterLast("size=").replace(" ", "").substringBeforeLast("KiB").toFloat() / 1.024f
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

        Thread.sleep(1000)

        val conversionFile = File(conversionFileName).also { it.createNewFile(); it.writeBytes(processInputStreamBytes) }
        val fileBytes = conversionFile.readBytes()

        var outputFile: File? = null
        if (conversionFileName.substringAfterLast(".") == "mov") {
            outputFile = File.createTempFile("encoded", ".txt").apply { this.deleteOnExit() }
            FileOutputStream(outputFile!!).use { ByteArrayInputStream(processInputStreamBytes).copyTo(it) }
        }

        return if (process.waitFor() != 0) {
            log.error("\u001B[31mffmpeg 프로세스를 실행하던 도중 문제가 발생했습니다.\u001B[0m")
            conversionFile.delete()
            return FileResponseObject("none", null)
        } else {
            log.info("\u001B[34mffmpeg 프로세스가 정상적으로 처리되어 {} 파일이 {} 파일로 변환되었습니다.\u001B[0m", fileName, conversionFileName)
            conversionFile.delete()

            if (outputFile != null) FileResponseObject(conversionFileName, Base64.getEncoder().encodeToString(outputFile.readBytes()))
            else FileResponseObject(conversionFileName, Base64.getEncoder().encodeToString(fileBytes))
        }
    }
}
