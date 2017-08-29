package com.packtpub.libgdx.bludbourne;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.packtpub.libgdx.bludbourne.screens.MainGameScreen;

public class BludBourne extends Game {

	public static final MainGameScreen _mainGameScreen = new MainGameScreen();

	@Override
	public void create () {
		setScreen(_mainGameScreen);
	}

	@Override
	public void render () {
		_mainGameScreen.render(Gdx.graphics.getDeltaTime());
	}
	
	@Override
	public void dispose () {
		_mainGameScreen.dispose();
	}
}
