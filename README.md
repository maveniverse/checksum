# Maveniverse Checksum Suite

Runtime Requirements:
* Java: 8+
* Maven: 3.9+

Checksum Suite empowers Maven 3.9+ with extending supported checksum algorithms.

Checksum Suite consists of one Maven3 plugin and one Maven3 extension, both are nearly trivial and dependency less. Also, they do not depend on each other, they can be used independently for each other.

## Backstory

From Maven 3.0+, checksums were literally "baked in" into Resolver supporting SHA1 and MD5 only. In Resolver 1.9.x (shipped with Maven 3.9.x) we changed that, and made it into SPI and extended out of the box supported checksums to MD5, SHA1, SHA2-256 and SHA2-512, see https://maven.apache.org/resolver/about-checksums.html. But, even today, defaults remained same, SHA1 and MD5.

## Using plugin

Plugin is usable standalone, to perform usual tasks: get list of supported checksums, checksum any file, or 
checksum project artifacts and so on.

The suite plugin is a trivial, dependency-less plugin, that allows you to query supported checksum algorithms, and allow you to generate checksums for any file, or project artifacts. Main motivation was ASF release process, where ASF projects use "defaults" (MD5 and SHA1) for all artifacts -- hence do not alter Maven/Resolver config for checksums --, but there is one "distinguished" artifact, the source release bundle ZIP, that must have SHA-512 checksums. The plugin allows you to use any "resolver supported" checksum on any file or project artifact, without tampering with Maven/Resolver configuration.

Pkugin documentation is available here https://maveniverse.eu/docs/checksum/plugin-documentation/plugin-info.html

## Using extension

If you want to have extra set of (more advanced) checksums in Resolver, just add the extension to Maven (as user
or as project extension).

The extension on the other hand, hooks into Checksum SPI, and dynamically exposes all discovered `MessageDigest` algorithms as checksums. On modern Java versions, there are new algorithms available like SHA3 variations, and with extension, those become supported as well. Finally, if asked, the extension registers BouncyCastle provider as well, and you end up with a ton of exotic checksum algorithms, like Blake2 is.


Example session using this suite https://gist.github.com/cstamas/162452886922af5c8b4658202027c0d6
