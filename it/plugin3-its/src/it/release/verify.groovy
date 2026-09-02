/*
 * Copyright (c) 2023-2024 Maveniverse Org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */
import groovy.io.FileType

File buildLog = new File( basedir, 'build.log' )
assert buildLog.exists()

assert buildLog.text.contains('-source-release.jar.sha512')

// log should contain the SHA-512 checksum for artifact
assert buildLog.text.contains('[INFO] smoke-0.1.0-SNAPSHOT-source-release.jar - SHA-512: 9301b7c832f7b6d39745c8fe65ae6d3ee828f809e2fb685e9613f3939c6d9ad509c84499a37dbfef2ffdffb7c21b6b4be7e25e72f63c1f435517d940a3fac3a9')

def sha512checksums = []
def dir = new File( basedir, 'target/repo' )
dir.eachFileRecurse (FileType.FILES) { file ->
    if (file.getName().endsWith(".sha512")) {
        sha512checksums << file
    }
}

assert sha512checksums.size() == 1 // only source bundle have it, nothing else

// output should be stored in target directory
assert new File( basedir, "target/smoke-0.1.0-SNAPSHOT-source-release.jar.sha512" ).exists()
