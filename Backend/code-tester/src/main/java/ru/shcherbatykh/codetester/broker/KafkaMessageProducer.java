package ru.shcherbatykh.codetester.broker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import ru.shcherbatykh.common.constants.Constants;
import ru.shcherbatykh.common.model.CodeCheckRequest;
import ru.shcherbatykh.common.model.CodeCheckResponse;
import ru.shcherbatykh.common.model.TestDefinitionRequest;
import ru.shcherbatykh.common.model.TestDefinitionResponse;

@Slf4j
@Service
public class KafkaMessageProducer {
    private final KafkaTemplate<String, CodeCheckResponse> codeCheckResultKafkaTemplate;
    private final KafkaTemplate<String, TestDefinitionResponse> testDefinitionResultKafkaTemplate;

    public KafkaMessageProducer(KafkaTemplate<String, CodeCheckResponse> codeCheckResultKafkaTemplate,
                                KafkaTemplate<String, TestDefinitionResponse> testDefinitionResultKafkaTemplate) {
        this.codeCheckResultKafkaTemplate = codeCheckResultKafkaTemplate;
        this.testDefinitionResultKafkaTemplate = testDefinitionResultKafkaTemplate;
    }

    public ListenableFuture<SendResult<String, CodeCheckResponse>> sendCodeCheckResultMessage(CodeCheckResponse codeCheckResponse) {
        ListenableFuture<SendResult<String, CodeCheckResponse>> future = codeCheckResultKafkaTemplate
                .send(Constants.TOPIC_SEND_RESULTS, codeCheckResponse);
        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(SendResult<String, CodeCheckResponse> result) {
                log.info("Sent code check result message = {} with offset = {}", codeCheckResponse, result.getRecordMetadata().offset());
            }

            @Override
            public void onFailure(Throwable throwable) {
                log.error("Unable to send code check message = {} due to: {}", codeCheckResponse, throwable.getMessage());
            }
        });
        return future;
    }

    public ListenableFuture<SendResult<String, TestDefinitionResponse>> sendTestDefinitionResultMessage(TestDefinitionResponse testDefinitionResponse) {
        ListenableFuture<SendResult<String, TestDefinitionResponse>> future = testDefinitionResultKafkaTemplate
                .send(Constants.TOPIC_SEND_TEST_DEFINITION_RESULT, testDefinitionResponse);
        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(SendResult<String, TestDefinitionResponse> result) {
                log.info("Sent test definition result message = {} with offset = {}", testDefinitionResponse, result.getRecordMetadata().offset());
            }

            @Override
            public void onFailure(Throwable throwable) {
                log.error("Unable to send test definition message = {} due to: {}", testDefinitionResponse, throwable.getMessage());
            }
        });
        return future;
    }
}
