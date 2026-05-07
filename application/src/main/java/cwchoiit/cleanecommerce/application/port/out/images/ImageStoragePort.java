package cwchoiit.cleanecommerce.application.port.out.images;

import java.net.URI;

public interface ImageStoragePort {
    PresignedUploadDescriptor issueUploadUrl(ImageUploadRequest request);

    URI resolvePublicUrl(String storageKey);
}
