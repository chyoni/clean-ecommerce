package cwchoiit.cleanecommerce.adapter.storage;

import static org.assertj.core.api.Assertions.assertThat;

import cwchoiit.cleanecommerce.application.port.out.images.ImageUploadRequest;
import cwchoiit.cleanecommerce.application.port.out.images.PresignedUploadDescriptor;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.BucketAlreadyOwnedByYouException;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Testcontainers
class S3PresignedImageStorageAdapterIT {

    private static final String BUCKET = "test-images";
    private static final String USER = "minioadmin";
    private static final String PASSWORD = "minioadmin";

    @Container
    static MinIOContainer minio =
            new MinIOContainer("minio/minio:RELEASE.2024-12-18T13-15-44Z")
                    .withUserName(USER)
                    .withPassword(PASSWORD);

    S3PresignedImageStorageAdapter adapter;
    HttpClient httpClient;

    @BeforeEach
    void setUp() {
        String endpoint = minio.getS3URL();

        S3Client s3Client =
                S3Client.builder()
                        .region(Region.US_EAST_1)
                        .endpointOverride(URI.create(endpoint))
                        .credentialsProvider(
                                StaticCredentialsProvider.create(
                                        AwsBasicCredentials.create(USER, PASSWORD)))
                        .serviceConfiguration(
                                S3Configuration.builder().pathStyleAccessEnabled(true).build())
                        .build();
        try {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(BUCKET).build());
        } catch (BucketAlreadyOwnedByYouException ignored) {
        }

        S3Presigner presigner =
                S3Presigner.builder()
                        .region(Region.US_EAST_1)
                        .endpointOverride(URI.create(endpoint))
                        .credentialsProvider(
                                StaticCredentialsProvider.create(
                                        AwsBasicCredentials.create(USER, PASSWORD)))
                        .serviceConfiguration(
                                S3Configuration.builder().pathStyleAccessEnabled(true).build())
                        .build();

        S3StorageProperties props =
                new S3StorageProperties(
                        endpoint,
                        "us-east-1",
                        BUCKET,
                        USER,
                        PASSWORD,
                        true,
                        Duration.ofMinutes(5),
                        endpoint,
                        10_485_760L,
                        List.of("image/jpeg", "image/png", "image/webp"));

        adapter = new S3PresignedImageStorageAdapter(presigner, props);
        httpClient = HttpClient.newHttpClient();
    }

    @Test
    @DisplayName("presigned URL로 실제 파일 업로드에 성공한다")
    void issueUploadUrlAndUpload() throws Exception {
        String storageKey = "products/thumbnail/2026/05/test-image.jpg";
        byte[] content = "fake-image-content".getBytes();

        ImageUploadRequest request =
                new ImageUploadRequest(
                        storageKey, "image/jpeg", content.length, Duration.ofMinutes(5));

        PresignedUploadDescriptor descriptor = adapter.issueUploadUrl(request);

        assertThat(descriptor.uploadUrl()).isNotNull();
        assertThat(descriptor.expiresAt()).isNotNull();

        HttpRequest.Builder putBuilder =
                HttpRequest.newBuilder()
                        .uri(descriptor.uploadUrl())
                        .PUT(HttpRequest.BodyPublishers.ofByteArray(content));

        // Java HttpClient 의 restricted header 는 자동 설정됨 — 수동 설정 불가
        Set<String> restricted =
                Set.of("content-length", "host", "connection", "expect", "upgrade");
        descriptor.requiredHeaders().entrySet().stream()
                .filter(e -> !restricted.contains(e.getKey().toLowerCase()))
                .forEach(e -> putBuilder.header(e.getKey(), e.getValue()));

        HttpResponse<String> response =
                httpClient.send(putBuilder.build(), HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("resolvePublicUrl은 storageKey 기반 URL을 반환한다")
    void resolvePublicUrl() {
        String storageKey = "products/thumbnail/2026/05/test-image.jpg";

        URI publicUrl = adapter.resolvePublicUrl(storageKey);

        assertThat(publicUrl.toString()).contains(BUCKET);
        assertThat(publicUrl.toString()).contains(storageKey);
    }
}
