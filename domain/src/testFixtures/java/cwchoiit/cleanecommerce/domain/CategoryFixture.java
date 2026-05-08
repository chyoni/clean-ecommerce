package cwchoiit.cleanecommerce.domain;

import cwchoiit.cleanecommerce.domain.catalog.category.Category;
import java.lang.reflect.Field;

public class CategoryFixture {

    public static Category register() {
        return registerWith("노트북", registerWith("전자기기", null));
    }

    public static Category registerWith(String name, Category parentCategory) {
        return Category.create(name, parentCategory);
    }

    public static Category registerWithId(Long id) {
        Category c = register();
        setField(c, "categoryId", id);
        return c;
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name = "노트북";
        private Category parent = CategoryFixture.registerWith("전자기기", null);

        public Builder name(String v) {
            this.name = v;
            return this;
        }

        public Builder parent(Category v) {
            this.parent = v;
            return this;
        }

        public Category build() {
            return Category.create(name, parent);
        }
    }
}
