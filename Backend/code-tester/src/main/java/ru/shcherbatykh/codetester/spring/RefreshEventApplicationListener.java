package ru.shcherbatykh.codetester.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import ru.shcherbatykh.codetester.services.TestClassProvider;

@Slf4j
@Component
public class RefreshEventApplicationListener implements ApplicationListener<ContextRefreshedEvent> {
    private final TestClassProvider testClassProvider;

    @Autowired
    public RefreshEventApplicationListener(TestClassProvider testClassProvider) {
        this.testClassProvider = testClassProvider;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("Started handling of event {}", event);
        testClassProvider.initProvider();
        log.info("Completed handling of event {}", event);
    }
}
