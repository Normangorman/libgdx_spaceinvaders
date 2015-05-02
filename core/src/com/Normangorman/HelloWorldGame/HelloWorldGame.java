package com.Normangorman.HelloWorldGame;

import com.badlogic.gdx.*;

public class HelloWorldGame extends Game {
	@Override
	public void create () {
		Gdx.app.setLogLevel(Application.LOG_NONE);

		startMenu();
	}

	public void startMenu() {
		setScreen(new MainMenuScreen(this));
	}

	public void startGame() {
		setScreen(new GameScreen(this));
	}
}
