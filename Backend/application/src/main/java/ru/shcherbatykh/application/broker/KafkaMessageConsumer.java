package ru.shcherbatykh.application.broker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.shcherbatykh.application.services.TestingService;
import ru.shcherbatykh.common.constants.Constants;
import ru.shcherbatykh.common.model.CodeCheckResponse;
import ru.shcherbatykh.common.model.TestDefinitionResponse;

@Slf4j
@Service
public class KafkaMessageConsumer {
    private final TestingService testingService;

    public KafkaMessageConsumer(TestingService testingService) {
        this.testingService = testingService;
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
        testingService.saveCodeCheckResponse(message);
    }

    @KafkaListener(topics = Constants.TOPIC_SEND_TEST_DEFINITION_RESULT, concurrency = "1",
            containerFactory = "testDefinitionResultKafkaListenerContainerFactory")
    public void consumeTestCodeResultMessage(@Payload TestDefinitionResponse message,
                                             @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                             @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                                             @Header(KafkaHeaders.OFFSET) int offset,
                                             @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp,
                                             Acknowledgment acknowledgment) {
        log.info("Consumed test definition result message: {} - timestamp = {}, topic = {}, partition = {}, offset = {}\n{}",
                Thread.currentThread().getName(), timestamp, topic, partition, offset, message);
        acknowledgment.acknowledge();
    }}
