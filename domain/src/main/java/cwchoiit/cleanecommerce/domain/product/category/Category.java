package cwchoiit.cleanecommerce.domain.product.category;

import static java.util.Objects.requireNonNull;

import cwchoiit.cleanecommerce.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString
@Table(name = "category")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    private String name;

    @JoinColumn(name = "parent_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Category parent;

    public static Category create(String name, Category parentCategory) {
        Category category = new Category();
        category.name = requireNonNull(name);
        category.parent = parentCategory;
        return category;
    }
}
