package ru.shcherbatykh.autochecker.services;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import ru.shcherbatykh.autochecker.tests.CodeTest;

import java.util.List;

@Service
public class TaskCodeTestsProvider implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    public List<CodeTest> getTestChain(String taskId) {
        return List.of(applicationContext.getBean("task_" + taskId, CodeTest.class), applicationContext.getBean("runCode", CodeTest.class));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
