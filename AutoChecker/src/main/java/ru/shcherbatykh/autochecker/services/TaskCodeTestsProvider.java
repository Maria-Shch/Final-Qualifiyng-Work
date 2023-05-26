package ru.shcherbatykh.autochecker.services;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import ru.shcherbatykh.autochecker.tests.CodeTest;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskCodeTestsProvider implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    public List<CodeTest> getTestChain(String taskId) {
        CodeTest checkCodeStyle = applicationContext.getBean("checkCodeStyle", CodeTest.class);
        if (taskId.equals("1.1.1")) {
            return List.of(applicationContext.getBean("task_" + taskId, CodeTest.class),
                    checkCodeStyle);
        }
        return List.of(applicationContext.getBean("task_" + taskId, CodeTest.class),
                applicationContext.getBean("runCode", CodeTest.class),
                checkCodeStyle);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
