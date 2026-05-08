package cwchoiit.cleanecommerce.application.catalog.product.images;

import cwchoiit.cleanecommerce.application.port.in.images.IssueImageUploadUrlCommand;
import cwchoiit.cleanecommerce.application.port.in.images.IssueImageUploadUrlResult;
import cwchoiit.cleanecommerce.application.port.in.images.IssueImageUploadUrlUseCase;
import cwchoiit.cleanecommerce.application.port.out.images.ImageStoragePort;
import cwchoiit.cleanecommerce.application.port.out.images.ImageUploadRequest;
import cwchoiit.cleanecommerce.application.port.out.images.PresignedUploadDescriptor;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class IssueImageUploadUrlService implements IssueImageUploadUrlUseCase {

    private final ImageStoragePort imageStoragePort;
    private final ImageUploadPolicy imageUploadPolicy;
    private final StorageKeyGenerator storageKeyGenerator = new StorageKeyGenerator();

    @Override
    public IssueImageUploadUrlResult issue(@Valid IssueImageUploadUrlCommand command) {
        validateContentType(command.contentType());
        validateContentLength(command.contentLength());

        String storageKey =
                storageKeyGenerator.generate(command.imageType(), command.originalFileName());

        PresignedUploadDescriptor descriptor =
                imageStoragePort.issueUploadUrl(
                        new ImageUploadRequest(
                                storageKey,
                                command.contentType(),
                                command.contentLength(),
                                imageUploadPolicy.presignExpiry()));

        return new IssueImageUploadUrlResult(
                descriptor.uploadUrl(),
                storageKey,
                imageStoragePort.resolvePublicUrl(storageKey).toString(),
                descriptor.expiresAt(),
                descriptor.requiredHeaders());
    }

    private void validateContentType(String contentType) {
        if (!imageUploadPolicy.allowedContentTypes().contains(contentType)) {
            throw new IllegalArgumentException(
                    "허용되지 않는 Content-Type입니다: "
                            + contentType
                            + ". 허용 목록: "
                            + imageUploadPolicy.allowedContentTypes());
        }
    }

    private void validateContentLength(long contentLength) {
        if (contentLength > imageUploadPolicy.maxUploadBytes()) {
            throw new IllegalArgumentException(
                    "파일 크기가 허용 한도를 초과했습니다. 최대: "
                            + imageUploadPolicy.maxUploadBytes()
                            + " bytes, 요청: "
                            + contentLength
                            + " bytes");
        }
    }
}
