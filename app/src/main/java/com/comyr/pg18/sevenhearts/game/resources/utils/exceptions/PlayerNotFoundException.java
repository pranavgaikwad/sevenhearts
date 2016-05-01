package com.comyr.pg18.sevenhearts.game.resources.utils.exceptions;

import com.comyr.pg18.sevenhearts.game.resources.Player;
import com.comyr.pg18.sevenhearts.game.resources.utils.exceptions.base.PlayerException;

public class PlayerNotFoundException extends PlayerException {

    public PlayerNotFoundException() {
        // TODO Auto-generated constructor stub
    }

    public PlayerNotFoundException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public PlayerNotFoundException(Player player) {
        super(player);
        // TODO Auto-generated constructor stub
    }

    public PlayerNotFoundException(String message, Player player) {
        super(message, player);
        // TODO Auto-generated constructor stub
    }

}
