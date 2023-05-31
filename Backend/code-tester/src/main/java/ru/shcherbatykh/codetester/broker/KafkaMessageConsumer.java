package ru.shcherbatykh.codetester.broker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.shcherbatykh.common.constants.Constants;
import ru.shcherbatykh.common.model.CodeCheckRequest;
import ru.shcherbatykh.common.model.CodeCheckResponse;
import ru.shcherbatykh.common.model.TestDefinitionRequest;
import ru.shcherbatykh.common.model.TestDefinitionResponse;
import ru.shcherbatykh.codetester.services.CodeCheckService;
import ru.shcherbatykh.codetester.services.TestDefinitionService;

@Slf4j
@Service
public class KafkaMessageConsumer {
    private final CodeCheckService codeCheckService;
    private final TestDefinitionService testDefinitionService;

    @Autowired
    public KafkaMessageConsumer(CodeCheckService codeCheckService,
                                TestDefinitionService testDefinitionService) {
        this.codeCheckService = codeCheckService;
        this.testDefinitionService = testDefinitionService;
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

    @KafkaListener(topics = Constants.TOPIC_TEST_DEFINITION, concurrency = "1",
            containerFactory = "testDefinitionKafkaListenerContainerFactory")
    public void consumeTestCodeMessage(@Payload TestDefinitionRequest message,
                                       @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                       @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                                       @Header(KafkaHeaders.OFFSET) int offset,
                                       @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp,
                                       Acknowledgment acknowledgment) {
        log.info("Consumed test definition message: {} - timestamp = {}, topic = {}, partition = {}, offset = {}\n{}",
                Thread.currentThread().getName(), timestamp, topic, partition, offset, message);

        testDefinitionService.defineNewTest(message);
        acknowledgment.acknowledge();
    }
}
