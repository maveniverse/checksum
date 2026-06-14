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

def logTxt = buildLog.text

assert logTxt.contains('[INFO]  * SHA-1 = 73bc5be628edeb297a1caf421a5a2e494798b92f') // Junit POM
assert logTxt.contains('[INFO]  * SHA-1 = 8ac9e16d933b6fb43bc7f576336b8f4d7eb5ba12') // Junit JAR
