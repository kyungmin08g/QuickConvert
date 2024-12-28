package io.github.quickconvert.types

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.opencsv.CSVWriter
import io.github.quickconvert.dto.FileResponseObject
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileWriter
import java.io.IOException

@Slf4j
object DataFileTypes {
    private val log = LoggerFactory.getLogger(this::class.java)
    private val objectMapper = ObjectMapper()
    private val xmlMapper = XmlMapper()

    enum class Conversion(val fileType: String) : io.github.quickconvert.service.Conversion {
        JSON("json") {
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val file = File("files/$fileName").apply {
                    this.createNewFile()
                    this.writeBytes(fileByteArray)
                }
                Thread.sleep(2000) // 생성될 때까지 기다림. 2초 정도는 되겠지?

                val fileByte = file.readBytes()
                if (fileName.substringAfterLast(".") == "json") {
                    file.delete()
                    return FileResponseObject(fileName, fileByte)
                }

                val fileContent = file.inputStream().bufferedReader().readText()
                val xmlObject = xmlMapper.readValue(fileContent, Object::class.java)
                val jsonObject = objectMapper.writeValueAsString(xmlObject)

                val conversionFileName = "${fileName.substringBeforeLast(".")}.${this.fileType}"
                val conversionFile = File("files/$conversionFileName").apply {
                    this.createNewFile()
                    this.writeBytes(jsonObject.toByteArray())
                }
                val fileBytes = conversionFile.readBytes()

                file.delete()
                conversionFile.delete()

                log.info("\u001B[34m{} 파일이 {} 파일로 변환되었습니다.\u001B[37m", fileName, conversionFileName)
                return FileResponseObject(conversionFileName, fileBytes)
            }
        },
        XML("xml") {
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val file = File("files/$fileName").apply {
                    this.createNewFile()
                    this.writeBytes(fileByteArray)
                }
                Thread.sleep(2000)

                val fileByte = file.readBytes()
                if (fileName.substringAfterLast(".") == "xml") {
                    file.delete()
                    return FileResponseObject(fileName, fileByte)
                }

                val fileContent = file.inputStream().bufferedReader().readText()
                val jsonObject = objectMapper.readValue(fileContent, Object::class.java)
                val xmlObject = xmlMapper.writeValueAsString(jsonObject)

                val conversionFileName = "${fileName.substringBeforeLast(".")}.${this.fileType}"
                val conversionFile = File("files/$conversionFileName").apply {
                    this.createNewFile()
                    this.writeBytes(xmlObject.toByteArray())
                }
                val fileBytes = conversionFile.readBytes()

                file.delete()
                conversionFile.delete()

                log.info("\u001B[34m{} 파일이 {} 파일로 변환되었습니다.\u001B[37m", fileName, conversionFileName)
                return FileResponseObject(conversionFileName, fileBytes)
            }
        },
        CSV("csv") {
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val file = File("files/$fileName").apply {
                    this.createNewFile()
                    this.writeBytes(fileByteArray)
                }
                val fileContent = file.inputStream().bufferedReader().readText()
                val conversionFileName = "${fileName.substringBeforeLast(".")}.${this.fileType}"

                try {
                    if (fileName.substringAfterLast(".") == "xml") {
                        file.delete()
                        return FileResponseObject("none", null)
                    }

                    val data: Any = try { objectMapper.readValue(fileContent, object : TypeReference<List<Map<String, Any>>>() {}) } catch (e: Exception) {
                        objectMapper.readValue(fileContent, Map::class.java)
                    }

                    var header: Array<String>?
                    CSVWriter(FileWriter("files/$conversionFileName")).use { writer ->
                        if (data is List<*>) {
                            val listData = data as List<Map<String, Any>>
                            if (listData.isNotEmpty()) {
                                header = listData[0].keys.toTypedArray()
                                writer.writeNext(header)

                                for (row in listData) {
                                    val values = row.values.map { it.toString() }.toTypedArray()
                                    writer.writeNext(values)
                                }
                            }
                        } else if (data is Map<*, *>) {
                            header = arrayOf(data.keys.toTypedArray().toString())
                            writer.writeNext(header)
                            val values = data.values.map { it?.toString() ?: "" }.toTypedArray()
                            writer.writeNext(values)
                        }
                    }
                } catch (e: IOException) { e.printStackTrace() }

                val conversionFile = File("files/$conversionFileName")
                val csvByteArray = conversionFile.readBytes()

                file.delete()
                conversionFile.delete()

                log.info("\u001B[34m{} 파일이 {} 파일로 변환되었습니다.\u001B[37m", fileName, conversionFileName)
                return FileResponseObject(conversionFileName, csvByteArray)
            }
        }
    }
}