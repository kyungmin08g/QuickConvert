package io.github.quickconvert.controller

import io.github.quickconvert.types.VideoTypes
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import java.util.UUID

@Controller
class ViewController {

    @GetMapping("/")
    fun index(model: Model): String {
        val uuid = UUID.randomUUID().toString()
        model.addAttribute("id", uuid)
        VideoTypes.uuidList.add(uuid)
        VideoTypes.uuid = uuid
        return "home"
    }

}