package com.azizi.app.store.exception;

import com.azizi.app.store.exception.base.NotFoundException;

public class UserNotFoundException extends NotFoundException {

    private static final String MESSAGE = "User not found!";

    public UserNotFoundException() {
        super(MESSAGE);
    }


}
