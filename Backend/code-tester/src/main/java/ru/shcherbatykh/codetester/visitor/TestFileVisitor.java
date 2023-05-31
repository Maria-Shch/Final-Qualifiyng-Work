package ru.shcherbatykh.codetester.visitor;

import javax.tools.JavaFileObject;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class TestFileVisitor extends SimpleFileVisitor<Path> {
    private final List<Path> classFilePaths = new ArrayList<>();

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        if (file.toString().endsWith(JavaFileObject.Kind.CLASS.extension)) {
            classFilePaths.add(file);
        }
        return FileVisitResult.CONTINUE;
    }

    public List<Path> getClassFilePaths() {
        return classFilePaths;
    }
}
