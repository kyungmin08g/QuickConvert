package io.github.quickconvert.types

import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Image
import io.github.quickconvert.dto.FileResponseObject
import io.github.quickconvert.service.Conversion
import org.apache.poi.common.usermodel.PictureType
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.springframework.core.io.FileSystemResource
import java.io.*
import java.nio.charset.StandardCharsets
import java.util.*


fun ffmpegProcessStart(command: String, conversionFileName: String, fileByteArray: ByteArray): FileResponseObject {
    val processBuilder = ProcessBuilder(command.trim().split(" "))
    processBuilder.redirectErrorStream(true)
    val process = processBuilder.start()

    val inputStream = ByteArrayInputStream(fileByteArray)
    inputStream.copyTo(process.outputStream)
    process.outputStream.close()

    val fileResource = FileSystemResource(File(conversionFileName))

    return if (process.waitFor() != 0) {
        println("ffmpeg 프로세스를 실행하던 도중 문제가 발생했습니다.")
        FileResponseObject("none", null)
    } else {
        println("ffmpeg 프로세스가 정상적으로 종료되었습니다.")
        FileResponseObject(fileResource.filename, null)
    }
}

enum class ConversionImageTypes(val fileType: String): Conversion {
    JPG("jpg") {
        override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
            val conversionFileName = "${fileName.substringBeforeLast(".").replace("+", "").replace(" ", "")}.${this.fileType}"
            val command = """
               ffmpeg -i pipe:0 $conversionFileName
            """
            return ffmpegProcessStart(command, conversionFileName, fileByteArray)
        }
    },
    PNG("png") {
        override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
            val conversionFileName = "${fileName.substringBeforeLast(".").replace("+", "").replace(" ", "")}.${this.fileType}"
            val command = """
               ffmpeg -i pipe:0 $conversionFileName
            """
            return ffmpegProcessStart(command, conversionFileName, fileByteArray)
        }
    },
    JPEG("jpeg") {
        override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
            val conversionFileName = "${fileName.substringBeforeLast(".").replace("+", "").replace(" ", "")}.${this.fileType}"
            val command = """
               ffmpeg -i pipe:0 $conversionFileName
            """
            return ffmpegProcessStart(command, conversionFileName, fileByteArray)
        }
    },
    GIF("gif") {
        override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
            val conversionFileName = "${fileName.substringBeforeLast(".").replace("+", "").replace(" ", "")}.${this.fileType}"
            val command = """
               ffmpeg -i pipe:0 $conversionFileName 
            """
            return ffmpegProcessStart(command, conversionFileName, fileByteArray)
        }
    },
    BMP("bmp") {
        override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
            val conversionFileName = "${fileName.substringBeforeLast(".").replace("+", "").replace(" ", "")}.${this.fileType}"
            val command = """
               ffmpeg -i pipe:0 $conversionFileName 
            """
            return ffmpegProcessStart(command, conversionFileName, fileByteArray)
        }
    },
    TIFF("tiff") {
        override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
            val conversionFileName = "${fileName.substringBeforeLast(".").replace("+", "").replace(" ", "")}.${this.fileType}"
            val command = """
               ffmpeg -i pipe:0 $conversionFileName 
            """
            return ffmpegProcessStart(command, conversionFileName, fileByteArray)
        }
    },
    WEBP("webp") {
        override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
            val conversionFileName = "${fileName.substringBeforeLast(".").replace("+", "").replace(" ", "")}.${this.fileType}"
            val command = """
               ffmpeg -i pipe:0 $conversionFileName 
            """
            return ffmpegProcessStart(command, conversionFileName, fileByteArray)
        }
    },
    PDF("pdf") {
        override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
            val file = File("./$fileName")
            if (file.createNewFile()) {
                FileOutputStream(file).apply {
                    this.write(fileByteArray)
                    this.close()
                }
            }

            // com.itextpdf:itext7-core:7.2.5 라이브러리 사용
            val writer = PdfWriter("./${fileName.substringBeforeLast(".")}.pdf")
            val pdfDocument = PdfDocument(writer)

            val imageData = ImageDataFactory.create(file.path)
            val image = Image(imageData)

            val pageSize = PageSize(image.imageScaledWidth, image.imageScaledHeight)
            pdfDocument.defaultPageSize = pageSize
            val document = Document(pdfDocument)

            image.scaleToFit(pageSize.width, pageSize.height)
            image.setFixedPosition(0f, 0f)

            document.add(image)
            document.close()
            file.delete()

            val pdfFile = File("${fileName.substringBeforeLast(".")}.pdf")
            val outputStream = ByteArrayOutputStream() // 바이트 배열을 전달하기 위해서 (실제로는 인코딩해서 전달하고 blob으로 만든 다음 다운로드함)
            pdfFile.inputStream().use { input ->
                val buf = ByteArray(2048)
                var bytesRead: Int
                while (input.read(buf).also { bytesRead = it } != -1) {
                    outputStream.write(buf, 0, bytesRead)
                }
            }

            return FileResponseObject(pdfFile.name, Base64.getEncoder().encodeToString(outputStream.toByteArray()))
        }
    },
    DOCX("docx") {
        override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
            val pictureType = when (fileName.substringAfterLast(".")) {
                "png" -> PictureType.PNG
                "jpeg", "jpg" -> PictureType.JPEG
                else -> throw IOException("지원하지 않는 포맷입니다.")
            }
            val imageFile = File("./$fileName").apply { this.writeBytes(fileByteArray) }

            val docxFile = File("./${fileName.substringBeforeLast(".")}.docx").apply {
                FileOutputStream(this).use { fos ->
                    XWPFDocument().apply {
                        this.createParagraph().createRun().addPicture(FileInputStream(imageFile), pictureType, fileName, 600, 800)
                        this.write(fos)
                    }
                }
            }

            val docxFileBytes = docxFile.readBytes()
            val base64Content = Base64.getEncoder().encodeToString(docxFileBytes)

            imageFile.delete()
            docxFile.delete()

            return FileResponseObject(docxFile.name, base64Content)
        }
    },
//    TXT("txt"),
//    SVG("svg"),
//    ICO("ico"),
//    PSD("psd")
}