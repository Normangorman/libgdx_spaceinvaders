package com.Normangorman.HelloWorldGame.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.Normangorman.HelloWorldGame.HelloWorldGame;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                int width = 32 * 16 + 32;
                int height = 560;
                GwtApplicationConfiguration cfg = new GwtApplicationConfiguration(width, height);
                return cfg;
        }

        @Override
        public ApplicationListener getApplicationListener () {
                return new HelloWorldGame();
        }
}