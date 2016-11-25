package sk.eastcode.microservice;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class RestApi {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostMapping("/files")
    public void handleFile(MultipartFile file) throws IOException {
        rabbitTemplate.convertAndSend("file-uploaded", file.getBytes());
    }
}
