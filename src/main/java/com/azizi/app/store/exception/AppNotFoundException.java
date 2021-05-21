package com.azizi.app.store.exception;

import com.azizi.app.store.exception.base.NotFoundException;

public class AppNotFoundException extends NotFoundException {

    private static final String MESSAGE = "App not found!";

    public AppNotFoundException() {
        super(MESSAGE);
    }

}
