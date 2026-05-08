package cwchoiit.cleanecommerce.application.catalog.category;

import static java.util.Objects.*;

import cwchoiit.cleanecommerce.application.port.in.CategoryCreateUseCase;
import cwchoiit.cleanecommerce.application.port.in.CategoryQueryUseCase;
import cwchoiit.cleanecommerce.application.port.out.CategoryRepository;
import cwchoiit.cleanecommerce.domain.catalog.category.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryCreateService implements CategoryCreateUseCase {

    private final CategoryRepository categoryRepository;
    private final CategoryQueryUseCase categoryQueryUseCase;

    @Override
    public Category create(String name, Long parentCategoryId) {
        checkDuplicateName(requireNonNull(name));

        Category parentCategory = findParentCategory(parentCategoryId);

        return categoryRepository.save(Category.create(name, parentCategory));
    }

    private Category findParentCategory(Long parentCategoryId) {
        if (nonNull(parentCategoryId)) {
            return categoryQueryUseCase.findById(parentCategoryId);
        }
        return null;
    }

    private void checkDuplicateName(String name) {
        if (categoryQueryUseCase.existsByName(name)) {
            throw new IllegalArgumentException("동일한 이름의 카테고리가 이미 존재합니다: " + name);
        }
    }
}
