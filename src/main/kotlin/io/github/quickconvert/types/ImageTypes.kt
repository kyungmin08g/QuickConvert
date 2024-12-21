package io.github.quickconvert.types

import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Image
import io.github.quickconvert.dto.FileResponseObject
import io.github.quickconvert.service.FFmpegProcess
import org.apache.poi.common.usermodel.PictureType
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.slf4j.LoggerFactory
import org.springframework.core.io.FileSystemResource
import java.io.*
import java.util.*
import javax.imageio.ImageIO

object ImageTypes : FFmpegProcess {
    private val log = LoggerFactory.getLogger(this::class.java)

    enum class Conversion(val fileType: String): io.github.quickconvert.service.Conversion {
        JPG("jpg") {
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".").replace("+", "").replace(" ", "")}.${this.fileType}"
                val command = """
                   ffmpeg -i pipe:0 $conversionFileName
                """
                return ffmpegProcess(command, fileName, conversionFileName, fileByteArray)
            }
        },
        PNG("png") {
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".").replace("+", "").replace(" ", "")}.${this.fileType}"
                val command = """
                    ffmpeg -i pipe:0 $conversionFileName
                """
                return ffmpegProcess(command, fileName, conversionFileName, fileByteArray)
            }
        },
        JPEG("jpeg") {
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".").replace("+", "").replace(" ", "")}.${this.fileType}"
                val command = """
                   ffmpeg -i pipe:0 $conversionFileName
                """
                return ffmpegProcess(command, fileName, conversionFileName, fileByteArray)
            }
        },
        GIF("gif") {
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".").replace("+", "").replace(" ", "")}.${this.fileType}"
                val command = """
                   ffmpeg -i pipe:0 $conversionFileName 
                """
                return ffmpegProcess(command, fileName, conversionFileName, fileByteArray)
            }
        },
        BMP("bmp") {
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".").replace("+", "").replace(" ", "")}.${this.fileType}"
                val command = """
                   ffmpeg -i pipe:0 $conversionFileName 
                """
                return ffmpegProcess(command, fileName, conversionFileName, fileByteArray)
            }
        },
        TIFF("tiff") {
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".").replace("+", "").replace(" ", "")}.${this.fileType}"
                val command = """
                   ffmpeg -i pipe:0 $conversionFileName 
                """
                return ffmpegProcess(command, fileName, conversionFileName, fileByteArray)
            }
        },
        WEBP("webp") {
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".").replace("+", "").replace(" ", "")}.${this.fileType}"
                val command = """
                   ffmpeg -i pipe:0 $conversionFileName 
                """
                return ffmpegProcess(command, fileName, conversionFileName, fileByteArray)
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
                val outputStream = ByteArrayOutputStream()
                pdfFile.inputStream().use { input ->
                    val buf = ByteArray(2048)
                    var bytesRead: Int
                    while (input.read(buf).also { bytesRead = it } != -1) {
                        outputStream.write(buf, 0, bytesRead)
                    }
                }
                pdfFile.delete()

                return FileResponseObject(pdfFile.name, Base64.getEncoder().encodeToString(outputStream.toByteArray()))
            }
        },
        DOCX("docx") {
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val pictureType = when (fileName.substringAfterLast(".")) {
                    "png" -> PictureType.PNG
                    "jpeg", "jpg" -> PictureType.JPEG
                    else -> return FileResponseObject("none", null)
                }

                val imageFile = File("./$fileName").apply { this.writeBytes(fileByteArray) }
                val docxFile = File("./${fileName.substringBeforeLast(".")}.docx").apply {
                    FileOutputStream(this).use {
                        XWPFDocument().apply {
                            this.createParagraph().createRun().addPicture(
                                FileInputStream(imageFile), pictureType, fileName, 600, 800
                            )
                            this.write(it)
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
        TXT("txt") {
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val imgFile = File("./$fileName").apply {
                    this.createNewFile()
                    FileOutputStream(this).apply { this.write(fileByteArray) }
                }

                val bufferedImage = ImageIO.read(imgFile)
                val width = bufferedImage.width
                val height = bufferedImage.height

                val txtFile = File("${fileName.substringBeforeLast(".")}.txt").apply {
                    this.createNewFile()
                    FileWriter(this.path).use { writer ->
                        for (y in 0..< height) {
                            for (x in 0..< width) {
                                val pixel = bufferedImage.getRGB(x, y)
                                writer.write(pixel)
                            }
                            writer.write("\n")
                        }
                    }
                }

                val txtFileOutputStream = ByteArrayOutputStream()
                txtFile.inputStream().use { input ->
                    val buf = ByteArray(2048)
                    var bytesRead: Int
                    while (input.read(buf).also { bytesRead = it } != -1) {
                        txtFileOutputStream.write(buf, 0, bytesRead)
                    }
                }
                imgFile.delete()
                txtFile.delete()

                return FileResponseObject(txtFile.name, Base64.getEncoder().encodeToString(txtFileOutputStream.toByteArray()))
            }
        },
        SVG("svg") {
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val file = File(fileName)
                val fileResource = FileSystemResource(file.apply {
                    this.createNewFile()
                    FileOutputStream(this).apply { this.write(fileByteArray) }
                })
                val svgFileName = "${fileName.substringBeforeLast(".")}.${this.fileType}"

                val processBuilder = ProcessBuilder(
                    "inkscape",
                    fileResource.file.absolutePath,
                    "--export-type=svg",
                    "--export-filename=$svgFileName"
                )
                processBuilder.redirectErrorStream(true)
                val process = processBuilder.start()

                if (process.waitFor() != 0) {
                    println("오류 발생")
                    file.delete()
                    return FileResponseObject("none", null)
                }

                val svgFile = File(svgFileName)
                if (!svgFile.exists()) {
                    println("변환된 SVG 파일을 찾을 수 없습니다.")
                    file.delete()
                    return FileResponseObject("none", null)
                }

                val svgFileBytes = svgFile.readBytes()
                file.delete()
                svgFile.delete()

                return FileResponseObject(svgFile.name, Base64.getEncoder().encodeToString(svgFileBytes))
            }
        },
        ICO("ico") {
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".").replace("+", "").replace(" ", "")}.${this.fileType}"
                val command = """
                   ffmpeg -i pipe:0 -vf scale=256:256 $conversionFileName
                """
                return ffmpegProcess(command, fileName, conversionFileName, fileByteArray)
            }
        },
        PSD("psd") {
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val inputFile = File(fileName).apply {
                    this.createNewFile()
                    FileOutputStream(this).apply { this.write(fileByteArray) }
                }
                val outputFileName = "${fileName.substringBeforeLast(".")}.psd"

                val processBuilder = ProcessBuilder("convert", inputFile.absolutePath, outputFileName)
                processBuilder.redirectErrorStream(true)
                val process = processBuilder.start()

                return if (process.waitFor() != 0) {
                    println("ImageMagick 프로세스를 실행하던 도중 문제가 발생했습니다.")
                    inputFile.delete()
                    FileResponseObject("none", null)
                } else {
                    println("ImageMagick 프로세스가 정상적으로 종료되었습니다.")
                    val psdFile = File(outputFileName)
                    val psdBytes = psdFile.readBytes()
                    inputFile.delete()
                    psdFile.delete()

                    FileResponseObject(outputFileName, Base64.getEncoder().encodeToString(psdBytes))
                }
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

        val fileResource = FileSystemResource(File(conversionFileName))

        return if (process.waitFor() != 0) {
            log.error("\u001B[31mffmpeg 프로세스를 실행하던 도중 문제가 발생했습니다.\u001B[37m")
            FileResponseObject("none", null)
        } else {
            log.info("\u001B[34mffmpeg 프로세스가 정상적으로 처리되어 {} 파일이 {} 파일로 변환되었습니다.\u001B[37m", fileName, conversionFileName)
            FileResponseObject(fileResource.filename, null)
        }
    }
}