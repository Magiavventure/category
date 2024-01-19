package it.magiavventure.category.error;

import it.magiavventure.common.error.MagiavventureException;
import it.magiavventure.common.model.Error;
import lombok.Getter;

@Getter
public class CategoryException extends MagiavventureException {

    public static final String CATEGORY_NOT_FOUND = "category-not-found";
    public static final String CATEGORY_EXISTS = "category-exists";

    public CategoryException(Error error) {
        super(error);
    }

}
