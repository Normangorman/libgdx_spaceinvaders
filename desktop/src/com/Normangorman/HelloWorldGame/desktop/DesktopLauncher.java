package com.Normangorman.HelloWorldGame.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.Normangorman.HelloWorldGame.HelloWorldGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Space Invaders clone by Normangorman";
		cfg.width = 32 * 16 + 32; // the aliens are 32px wide and there are 11 columns of them.
		cfg.height = 560;
		new LwjglApplication(new HelloWorldGame(), cfg);
	}
}
