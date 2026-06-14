/*
 * Copyright (c) 2023-2024 Maveniverse Org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */
package eu.maveniverse.maven.checksum.plugin3;

import javax.inject.Inject;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.eclipse.aether.spi.connector.checksum.ChecksumAlgorithmFactorySelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Just list available checksum algorithm names.
 */
@Mojo(name = "list-algorithms", threadSafe = true, requiresProject = false)
public class ListAlgorithmsMojo extends AbstractMojo {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    private ChecksumAlgorithmFactorySelector checksumAlgorithmFactorySelector;

    @Override
    public void execute() throws MojoExecutionException {
        logger.info("Supported checksum algorithms:");
        checksumAlgorithmFactorySelector
                .getChecksumAlgorithmFactories()
                .forEach(a -> logger.info(" * {} (file extension: .{})", a.getName(), a.getFileExtension()));
    }
}
