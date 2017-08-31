package com.packtpub.libgdx.bludbourne;

import java.util.Hashtable;

/**
 * Created by kdenzel on 31.08.2017.
 */

public class MapFactory {

    //All maps for the game
    private static final Hashtable<MapType, Map> _mapTable = new Hashtable<MapType, Map>();

    public static enum MapType {
        TOP_WORLD,
        TOWN,
        CASTLE_OF_DOOM
    }

    public static Map getMap(MapType mapType) {
        Map map = null;
        switch (mapType) {
            case TOP_WORLD:
                map = _mapTable.get(MapType.TOP_WORLD);
                break;
            case TOWN:
                map = _mapTable.get(MapType.TOWN);
                break;
            case CASTLE_OF_DOOM:
                map = _mapTable.get(MapType.CASTLE_OF_DOOM);
                break;
            default:
                break;
        }
        return map;
    }
}
