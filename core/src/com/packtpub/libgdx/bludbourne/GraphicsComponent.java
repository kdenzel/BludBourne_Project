package com.packtpub.libgdx.bludbourne;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import java.util.Hashtable;

/**
 * Created by kdenzel on 30.08.2017.
 */

public abstract class GraphicsComponent implements Component {

    protected Hashtable<Entity.AnimationType, Animation>
            _animations;
    protected float _frameTime = 0f;
    protected TextureRegion _currentFrame = null;
    protected ShapeRenderer _shapeRenderer;
    protected Entity.State _currentState;
    protected Entity.Direction _currentDirection;
    protected Json _json;
    protected Vector2 _currentPosition;

    protected GraphicsComponent() {
        _currentPosition = new Vector2(0,0);
        _currentState = Entity.State.WALKING;
        _currentDirection = Entity.Direction.DOWN;
        _json = new Json();
        _animations = new Hashtable<>();
        _shapeRenderer = new ShapeRenderer();
    }

    public abstract void update(Entity entity, MapManager
            mapManager, Batch batch, float delta);

    protected void updateAnimations(float delta) {
        //Want to avoid overflow
        _frameTime = (_frameTime + delta) % 5;
        Animation<TextureRegion> animation = null;
        //Look into the appropriate variable
        //when changing position
        switch (_currentDirection) {
            case DOWN:
                animation = _animations.get(Entity.AnimationType.WALK_DOWN);
                if (_currentState == Entity.State.WALKING) {
                    if (animation == null) return;
                    _currentFrame = animation.getKeyFrame(_frameTime);
                } else if (_currentState == Entity.State.IDLE) {
                    if (animation == null) return;
                    _currentFrame = animation.getKeyFrames()[0];
                } else if (_currentState == Entity.State.IMMOBILE) {
                    animation = _animations.get(Entity.AnimationType.IMMOBILE);
                    if (animation == null) return;
                    _currentFrame = animation.getKeyFrame(_frameTime);
                }
                break;
            case LEFT:
                animation = _animations.get(Entity.AnimationType.WALK_LEFT);
                if (_currentState == Entity.State.WALKING) {
                    if (animation == null) return;
                    _currentFrame = animation.getKeyFrame(_frameTime);
                } else if (_currentState == Entity.State.IDLE) {
                    if (animation == null) return;
                    _currentFrame = animation.getKeyFrames()[0];
                } else if (_currentState == Entity.State.IMMOBILE) {
                    animation = _animations.get(Entity.AnimationType.IMMOBILE);
                    if (animation == null) return;
                    _currentFrame = animation.getKeyFrame(_frameTime);
                }
                break;
            case UP:
                animation = _animations.get(Entity.AnimationType.WALK_UP);
                if (_currentState == Entity.State.WALKING) {
                    if (animation == null) return;
                    _currentFrame = animation.getKeyFrame(_frameTime);
                } else if (_currentState == Entity.State.IDLE) {
                    if (animation == null) return;
                    _currentFrame = animation.getKeyFrames()[0];
                } else if (_currentState == Entity.State.IMMOBILE) {
                    animation = _animations.get(Entity.AnimationType.IMMOBILE);
                    if (animation == null) return;
                    _currentFrame = animation.getKeyFrame(_frameTime);
                }
                break;
            case RIGHT:
                animation = _animations.get(Entity.AnimationType.WALK_RIGHT);
                if (_currentState == Entity.State.WALKING) {
                    if (animation == null) return;
                    _currentFrame = animation.getKeyFrame(_frameTime);
                } else if (_currentState == Entity.State.IDLE) {
                    if (animation == null) return;
                    _currentFrame = animation.getKeyFrames()[0];
                } else if (_currentState == Entity.State.IMMOBILE) {
                    animation = _animations.get(Entity.AnimationType.IMMOBILE);
                    if (animation == null) return;
                    _currentFrame = animation.getKeyFrame(_frameTime);
                }
                break;
            default:
                break;
        }
    }

    //Specific to two frame animations where each frame is stored
    //in a separate texture
    protected Animation loadAnimation(String firstTexture, String
            secondTexture, Array<GridPoint2> points, float
                                              frameDuration){
        Utility.loadTextureAsset(firstTexture);
        Texture texture1 = Utility.getTextureAsset(firstTexture);

        Utility.loadTextureAsset(secondTexture);
        Texture texture2 = Utility.getTextureAsset(secondTexture);

        TextureRegion[][] texture1Frames = TextureRegion.split(
                texture1,
                Entity.FRAME_WIDTH,
                Entity.FRAME_HEIGHT);
        TextureRegion[][] texture2Frames = TextureRegion.split(
                texture2,
                Entity.FRAME_WIDTH,
                Entity.FRAME_HEIGHT);

        Array<TextureRegion> animationKeyFrames = new
                Array<TextureRegion>(2);
        GridPoint2 point = points.first();
        animationKeyFrames.add(texture1Frames[point.x][point.y]);
        animationKeyFrames.add(texture2Frames[point.x][point.y]);
        return new Animation(frameDuration, animationKeyFrames,
                Animation.PlayMode.LOOP);
    }

    protected Animation loadAnimation(String textureName, Array
            <GridPoint2> points, float frameDuration){
        Utility.loadTextureAsset(textureName);
        Texture texture = Utility.getTextureAsset(textureName);
        TextureRegion[][] textureFrames = TextureRegion.split(
                texture,
                Entity.FRAME_WIDTH,
                Entity.FRAME_HEIGHT);
        Array<TextureRegion> animationKeyFrames = new
                Array<TextureRegion>(points.size);
        for( GridPoint2 point : points){
            animationKeyFrames.add(
                    textureFrames[point.x][point.y]);
        }
        return new Animation(frameDuration, animationKeyFrames,
                Animation.PlayMode.LOOP);
    }
}
