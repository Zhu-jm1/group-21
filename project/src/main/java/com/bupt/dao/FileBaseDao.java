package com.bupt.dao;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for file-based data access.
 * All data is stored in txt files under the configured data directory.
 */
public abstract class FileBaseDao {

    private static String dataDir;

    /**
     * Initialize the data directory. Called once at application startup.
     */
    public static void initDataDir(String dir) {
        dataDir = dir;
        try {
            Files.createDirectories(Paths.get(dataDir));
        } catch (IOException e) {
            throw new RuntimeException("Cannot create data directory: " + dir, e);
        }
    }

    public static String getDataDir() {
        return dataDir;
    }

    protected Path getFilePath(String filename) {
        return Paths.get(dataDir, filename);
    }

    /**
     * Read all non-empty lines from a data file.
     */
    protected List<String> readAllLines(String filename) {
        Path path = getFilePath(filename);
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }
        try {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            lines.removeIf(String::isEmpty);
            return lines;
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + filename, e);
        }
    }

    /**
     * Write all lines to a data file (overwrite).
     */
    protected void writeAllLines(String filename, List<String> lines) {
        Path path = getFilePath(filename);
        try {
            Files.write(path, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Error writing file: " + filename, e);
        }
    }

    /**
     * Append a single line to a data file.
     */
    protected void appendLine(String filename, String line) {
        Path path = getFilePath(filename);
        try {
            Files.write(path, (line + System.lineSeparator()).getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException("Error appending to file: " + filename, e);
        }
    }
}
