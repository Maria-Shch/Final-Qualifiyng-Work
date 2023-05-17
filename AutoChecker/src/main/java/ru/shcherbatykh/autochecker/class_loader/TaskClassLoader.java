package ru.shcherbatykh.autochecker.class_loader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class TaskClassLoader extends ClassLoader {
    private final Queue<Path> paths;

    public TaskClassLoader(Collection<Path> paths) {
        this.paths = new ArrayDeque<>(paths);
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(name));
            return defineClass(null, bytes, 0, bytes.length);
        } catch (Exception e) {
            throw new ClassNotFoundException("Class " + name + " is not found", e);
        }
    }

    public Map<Path, Class<?>> findAllClasses() {
        Map<Path, Class<?>> loadedClasses = new HashMap<>();
        while (!paths.isEmpty()) {
            Path path = paths.poll();
            try {
                loadedClasses.put(path, findClass(path.toString().replace(".java", ".class")));
            } catch (Throwable e) {
                paths.add(path);
            }
        }
        return loadedClasses;
    }
}
