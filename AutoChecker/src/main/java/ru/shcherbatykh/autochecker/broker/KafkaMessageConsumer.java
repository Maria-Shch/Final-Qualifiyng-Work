package ru.shcherbatykh.autochecker.broker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.shcherbatykh.autochecker.model.CodeCheckRequest;
import ru.shcherbatykh.autochecker.model.CodeCheckResponse;
import ru.shcherbatykh.autochecker.model.Constants;
import ru.shcherbatykh.autochecker.services.CodeCheckService;

@Slf4j
@Service
public class KafkaMessageConsumer {
    private final CodeCheckService codeCheckService;

    @Autowired
    public KafkaMessageConsumer(CodeCheckService codeCheckService) {
        this.codeCheckService = codeCheckService;
    }

    @KafkaListener(topics = Constants.TOPIC_COMPILE_AND_RUN_TESTS, concurrency = "4",
            containerFactory = "codeCheckKafkaListenerContainerFactory")
    public void consumeCodeCheckMessage(@Payload CodeCheckRequest message,
                                        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                                        @Header(KafkaHeaders.OFFSET) int offset,
                                        @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp,
                                        Acknowledgment acknowledgment) {
        log.info("Consumed code check message: {} - timestamp = {}, topic = {}, partition = {}, offset = {}\n{}",
                Thread.currentThread().getName(), timestamp, topic, partition, offset, message);

        codeCheckService.checkCode(message);
        acknowledgment.acknowledge();
    }

    @KafkaListener(topics = Constants.TOPIC_SEND_RESULTS, concurrency = "4",
            containerFactory = "codeCheckResultKafkaListenerContainerFactory")
    public void consumeCodeCheckResultMessage(@Payload CodeCheckResponse message,
                                              @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                              @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                                              @Header(KafkaHeaders.OFFSET) int offset,
                                              @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp,
                                              Acknowledgment acknowledgment) {
        log.info("Consumed code check result message: {} - timestamp = {}, topic = {}, partition = {}, offset = {}\n{}",
                Thread.currentThread().getName(), timestamp, topic, partition, offset, message);
        acknowledgment.acknowledge();
    }
}
