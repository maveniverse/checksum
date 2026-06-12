/*
 * Copyright (c) 2023-2024 Maveniverse Org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */
package eu.maveniverse.maven.checksum.plugin3;

import java.io.File;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.apache.maven.RepositoryUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.artifact.ProjectArtifact;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.spi.connector.checksum.ChecksumAlgorithmFactory;
import org.eclipse.aether.spi.connector.checksum.ChecksumAlgorithmFactorySelector;
import org.eclipse.aether.util.artifact.ArtifactIdUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Support class.
 */
public abstract class ChecksumMojoSupport extends AbstractMojo {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    protected ChecksumAlgorithmFactorySelector checksumAlgorithmFactorySelector;

    protected Map<String, ChecksumAlgorithmFactory> selectChecksumAlgorithmFactories(String alg)
            throws MojoExecutionException {
        Map<String, ChecksumAlgorithmFactory> selectedFactories;
        if ("all".equalsIgnoreCase(alg)) {
            selectedFactories = checksumAlgorithmFactorySelector.getChecksumAlgorithmFactories().stream()
                    .map(a -> new AbstractMap.SimpleEntry<>(a.getName(), a))
                    .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
        } else {
            try {
                selectedFactories =
                        checksumAlgorithmFactorySelector.selectList(Arrays.asList(alg.split("[,;|]"))).stream()
                                .map(a -> new AbstractMap.SimpleEntry<>(a.getName(), a))
                                .collect(Collectors.toMap(
                                        AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
            } catch (IllegalArgumentException e) {
                throw new MojoExecutionException("Algorithm " + alg + " is not supported", e);
            }
        }
        return selectedFactories;
    }

    protected List<Artifact> collectArtifacts(MavenProject project, Predicate<Artifact> filter)
            throws MojoExecutionException {
        // always exists, as project exists
        Artifact pomArtifact = RepositoryUtils.toArtifact(new ProjectArtifact(project));
        // always exists, but at "init" is w/o file (packaging plugin assigns file to this when packaged)
        Artifact projectArtifact = RepositoryUtils.toArtifact(project.getArtifact());

        // pom project: pomArtifact and projectArtifact are SAME
        // jar project: pomArtifact and projectArtifact are DIFFERENT
        // incomplete project: is not pom project and projectArtifact has no file

        // we must compare coordinates ONLY (as projectArtifact may not have file, and Artifact.equals factors it in)
        // BUT if projectArtifact has file set, use that one
        if (ArtifactIdUtils.equalsId(pomArtifact, projectArtifact)) {
            if (isFile(projectArtifact.getFile())) {
                pomArtifact = projectArtifact;
            }
            projectArtifact = null;
        }

        ArrayList<Artifact> artifacts = new ArrayList<>();
        if (isFile(pomArtifact.getFile())) {
            artifacts.add(pomArtifact);
        } else {
            throw new MojoExecutionException(
                    "The POM for project " + project.getArtifactId() + " points to non-existent file");
        }

        // is not packaged, is "incomplete"; this mojos can work only with packaged artifacts -> error
        if (projectArtifact != null) {
            if (isFile(projectArtifact.getFile())) {
                artifacts.add(projectArtifact);
            } else {
                throw new MojoExecutionException(
                        "The main artifact for project " + project.getArtifactId() + " points to non-existent file");
            }
        }

        for (org.apache.maven.artifact.Artifact attached : project.getAttachedArtifacts()) {
            Artifact attachedArtifact = RepositoryUtils.toArtifact(attached);
            if (isFile(attached.getFile())) {
                logger.debug("Collected: {}", attachedArtifact);
                artifacts.add(attachedArtifact);
            } else {
                throw new MojoExecutionException("The attached artifact " + attachedArtifact + " for project "
                        + project.getArtifactId() + " points to non-existent file.");
            }
        }
        return artifacts.stream().filter(filter).collect(Collectors.toList());
    }

    protected boolean isFile(File file) {
        return file != null && file.isFile();
    }
}
