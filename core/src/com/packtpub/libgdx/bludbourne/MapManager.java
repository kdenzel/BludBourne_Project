package com.packtpub.libgdx.bludbourne;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.Hashtable;

/**
 * Created by kdenzel on 29.08.2017.
 */

public class MapManager {

    private static final String TAG = MapManager.class.getSimpleName();

    //All maps for the game
    private Hashtable<String, String> _mapTable;
    private Hashtable<String, Vector2> _playerStartLocationTable;

    //maps
    private final static String TOP_WORLD = "TOP_WORLD";
    private final static String TOWN = "TOWN";
    private final static String CASTLE_OF_DOOM = "CASTLE_OF_DOOM";

    //Map layers
    private final static String MAP_COLLISION_LAYER = "MAP_COLLISION_LAYER";
    private final static String MAP_SPAWNS_LAYER = "MAP_SPAWNS_LAYER";
    private final static String MAP_PORTAL_LAYER = "MAP_PORTAL_LAYER";
    private final static String PLAYER_START = "PLAYER_START";
    private Vector2 _playerStartPositionRect;

    private Vector2 _closestPlayerStartPosition;
    private Vector2 _convertedUnits;
    private Vector2 _playerStart;
    private Map _currentMap = null;
    private MapFactory.MapType _currentMapName;
    private MapLayer _collisionLayer = null;
    private MapLayer _portalLayer = null;
    private MapLayer _spawnsLayer = null;
    public final static float UNIT_SCALE = 1 / 16f;

    public MapManager() {
        _playerStart = new Vector2(0, 0);
        _mapTable = new Hashtable();
        _mapTable.put(TOP_WORLD, "maps/topworld.tmx");
        _mapTable.put(TOWN, "maps/town.tmx");
        _mapTable.put(CASTLE_OF_DOOM, "maps/castle_of_doom.tmx");
        _playerStartLocationTable = new Hashtable();
        _playerStartLocationTable.put(TOP_WORLD,
                _playerStart.cpy());
        _playerStartLocationTable.put(TOWN, _playerStart.cpy());
        _playerStartLocationTable.put(CASTLE_OF_DOOM,
                _playerStart.cpy());
        _playerStartPositionRect = new Vector2(0, 0);
        _closestPlayerStartPosition = new Vector2(0, 0);
        _convertedUnits = new Vector2(0, 0);
    }

    public void loadMap(MapFactory.MapType mapType) {
        Map map = MapFactory.getMap(mapType);

        if( map == null ){
            Gdx.app.debug(TAG, "Map does not exist!  ");
            return;
        }
        _currentMap = map;

    }

    public Map getCurrentMap() {
        if (_currentMap == null) {
            _currentMapName = MapFactory.MapType.TOWN;
            loadMap(_currentMapName);
        }
        return _currentMap;
    }

    public MapLayer getCollisionLayer() {
        return _collisionLayer;
    }

    public MapLayer getPortalLayer() {
        return _portalLayer;
    }

    public final Array<Entity> getCurrentMapEntities(){
        return _currentMap.getMapEntities();
    }

    public Vector2 getPlayerStartUnitScaled() {
        Vector2 playerStart = _playerStart.cpy();
        playerStart.set(_playerStart.x * UNIT_SCALE, _playerStart.y * UNIT_SCALE);
        return playerStart;
    }

    private void setClosestStartPosition(final Vector2 position) {
        _currentMap.setClosestStartPositionFromScaledUnits(position);
    }

    public void setClosestStartPositionFromScaledUnits(Vector2 position){
        if( UNIT_SCALE <= 0 )
            return;
        _convertedUnits.set(position.x/UNIT_SCALE,
                position.y/UNIT_SCALE);
        setClosestStartPosition(_convertedUnits);
    }

    //TODO return Camera
    public Camera getCamera() {
        return null;
    }


}

