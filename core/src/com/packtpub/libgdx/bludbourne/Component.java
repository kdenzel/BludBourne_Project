package com.packtpub.libgdx.bludbourne;

import com.badlogic.gdx.utils.Disposable;

/**
 * Created by kdenzel on 30.08.2017.
 */

public interface Component {

    public static final String MESSAGE_TOKEN = ":::::";

    public static enum MESSAGE {
        CURRENT_POSITION,
        INIT_START_POSITION,
        CURRENT_DIRECTION,
        CURRENT_STATE,
        COLLISION_WITH_MAP,
        COLLISION_WITH_ENTITY,
        LOAD_ANIMATIONS,
        INIT_DIRECTION,
        INIT_STATE,
        INIT_SELECT_ENTITY,
        ENTITY_SELECTED,
        ENTITY_DESELECTED
    }

    void dispose();
    void receiveMessage(String message);

}
