package com.azizi.app.store.exception;

import com.azizi.app.store.exception.base.NotFoundException;

public class CategoryNotFoundException extends NotFoundException {

    private static final String MESSAGE = "Category not found!";

    public CategoryNotFoundException() {
        super(MESSAGE);
    }


}
