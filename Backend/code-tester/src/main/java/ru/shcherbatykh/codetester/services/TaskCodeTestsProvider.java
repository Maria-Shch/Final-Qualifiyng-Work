package ru.shcherbatykh.codetester.services;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import ru.shcherbatykh.codetester.tests.CodeTest;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskCodeTestsProvider implements ApplicationContextAware {
    private final TestClassProvider testClassProvider;

    private ApplicationContext applicationContext;

    @Autowired
    public TaskCodeTestsProvider(TestClassProvider testClassProvider) {
        this.testClassProvider = testClassProvider;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public List<CodeTest> getTestChain(String taskId) {
        List<CodeTest> tests = new ArrayList<>(3);

        CodeTest mainTest = testClassProvider.getTestByTaskId(taskId);
        tests.add(mainTest);

        CodeTest codeStyleTest = applicationContext.getBean("checkCodeStyle", CodeTest.class);
        tests.add(codeStyleTest);

        if (mainTest.isRunMainClassRequired()) {
            CodeTest runCodeTest = applicationContext.getBean("runCode", CodeTest.class);
            tests.add(runCodeTest);
        }

        return tests;
    }
}
