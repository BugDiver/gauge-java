// Copyright 2015 ThoughtWorks, Inc.

// This file is part of Gauge-Java.

// This program is free software.
//
// It is dual-licensed under:
// 1) the GNU General Public License as published by the Free Software Foundation,
// either version 3 of the License, or (at your option) any later version;
// or
// 2) the Eclipse Public License v1.0.
//
// You can redistribute it and/or modify it under the terms of either license.
// We would then provide copied of each license in a separate .txt file with the name of the license as the title of the file.

package com.thoughtworks.gauge;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.google.common.io.Files.getNameWithoutExtension;
import static com.thoughtworks.gauge.GaugeConstant.DEFAULT_SRC_DIR;
import static com.thoughtworks.gauge.GaugeConstant.GAUGE_PROJECT_ROOT;
import static com.thoughtworks.gauge.GaugeConstant.GAUGE_CUSTOM_COMPILE_DIR;

public class FileHelper {

    public static Iterable<String> getAllImplementationFiles() {
        ArrayList<String> outputFiles = new ArrayList<>();
        getStepImplDir().forEach(dir -> {
            try (Stream<Path> filePathStream = Files.walk(getAbsolutePath(dir))) {
                filePathStream.forEach(filePath -> {
                    if (Files.isRegularFile(filePath) && filePath.toString().endsWith(".java")) {
                        outputFiles.add(filePath.toString());
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return outputFiles;
    }

    private static Path getAbsolutePath(String dir) {
        Path path = Paths.get(dir);
        return !path.isAbsolute() ? Paths.get(System.getenv(GAUGE_PROJECT_ROOT), dir) : path;
    }

    private static List<String> getStepImplDir() {
        List<String> srcDirs = new ArrayList<>();
        String customCompileDirs = System.getenv(GAUGE_CUSTOM_COMPILE_DIR);
        if (customCompileDirs != null && !customCompileDirs.isEmpty()) {
            srcDirs.addAll(Arrays.asList(customCompileDirs.trim().split(";")));
        }
        srcDirs.add(DEFAULT_SRC_DIR);
        return srcDirs;
    }

    public static String getClassName(File filepath) {
        String fileName = filepath.getName();
        return getNameWithoutExtension(fileName);
    }

    public static File getFileName(String suffix, int count) {
        String filename = "StepImplementation" + suffix + ".java";
        Path filepath = Paths.get(getDefaultStepImplDir(), filename);
        File file = new File(filepath.toString());
        return file.exists() ? getFileName(String.valueOf(++count), count) : file;
    }

    private static String getDefaultStepImplDir() {
        return getAbsolutePath(DEFAULT_SRC_DIR).toString();
    }
}
