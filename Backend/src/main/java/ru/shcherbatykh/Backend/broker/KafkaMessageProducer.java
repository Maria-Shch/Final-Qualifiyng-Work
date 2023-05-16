package ru.shcherbatykh.Backend.broker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import ru.shcherbatykh.Backend.classes.CodeCheckRequest;
import ru.shcherbatykh.Backend.classes.Constants;

@Slf4j
@Service
public class KafkaMessageProducer {
    private final KafkaTemplate<String, CodeCheckRequest> codeCheckKafkaTemplate;

    public KafkaMessageProducer(KafkaTemplate<String, CodeCheckRequest> codeCheckKafkaTemplate) {
        this.codeCheckKafkaTemplate = codeCheckKafkaTemplate;
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
}
