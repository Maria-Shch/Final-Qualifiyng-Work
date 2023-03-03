package ru.shcherbatykh.Backend.services;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.shcherbatykh.Backend.classes.TestError;
import ru.shcherbatykh.Backend.dto.TestingResultResponse;
import ru.shcherbatykh.Backend.models.StudentTask;
import ru.shcherbatykh.Backend.models.Task;
import ru.shcherbatykh.Backend.models.User;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class TestingService {

    @Value("${app.codeStorage.path}")
    private String CODE_STORAGE_PATH;

    private final TaskService taskService;
    private final StudentTasksService studentTasksService;

    public TestingService(TaskService taskService, StudentTasksService studentTasksService) {
        this.taskService = taskService;
        this.studentTasksService = studentTasksService;
    }

    public TestingResultResponse testing(int serialNumberOfChapter, int serialNumberOfBlock, int serialNumberOfTask, User user, List<String> codes){
        Task task = taskService.getTask(serialNumberOfChapter, serialNumberOfBlock, serialNumberOfTask);
        StudentTask stTask = studentTasksService.getStudentTask(user, task);
        if (saveCodeToFiles(stTask, codes) == false){
            return new TestingResultResponse(false, TestError.TEST_ERR_001);
        };
        return new TestingResultResponse(true);
    }

    private boolean saveCodeToFiles(StudentTask stTask, List<String> codes)  {
        String path = getPathToSave(stTask);
        if (Files.exists(Path.of(path))) {
            try {
                FileUtils.cleanDirectory(new File(path));
            } catch (IOException e) {
                log.error("Error during saving code", e);
                return false;
            }
        }
        else {
            try {
                Files.createDirectories(Paths.get(path));
            } catch (IOException e) {
                log.error("Error during saving code", e);
                return false;
            }
        }
        for(String code: codes){
            String className = getClassName(code);
            if(className == null) return false;
            String fullPath = path + "//" + className + ".txt";
            try {
                File file = new File(fullPath);
                file.createNewFile();
                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(code);
                bw.close();
            } catch (IOException e) {
                log.error("Error during saving code", e);
                return false;
            }
        }
        return true;
    }

    private String getClassName(String code){
        int index = code.indexOf(" class ") ;
        if (index == -1) return null;
        int startClassNameIndex = index + 7;
        while(code.charAt(startClassNameIndex) == ' '){
            startClassNameIndex++;
        }
        StringBuilder className = new StringBuilder();
        while (startClassNameIndex < code.length() &&
                code.charAt(startClassNameIndex) != ' ' &&
                code.charAt(startClassNameIndex) != '{') {
            className.append(code.charAt(startClassNameIndex));
            startClassNameIndex++;
        }
        return className.toString();
    }

    private String getPathToSave(StudentTask stTask){
        StringBuilder path = new StringBuilder(CODE_STORAGE_PATH);
        path.append('/')
            .append(stTask.getUser().getId()).append('/')
            .append(stTask.getTask().getBlock().getChapter().getSerialNumber()).append('/')
            .append(stTask.getTask().getBlock().getSerialNumber()).append('/')
            .append(stTask.getTask().getSerialNumber());
        return path.toString();
    }


    public List<String> getClasses(int serialNumberOfChapter, int serialNumberOfBlock, int serialNumberOfTask, User user){
        Task task = taskService.getTask(serialNumberOfChapter, serialNumberOfBlock, serialNumberOfTask);
        StudentTask stTask = studentTasksService.getStudentTask(user, task);
        if(stTask == null) return null;
        String path = getPathToSave(stTask);
        if (Files.exists(Path.of(path))) {
            try (Stream<Path> paths = Files.walk(Paths.get(path))) {
                return paths
                    .filter(Files::isRegularFile)
                    .map(p -> {
                        try {
                            return Files.readString(p, StandardCharsets.UTF_8);
                        } catch (IOException e) {
                            log.error("Error during reading files", e);
                            return null;
                        }
                    })
                    .toList();
            } catch (IOException ex){
                log.error("Error during reading files", ex);
                return null;
            }
        }
        else {
            return null;
        }
    }
}
