package com.packtpub.libgdx.bludbourne;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;

/**
 * Created by kdenzel on 31.08.2017.
 */

public class PlayerPhysicsComponent extends PhysicsComponent {

    private static final String TAG = PlayerPhysicsComponent.class.getName();
    private Vector3 _mouseSelectCoordinates;
    private boolean _isMouseSelectEnabled = false;
    private Ray _selectionRay;
    private float _selectRayMaximumDistance = 32.0f;

    public PlayerPhysicsComponent() {
        super();
        _mouseSelectCoordinates = new Vector3(0,0,0);
        _selectionRay = new Ray(new Vector3(), new Vector3());
        _nextEntityPosition = new Vector2(0,0);
    }

    @Override
    public void update(Entity entity, MapManager mapMgr, float delta) {
        //We want the hitbox to be at the feet for a better feel
        updateBoundingBoxPosition(_nextEntityPosition);
        updatePortalLayerActivation(mapMgr);
        if( _isMouseSelectEnabled ){
            selectMapEntityCandidate(mapMgr);
            _isMouseSelectEnabled = false;
        }

        if ( !isCollisionWithMapLayer(entity, mapMgr) &&
                !isCollisionWithMapEntities(entity, mapMgr) &&
                _state == Entity.State.WALKING){
            setNextPositionToCurrent(entity);
            Camera camera = mapMgr.getCamera();
            camera.position.set(_currentEntityPosition.x,
                    _currentEntityPosition.y, 0f);
            camera.update();
        } else {
            updateBoundingBoxPosition(_currentEntityPosition);
        }
        calculateNextPosition(delta);
    }

    private boolean updatePortalLayerActivation(MapManager mapMgr){
        MapLayer mapPortalLayer =  mapMgr.getPortalLayer();

        if( mapPortalLayer == null ){
            //Gdx.app.debug(TAG, "Portal Layer doesn't exist!");
            return false;
        }

        Rectangle rectangle = null;

        for( MapObject object: mapPortalLayer.getObjects()){
            if(object instanceof RectangleMapObject) {
                rectangle = ((RectangleMapObject)object).getRectangle();

                if (_boundingBox.overlaps(rectangle) ){
                    String mapName = object.getName();
                    if( mapName == null ) {
                        return false;
                    }

                    mapMgr.setClosestStartPositionFromScaledUnits(_currentEntityPosition);
                    mapMgr.loadMap(MapFactory.MapType.valueOf(mapName));

                    _currentEntityPosition.x = mapMgr.getPlayerStartUnitScaled().x;
                    _currentEntityPosition.y = mapMgr.getPlayerStartUnitScaled().y;
                    _nextEntityPosition.x = mapMgr.getPlayerStartUnitScaled().x;
                    _nextEntityPosition.y = mapMgr.getPlayerStartUnitScaled().y;

                    Gdx.app.debug(TAG, "Portal Activated");
                    return true;
                }
            }
        }
        return false;
}

    @Override
    public void dispose(){
    }

    @Override
    public void receiveMessage(String message) {
        String[] string = message.split(Component.MESSAGE_TOKEN);
        if( string.length == 0 ) return;
        //Specifically for messages with 1 object payload
        if( string.length == 2 ) {
            if (string[0].equalsIgnoreCase(
                    MESSAGE.INIT_START_POSITION.toString())) {
                _currentEntityPosition =
                        _json.fromJson(Vector2.class, string[1]);
                _nextEntityPosition.set(_currentEntityPosition.x,
                        _currentEntityPosition.y);
            } else if (string[0].equalsIgnoreCase(
                    MESSAGE.CURRENT_STATE.toString())) {
                _state = _json.fromJson(Entity.State.class, string[1]);
            } else if (string[0].equalsIgnoreCase(
                    MESSAGE.CURRENT_DIRECTION.toString())) {
                _currentDirection =
                        _json.fromJson(Entity.Direction.class,
                                string[1]);
            } else if (string[0].equalsIgnoreCase(
                    MESSAGE.INIT_SELECT_ENTITY.toString())) {
                _mouseSelectCoordinates =
                        _json.fromJson(Vector3.class, string[1]);
                _isMouseSelectEnabled = true;
            }
        }
    }

    private void selectMapEntityCandidate(MapManager mapMgr){
        Array<Entity> currentEntities =
                mapMgr.getCurrentMapEntities();
        //Convert screen coordinates to world coordinates,
        //then to unit scale coordinates
        mapMgr.getCamera().unproject(_mouseSelectCoordinates);
        _mouseSelectCoordinates.x /= Map.UNIT_SCALE;
        _mouseSelectCoordinates.y /= Map.UNIT_SCALE;
        for( Entity mapEntity : currentEntities ) {
        //Don't break, reset all entities
            mapEntity.sendMessage(MESSAGE.ENTITY_DESELECTED);
            Rectangle mapEntityBoundingBox =
                    mapEntity.getCurrentBoundingBox();
            if (mapEntity.getCurrentBoundingBox().contains(
                    _mouseSelectCoordinates.x,
                    _mouseSelectCoordinates.y)) {
        //Check distance
                _selectionRay.set(_boundingBox.x, _boundingBox.y,
                        0.0f, mapEntityBoundingBox.x,
                        mapEntityBoundingBox.y, 0.0f);
                float distance = _selectionRay.origin.dst(
                        _selectionRay.direction);
                if( distance <= _selectRayMaximumDistance ){
        //We have a valid entity selection
        //Picked/Selected
                    Gdx.app.debug(TAG, "Selected Entity! " +
                            mapEntity.getEntityConfig().getEntityID());
                    mapEntity.sendMessage(MESSAGE.ENTITY_SELECTED);
                }
            }
        }
    }

    public void setBoundingBoxSize(float percentageWidthReduced,
                                   float percentageHeightReduced) {
        //Update the current bounding box
        float width;
        float height;
        float widthReductionAmount = 1.0f - percentageWidthReduced;
        //.8f for 20% (1 - .20)
        float heightReductionAmount = 1.0f - percentageHeightReduced;
        //.8f for 20% (1 - .20)
        if (widthReductionAmount > 0 && widthReductionAmount < 1) {
            width = Entity.FRAME_WIDTH * widthReductionAmount;
        } else {
            width = Entity.FRAME_WIDTH;
        }

        if (heightReductionAmount > 0 && heightReductionAmount < 1) {
            height = Entity.FRAME_HEIGHT * heightReductionAmount;
        } else {
            height = Entity.FRAME_HEIGHT;
        }

        if (width == 0 || height == 0) {
            Gdx.app.debug(TAG, "Width and Height are 0!! " + width + ":" + height);
        }

        //neet do account for the unitscale, since the map coordiantes will be in pixels
        float minX;
        float minY;
        if (MapManager.UNIT_SCALE > 0) {
            minX = _nextEntityPosition.x / MapManager.UNIT_SCALE;
            minY = _nextEntityPosition.y / MapManager.UNIT_SCALE;
        } else {
            minX = _nextEntityPosition.x;
            minY = _nextEntityPosition.y;
        }
        _boundingBox.set(minX, minY, width, height);
    }
}
