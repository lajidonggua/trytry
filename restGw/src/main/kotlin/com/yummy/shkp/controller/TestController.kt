package com.yummy.shkp.controller
import com.yummy.shkp.base.components.MessagePublisher
import com.yummy.shkp.base.const.MessageTopic
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/test")
class Test2Controller(
    private val messagePublisher: MessagePublisher
) {

    @GetMapping("/testSleep")
    suspend fun testSleep(@RequestParam sender: Int): String {
        return messagePublisher.publish(MessageTopic.DEMO_SVC,sender)
    }
}


@RestController
@RequestMapping("/upload")
class FileUploadController {
    @PostMapping("/file")
    fun handleFileUpload(@ModelAttribute req: UploadImageReq): ResponseEntity<String> {
        // 处理文件上传逻辑
        // 这里可以根据你的需求进行文件处理，比如保存到本地或者存储到数据库
        println(req.file.filename())
        return ResponseEntity.ok("文件上传成功！")
    }
}

data class UploadImageReq(
    val file: FilePart
)

