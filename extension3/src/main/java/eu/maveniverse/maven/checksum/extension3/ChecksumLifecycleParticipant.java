/*
 * Copyright (c) 2023-2024 Maveniverse Org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */
package eu.maveniverse.maven.checksum.extension3;

import java.security.Security;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Simple lifecycle participant, only here to register BC Provider.
 */
@Singleton
@Named
public class ChecksumLifecycleParticipant extends AbstractMavenLifecycleParticipant {
    public ChecksumLifecycleParticipant() {
        Security.addProvider(new BouncyCastleProvider());
    }
}
