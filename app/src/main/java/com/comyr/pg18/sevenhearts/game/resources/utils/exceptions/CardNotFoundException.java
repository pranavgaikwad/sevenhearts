package com.comyr.pg18.sevenhearts.game.resources.utils.exceptions;

import com.comyr.pg18.sevenhearts.game.resources.utils.exceptions.base.CardException;

public class CardNotFoundException extends CardException {

    public CardNotFoundException() {
        // TODO Auto-generated constructor stub
    }

    public CardNotFoundException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public CardNotFoundException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    public CardNotFoundException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }
}
