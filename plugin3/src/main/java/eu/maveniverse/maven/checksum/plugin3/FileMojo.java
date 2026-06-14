/*
 * Copyright (c) 2023-2024 Maveniverse Org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */
package eu.maveniverse.maven.checksum.plugin3;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.eclipse.aether.spi.connector.checksum.ChecksumAlgorithmFactory;
import org.eclipse.aether.spi.connector.checksum.ChecksumAlgorithmHelper;

/**
 * Checksums given file with given algorithm, or "all".
 */
@Mojo(name = "file", threadSafe = true, requiresProject = false)
public class FileMojo extends ChecksumMojoSupport {
    /**
     * The existing file that should have checksums calculated.
     */
    @Parameter(property = "file", required = true)
    private File file;

    /**
     * The checksum algorithm name or names (comma separated) to use, or {@code "all"} string, if all should be used.
     */
    @Parameter(property = "alg", required = true)
    private String alg;

    /**
     * If the calculated checksums should be written out, set this to {@code true}.
     */
    @Parameter(property = "write", required = true, defaultValue = "false")
    private boolean write;

    @Override
    public void execute() throws MojoExecutionException {
        if (!file.isFile()) {
            throw new MojoExecutionException("File " + file + " is not an existing file");
        }

        Map<String, ChecksumAlgorithmFactory> selectedFactories = selectChecksumAlgorithmFactories(alg);

        try {
            Map<String, String> result =
                    ChecksumAlgorithmHelper.calculate(file, new ArrayList<>(selectedFactories.values()));
            logger.info("Calculated checksums for {}", file);
            for (Map.Entry<String, String> entry : result.entrySet()) {
                if (write) {
                    Path checksumFile = file.toPath()
                            .getParent()
                            .resolve(file.getName() + "."
                                    + selectedFactories.get(entry.getKey()).getFileExtension());
                    Files.write(checksumFile, entry.getValue().getBytes(StandardCharsets.UTF_8));
                }
                logger.info(" * {} = {}", entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
            throw new MojoExecutionException("Error while calculating checksums", e);
        }
    }
}
