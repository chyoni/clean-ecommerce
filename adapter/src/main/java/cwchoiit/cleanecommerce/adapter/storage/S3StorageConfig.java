package cwchoiit.cleanecommerce.adapter.storage;

import cwchoiit.cleanecommerce.application.product.images.ImageUploadPolicy;
import java.net.URI;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
@EnableConfigurationProperties(S3StorageProperties.class)
public class S3StorageConfig {

    @Bean(destroyMethod = "close")
    S3Presigner s3Presigner(S3StorageProperties props) {
        return S3Presigner.builder()
                .region(Region.of(props.region()))
                .endpointOverride(URI.create(props.endpoint()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(props.accessKey(), props.secretKey())))
                .serviceConfiguration(
                        S3Configuration.builder()
                                .pathStyleAccessEnabled(props.forcePathStyle())
                                .build())
                .build();
    }

    @Bean
    ImageUploadPolicy imageUploadPolicy(S3StorageProperties props) {
        return new ImageUploadPolicy(
                props.maxUploadBytes(), props.allowedContentTypes(), props.presignExpiry());
    }
}
