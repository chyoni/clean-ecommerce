package cwchoiit.cleanecommerce.application.catalog.category;

import cwchoiit.cleanecommerce.application.port.in.CategoryQueryUseCase;
import cwchoiit.cleanecommerce.application.port.out.CategoryRepository;
import cwchoiit.cleanecommerce.domain.catalog.category.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryQueryService implements CategoryQueryUseCase {

    private final CategoryRepository categoryRepository;

    @Override
    public Category findById(Long categoryId) {
        return categoryRepository.findByCategoryId(categoryId).orElseThrow();
    }

    @Override
    public boolean existsByName(String name) {
        return categoryRepository.findByName(name).isPresent();
    }
}
