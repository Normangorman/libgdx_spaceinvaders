package com.Normangorman.HelloWorldGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by Ben on 23/04/2015.
 */
public class SpriteAnimation {
    Animation animation;
    TextureRegion currentFrame;

    float frameTimer;
    boolean looping = true;

    public SpriteAnimation(String filePath, int frameWidth, int frameHeight) {
        this(filePath, frameWidth, frameHeight, 0.25f);
    }

    public SpriteAnimation(String filePath, int frameWidth, int frameHeight, float speed) {
        Texture spritesheet = new Texture(filePath);
        int numRows = spritesheet.getHeight() / frameHeight;
        int numCols = spritesheet.getWidth() / frameWidth;


        TextureRegion[][] tmp = TextureRegion.split(spritesheet, frameWidth, frameHeight);
        TextureRegion[] frames = new TextureRegion[numRows * numCols];

        int index = 0;
        for (int i = 0; i < numRows; i++)
            for (int j = 0; j < numCols; j++) {
                frames[index++] = tmp[i][j];
            }

        animation = new Animation(speed, frames);
    }

    public void update(float dt) {
        frameTimer += dt;
    }

    public void render(SpriteBatch batch, float x, float y) {
        currentFrame = animation.getKeyFrame(frameTimer, looping);
        batch.draw(currentFrame, x, y);
    }

    public float getSpeed() {
        return animation.getFrameDuration();
    }

    public void setSpeed(float speed) {
        animation.setFrameDuration(speed);
    }

    public void setPlayMode(Animation.PlayMode p) {
        animation.setPlayMode(p);
    }

    public void forceNextFrame() {
        frameTimer += animation.getFrameDuration();
    }

    public void setLooping(boolean val) {
        looping = val;
    }

    public boolean isFinished() {
        return animation.isAnimationFinished(frameTimer);
    }
}
