package cwchoiit.cleanecommerce.adapter.web.product.response;

import cwchoiit.cleanecommerce.domain.catalog.product.ProductSku;
import java.util.List;
import java.util.Map;

public record ProductSkusResponse(Long productId, List<ProductSkuResponse> skus) {

    record ProductSkuResponse(
            String skuCode,
            Long skuId,
            Integer price,
            Integer stockQuantity,
            Map<String, Object> options,
            boolean active) {}

    public static ProductSkusResponse from(Long productId, List<ProductSku> skus) {
        List<ProductSkuResponse> list =
                skus.stream()
                        .map(
                                sku ->
                                        new ProductSkuResponse(
                                                sku.getSkuCode(),
                                                sku.getSkuId(),
                                                sku.getPrice(),
                                                sku.getStockQuantity(),
                                                sku.getOptions(),
                                                sku.isActive()))
                        .toList();

        return new ProductSkusResponse(productId, list);
    }
}
