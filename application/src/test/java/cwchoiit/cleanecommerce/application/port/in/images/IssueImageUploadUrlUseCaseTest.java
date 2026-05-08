package cwchoiit.cleanecommerce.application.port.in.images;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import cwchoiit.cleanecommerce.application.catalog.product.images.ImageUploadPolicy;
import cwchoiit.cleanecommerce.application.catalog.product.images.IssueImageUploadUrlService;
import cwchoiit.cleanecommerce.application.port.out.images.ImageStoragePort;
import cwchoiit.cleanecommerce.application.port.out.images.PresignedUploadDescriptor;
import cwchoiit.cleanecommerce.domain.catalog.product.ProductImageType;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IssueImageUploadUrlUseCaseTest {

    @Mock ImageStoragePort imageStoragePort;

    IssueImageUploadUrlService service;

    @BeforeEach
    void setUp() {
        ImageUploadPolicy policy =
                new ImageUploadPolicy(
                        10_485_760L,
                        List.of("image/jpeg", "image/png", "image/webp"),
                        Duration.ofMinutes(5));
        service = new IssueImageUploadUrlService(imageStoragePort, policy);
    }

    @Test
    @DisplayName("presigned URL을 정상 발급한다")
    void issue() {
        URI uploadUrl = URI.create("http://localhost:9000/presigned-put");
        Instant expiresAt = Instant.now().plusSeconds(300);
        when(imageStoragePort.issueUploadUrl(any()))
                .thenReturn(
                        new PresignedUploadDescriptor(
                                uploadUrl, expiresAt, Map.of("Content-Type", "image/jpeg")));
        when(imageStoragePort.resolvePublicUrl(any()))
                .thenReturn(
                        URI.create(
                                "http://localhost:9000/product-images/products/thumbnail/2026/05/uuid.jpg"));

        IssueImageUploadUrlCommand command =
                new IssueImageUploadUrlCommand(
                        ProductImageType.THUMBNAIL, "photo.jpg", "image/jpeg", 1024L);

        IssueImageUploadUrlResult result = service.issue(command);

        assertThat(result.uploadUrl()).isEqualTo(uploadUrl);
        assertThat(result.storageKey()).startsWith("products/thumbnail/");
        assertThat(result.storageKey()).endsWith(".jpg");
        assertThat(result.publicUrl()).contains("localhost");
        assertThat(result.expiresAt()).isEqualTo(expiresAt);
        assertThat(result.requiredHeaders()).containsKey("Content-Type");
    }

    @Test
    @DisplayName("허용되지 않는 Content-Type이면 예외가 발생한다")
    void issueFailDisallowedContentType() {
        IssueImageUploadUrlCommand command =
                new IssueImageUploadUrlCommand(
                        ProductImageType.THUMBNAIL, "file.pdf", "application/pdf", 1024L);

        assertThatThrownBy(() -> service.issue(command))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("파일 크기가 허용 한도를 초과하면 예외가 발생한다")
    void issueFailExceedsMaxSize() {
        IssueImageUploadUrlCommand command =
                new IssueImageUploadUrlCommand(
                        ProductImageType.THUMBNAIL, "huge.jpg", "image/jpeg", 99_999_999L);

        assertThatThrownBy(() -> service.issue(command))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("허용되지 않는 파일 확장자이면 예외가 발생한다")
    void issueFailDisallowedExtension() {
        IssueImageUploadUrlCommand command =
                new IssueImageUploadUrlCommand(
                        ProductImageType.THUMBNAIL, "malicious.exe", "image/jpeg", 1024L);

        assertThatThrownBy(() -> service.issue(command))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("파일명에 확장자가 없으면 예외가 발생한다")
    void issueFailNoExtension() {
        IssueImageUploadUrlCommand command =
                new IssueImageUploadUrlCommand(
                        ProductImageType.THUMBNAIL, "noextension", "image/jpeg", 1024L);

        assertThatThrownBy(() -> service.issue(command))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
