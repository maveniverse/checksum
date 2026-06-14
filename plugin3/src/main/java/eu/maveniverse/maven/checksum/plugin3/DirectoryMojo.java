/*
 * Copyright (c) 2023-2024 Maveniverse Org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */
package eu.maveniverse.maven.checksum.plugin3;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Map;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.eclipse.aether.spi.connector.checksum.ChecksumAlgorithmFactory;
import org.eclipse.aether.spi.connector.checksum.ChecksumAlgorithmHelper;

/**
 * Checksums all files found recursively in given directory with given algorithm, or "all".
 * Default algorithms are same as in Maven 3: SHA-1 and MD5.
 */
@Mojo(name = "directory", threadSafe = true, requiresProject = false)
public class DirectoryMojo extends ChecksumMojoSupport {
    /**
     * The existing directory that should have checksums calculated.
     * Defaults to current working directory.
     */
    @Parameter(property = "dir", required = true, defaultValue = "")
    private File dir;

    /**
     * The checksum algorithm name or names (comma separated) to use, or {@code "all"} string, if all should be used.
     * Defaults to Maven 3 defaults.
     */
    @Parameter(property = "alg", required = true, defaultValue = "SHA-1,MD5")
    private String alg;

    /**
     * If the calculated checksums should not be written out, set this to {@code false}.
     */
    @Parameter(property = "write", required = true, defaultValue = "true")
    private boolean write;

    @Override
    public void execute() throws MojoExecutionException {
        if (!dir.isDirectory()) {
            throw new MojoExecutionException("Directory " + dir + " is not an existing directory");
        }

        Map<String, ChecksumAlgorithmFactory> selectedFactories = selectChecksumAlgorithmFactories(alg);

        try {
            Files.walkFileTree(dir.toPath(), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String fileName = file.getFileName().toString();
                    int lastDotIndex = fileName.lastIndexOf('.');
                    if (lastDotIndex == -1
                            || !checksumAlgorithmFactorySelector.isChecksumExtension(
                                    fileName.substring(lastDotIndex + 1))) {
                        Map<String, String> result = ChecksumAlgorithmHelper.calculate(
                                file.toFile(), new ArrayList<>(selectedFactories.values()));
                        logger.info("Calculated checksums for {}", file);
                        for (Map.Entry<String, String> entry : result.entrySet()) {
                            if (write) {
                                Path checksumFile = file.getParent()
                                        .resolve(file.getFileName() + "."
                                                + selectedFactories
                                                        .get(entry.getKey())
                                                        .getFileExtension());
                                Files.write(checksumFile, entry.getValue().getBytes(StandardCharsets.UTF_8));
                            }
                            logger.info(" * {} = {}", entry.getKey(), entry.getValue());
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new MojoExecutionException("Error while calculating checksums recursively in directory" + dir, e);
        }
    }
}
