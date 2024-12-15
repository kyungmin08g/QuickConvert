package io.github.quickconvert

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class QuickConvertApplication

fun main(args: Array<String>) {
    runApplication<QuickConvertApplication>(*args)
}
