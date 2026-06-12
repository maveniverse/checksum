package eu.maveniverse.maven.checksum.extension3.providers;

import static java.util.Objects.requireNonNull;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.function.Supplier;
import org.eclipse.aether.spi.connector.checksum.ChecksumAlgorithm;
import org.eclipse.aether.spi.connector.checksum.ChecksumAlgorithmFactorySupport;
import org.eclipse.aether.util.ChecksumUtils;

/**
 * Support class to implement {@link org.eclipse.aether.spi.connector.checksum.ChecksumAlgorithmFactory} based
 * on Java {@link MessageDigest}.
 */
public class MessageDigestChecksumAlgorithmFactorySupport extends ChecksumAlgorithmFactorySupport {
    private final Supplier<MessageDigest> messageDigestSupplier;

    public MessageDigestChecksumAlgorithmFactorySupport(
            String name, String extension, Supplier<MessageDigest> messageDigestSupplier) {
        super(name, extension);
        this.messageDigestSupplier = requireNonNull(messageDigestSupplier);
    }

    @Override
    public ChecksumAlgorithm getAlgorithm() {
        MessageDigest messageDigest = messageDigestSupplier.get();
        return new ChecksumAlgorithm() {
            @Override
            public void update(final ByteBuffer input) {
                messageDigest.update(input);
            }

            @Override
            public String checksum() {
                return ChecksumUtils.toHexString(messageDigest.digest());
            }
        };
    }
}
