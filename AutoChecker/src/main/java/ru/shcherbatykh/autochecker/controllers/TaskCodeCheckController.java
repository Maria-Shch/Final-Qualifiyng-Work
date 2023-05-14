package ru.shcherbatykh.autochecker.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.shcherbatykh.autochecker.model.CodeCheckRequest;
import ru.shcherbatykh.autochecker.model.results.CompileCodeResult;
import ru.shcherbatykh.autochecker.broker.KafkaMessageProducer;
import ru.shcherbatykh.autochecker.model.CodeCheckResponse;
import ru.shcherbatykh.autochecker.model.CodeSource;
import ru.shcherbatykh.autochecker.services.CodeCheckService;
import ru.shcherbatykh.autochecker.services.InputRequestValidationService;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/check", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class TaskCodeCheckController {
    private final InputRequestValidationService inputRequestValidationService;
    private final KafkaMessageProducer kafkaMessageProducer;
    private final CodeCheckService codeCheckService;

    @Autowired
    public TaskCodeCheckController(InputRequestValidationService inputRequestValidationService,
                                   KafkaMessageProducer kafkaMessageProducer,
                                   CodeCheckService codeCheckService) {
        this.inputRequestValidationService = inputRequestValidationService;
        this.kafkaMessageProducer = kafkaMessageProducer;
        this.codeCheckService = codeCheckService;
    }

    @PostMapping
    public CompileCodeResult testCodeSourcesAsync(@RequestParam(name = "student_id") String studentId,
                                                  @RequestParam(name = "task_id") String taskId,
                                                  @RequestParam(name = "request_uuid") String requestUuid,
                                                  @RequestBody List<CodeSource> codeSources) {
        inputRequestValidationService.validateInputRequestParameters(codeSources);
        CodeCheckRequest codeCheckRequest = new CodeCheckRequest(studentId, taskId, requestUuid, codeSources);
        kafkaMessageProducer.sendCodeCheckMessage(codeCheckRequest);
        return CompileCodeResult.OK_RESPONSE;
    }

    @PostMapping("/immediate")
    public CodeCheckResponse testCodeSourcesImmediate(@RequestParam(name = "student_id") String studentId,
                                                      @RequestParam(name = "task_id") String taskId,
                                                      @RequestParam(name = "request_uuid") String requestUuid,
                                                      @RequestBody List<CodeSource> codeSources) {
        inputRequestValidationService.validateInputRequestParameters(codeSources);
        CodeCheckRequest codeCheckRequest = new CodeCheckRequest(studentId, taskId, requestUuid, codeSources);
        return codeCheckService.checkCodeImmediately(codeCheckRequest);
    }
}
