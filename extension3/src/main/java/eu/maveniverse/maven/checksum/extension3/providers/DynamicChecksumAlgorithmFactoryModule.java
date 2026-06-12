package eu.maveniverse.maven.checksum.extension3.providers;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.name.Names;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.function.Supplier;
import javax.inject.Named;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.eclipse.aether.spi.connector.checksum.ChecksumAlgorithmFactory;

/**
 * Exposes all discovered algorithms via SPI.
 */
@Named
public class DynamicChecksumAlgorithmFactoryModule extends AbstractModule {
    @Override
    protected void configure() {
        // all this happens early, this must be Java System Property
        final boolean bouncyCastleWanted =
                Boolean.parseBoolean(System.getProperty("maveniverse.checksum.registerBouncyCastle", "false"));
        if (bouncyCastleWanted) {
            Security.addProvider(new BouncyCastleProvider());
        }

        // now go over Java available ones and dynamically register all wanted ones
        Set<String> algorithms = new HashSet<>(Security.getAlgorithms("MessageDigest"));
        // remove Resolver default ones to avoid duplicates
        Arrays.asList("MD5", "SHA-1", "SHA-256", "SHA-512").forEach(algorithms::remove);

        for (String algorithm : algorithms) {
            String extension = algorithm.toLowerCase(Locale.ROOT).replaceAll("[^a-zA-Z0-9-_\\.]", "_");
            // fix to align with existing extensions (all SHA-2 families do not have dash, but SHA-3 does)
            if (extension.startsWith("sha-")) {
                extension = "sha" + extension.substring(4);
            }
            Supplier<MessageDigest> supplier = () -> {
                try {
                    return MessageDigest.getInstance(algorithm);
                } catch (NoSuchAlgorithmException e) {
                    throw new IllegalStateException(
                            "MessageDigest algorithm " + algorithm + " not supported, but is required by resolver.", e);
                }
            };
            bind(Key.get(ChecksumAlgorithmFactory.class, Names.named(algorithm)))
                    .toInstance(new MessageDigestChecksumAlgorithmFactorySupport(algorithm, extension, supplier));
        }
    }
}
