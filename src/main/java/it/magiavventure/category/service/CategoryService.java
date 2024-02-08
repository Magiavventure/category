package it.magiavventure.category.service;

import it.magiavventure.category.error.CategoryException;
import it.magiavventure.category.mapper.CategoryMapper;
import it.magiavventure.category.model.CreateCategory;
import it.magiavventure.category.model.UpdateCategory;
import it.magiavventure.common.error.MagiavventureException;
import it.magiavventure.mongo.entity.ECategory;
import it.magiavventure.mongo.model.Category;
import it.magiavventure.mongo.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CategoryService {

    private final CategoryService self;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @CacheEvict(value = {"categories"}, key = "'all'")
    public Category createCategory(CreateCategory createCategory) {
        this.checkIfCategoryExists(createCategory.getName());

        var categoryToSave = ECategory
                .builder()
                .id(UUID.randomUUID())
                .name(createCategory.getName())
                .background(createCategory.getBackground())
                .active(createCategory.isActive())
                .build();
        ECategory savedCategory = categoryRepository.save(categoryToSave);
        return categoryMapper.map(savedCategory);
    }

    @Caching(
            evict = {
                    @CacheEvict(value = {"category"}, key = "#p0.id"),
                    @CacheEvict(value = {"categories"}, key = "'all'")
            }
    )
    public Category updateCategory(UpdateCategory updateCategory) {
        ECategory categoryToUpdate = self.findEntityById(updateCategory.getId());

        if(!updateCategory.getName().equals(categoryToUpdate.getName()))
            this.checkIfCategoryExists(updateCategory.getName());

        categoryToUpdate.setName(updateCategory.getName());
        categoryToUpdate.setBackground(updateCategory.getBackground());

        if(Objects.nonNull(updateCategory.getActive())
                && !updateCategory.getActive().equals(categoryToUpdate.isActive()))
            categoryToUpdate.setActive(updateCategory.getActive());

        ECategory updatedCategory = categoryRepository.save(categoryToUpdate);
        return categoryMapper.map(updatedCategory);
    }


    public Category findById(UUID id) {
        return categoryMapper.map(self.findEntityById(id));
    }

    @Caching(
            evict = {
                    @CacheEvict(value = {"category"}, key = "#p0"),
                    @CacheEvict(value = {"categories"}, key = "'all'")
            }
    )
    public void deleteById(UUID id) {
        self.findEntityById(id);
        categoryRepository.deleteById(id);
    }

    @Cacheable(value = "category", key = "#p0")
    public ECategory findEntityById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> MagiavventureException.of(CategoryException.CATEGORY_NOT_FOUND, id.toString()));
    }

    @Cacheable(value = "categories", key = "'all'")
    public List<Category> findAll() {
        var sort = Sort.by(Sort.Direction.ASC, "name");
        return categoryRepository.findAll(sort)
                .stream()
                .map(categoryMapper::map)
                .toList();
    }

    private void checkIfCategoryExists(String name) {
        Example<ECategory> categoryExample = Example.of(ECategory
                .builder()
                .name(name)
                .build(), ExampleMatcher.matchingAny());

        if(categoryRepository.exists(categoryExample))
            throw MagiavventureException.of(CategoryException.CATEGORY_EXISTS, name);
    }
}
