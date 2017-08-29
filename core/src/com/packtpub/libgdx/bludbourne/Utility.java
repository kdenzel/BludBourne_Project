package com.packtpub.libgdx.bludbourne;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

/**
 * Created by kdenzel on 29.08.2017.
 */
public class Utility {

    public static final AssetManager _assetManager = new AssetManager();

    private static final String TAG = Utility.class.getName();

    private static InternalFileHandleResolver _filePathResolver = new InternalFileHandleResolver();

    public static void unloadAsset(String assetFilenamePath) {
        //once the asset manager is done loading
        if (_assetManager.isLoaded(assetFilenamePath)) {
            _assetManager.unload(assetFilenamePath);
        } else {
            Gdx.app.debug(TAG, "Asset is not loaded; Nothing to unload: " + assetFilenamePath);
        }
    }

    public static float loadCompleted() {
        return _assetManager.getProgress();
    }

    public static int numberAssetsQueued() {
        return _assetManager.getQueuedAssets();
    }

    public static boolean updateAssetLoading() {
        return _assetManager.update();
    }

    public static boolean isAssetLoaded(String fileName) {
        return _assetManager.isLoaded(fileName);
    }

    public static void loadMapAsset(String mapFileNamePath) {
        if (mapFileNamePath == null || mapFileNamePath.isEmpty()) {
            return;
        }
        //load asset
        if (_filePathResolver.resolve(mapFileNamePath).exists()) {
            _assetManager.setLoader(TiledMap.class, new TmxMapLoader(_filePathResolver));

            _assetManager.load(mapFileNamePath, TiledMap.class);

            //Until we add loading screen
            //just block until we load the map
            _assetManager.finishLoadingAsset(mapFileNamePath);
            Gdx.app.debug(TAG, "Map loades! " + mapFileNamePath);
        } else {
            Gdx.app.debug(TAG, "Map doesn't exist!: " + mapFileNamePath);
        }
    }

    public static TiledMap getMapAsset(String mapFileNamePath) {
        TiledMap map = null;

        //once the asset manager is done loading
        if (_assetManager.isLoaded(mapFileNamePath)) {
            map = _assetManager.get(mapFileNamePath, TiledMap.class);
        } else {
            Gdx.app.debug(TAG, "Map is not loaded: " + mapFileNamePath);
        }

        return map;
    }

    public static void loadTextureAsset(String textureFilenamPath) {
        if (textureFilenamPath == null ||
                textureFilenamPath.isEmpty()) {
            return;
        }
        //load asset
        if (_filePathResolver.resolve(textureFilenamPath).exists()) {
            _assetManager.setLoader(Texture.class, new TextureLoader(_filePathResolver));
            _assetManager.load(textureFilenamPath, Texture.class);

            //Until we add loading screen,
            //just block until we load the map
            _assetManager.finishLoadingAsset(textureFilenamPath);
        } else {
            Gdx.app.debug(TAG, "Texture doesn't exist!: " + textureFilenamPath);
        }
    }

    public static Texture getTextureAsset(String textureFilenamePath) {
        Texture texture = null;

        //once the asset manager is done loading
        if (_assetManager.isLoaded(textureFilenamePath)) {
            texture = _assetManager.get(textureFilenamePath, Texture.class);
        } else {
            Gdx.app.debug(TAG, "Texture is not loaded: " + textureFilenamePath);
        }

        return texture;
    }

}
