package sk.eastcode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class FileHandler {
    public final Logger logger = LoggerFactory.getLogger(getClass());

    @RabbitListener(queues = "file")
    public void handleFile(byte[] fileBytes) {
        logger.info("Handling file [{} bytes]", fileBytes.length);
    }

}
