# Maveniverse Checksum Suite

Runtime Requirements:
* Java: 8+
* Maven: 3.9+

Checksum suite tries to fix an unexplainable problem: Resolver 1.9.x supports extensible checksum SPI and exposes
all the needed components to perform any checksum calculation and verification, but Maven project itself is 
still going for a third-party plugin. That is just a loss and redundancy.

This suite provides a Maven3 extension, that hugely broadens Resolver supported checksum algorithms.
And provides a Maven3 plugin, that provides basic checksum ops.

## Using plugin

Plugin is usable standalone, to perform usual tasks: get list of supported checksums, checksum any file, or 
checksum project artifacts and so on.

## Using extension

If you want to have extra set of (more advanced) checksums in Resolver, just add the extension to Maven (as user
or as project extension).

That's it!


Example session using this suite https://gist.github.com/cstamas/162452886922af5c8b4658202027c0d6