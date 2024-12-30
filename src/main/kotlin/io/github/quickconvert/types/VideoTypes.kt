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


@Slf4j
@Component
object VideoTypes: FFmpegProcess {
    private val log = LoggerFactory.getLogger(this::class.java)
    private lateinit var stompTemplate: SimpMessagingTemplate
    val uuidList: MutableList<String> = mutableListOf()
    var uuid: String? = null

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
                if (fileName.substringAfterLast(".") != "gif") {
                    val conversionFileName = "${fileName.substringBeforeLast(".").replace(" ", "")}:${uuid!!}.${this.fileType}"
                    val filename = "${fileName.substringBeforeLast(".").replace(" ", "")}.${fileName.substringAfterLast(".")}"

                    val restorationFile = File("files/$filename").also { it.createNewFile(); it.writeBytes(fileByteArray) }
                    // ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libx264 -preset slow -crf 14 -c:a aac -b:a 320k -vf scale=1920:1080 -threads 4 -strict experimental -f mp4 files/convert-$conversionFileName
                    val command = """
                        ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libx264 -preset veryfast -crf 18 -c:a aac -b:a 320k -vf scale=1920:1080 -pix_fmt yuv420p -threads 8 -f mp4 files/convert-$conversionFileName
                    """

                    return ffmpegProcess(command, filename, conversionFileName, fileByteArray)
                }

                return FileResponseObject("none", null)
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
             */
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".").replace(" ", "")}:${uuid!!}.${this.fileType}"
                val filename = "${fileName.substringBeforeLast(".").replace(" ", "")}.${fileName.substringAfterLast(".")}"

                val restorationFile = File("files/$filename").also { it.createNewFile(); it.writeBytes(fileByteArray) }
                val command = """
                    ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libx264 -preset veryfast -crf 18 -c:a aac -b:a 320k -vf scale=1920:1080 -pix_fmt yuv420p -threads 8 -f mov files/convert-$conversionFileName
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
                val conversionFileName = "${fileName.substringBeforeLast(".").replace(" ", "")}:${uuid!!}.${this.fileType}"
                val filename = "${fileName.substringBeforeLast(".").replace(" ", "")}.${fileName.substringAfterLast(".")}"

                val restorationFile = File("files/$filename").also { it.createNewFile(); it.writeBytes(fileByteArray) }
                // ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libvpx-vp9 -crf 30 -b:v 2M -c:a libopus -b:a 320k -vf scale=1920:1080 -threads 4 -strict experimental -f webm files/convert-$conversionFileName
                val command = """
                    ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libvpx-vp9 -preset veryfast -crf 30 -c:a libopus -b:a 320k -vf scale=1920:1080 -pix_fmt yuv420p -threads 8 -f webm files/convert-$conversionFileName
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
                val conversionFileName = "${fileName.substringBeforeLast(".").replace(" ", "")}:${uuid!!}.${this.fileType}"
                val filename = "${fileName.substringBeforeLast(".").replace(" ", "")}.${fileName.substringAfterLast(".")}"

                val restorationFile = File("files/$filename").also { it.createNewFile(); it.writeBytes(fileByteArray) }
                // ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libx264 -preset slow -crf 14 -c:a aac -b:a 320k -vf scale=1920:1080 -threads 4 -strict experimental -f flv files/convert-$conversionFileName
                val command = """
                   ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libx264 -preset veryfast -crf 18 -c:a libmp3lame -b:a 320k -vf scale=1920:1080 -pix_fmt yuv420p -threads 8 -f flv files/convert-$conversionFileName
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
                if (fileName.substringAfterLast(".") != "gif") {
                    val conversionFileName = "${fileName.substringBeforeLast(".").replace(" ", "")}:${uuid!!}.${this.fileType}"
                    val filename = "${fileName.substringBeforeLast(".").replace(" ", "")}.${fileName.substringAfterLast(".")}"

                    val restorationFile = File("files/$filename").also { it.createNewFile(); it.writeBytes(fileByteArray) }
                    // ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v mpeg2video -b:v 20000k -s 1920x1080 -c:a mp2 -b:a 320k -y files/convert-$conversionFileName
                    val command = """
                        ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v mpeg2video -preset veryfast -crf 18 -c:a mp2 -b:a 320k -vf scale=1920:1080 -pix_fmt yuv420p -threads 8 -f mpeg files/convert-$conversionFileName
                    """

                    return ffmpegProcess(command, filename, conversionFileName, fileByteArray)
                }

                return FileResponseObject("none", null)
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
                if (fileName.substringAfterLast(".") != "gif") {
                    val conversionFileName = "${fileName.substringBeforeLast(".").replace(" ", "")}:${uuid!!}.${this.fileType}"
                    val filename = "${fileName.substringBeforeLast(".").replace(" ", "")}.${fileName.substringAfterLast(".")}"

                    val restorationFile = File("files/$filename").also { it.createNewFile(); it.writeBytes(fileByteArray) }
                    // ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libx264 -preset slow -crf 14 -c:a libmp3lame -b:a 320k -vf scale=1920:1080 -threads 4 -strict experimental -f avi files/convert-$conversionFileName
                    val command = """
                        ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:v libx264 -preset veryfast -crf 18 -c:a libmp3lame -b:a 320k -vf scale=1920:1080 -pix_fmt yuv420p -threads 8 -f avi files/convert-$conversionFileName
                    """

                    return ffmpegProcess(command, filename, conversionFileName, fileByteArray)
                }

                return FileResponseObject("none", null)
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
                val conversionFileName = "${fileName.substringBeforeLast(".").replace(" ", "")}:${uuid!!}.${this.fileType}"
                val filename = "${fileName.substringBeforeLast(".").replace(" ", "")}.${fileName.substringAfterLast(".")}"

                val restorationFile = File("files/$filename").also { it.createNewFile(); it.writeBytes(fileByteArray) }
                // ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -vf fps=15,scale=1920:1080:flags=lanczos -c:v gif -an -threads 4 files/convert-$conversionFileName
                val command = """
                    ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -vf fps=15,scale=1920:1080 -pix_fmt rgb24 -threads 8 -f gif files/convert-$conversionFileName
                """

                return ffmpegProcess(command, filename, conversionFileName, fileByteArray)
            }
        }
    }

    // 2024년 12월 22일 04시 02분에 처음 알았는데 원본 데이터만 변환 가능하다. 왜냐하면 원본 파일이 아니면 일부 데이터가 손상되었을 위험이 있기 때문에 ffmpeg가 에러를 이르키기 때문이다. (자세한 건 아닌데 내 방식으로 정리)
    override fun ffmpegProcess(command: String, fileName: String, conversionFileName: String, fileByteArray: ByteArray): FileResponseObject {
        val process = ProcessBuilder(command.trim().split(" ")).also { it.redirectErrorStream(true) }.start()

        // 실시간으로 변환 출력 조회함.
        val thread = Thread {
            BufferedReader(InputStreamReader(process.inputStream)).useLines { reader ->
                var clientID: String? = null
                var predictedFileSize: String? = null
                reader.forEach {
                    println(it)

                    if (it.contains("Duration")) {
                        val videoTime = it.substringAfter("Duration: ").substringBefore(",")
                        val bitrate = it.substringAfter("bitrate: ").substringBeforeLast(" kb/s")
                        println("time: $videoTime, bitrate: $bitrate")

                        val hour = videoTime.substringBefore(":")
                        val minute = videoTime.substringAfter(":").substringBeforeLast(":")
                        val second = videoTime.substringAfterLast(":")
                        var totalSeconds = hour.toInt() + (minute.toInt() * 60) + second.toFloat()

                        if (hour != "00") totalSeconds = (hour.toInt() * 60 * 60) + (minute.toInt() * 60) + second.toFloat()
                        println("시간: $hour")
                        println("분: $minute")
                        println("초: $second")

                        val perSecond = ((bitrate.toInt() * 1000) * totalSeconds) / 8
                        var finalFileSize: String = (perSecond / 1024).toString()
                        var reallyFinalFileSize: Float = finalFileSize.substring(0, finalFileSize.lastIndexOf(".") + 3).toFloat()
                        predictedFileSize = "약 ${reallyFinalFileSize}KB"
                        println(predictedFileSize)

                        if (reallyFinalFileSize / 1024 >= 1) {
                            finalFileSize = (perSecond / (1024 * 1024)).toString()
                            reallyFinalFileSize = finalFileSize.substring(0, finalFileSize.lastIndexOf(".") + 3).toFloat()
                            predictedFileSize = "약 ${reallyFinalFileSize}MB"
                            println(predictedFileSize)

                            if (reallyFinalFileSize / 1024 >= 1) {
                                finalFileSize = (perSecond / (1024 * 1024 * 1024)).toString()
                                reallyFinalFileSize = finalFileSize.substring(0, finalFileSize.lastIndexOf(".") + 3).toFloat()
                                predictedFileSize = "약 ${reallyFinalFileSize}GB"
                                println(predictedFileSize)
                            }
                        }

                    }

                    if (it.contains("Output #0")) clientID = it.substringAfter(":").substringBefore(".")

                    // 실시간 변환 중인 파일의 크기 구하기
                    if (it.contains("size=")) {
                        val convertSizeKB = it.substringAfterLast("size=").replace(" ", "").substringBeforeLast("KiB").toFloat() / 1.024f
                        val convertSizeMB = convertSizeKB / 1024.0f
                        val convertSizeGB = convertSizeMB / 1024.0f

                        val content = if (convertSizeKB >= 1024) { // MB일 경우
                            if (convertSizeMB >= 1024) { // GB일 경우
                                "${convertSizeGB.toString().let {
                                    if ((it.lastIndexOf(".")).toString().length == 2) it.substring(0, it.lastIndexOf(".") + 2)
                                    else it.substring(0, it.lastIndexOf(".") + 3)
                                }}GB / $predictedFileSize"
                            } else {
                                "${convertSizeMB.toString().let {
                                    if ((it.lastIndexOf(".")).toString().length == 2) it.substring(0, it.lastIndexOf(".") + 2)
                                    else it.substring(0, it.lastIndexOf(".") + 3)
                                }}MB / $predictedFileSize"
                            }
                        } else { // MB와 GB가 아닐 경우
                            "${convertSizeKB.toString().let {
                                if ((it.lastIndexOf(".")).toString().length <= 2) it.substring(0, it.lastIndexOf(".") + 2)
                                else it.substring(0, it.lastIndexOf(".") + 3)
                            }}KB / $predictedFileSize"
                        }

                        println(uuidList)
                        for (id in uuidList) {
                            if (clientID == id) stompTemplate.convertAndSend("/sub/$clientID", content)
                        }
                    }
                }
            }
        }

        thread.start()
        thread.join()
        process.outputStream.close()

//        val processInputStreamBytes = process.inputStream.readBytes()
//        val outputLogs = File.createTempFile("ffmpegLogs", ".txt").also {
//            it.deleteOnExit()
//            it.writeBytes(processInputStreamBytes)
//        }
//
//        ByteArrayInputStream(processInputStreamBytes).bufferedReader().useLines { input ->
//            input.forEach {
//                val line = "$it Sent ID: $uuid"
//
//                // 파일 변환 크기 구하기
//                var lastConvertSize: String? = null
//                BufferedReader(InputStreamReader(ByteArrayInputStream(outputLogs.readBytes()))).use { lines ->
//                    lines.readLines().forEach { line ->
//                        if (line.contains("Lsize=")) {
//                            val lastConvertSizeKB = line.substringAfterLast("Lsize=").replace(" ", "").substringBeforeLast("KiB").toFloat() / 1.024f
//                            val lastConvertSizeMB = lastConvertSizeKB / 1024.0f
//                            val lastConvertSizeGB = lastConvertSizeMB / 1024.0f
//
//                            lastConvertSize = if (lastConvertSizeKB >= 1024) { // MB일 경우
//                                if (lastConvertSizeMB >= 1024) "${lastConvertSizeGB.toString().let { it.substring(0, it.lastIndexOf(".") + 3) }}GB" // GB일 경우
//                                else "${lastConvertSizeMB.toString().let { it.substring(0, it.lastIndexOf(".") + 3) }}MB"
//                            } else { // KB일 경우
//                                "${lastConvertSizeKB.toString().let { it.substring(0, it.lastIndexOf(".") + 3) }}KB"
//                            }
//                        }
//                    }
//
//                    // 실시간 변환 중인 파일의 크기 구하기
//                    if (it.contains("size=")) {
//                        // 각각의 크기
//                        val convertSizeKB = it.substringAfterLast("size=").replace(" ", "").substringBeforeLast("KiB").toFloat() / 1.024f
//                        val convertSizeMB = convertSizeKB / 1024.0f
//                        val convertSizeGB = convertSizeMB / 1024.0f
//
//                        val content = if (convertSizeKB >= 1024) { // MB일 경우
//                            if (convertSizeMB >= 1024) { // GB일 경우
//                                "${convertSizeGB.toString().let {
//                                    if ((it.lastIndexOf(".")).toString().length == 2) it.substring(0, it.lastIndexOf(".") + 2)
//                                    else it.substring(0, it.lastIndexOf(".") + 3)
//                                }}GB / $lastConvertSize"
//                            } else {
//                                "${convertSizeMB.toString().let {
//                                    if ((it.lastIndexOf(".")).toString().length == 2) it.substring(0, it.lastIndexOf(".") + 2)
//                                    else it.substring(0, it.lastIndexOf(".") + 3)
//                                }}MB / $lastConvertSize"
//                            }
//                        } else { // MB와 GB가 아닐 경우
//                            "${convertSizeKB.toString().let {
//                                if ((it.lastIndexOf(".")).toString().length <= 2) it.substring(0, it.lastIndexOf(".") + 2)
//                                else it.substring(0, it.lastIndexOf(".") + 3)
//                            }}KB / $lastConvertSize"
//                        }
//
//                        println(uuidList)
//                        for (id in uuidList) {
//                            if (File("files/convert-$conversionFileName").name.substringAfterLast(":").substringBeforeLast(".") == id) {
//                                stompTemplate.convertAndSend("/sub/${File("files/convert-$conversionFileName").name.substringAfterLast(":").substringBeforeLast(".")}", content)
//                                Thread.sleep(100)
//                            }
//                        }
//
//                        // 1. 클라이언트에서 고유한 아이디를 보낸다.
//                        // 2. 서버는 받은 아이디를.. 어떻게 어떻게하면 되겠지..?
//                    }
//                }
//            }
//        }

        val conversionFile = File("files/convert-$conversionFileName")
        val fileBytes = conversionFile.readBytes()

        return if (process.waitFor() != 0) {
            File("files/$fileName").delete()
            conversionFile.delete()
            log.error("\u001B[31mffmpeg 프로세스를 실행하던 도중 문제가 발생했습니다.\u001B[0m")
            FileResponseObject("none", null)
        } else {
            File("files/$fileName").delete()
            if (conversionFileName.substringAfterLast(".") != "gif") conversionFile.delete()

            log.info("\u001B[34mffmpeg 프로세스가 정상적으로 처리되어 {} 파일이 {} 파일로 변환되었습니다.\u001B[0m", fileName, conversionFileName)
            FileResponseObject(conversionFileName, fileBytes)
        }
    }
}
