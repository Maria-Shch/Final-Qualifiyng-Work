package ru.shcherbatykh.codetester.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.shcherbatykh.codetester.tests.CodeTest;
import ru.shcherbatykh.codetester.tests.task.TaskTest;
import ru.shcherbatykh.codetester.visitor.TestFileVisitor;
import ru.shcherbatykh.codetester.class_loader.PathClassLoader;

import javax.tools.JavaFileObject;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class TestClassProvider {
    private final Map<String, Class<CodeTest>> taskIdToCodeTestMap = new ConcurrentHashMap<>();
    private final Map<String, Path> taskIdToJavaPathMap = new ConcurrentHashMap<>();

    @Value("${autochecker.hot_reload.io.test_classes_folder}")
    private String testClassesFolder;

    public void initProvider() {
        log.info("Start of test class provider initialization");

        try {
            safeInit();
        } catch (Throwable t) {
            log.error("Error during test initialization", t);
            System.exit(0);
        }

        log.info("Finish of test class provider initialization");
    }

    @SuppressWarnings("unchecked")
    private void safeInit() throws IOException {
        TestFileVisitor visitor = new TestFileVisitor();
        Files.walkFileTree(Paths.get(testClassesFolder), visitor);
        PathClassLoader pathClassLoader = new PathClassLoader(visitor.getClassFilePaths());
        Map<Path, Class<?>> allClasses = pathClassLoader.findAllClasses();
        for (Map.Entry<Path, Class<?>> entry : allClasses.entrySet()) {
            if (CodeTest.class.isAssignableFrom(entry.getValue())) {
                TaskTest annotation = entry.getValue().getAnnotation(TaskTest.class);
                if (annotation != null) {
                    String taskId = annotation.value();
                    Path javaPath = Paths.get(entry.getKey().toString()
                            .replace(JavaFileObject.Kind.CLASS.extension, JavaFileObject.Kind.SOURCE.extension));
                    defineTestClass(taskId, (Class<CodeTest>) entry.getValue(), javaPath);
                } else {
                    log.warn("Class {} has no annotation TaskTest", entry);
                }
            }
        }
    }

    public CodeTest getTestByTaskId(String taskId) {
        if (taskIdToCodeTestMap.containsKey(taskId)) {
            Class<CodeTest> codeTestClass = taskIdToCodeTestMap.get(taskId);
            try {
                Constructor<CodeTest> declaredConstructor = codeTestClass.getDeclaredConstructor();
                declaredConstructor.setAccessible(true);
                return declaredConstructor.newInstance();
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                     IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new IllegalArgumentException("Not found test for task " + taskId);
        }
    }

    public void defineTestClass(String taskId, Class<CodeTest> clazz, Path javaPath) {
        Class<CodeTest> previousClass = taskIdToCodeTestMap.put(taskId, clazz);
        if (log.isInfoEnabled()) {
            log.info("Defined new class {} for task {}. Previous value: {}", clazz, taskId, previousClass);
        }

        Path previousJavaPath = taskIdToJavaPathMap.put(taskId, javaPath);
        if (log.isInfoEnabled()) {
            log.info("Defined new path {} for task {}. Previous value: {}", javaPath, taskId, previousJavaPath);
        }
    }
}
