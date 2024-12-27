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
            /*
                MP4 : O
                MOV : O
                MPEG : O
                WEBM : O
                FLV : O
                AVI : O
                GIF : X
             */
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".").replace(" ", "")}.${this.fileType}"
                val filename = "${fileName.substringBeforeLast(".").replace(" ", "")}.${fileName.substringAfterLast(".")}"

                val restorationFile = File(filename).also { it.createNewFile(); it.writeBytes(fileByteArray) }
                val command = """
                   ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libx264 -preset slow -crf 14 -c:a aac -b:a 320k -vf scale=1920:1080 -threads 4 -strict experimental -f mp4 convert-$conversionFileName
                """

                return ffmpegProcess(command, filename, conversionFileName, fileByteArray)
            }
        },
        MOV("mov") {
            /*
                MP4 : O
                MOV : O
                MPEG : O
                WEBM : O
                FLV : O
                AVI : O
                GIF : O
                근데 이제 파일을 다운로드하는 문제만 해결되면 됨
             */
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".").replace(" ", "")}.${this.fileType}"
                val filename = "${fileName.substringBeforeLast(".").replace(" ", "")}.${fileName.substringAfterLast(".")}"

                val restorationFile = File(filename).also { it.createNewFile(); it.writeBytes(fileByteArray) }
                val command = """
                    ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v prores_ks -profile:v 3 -c:a aac -b:a 320k -vf scale=1920:1080 -crf 35 -preset fast -threads 4 -strict experimental -f mov convert-$conversionFileName
                """

                return ffmpegProcess(command, filename, conversionFileName, fileByteArray)
            }
        },
        WEBM("webm") {
            /*
                MP4 : O
                MOV : O
                MPEG : O
                WEBM : O
                FLV : O
                AVI : O
                GIF : O
             */
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".").replace(" ", "")}.${this.fileType}"
                val filename = "${fileName.substringBeforeLast(".").replace(" ", "")}.${fileName.substringAfterLast(".")}"

                val restorationFile = File(filename).also { it.createNewFile(); it.writeBytes(fileByteArray) }
                val command = """
                   ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libvpx-vp9 -crf 30 -b:v 2M -c:a libopus -b:a 320k -vf scale=1920:1080 -threads 4 -strict experimental -f webm convert-$conversionFileName
                """

                return ffmpegProcess(command, filename, conversionFileName, fileByteArray)
            }
        },
        FLV("flv") {
            /*
                MP4 : O
                MOV : O
                FLV : O
                WEBM : O
                MPEG : O
                AVI : O
                GIF : X
             */
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".").replace(" ", "")}.${this.fileType}"
                val filename = "${fileName.substringBeforeLast(".").replace(" ", "")}.${fileName.substringAfterLast(".")}"

                val restorationFile = File(filename).also { it.createNewFile(); it.writeBytes(fileByteArray) }
                val command = """
                   ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libx264 -preset slow -crf 14 -c:a aac -b:a 320k -vf scale=1920:1080 -threads 4 -strict experimental -f flv convert-$conversionFileName
                """

                return ffmpegProcess(command, filename, conversionFileName, fileByteArray)
            }
        },
        MPEG("mpeg") {
            /*
                MP4 : O
                MOV : O
                FLV : O
                WEBM : O
                MPEG : O
                AVI : O
                GIF : X
             */
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".").replace(" ", "")}.${this.fileType}"
                val filename = "${fileName.substringBeforeLast(".").replace(" ", "")}.${fileName.substringAfterLast(".")}"

                val restorationFile = File(filename).also { it.createNewFile(); it.writeBytes(fileByteArray) }
                val command = """
                   ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v mpeg2video -b:v 20000k -s 1920x1080 -c:a mp2 -b:a 320k -y convert-$conversionFileName
                """

                return ffmpegProcess(command, filename, conversionFileName, fileByteArray)
            }
        },
        AVI("avi") {
            /*
                MP4 : O
                MOV : O
                WEBM : O
                FLV : O
                MPEG : O
                AVI : O
                GIF : X
             */
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".").replace(" ", "")}.${this.fileType}"
                val filename = "${fileName.substringBeforeLast(".").replace(" ", "")}.${fileName.substringAfterLast(".")}"

                val restorationFile = File(filename).also { it.createNewFile(); it.writeBytes(fileByteArray) }
                val command = """
                   ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libx264 -preset slow -crf 14 -c:a libmp3lame -b:a 320k -vf scale=1920:1080 -threads 4 -strict experimental -f avi convert-$conversionFileName
                """

                return ffmpegProcess(command, filename, conversionFileName, fileByteArray)
            }
        },
        GIF("gif") {
            /*
                MP4 : O
                MOV : O
                WEBM : O
                FLV : O
                MPEG : O
                AVI : O
                GIF : O
             */
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".").replace(" ", "")}.${this.fileType}"
                val filename = "${fileName.substringBeforeLast(".").replace(" ", "")}.${fileName.substringAfterLast(".")}"

                val restorationFile = File(filename).also { it.createNewFile(); it.writeBytes(fileByteArray) }
                val command = """
                    ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -vf fps=15,scale=1920:1080:flags=lanczos -c:v gif -an -threads 4 convert-$conversionFileName
                """

                return ffmpegProcess(command, filename, conversionFileName, fileByteArray)
            }
        }
    }

    // 2024년 12월 22일 04시 02분에 처음 알았는데 원본 데이터만 변환 가능하다. 왜냐하면 원본 파일이 아니면 일부 데이터가 손상되었을 위험이 있기 때문에 ffmpeg가 에러를 이르키기 때문이다. (자세한 건 아닌데 내 방식으로 정리)
    override fun ffmpegProcess(command: String, fileName: String, conversionFileName: String, fileByteArray: ByteArray): FileResponseObject {
        val process = ProcessBuilder(command.trim().split(" ")).apply { this.redirectErrorStream(true) }.start()
        process.outputStream.close()

        val processInputStreamBytes = process.inputStream.readBytes()
        ByteArrayInputStream(processInputStreamBytes).bufferedReader().useLines { input -> input.forEach { println(it) } }

        val outputLogs = File.createTempFile("ffmpegLogs", ".txt").apply {
            this.deleteOnExit()
            this.writeBytes(processInputStreamBytes)
        }

        ByteArrayInputStream(processInputStreamBytes).bufferedReader().useLines { input ->
            input.forEach {
                println(it)

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
                                    if ((it.lastIndexOf(".") + 1).toString().length < 3) it.substring(0, it.lastIndexOf(".") + 2)
                                    else it.substring(0, it.lastIndexOf(".") + 3)
                                }}MB / $lastConvertSize"
                            }
                        } else { // MB와 GB가 아닐 경우
                            "${convertSizeKB.toString().let {
                                if ((it.lastIndexOf(".") + 1).toString().length < 3) it.substring(0, it.lastIndexOf(".") + 2)
                                else it.substring(0, it.lastIndexOf(".") + 3)
                            }}KB / $lastConvertSize"
                        }

                        stompTemplate.convertAndSend("/sub/1", content)
                        Thread.sleep(100)
                    }
                }
            }
        }

        Thread.sleep(2000)

        val conversionFile = File("convert-${conversionFileName}")
        val fileBytes = conversionFile.readBytes()

        // ---------------------------------------------------------------------------------------------------------

//        var fakeFile: File? = null
//        if (conversionFileName.substringAfterLast(".") == "mov") {
//            fakeFile = File("output.txt").also{ it.createNewFile() }
//            fakeFile.outputStream().write(fileBytes)
//        }

        return if (process.waitFor() != 0) {
            log.error("\u001B[31mffmpeg 프로세스를 실행하던 도중 문제가 발생했습니다.\u001B[0m")
            File(fileName).delete()
            conversionFile.delete()
            FileResponseObject("none", null)
        } else {
            log.info("\u001B[34mffmpeg 프로세스가 정상적으로 처리되어 {} 파일이 {} 파일로 변환되었습니다.\u001B[0m", fileName, conversionFileName)
            File(fileName).delete()
            if (conversionFileName.substringAfterLast(".") != "gif") conversionFile.delete()

//            if (fakeFile != null) FileResponseObject(conversionFileName, Base64.getEncoder().encodeToString(fakeFile.readBytes()))
//            else FileResponseObject(conversionFileName, Base64.getEncoder().encodeToString(fileBytes))
            FileResponseObject(conversionFileName, fileBytes)
        }
    }
}
