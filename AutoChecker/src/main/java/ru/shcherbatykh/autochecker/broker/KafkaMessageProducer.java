package ru.shcherbatykh.autochecker.broker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import ru.shcherbatykh.autochecker.model.CodeCheckRequest;
import ru.shcherbatykh.autochecker.model.CodeCheckResponse;
import ru.shcherbatykh.autochecker.model.Constants;

@Slf4j
@Service
public class KafkaMessageProducer {
    private final KafkaTemplate<String, CodeCheckRequest> codeCheckKafkaTemplate;
    private final KafkaTemplate<String, CodeCheckResponse> codeCheckResultKafkaTemplate;

    public KafkaMessageProducer(KafkaTemplate<String, CodeCheckRequest> codeCheckKafkaTemplate,
                                KafkaTemplate<String, CodeCheckResponse> codeCheckResultKafkaTemplate) {
        this.codeCheckKafkaTemplate = codeCheckKafkaTemplate;
        this.codeCheckResultKafkaTemplate = codeCheckResultKafkaTemplate;
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
                log.error("Unable to send code check message = {} dut to: {}", codeCheckResponse, throwable.getMessage());
            }
        });
        return future;
    }
}
