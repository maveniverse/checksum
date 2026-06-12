package eu.maveniverse.maven.checksum.extension3.providers;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.eclipse.aether.spi.connector.checksum.ChecksumAlgorithm;
import org.eclipse.aether.spi.connector.checksum.ChecksumAlgorithmFactorySupport;
import org.eclipse.aether.util.ChecksumUtils;

/**
 * Support class to implement {@link org.eclipse.aether.spi.connector.checksum.ChecksumAlgorithmFactory} based
 * on Java {@link MessageDigest}.
 */
public abstract class MessageDigestChecksumAlgorithmFactorySupport extends ChecksumAlgorithmFactorySupport {
    public MessageDigestChecksumAlgorithmFactorySupport(String name, String extension) {
        super(name, extension);
    }

    @Override
    public ChecksumAlgorithm getAlgorithm() {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(getName());
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
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "MessageDigest algorithm " + getName() + " not supported, but is required by resolver.", e);
        }
    }
}
