package cwchoiit.cleanecommerce.adapter.storage;

import cwchoiit.cleanecommerce.application.port.out.images.ImageStoragePort;
import cwchoiit.cleanecommerce.application.port.out.images.ImageUploadRequest;
import cwchoiit.cleanecommerce.application.port.out.images.PresignedUploadDescriptor;
import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Component
@RequiredArgsConstructor
public class S3PresignedImageStorageAdapter implements ImageStoragePort {

    private final S3Presigner s3Presigner;
    private final S3StorageProperties props;

    @Override
    public PresignedUploadDescriptor issueUploadUrl(ImageUploadRequest request) {
        PutObjectRequest putObjectRequest =
                PutObjectRequest.builder()
                        .bucket(props.bucket())
                        .key(request.storageKey())
                        .contentType(request.contentType())
                        .contentLength(request.contentLength())
                        .build();

        PutObjectPresignRequest presignRequest =
                PutObjectPresignRequest.builder()
                        .signatureDuration(request.expiresIn())
                        .putObjectRequest(putObjectRequest)
                        .build();

        PresignedPutObjectRequest presigned = s3Presigner.presignPutObject(presignRequest);

        Map<String, String> requiredHeaders =
                presigned.signedHeaders().entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getFirst()));

        return new PresignedUploadDescriptor(
                URI.create(presigned.url().toString()), presigned.expiration(), requiredHeaders);
    }

    @Override
    public URI resolvePublicUrl(String storageKey) {
        String base = props.publicUrlBase().replaceAll("/$", "");
        return URI.create(base + "/" + props.bucket() + "/" + storageKey);
    }
}
