package ru.shcherbatykh.application.broker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import ru.shcherbatykh.common.constants.Constants;
import ru.shcherbatykh.common.model.CodeCheckRequest;
import ru.shcherbatykh.common.model.TestDefinitionRequest;

@Slf4j
@Service
public class KafkaMessageProducer {
    private final KafkaTemplate<String, CodeCheckRequest> codeCheckKafkaTemplate;
    private final KafkaTemplate<String, TestDefinitionRequest> testDefinitionKafkaTemplate;

    public KafkaMessageProducer(
            KafkaTemplate<String, CodeCheckRequest> codeCheckKafkaTemplate, KafkaTemplate<String,
            TestDefinitionRequest> testDefinitionKafkaTemplate) {
        this.codeCheckKafkaTemplate = codeCheckKafkaTemplate;
        this.testDefinitionKafkaTemplate = testDefinitionKafkaTemplate;
    }

    public ListenableFuture<SendResult<String, CodeCheckRequest>> sendCodeCheckMessage(CodeCheckRequest codeCheckRequest) {
        ListenableFuture<SendResult<String, CodeCheckRequest>> future = codeCheckKafkaTemplate
                .send(Constants.TOPIC_COMPILE_AND_RUN_TESTS, codeCheckRequest);
        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(SendResult<String, CodeCheckRequest> result) {
                log.info("Sent code check message = {} with offset = {}", codeCheckRequest, result.getRecordMetadata().offset());
            }

            @Override
            public void onFailure(Throwable throwable) {
                log.error("Unable to send code check message = {} dut to: {}", codeCheckRequest, throwable.getMessage());
            }
        });
        return future;
    }

    public ListenableFuture<SendResult<String, TestDefinitionRequest>> sendTestDefinitionMessage(TestDefinitionRequest testDefinitionRequest) {
        ListenableFuture<SendResult<String, TestDefinitionRequest>> future = testDefinitionKafkaTemplate
                .send(Constants.TOPIC_TEST_DEFINITION, testDefinitionRequest);
        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(SendResult<String, TestDefinitionRequest> result) {
                log.info("Sent test definition message = {} with offset = {}", testDefinitionRequest, result.getRecordMetadata().offset());
            }

            @Override
            public void onFailure(Throwable throwable) {
                log.error("Unable to send test definition message = {} due to: {}", testDefinitionRequest, throwable.getMessage());
            }
        });
        return future;
    }
}
