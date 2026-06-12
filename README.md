# Maveniverse Checksum Suite

Runtime Requirements:
* Java: 8+
* Maven: 3.9+

Maven 3.9.0 is available since Feb 14, 2023. This means that Resolver 1.9.x is as well. Still, we did nothing
to exploit new features on those two, like Resolver Checksum SPI is. This suite fixes this.

Resolver 1.9.x supports extensible checksum SPI and exposes all the needed components to perform any checksum 
calculation and verification, but Maven project itself is still going for a third-party plugins. 
That is just a loss and redundancy.

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