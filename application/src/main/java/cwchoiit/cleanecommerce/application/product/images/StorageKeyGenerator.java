package cwchoiit.cleanecommerce.application.product.images;

import cwchoiit.cleanecommerce.domain.product.ProductImageType;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

class StorageKeyGenerator {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");

    String generate(ProductImageType imageType, String originalFileName) {
        String ext = extractExtension(originalFileName);
        LocalDate now = LocalDate.now();
        return String.format(
                "products/%s/%d/%02d/%s.%s",
                imageType.name().toLowerCase(),
                now.getYear(),
                now.getMonthValue(),
                UUID.randomUUID(),
                ext);
    }

    private String extractExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == fileName.length() - 1) {
            throw new IllegalArgumentException("파일명에 확장자가 없습니다: " + fileName);
        }
        String ext = fileName.substring(dotIndex + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new IllegalArgumentException(
                    "허용되지 않는 파일 확장자입니다: " + ext + ". 허용 목록: " + ALLOWED_EXTENSIONS);
        }
        return ext;
    }
}
