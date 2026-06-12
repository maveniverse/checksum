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

def sha512checksums = []
def dir = new File( basedir, 'target/repo' )
dir.eachFileRecurse (FileType.FILES) { file ->
    if (file.getName().endsWith(".sha512")) {
        sha512checksums << file
    }
}

assert sha512checksums.size() == 1 // only source bundle have it, nothing else