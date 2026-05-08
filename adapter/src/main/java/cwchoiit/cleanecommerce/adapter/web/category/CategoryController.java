package cwchoiit.cleanecommerce.adapter.web.category;

import cwchoiit.cleanecommerce.adapter.web.category.request.CategoryCreateRequest;
import cwchoiit.cleanecommerce.adapter.web.category.response.CategoryCreateResponse;
import cwchoiit.cleanecommerce.application.port.in.CategoryCreateUseCase;
import cwchoiit.cleanecommerce.domain.catalog.category.Category;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryCreateUseCase categoryCreateUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryCreateResponse create(@RequestBody @Valid CategoryCreateRequest request) {
        Category category =
                categoryCreateUseCase.create(request.name(), request.parentCategoryId());
        return CategoryCreateResponse.from(category);
    }
}
