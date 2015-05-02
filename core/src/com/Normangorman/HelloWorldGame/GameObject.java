package com.Normangorman.HelloWorldGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Ben on 23/04/2015.
 */
public abstract class GameObject {
    public Vector2 position = new Vector2(); // position refers to center-point
    public Vector2 velocity = new Vector2();

    public int width;
    public int height;

    private boolean dead;

    boolean hasAnimation = false;
    SpriteAnimation animation;

    public GameObject(float x, float y, int width, int height) {
        position.x = x;
        position.y = y;
        this.width = width;
        this.height = height;
    }

    public void update(float dt) {
        Vector2 v = velocity.cpy();
        position.add(v.scl(dt));

        if (this.hasAnimation)
            animation.update(dt);
    }

    public void render(SpriteBatch batch) {
        if (this.hasAnimation) {
            animation.render(batch, position.x - width/2f, position.y - height/2f);
        }
        else {
            ShapeRenderer sr = new ShapeRenderer();

            sr.begin(ShapeRenderer.ShapeType.Filled);
            sr.setColor(0.5f, 0.5f, 0.5f, 1.0f);
            sr.rect(position.x - width/2f, position.y - height/2f, width, height);
            sr.end();
        }
    }

    public Rectangle getRect() {
        return new Rectangle(this.position.x - this.width/2f, this.position.y - this.height/2f, this.width, this.height);
    }

    public boolean collides (GameObject other) {
        Rectangle r1 = this.getRect();
        Rectangle r2 = other.getRect();
        return r1.overlaps(r2);
    }

    public void handleCollision(GameObject other) {};

    public void setAnimation(SpriteAnimation a) {
        this.animation = a;
        hasAnimation = true;
    }

    public boolean isOffScreen() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        Rectangle screenRect = new Rectangle(0, 0, screenWidth, screenHeight);
        Rectangle thisRect = this.getRect();
        boolean onScreen = thisRect.overlaps(screenRect);
        return !onScreen;
    }

    public boolean isDead() { return dead; }
    public void setDead(boolean state) { dead = state; }
}
