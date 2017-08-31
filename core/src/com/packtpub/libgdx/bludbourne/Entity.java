package com.packtpub.libgdx.bludbourne;

import java.util.ArrayList;
import java.util.UUID;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Created by kdenzel on 29.08.2017.
 */

public class Entity implements Disposable{

    private static final String TAG = Entity.class.getSimpleName();

    public static final int FRAME_WIDTH = 16;
    public static final int FRAME_HEIGHT = 16;

    private static final int MAX_COMPONENTS = 5;

    private Json _json;
    private EntityConfig _entityConfig;
    private static final String _defaultSpritePath = "sprites/characters/Warrior.png";
    private Vector2 _velocity;
    private String _entityID;
    private Direction _currentDirection = Direction.LEFT;
    private Direction _previousDirection = Direction.UP;
    private Animation<TextureRegion> _walkLeftAnimation;
    private Animation<TextureRegion> _walkRightAnimation;
    private Animation<TextureRegion> _walkUpAnimation;
    private Animation<TextureRegion> _walkDownAnimation;

    private Array<TextureRegion> _walkLeftFrames;
    private Array<TextureRegion> _walkRightFrames;
    private Array<TextureRegion> _walkUpFrames;
    private Array<TextureRegion> _walkDownFrames;

    private Array<Component> _components;
    private InputComponent _inputComponent;
    private GraphicsComponent _graphicsComponent;
    private PhysicsComponent _physicsComponent;


    protected Vector2 _currentPlayerPosition;
    protected State _state = State.IDLE;
    protected float _frameTime = 0f;
    protected Sprite _frameSprite = null;
    protected TextureRegion _currentFrame = null;


    public static enum State {
        IDLE,
        WALKING,

        IMMOBILE; //This should always be last

        public static State getRandomNext() {
            //Ignore IMMOBILE which should be last state
            return State.values()[MathUtils.random(State.values().length - 2)];
        }
    }

    public static enum Direction {
        UP, RIGHT, DOWN, LEFT;

        public static Direction getRandomNext() {
            return Direction.values()[MathUtils.random(Direction.values().length - 1)];
        }

        public Direction getOpposite() {
            if (this == LEFT) {
                return RIGHT;
            } else if (this == RIGHT) {
                return LEFT;
            } else if (this == UP) {
                return DOWN;
            } else {
                return UP;
            }
        }
    }

    public static enum AnimationType {
        WALK_LEFT,
        WALK_RIGHT,
        WALK_UP,
        WALK_DOWN,
        IDLE,
        IMMOBILE
    }

    public Entity(InputComponent inputComponent, PhysicsComponent physicsComponent, GraphicsComponent graphicsComponent) {
        _entityConfig = new EntityConfig();
        _json = new Json();

        _components = new Array<Component>(MAX_COMPONENTS);

        _inputComponent = inputComponent;
        _physicsComponent = physicsComponent;
        _graphicsComponent = graphicsComponent;

        _components.add(inputComponent);
        _components.add(physicsComponent);
        _components.add(graphicsComponent);
    }

    public Entity() {
        initEntity();
    }

    public void initEntity() {
        this._entityID = UUID.randomUUID().toString();
        this._currentPlayerPosition = new Vector2();
        this._velocity = new Vector2(2f, 2f);

        Utility.loadTextureAsset(_defaultSpritePath);
        loadDefaultSprite();
        loadAllAnimations();
    }

    public void update(MapManager mapMgr, Batch batch, float deltaTime) {
        _frameTime = (_frameTime + deltaTime) % 5; //Avoid Overflow 0.x % 5 = 0.x
        _inputComponent.update(this, deltaTime);
        _physicsComponent.update(this, mapMgr, deltaTime);
        _graphicsComponent.update(this, mapMgr,batch,deltaTime);
    }

    private void loadDefaultSprite() {
        Texture texture = Utility.getTextureAsset(_defaultSpritePath);
        TextureRegion[][] textureFrames = TextureRegion.split(texture, FRAME_WIDTH, FRAME_HEIGHT);
        _frameSprite = new Sprite(textureFrames[0][0], 0, 0, FRAME_WIDTH, FRAME_HEIGHT);
        _currentFrame = textureFrames[0][0];
    }

    private void loadAllAnimations() {
        //Walking animation
        Texture texture = Utility.getTextureAsset(_defaultSpritePath);
        TextureRegion[][] textureFrames = TextureRegion.split(texture, FRAME_WIDTH, FRAME_HEIGHT);
        _walkDownFrames = new Array<>(4);
        _walkLeftFrames = new Array<>(4);
        _walkRightFrames = new Array<>(4);
        _walkUpFrames = new Array<>(4);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                TextureRegion region = textureFrames[i][j];
                if (region == null) {
                    Gdx.app.debug(TAG, "Got null animation frame " + i + "," + j);
                }
                switch (i) {
                    case 0:
                        _walkDownFrames.insert(j, region);
                        break;
                    case 1:
                        _walkLeftFrames.insert(j, region);
                        break;
                    case 2:
                        _walkRightFrames.insert(j, region);
                        break;
                    case 3:
                        _walkUpFrames.insert(j, region);
                        break;
                }
            }
        }

        _walkDownAnimation = new Animation(0.25f, _walkDownFrames, Animation.PlayMode.LOOP);
        _walkLeftAnimation = new Animation(0.25f, _walkLeftFrames, Animation.PlayMode.LOOP);
        _walkRightAnimation = new Animation(0.25f, _walkRightFrames, Animation.PlayMode.LOOP);
        _walkUpAnimation = new Animation(0.25f, _walkUpFrames, Animation.PlayMode.LOOP);
    }

    @Override
    public void dispose() {
        for(Component component : _components){
            component.dispose();
        }
        //Utility.unloadAsset(_defaultSpritePath);
    }

    public void setState(State state) {
        this._state = state;
    }

    public Sprite getFrameSprite() {
        return _frameSprite;
    }

    public TextureRegion getFrame() {
        return _currentFrame;
    }

    public Vector2 getCurrentPosition() {
        return _currentPlayerPosition;
    }

    public void setDirection(Direction direction, float
            deltaTime) {
        this._previousDirection = this._currentDirection;
        this._currentDirection = direction;
        //Look into the appropriate variable when changing position
        switch (_currentDirection) {
            case DOWN:
                _currentFrame = _walkDownAnimation.getKeyFrame(_frameTime);
                break;
            case LEFT:
                _currentFrame = _walkLeftAnimation.getKeyFrame(_frameTime);
                break;
            case UP:
                _currentFrame = _walkUpAnimation.getKeyFrame(_frameTime);
                break;
            case RIGHT:
                _currentFrame = _walkRightAnimation.
                        getKeyFrame(_frameTime);
                break;
            default:
                break;
        }
    }

    public EntityConfig getEntityConfig() {
        return _entityConfig;
    }

    public void sendMessage(Component.MESSAGE messageType, String ...args){
        String fullMessage = messageType.toString();

        for(String string : args){
            fullMessage += Component.MESSAGE_TOKEN + string;
        }

        for(Component component : _components){
            component.receiveMessage(fullMessage);
        }
    }

    public Rectangle getCurrentBoundingBox() {
        return _physicsComponent._boundingBox;
    }

    public void setEntityConfig(EntityConfig config){
        _entityConfig = config;
    }

    public static EntityConfig getEntityConfig(String configFilePath){
        Json json = new Json();
        return json.fromJson(EntityConfig.class,
                Gdx.files.internal(configFilePath));
    }

    public static Array<EntityConfig> getEntityConfigs(String configFilePath){
        Json json = new Json();
        Array<EntityConfig> configs = new Array<EntityConfig>();
        ArrayList<JsonValue> list = json.fromJson(ArrayList.class,
                Gdx.files.internal(configFilePath));
        for (JsonValue jsonVal : list) {
            configs.add(json.readValue(EntityConfig.class,
                    jsonVal));
        }
        return configs;
    }
}
