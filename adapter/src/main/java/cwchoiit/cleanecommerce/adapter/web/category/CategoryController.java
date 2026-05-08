package cwchoiit.cleanecommerce.adapter.web.category;

import cwchoiit.cleanecommerce.adapter.web.category.request.CategoryCreateRequest;
import cwchoiit.cleanecommerce.adapter.web.category.response.CategoryCreateResponse;
import cwchoiit.cleanecommerce.application.port.in.CategoryCreateUseCase;
import cwchoiit.cleanecommerce.domain.catalog.category.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryCreateUseCase categoryCreateUseCase;

    @PostMapping
    public CategoryCreateResponse create(@RequestBody CategoryCreateRequest request) {
        Category category = categoryCreateUseCase.create(request.name(), request.parentCategoryId());
        return CategoryCreateResponse.from(category);
    }
}
