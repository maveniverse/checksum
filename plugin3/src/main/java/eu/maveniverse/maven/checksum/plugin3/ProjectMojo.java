/*
 * Copyright (c) 2023-2024 Maveniverse Org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */
package eu.maveniverse.maven.checksum.plugin3;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import javax.inject.Inject;
import org.apache.maven.RepositoryUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.DefaultMavenProjectHelper;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.spi.connector.checksum.ChecksumAlgorithmFactory;
import org.eclipse.aether.spi.connector.checksum.ChecksumAlgorithmHelper;
import org.eclipse.aether.util.artifact.SubArtifact;

/**
 * Checksums project artifacts with given algorithm, or "all" and attach checksums.
 */
@Mojo(name = "project", threadSafe = true)
public class ProjectMojo extends ChecksumMojoSupport {
    @Inject
    private MavenSession mavenSession;

    @Inject
    private DefaultMavenProjectHelper mavenProjectHelper;

    /**
     * The checksum algorithm name or names (comma separated) to use.
     */
    @Parameter(property = "alg", required = true)
    private String alg;

    /**
     * Whether the checksums should be attached to project.
     */
    @Parameter(property = "attach", required = true, defaultValue = "true")
    private boolean attach;

    /**
     * List of classifiers (exact) that are included in this execution.
     */
    @Parameter(property = "includedClassifiers")
    private List<String> includedClassifiers;

    @Override
    public void execute() throws MojoExecutionException {
        MavenProject currentProject = mavenSession.getCurrentProject();
        List<Artifact> artifacts = collectArtifacts(currentProject, filter());
        Map<String, ChecksumAlgorithmFactory> selectedFactories = selectChecksumAlgorithmFactories(alg);
        try {
            for (Artifact artifact : artifacts) {
                Map<String, String> result = ChecksumAlgorithmHelper.calculate(
                        artifact.getFile(), new ArrayList<>(selectedFactories.values()));
                logger.debug("Calculated checksums for {}", artifact);
                for (Map.Entry<String, String> entry : result.entrySet()) {
                    String ext = selectedFactories.get(entry.getKey()).getFileExtension();
                    Path checksumFile = Files.createTempFile(artifact.getFile().getName(), ext);
                    Files.write(checksumFile, entry.getValue().getBytes(StandardCharsets.UTF_8));
                    Artifact checksumArtifact =
                            new SubArtifact(artifact, "*", "*." + ext).setFile(checksumFile.toFile());
                    if (attach) {
                        mavenProjectHelper.attachArtifact(currentProject, RepositoryUtils.toArtifact(checksumArtifact));
                    }
                    logger.debug(" * {} > {}", entry.getKey(), checksumArtifact);
                }
            }
        } catch (Exception e) {
            throw new MojoExecutionException("Error while calculating checksums", e);
        }
    }

    private Predicate<Artifact> filter() {
        if (includedClassifiers != null && !includedClassifiers.isEmpty()) {
            return artifact ->
                    !artifact.getClassifier().isEmpty() && includedClassifiers.contains(artifact.getClassifier());
        } else {
            return a -> true;
        }
    }
}
